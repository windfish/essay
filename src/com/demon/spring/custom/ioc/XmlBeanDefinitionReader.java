package com.demon.spring.custom.ioc;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.demon.spring.custom.ioc.config.BeanDefinition;
import com.demon.spring.custom.ioc.config.BeanDefinitionReader;
import com.demon.spring.custom.ioc.config.BeanReference;
import com.demon.spring.custom.ioc.config.PropertyValue;

/**
 * 加载和解析xml 配置文件，构造BeanDefinitionMap，记录bean 的配置信息
 * @author xuliang
 * @since 2018年12月18日 上午10:38:18
 *
 */
public class XmlBeanDefinitionReader implements BeanDefinitionReader {

    private Map<String, BeanDefinition> beanMap;
    
    public XmlBeanDefinitionReader() {
        beanMap = new HashMap<>();
    }
    
    public Map<String, BeanDefinition> getBeans(){
        return beanMap;
    }
    
    /**
     * 1. 加载xml 配置文件
     * 2. 获取根节点对象
     * 3. 解析子节点
     */
    @Override
    public void loadBeanDefinition(String location) throws Exception {
        InputStream is = new FileInputStream(location);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(is);
        Element root = doc.getDocumentElement();
        parseBeanDefinitions(root);
    }
    
    /**
     * 循环处理根节点的子节点
     */
    private void parseBeanDefinitions(Element root){
        NodeList nodes = root.getChildNodes();
        for(int i=0;i<nodes.getLength();i++){
            Node item = nodes.item(i);
            if(item instanceof Element){
                parseBeanDefinition((Element)item);
            }
        }
    }
    
    /**
     * 1. 获取bean 标签的id 和class 属性
     * 2. 构造BeanDefinition 对象，记录bean 的配置信息，但是并没有生成bean 对象，对应Spring 里的懒加载
     * 3. 记录bean 对象的id、className、Class 以及property
     * 4. 将BeanDefinition 对象写入map
     */
    private void parseBeanDefinition(Element item){
        String id = item.getAttribute("id");
        String className = item.getAttribute("class");
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setId(id);
        beanDefinition.setClassName(className);
        parseProperty(item, beanDefinition);
        beanMap.put(id, beanDefinition);
    }
    
    /**
     * 1. 获取bean 标签的子标签property，获取对象的属性配置
     * 2. 若property 的value 存在，则直接构造PropertyValue，并写入BeanDefinition 的PropertyValues 中
     * 3. 若property 的ref 存在，则先构造BeanReference（同样的懒加载，只记录ref 对象名，并没有生成对应的ref 对象），再构造PropertyValue
     */
    private void parseProperty(Element item, BeanDefinition beanDefinition){
        NodeList propertyNodes = item.getElementsByTagName("property");
        for(int i=0;i<propertyNodes.getLength();i++){
            Node propertyNode = propertyNodes.item(i);
            if(!(propertyNode instanceof Element)){
                continue;
            }
            Element property = (Element) propertyNode;
            String name = property.getAttribute("name");
            String value = property.getAttribute("value");
            if(value != null && value.length() > 0){
                beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, value));
            }else{
                String ref = property.getAttribute("ref");
                if(ref == null || ref.length() <= 0){
                    throw new IllegalArgumentException("ref config error");
                }
                BeanReference reference = new BeanReference(name, ref);
                beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, reference));
            }
        }
    }
    
}
