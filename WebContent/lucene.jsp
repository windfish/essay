<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.Calendar" %>
<%
	Calendar c = Calendar.getInstance();
	int year = c.get(Calendar.YEAR);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Lucene 文件检索</title>
<link type="text/css" rel="stylesheet" href="css/lucene.css">
</head>
<body>
<div class="indexbox">
    <div class="logo">
        <a href="lucene.jsp"><img alt="文件检索" src="images/logo.png"></a>
    </div>
    <div class="searchform">
        <form action="searchFile" method="get">
            <input type="text" name="query">
            <input type="submit" value="搜索">
        </form>
    </div>
    <div class="info">
        <p>基于Lucene 的文件检索系统</p>
        <p>&copy; <%=year > 2019 ? (2019 + "-" + year) : year %> essay 读书笔记 </p> 
    </div>
</div>
</body>
</html>