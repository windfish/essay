<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.ArrayList"
    import="com.demon.lucene.book.chapter3.model.FileModel"
    import="java.util.regex.*"
    import="com.demon.lucene.book.chapter3.service.RegexHtml"
    import="java.util.Iterator" %>
<%
String path = request.getContextPath(); // 工厂根目录
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
ArrayList<FileModel> list = (ArrayList<FileModel>)request.getAttribute("fileList");
RegexHtml regexHtml = new RegexHtml();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="this is the result page">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<base href="<%=basePath %>">
<title>搜索结果</title>
</head>
<body>
<div class="searchform">
    <div class="logo">
        <a href="lucene.jsp"><img alt="文件检索" src="images/logo.png"></a>
    </div>
    <div class="searchform">
        <form action="searchFile" method="get">
            <input type="text" name="query">
            <input type="submit" value="搜索">
        </form>
    </div>
</div>
<div class="result">
    <h4>共搜到<span style="color:red;font-weight:bold;"><%=list.size() %></span>条结果</h4>
    <%
    if(list.size() > 0){
        Iterator<FileModel> iter = list.iterator();
        FileModel fm = null;
        while(iter.hasNext()){
            fm = iter.next();
            System.out.println(fm.getTitle());
    %>
    <div class="item">
        <div class="itemTop">
            <h4>
                <%=fm.getTitle() %>
            </h4>
            <h3>
                <a href="download?filename=<%=regexHtml.delHtmlTag(fm.getTitle()) %>">点击下载</a>
            </h3>
        </div>
        <div class="itemContent">
            <p>
                <%=fm.getContent().length() > 210 ? fm.getContent().substring(0, 210) : fm.getContent() %>
            </p>
        </div>
        ----------------------------------------<br>
    </div>
    <%
        }
    }
    %>
</div>
<div class="info">
    <p>基于Lucene 的文件检索系统</p>
    <p>&copy; 2019 读书笔记 </p> 
</div>
</body>
</html>