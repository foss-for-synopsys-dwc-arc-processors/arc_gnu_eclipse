/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.cdt.toolchain.tcf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.cdt.utils.CommandLineUtil;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.arc.cdt.toolchain.ArcCpu;
import com.arc.cdt.toolchain.ArcCpuFamily;

public class TcfContent {

    public static final String GCC_OPTIONS_SECTION = "gcc_compiler";
    public static final String LINKER_MEMORY_MAP_SECTION = "gnu_linker_command_file";
    public static final String C_DEFINES_SECTION = "C_defines";
    public static final List<String> knownSections = 
            Arrays.asList( GCC_OPTIONS_SECTION, LINKER_MEMORY_MAP_SECTION, C_DEFINES_SECTION );

    private String[] gccOptionsArray;
    private FileTime lastModificationTime;
    private final Map<String, String> sectionNames = new HashMap<>();
    private final Map<String, String> sectionContent = new HashMap<>();

    private static Map<String, TcfContent> cache = new HashMap<>();

    private TcfContent() {
    }

    /**
     * Checks that file exists, then reads it and checks that TCF and used tool chain are for the
     * same processor. If cannot read file or some of the checks fail, throws
     * <code>TcfContentException</code>.
     * 
     * @param path The path to a TCF to read.
     * @throws TcfContentException if file at the {@code path} doesn't exist or can't be read.
     */
    public static TcfContent readFile(Path path) throws TcfContentException
    {
        if (!Files.isReadable(path)) {
            throw new TcfContentException(MessageFormat.format(
                "File {} does not exist in the system or can't be read.",
                path.toAbsolutePath()));
        }

        FileTime tcfModificationTime;
        try {
            tcfModificationTime = Files.getLastModifiedTime(path);
        } catch (IOException err) {
            throw new TcfContentException("Failed to read last modification time of TCF.", err);
        }

        TcfContent tcfContent = cache.get(path.toString());
        if (tcfContent != null
            && tcfContent.lastModificationTime.equals(tcfModificationTime)) {
            return tcfContent;
        }

        try {
            tcfContent = new TcfContent();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(path.toFile());
            Element root = document.getDocumentElement();
            NodeList attributes = root.getElementsByTagName("configuration");
            for (int index = 0; index < attributes.getLength(); index++) {
                Element e = (Element) attributes.item(index);
                String elementName = e.getAttribute("name");
                if (!knownSections.contains(elementName)) {
                    continue;
                }
                NodeList stringList = e.getElementsByTagName("string");
                if (stringList == null || stringList.item(0) == null) {
                    throw new TcfContentException(
                            "Malformed TCF:  No 'string' element in "
                                    + e.getAttribute("name") + " configuration");
                }
                String data = getCharacterDataFromElement((Element) stringList.item(0));
                if (elementName.equals(GCC_OPTIONS_SECTION)) {
                    tcfContent.gccOptionsArray = CommandLineUtil.argumentsToArray(data);
                    tcfContent.sectionContent.put(GCC_OPTIONS_SECTION, data);
                    tcfContent.sectionNames.put(GCC_OPTIONS_SECTION, e.getAttribute("filename"));
                }
                if (elementName.equals(LINKER_MEMORY_MAP_SECTION)) {
                    tcfContent.sectionContent.put(LINKER_MEMORY_MAP_SECTION, data);
                    tcfContent.sectionNames.put(LINKER_MEMORY_MAP_SECTION,
                        e.getAttribute("filename"));
                }
                if (elementName.equals(C_DEFINES_SECTION)) {
                    tcfContent.sectionContent.put(C_DEFINES_SECTION, data);
                    tcfContent.sectionNames.put(C_DEFINES_SECTION, e.getAttribute("filename"));
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new TcfContentException("Couldn't read TCF: " + e.getMessage(), e);
        }

        for (var sectionName : knownSections) {
            if (!tcfContent.sectionContent.containsKey(sectionName)) {
                throw new TcfContentException(MessageFormat
                    .format("Malformed TCF: {} configuration is missing.", sectionName));
            }
        }

        if (tcfContent.getCpuFamily().isEmpty()) {
            throw new TcfContentException(
                "Malformed TCF: doesn't have valid GCC -mcpu option value.");
        }

        tcfContent.lastModificationTime = tcfModificationTime;
        cache.put(path.toString(), tcfContent);
        return tcfContent;
    }

    /**
     * Return the target CPU family of the TCF.
     */
    public Optional<ArcCpuFamily> getCpuFamily()
    {
        return Arrays.stream(gccOptionsArray)
            .map(String::strip)
            .filter(s -> s.startsWith("-mcpu="))
            .findFirst()
            .map(ArcCpu::fromCommand)
            .map(ArcCpu::getToolChain);
    }

    private static String getCharacterDataFromElement(Element e) throws TcfContentException {
        Node child = e.getFirstChild();
        if (child == null) {
            throw new TcfContentException(
                    "Malformed TCF: Couldn't get character data from element " + e.getNodeName());
        }
        if (child instanceof CharacterData) {
            CharacterData data = (CharacterData) child;
            return data.getData();
        }
        throw new TcfContentException(
                "Malformed TCF: Couldn't get character data from element " + e.getNodeName());
    }

    public String[] getGccOptions()
    {
        return gccOptionsArray;
    }

    public FileTime getLastModifiedTime()
    {
        return lastModificationTime;
    }

    public String getSectionFilename(String sectionId)
    {
        return sectionNames.get(sectionId);
    }

    public String getSectionContent(String sectionId)
    {
        return sectionContent.get(sectionId);
    }
}
