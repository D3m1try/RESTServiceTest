package com.thomas_bayer.www.product.util;

import com.thomas_bayer.www.product.bean.Product;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.element.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dzmitry_Sankouski on 31-Mar-17.
 */
public class Util {

    public static Product getProduct(String s){
        return getProduct(getXMLPath(s));
    }

    public static Product getProduct(File file){
        return getProduct(getXMLPath(file));
    }

    public static XmlPath getXMLPath(String s){
        return new XmlPath(s).setRoot("PRODUCT");
    }

    public static XmlPath getXMLPath(File file){
        return new XmlPath(file).setRoot("PRODUCT");
    }

    public static String getFirstFreeId(String s){
        return getFirstId(s, false);
    }

    public static String getFirstBusyId(String s){
        return getFirstId(s, true);
    }

    private static String getFirstId(String s, boolean isBusy){
        XmlPath path = new XmlPath(s);
        String rawNumbers = null;
        try {
            rawNumbers = String.valueOf(path.getNode("PRODUCTList").get("PRODUCT"));
        } catch (Exception e) {
            System.out.println(s);
        }

        Pattern pattern = Pattern.compile("(\\d+)"); //pattern to retieve numbers from String
        Matcher matcher = pattern.matcher(rawNumbers);

        int i = 0;
        while (matcher.find()){
            if (isBusy) {
                return matcher.group(0);
            }
            if (i != Integer.valueOf(matcher.group(0))){
                break;
            }
            i++;
        }
        return String.valueOf(i);
    }

    public static List<String> getBusyIDs(String s){
        XmlPath path = new XmlPath(s);
        List<String> busyIDs = new ArrayList<String>();
        String rawNumbers = String.valueOf(path.getNode("PRODUCTList").get("PRODUCT"));

        Pattern pattern = Pattern.compile("(\\d+)"); //pattern to retieve numbers from String
        Matcher matcher = pattern.matcher(rawNumbers);

        while (matcher.find()){
            busyIDs.add(matcher.group(0));
        }

        return busyIDs;
    }

    private static Product getProduct(XmlPath path){
        Product product = new Product();
        product.setName(String.valueOf(path.get("NAME")));
        product.setPrice(String.valueOf(path.get("PRICE")));
        product.setID(String.valueOf(path.get("ID")));
        return product;
    }



}
