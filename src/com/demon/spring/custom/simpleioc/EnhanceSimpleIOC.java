package com.demon.spring.custom.simpleioc;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.fastjson.JSON;

/**
 * 增强的IOC 实现类，解决xml 配置顺序问题
 * 
 * @author xuliang
 * @since 2018年12月14日 下午4:39:51
 *
 */
public class EnhanceSimpleIOC {

    public static void main(String[] args) throws Exception {
//      String location = SimpleIOC.class.getClassLoader().getResource("ioc.xml").getFile();
      String location = "D:\\work\\git\\essay\\src\\com\\demon\\spring\\custom\\simpleioc\\ioc.xml";
      EnhanceSimpleIOC bf = new EnhanceSimpleIOC(location);
      Wheel wheel = (Wheel) bf.getBean("wheel");
      System.out.println(JSON.toJSONString(wheel));
      Car car = (Car) bf.getBean("car");
      System.out.println(JSON.toJSONString(car));
    }
    
    private Map<String, Object> beanMap = new HashMap<>();
    private Map<String, List<BeanProperty>> propertyMap = new HashMap<>();
    
    public EnhanceSimpleIOC(String location) throws Exception {
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
            
            // 将bean 注册到容器
            registerBean(id, bean);
            
            // 读取bean 的property，并将其缓存起来，后续再赋值
            cacheBeanProperty(id, ele);
        }
        // bean 赋值
        for(Map.Entry<String, Object> entry: beanMap.entrySet()){
            List<BeanProperty> props = propertyMap.get(entry.getKey());
            if(props == null || props.isEmpty()){
                continue;
            }
            initBeanProperty(entry.getValue(), props);
        }
    }
    
    private void cacheBeanProperty(String id, Element ele){
        // 遍历property
        NodeList propertyNodes = ele.getElementsByTagName("property");
        List<BeanProperty> props = new LinkedList<>();
        for(int j=0;j<propertyNodes.getLength();j++){
            Node propertyNode = propertyNodes.item(j);
            if(!(propertyNode instanceof Element)){
                continue;
            }
            Element propertyEle = (Element) propertyNode;
            String name = propertyEle.getAttribute("name");
            String value = propertyEle.getAttribute("value");
            String ref = propertyEle.getAttribute("ref");
            
            BeanProperty prop = new BeanProperty();
            prop.name = name;
            prop.value = value;
            prop.ref = ref;
            props.add(prop);
        }
        propertyMap.put(id, props);
    }
    
    private void initBeanProperty(Object bean, List<BeanProperty> props) throws Exception{
        if(bean == null || props == null || props.isEmpty()){
            return;
        }
        for(BeanProperty prop: props){
            // 利用反射获取bean 的相关变量，并将访问权限设置为可访问
            String name = prop.name;
            String value = prop.value;
            String ref = prop.ref;
            
            Field field = bean.getClass().getDeclaredField(name);
            field.setAccessible(true);
            
            if(value != null && value.length() > 0){
                field.set(bean, value);
            }else{
                if(ref == null || ref.length() <= 0){
                    throw new IllegalArgumentException("ref config error");
                }
                field.set(bean, getBean(ref));
            }
        }
    }
    
    private void registerBean(String id, Object bean){
        beanMap.put(id, bean);
    }
    
    static class BeanProperty {
        public String name;
        public String value;
        public String ref;
    }
    
}
