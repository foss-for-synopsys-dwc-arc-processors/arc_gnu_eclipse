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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.cdt.cross.arc.gnu.ARCPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TcfContent {

    public static final String GCC_OPTIONS_SECTION = "gcc_compiler";
    public static final String LINKER_MEMORY_MAP_SECTION = "gnu_linker_command_file";

    private Properties gccOptions;
    private String linkerMemoryMap;
    private long modTime;

    private static Map<File, TcfContent> cache = new HashMap<File, TcfContent>();

    private TcfContent() {
    }

    private static void checkNameNotEmpty(File f) throws FileNotFoundException {
        if (f.getAbsolutePath().isEmpty()) {
            throw new FileNotFoundException("TCF path's value must not be empty.");
        }
    }

    private static void checkFileExists(File f) throws FileNotFoundException {
        if (!f.exists()) {
            throw new FileNotFoundException(
                    "File " + f.getAbsolutePath() + " does not exist in the system.");
        }
    }

    /**
     * Check that TCF file and used tool chain are for the same processor
     * @param cpu  processor value from tool chain
     * @throws TcfContentException
     */
    private void checkArchitecture(String cpu)
            throws TcfContentException {
        String cpu_option = "-mcpu";
        String value = gccOptions.getProperty(cpu_option);
        if (value.isEmpty()) {
            throw new TcfContentException("Invalid option in TCF: " + cpu_option + ".");
        }
        cpu_option = cpu_option + "=" + value;
        if (!cpu_option.equals(cpu)) {
            String expected_arch = cpu.split("=")[1].toUpperCase();
            expected_arch = expected_arch.substring(0, 3) + " "
                    + expected_arch.substring(3);
            value = (value.substring(0, 3) + " " + value.substring(3)).toUpperCase();
            throw new TcfContentException("TCF describes " + value
                    + " architecture, but selected tool chain is for " + expected_arch + ".");
        }
    }

    /**
     * Checks that file exists, then reads it and checks that TCF and used tool chain are for the
     * same processor. If cannot read file or some of the checks fail, shows or logs the error or
     * does nothing depending on the value of <code>showStyle</code>.
     * 
     * @param f
     *            TCF to read
     * @param cpu
     *            processor value from tool chain
     * @param showStyle
     *            style indicating what should be done in case exception occurred while reading.
     *            Applicable values are <code>StatusManager.NONE</code>,
     *            <code>StatusManager.LOG</code>, <code>StatusManager.SHOW</code> and
     *            <code>StatusManager.BLOCK</code>.
     * @param messages
     *            Additional messages to display
     * @return TcfContent, if reading was successful, and null otherwise
     */
    public static TcfContent readFile(File f, String cpu, int showStyle, String... messages) {
        TcfContent tcfContent = null;
        try {
            tcfContent = readFile(f, cpu);
        } catch (TcfContentException e) {
            StringBuilder builder = new StringBuilder(e.getMessage());
            for (String message: messages) {
                builder.append(message);
            }
            String message = builder.toString();
            StatusManager.getManager().handle(new Status(IStatus.ERROR, ARCPlugin.PLUGIN_ID, message), showStyle);
        }
        return tcfContent;
    }

    /**
     * Checks that file exists, then reads it and checks that TCF and used tool chain are for the
     * same processor. If cannot read file or some of the checks fail, throws
     * <code>TcfContentException</code>.
     * 
     * @param f
     *            TCF to read
     * @param cpu
     *            processor value from tool chain
     * @return TcfContent or null if cannot read
     * @throws TcfContentException
     */
    public static TcfContent readFile(File f, String cpu) throws TcfContentException {

        TcfContent tcfContent = cache.get(f);
        if (tcfContent != null && tcfContent.modTime == f.lastModified()) {
            return tcfContent;
        }

        try {
            checkNameNotEmpty(f);
            checkFileExists(f);

            tcfContent = new TcfContent();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(f);
            Element root = document.getDocumentElement();
            NodeList attributes = root.getElementsByTagName("configuration");
            for (int index = 0; index < attributes.getLength(); index++) {
                Element e = (Element) attributes.item(index);
                String elementName = e.getAttribute("name");
                if (!elementName.equals(GCC_OPTIONS_SECTION)
                        && !elementName.equals(LINKER_MEMORY_MAP_SECTION)) {
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
                    tcfContent.gccOptions = new Properties();
                    /*
                     * Need to escape whitespaces here because in java.util.Properties key termination
                     * characters are '=', ':' and whitespace. So if our TCF has several option like
                     * "--param ...", --param will be considered a key and therefore Properties will
                     * load only one of these options. If we escape a whitespace, it will be considered
                     * part of a key.
                     */
                    data = data.replace(" ", "\\ ");
                    tcfContent.gccOptions.load(new StringReader(data));
                    tcfContent.checkArchitecture(cpu);
                }
                if (elementName.equals(LINKER_MEMORY_MAP_SECTION)) {
                    tcfContent.linkerMemoryMap = data;
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new TcfContentException("Couldn't read TCF:  " + e.getMessage(), e);
        }
        if (tcfContent.gccOptions == null || tcfContent.linkerMemoryMap == null) {
            String sectionName = tcfContent.getGccOptions() == null ? GCC_OPTIONS_SECTION
                    : LINKER_MEMORY_MAP_SECTION;
            throw new TcfContentException(
                    "Malformed TCF: " + sectionName + " configuration is missing");
        }
        tcfContent.modTime = f.lastModified();
        cache.put(f, tcfContent);
        return tcfContent;
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

    public Properties getGccOptions() {
        return gccOptions;
    }

    public String getLinkerMemoryMap() {
        return linkerMemoryMap;
    }

}
