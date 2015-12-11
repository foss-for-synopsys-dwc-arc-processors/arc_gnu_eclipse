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
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

    public static TcfContent readFile(File f) throws TcfContentException {

        TcfContent tcfContent = cache.get(f);
        if (tcfContent != null && tcfContent.modTime == f.lastModified()) {
            return tcfContent;
        }
        try {
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
                    tcfContent.gccOptions.load(new StringReader(data));
                }
                if (elementName.equals(LINKER_MEMORY_MAP_SECTION)) {
                    tcfContent.linkerMemoryMap = data;
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new TcfContentException("Couldn't read TCF:  " + e.getMessage(), e);
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
