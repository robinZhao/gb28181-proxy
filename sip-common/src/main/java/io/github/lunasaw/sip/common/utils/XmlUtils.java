package io.github.lunasaw.sip.common.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.util.ResourceUtils;

import com.google.common.base.Joiner;

import lombok.SneakyThrows;

/**
 * @author luna
 * @date 2023/10/15
 */
public class XmlUtils {

    @SneakyThrows
    public static String toString(String charset, Object object) {
        JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, charset);

        StringWriter writer = new StringWriter();
        marshaller.marshal(object, writer);
        return writer.toString();
    }

    @SneakyThrows
    public static <T> Object parseObj(String xmlStr, Class<T> clazz, String charset) {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return unmarshaller.unmarshal(new StringReader(new String(xmlStr.getBytes(charset), charset)));
    }

    @SneakyThrows
    public static <T> Object parseObj(String xmlStr, Class<T> clazz) {
        return parseObj(xmlStr, clazz, "UTF-8");
    }

    @SneakyThrows
    public static <T> Object parseFile(String resource, Class<T> clazz) {
        return parseFile(resource, clazz, StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public static <T> Object parseFile(String resource, Class<T> clazz, Charset charset) {
        File file = ResourceUtils.getFile(resource);
        List<String> strings = Files.readAllLines(Paths.get(file.getAbsolutePath()), charset);

        String join = Joiner.on("\n").join(strings);
        return parseObj(join, clazz);
    }

    @SneakyThrows
    public static String getCmdType(String xmlStr) {
        SAXReader reader = new SAXReader();

        Document document = reader.read(new StringReader(xmlStr));
        // 获取根元素
        Element root = document.getRootElement();
        // 获取CmdType子元素
        Element cmdType = root.element("CmdType");

        return cmdType.getText();
    }


    @SneakyThrows
    public static String getRootType(String xmlStr) {
        SAXReader reader = new SAXReader();

        Document document = reader.read(new StringReader(xmlStr));
        // 获取根元素
        Element root = document.getRootElement();

        return root.getName();
    }

    
    public static String getXmlEncoding(byte[] content){
         String xmlDeclaration=null;
        try{
            xmlDeclaration = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content))).readLine();
        }catch(Exception e){
                     
        }
        return getXmlHeadEncoding(xmlDeclaration);
    }

        
    public static String getXmlHeadEncoding(String xmlDeclaration){
        if(xmlDeclaration.startsWith("<?xml")){
            Pattern pattern = Pattern.compile("encoding=[\"']([^\"']+)[\"']");
            Matcher matcher = pattern.matcher(xmlDeclaration);
            if (matcher.find()) {
                String encoding = matcher.group(1);
                if(StringUtils.isNotBlank(encoding)){
                    return encoding;
                }
            }
        }
        return "UTF-8";
    }
}
