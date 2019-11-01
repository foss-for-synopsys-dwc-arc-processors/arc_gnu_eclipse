// SPDX-License-Identifier: EPL-2.0

package com.synopsys.arc.gnu.elf.tcf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.cdt.utils.CommandLineUtil;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.arc.cdt.toolchain.ArcCpu;
import com.arc.cdt.toolchain.ArcCpuFamily;

public final class TcfContent
{
    public static final String GCC_OPTIONS_SECTION = "gcc_compiler";
    public static final String LINKER_MEMORY_MAP_SECTION = "gnu_linker_command_file";
    public static final String C_DEFINES_SECTION = "C_defines";

    private static final Set<String> KNOWN_SECTIONS = Set.of(
        GCC_OPTIONS_SECTION,
        LINKER_MEMORY_MAP_SECTION,
        C_DEFINES_SECTION);
    private static final Map<String, TcfContent> CACHE = new HashMap<>();

    private final Map<String, String> sectionsFilenames;
    private final Map<String, String> sectionsContent;
    private final String[] gccOptionsArray;
    private final FileTime lastModificationTime;

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

        // Check if file is already in the cache.
        TcfContent cachedTcf = CACHE.get(path.toString());
        if (cachedTcf != null
            && cachedTcf.lastModificationTime.equals(tcfModificationTime)) {
            return cachedTcf;
        }

        var sectionsContent = new HashMap<String, String>();
        var sectionsFiles = new HashMap<String, String>();
        try {
            var root = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(path.toFile())
                .getDocumentElement();
            var attributes = root.getElementsByTagName("configuration");
            for (int index = 0; index < attributes.getLength(); index++) {
                var e = (Element) attributes.item(index);
                var sectionName = e.getAttribute("name");
                if (!KNOWN_SECTIONS.contains(sectionName)) {
                    continue;
                }
                sectionsContent.put(sectionName, getConfigurationContent(e));
                sectionsFiles.put(sectionName, e.getAttribute("filename"));
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new TcfContentException("Couldn't read TCF: " + e.getMessage(), e);
        }

        for (var sectionName : KNOWN_SECTIONS) {
            if (!sectionsContent.containsKey(sectionName)) {
                throw new TcfContentException(MessageFormat
                    .format("Malformed TCF: {} configuration is missing.", sectionName));
            }
        }

        var tcfContent = new TcfContent(sectionsContent, sectionsFiles, tcfModificationTime);
        if (tcfContent.getCpuFamily().isEmpty()) {
            throw new TcfContentException(
                "Malformed TCF: doesn't have valid GCC -mcpu option value.");
        }

        CACHE.put(path.toString(), tcfContent);
        return tcfContent;
    }

    private static String getCharacterDataFromElement(Element e) throws TcfContentException
    {
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

    private static String getConfigurationContent(Element e) throws TcfContentException
    {
        NodeList stringList = e.getElementsByTagName("string");
        if (stringList == null || stringList.item(0) == null) {
            throw new TcfContentException(MessageFormat.format(
                "Malformed TCF: No 'string' element in {} configuration.",
                e.getAttribute("name")));
        }
        return getCharacterDataFromElement((Element) stringList.item(0));
    }

    private TcfContent(
        Map<String, String> sectionsContent,
        Map<String, String> sectionsFiles,
        FileTime lastModificationTime)
    {
        this.sectionsContent = sectionsContent;
        this.sectionsFilenames = sectionsFiles;
        this.lastModificationTime = lastModificationTime;
        this.gccOptionsArray =
            CommandLineUtil.argumentsToArray(sectionsContent.get(GCC_OPTIONS_SECTION));
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
        return sectionsFilenames.get(sectionId);
    }

    public String getSectionContent(String sectionId)
    {
        return sectionsContent.get(sectionId);
    }
}
