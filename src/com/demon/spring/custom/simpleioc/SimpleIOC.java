package com.demon.spring.custom.simpleioc;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.fastjson.JSON;

/**
 * IOC 实现类
 * 问题：一个bean 引用了另一个bean，那么在xml里，必须将被引用的bean 配置在前面
 * 
 * @author xuliang
 * @since 2018年12月14日 下午4:39:51
 *
 */
public class SimpleIOC {

    public static void main(String[] args) throws Exception {
//      String location = SimpleIOC.class.getClassLoader().getResource("ioc.xml").getFile();
      String location = "D:\\work\\git\\essay\\src\\com\\demon\\spring\\custom\\simpleioc\\ioc.xml";
      SimpleIOC bf = new SimpleIOC(location);
      Wheel wheel = (Wheel) bf.getBean("wheel");
      System.out.println(JSON.toJSONString(wheel));
      Car car = (Car) bf.getBean("car");
      System.out.println(JSON.toJSONString(car));
    }
    
    private Map<String, Object> beanMap = new HashMap<>();
    
    public SimpleIOC(String location) throws Exception {
        loadBeans(location);
    }
    
    public Object getBean(String name){
        Object bean = beanMap.get(name);
        if(bean == null){
            throw new IllegalArgumentException("no bean whith name: " + name);
        }
        return bean;
    }
    
    private void loadBeans(String location) throws Exception{
        // 加载xml 文件
        InputStream inputStream = new FileInputStream(location);
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        Element root = doc.getDocumentElement();
        NodeList nodes = root.getChildNodes();
        
        for(int i=0;i<nodes.getLength();i++){
            Node node = nodes.item(i);
            if(!(node instanceof Element)){
                continue;
            }
            Element ele = (Element) node;
            // 处理Bean 标签
            if(!"bean".equals(ele.getTagName())){
                continue;
            }
            String id = ele.getAttribute("id");
            String className = ele.getAttribute("class");
            
            // 加载Class
            Class<?> beanClass = null;
            try{
                beanClass = Class.forName(className);
            }catch (Exception e) {
                e.printStackTrace();
                return;
            }
            // 创建bean 对象
            Object bean = beanClass.newInstance();
            // 遍历property
            NodeList propertyNodes = ele.getElementsByTagName("property");
            for(int j=0;j<propertyNodes.getLength();j++){
                Node propertyNode = propertyNodes.item(j);
                if(!(propertyNode instanceof Element)){
                    continue;
                }
                Element propertyEle = (Element) propertyNode;
                String name = propertyEle.getAttribute("name");
                String value = propertyEle.getAttribute("value");
                // 利用反射获取bean 的相关变量，并将访问权限设置为可访问
                Field field = bean.getClass().getDeclaredField(name);
                field.setAccessible(true);
                
                if(value != null && value.length() > 0){
                    field.set(bean, value);
                }else{
                    String ref = propertyEle.getAttribute("ref");
                    if(ref == null || ref.length() <= 0){
                        throw new IllegalArgumentException("ref config error");
                    }
                    field.set(bean, getBean(ref));
                }
            }
            // 将bean 注册到容器
            registerBean(id, bean);
        }
    }
    
    private void registerBean(String id, Object bean){
        beanMap.put(id, bean);
    }
    
}
