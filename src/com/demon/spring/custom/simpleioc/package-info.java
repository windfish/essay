/**
 * 简单IOC 的实现步骤：
 * 1. 加载xml 文件，遍历其中的bean 标签
 * 2. 获取bean 标签中的id 和class 属性，加载class 属性对应的类，并创建bean
 * 3. 遍历bean 标签的子标签，获取属性值，并将属性值填充到bean 中
 * 4. 将bean 注册到bean 容器中
 */
package com.demon.spring.custom.simpleioc;