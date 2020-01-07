package com.jyblife.datasource.util;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XmlUtil {

    /**
     * 读取XML
     *
     * @param file
     * @return
     */
    public static Map<String,Document> getNamespace(File file){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(file);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        NodeList links = doc.getElementsByTagName("mapper");
        Map<String,Document> data = new HashMap<>();
        data.put(links.item(0).getAttributes().item(0).getNodeValue(),doc);
        return data;
    }
}
