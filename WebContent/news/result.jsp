<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String queryBack = (String) request.getAttribute("queryBack");
Integer currentPage = (Integer) request.getAttribute("currentPage");
ArrayList<Map<String, Object>> newslist = (ArrayList<Map<String, Object>>)request.getAttribute("newslist");
String totalHits = (String) request.getAttribute("totalHits");
String totalTime = (String) request.getAttribute("totalTime");
int pages = Integer.parseInt(totalHits) /10 + 1;
//pages = pages > 10 ? 10 : pages;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>搜索结果</title>
<link type="text/css" rel="stylesheet" href="result.css">
</head>
<body>
<div class="result_search">
    <div class="logo"><h2><a href="news/news.jsp">新闻搜索</a></h2></div>
    <div class="searchbox">
        <form action="SearchNews" method="get">
            <input type="text" name="query" value="<%=queryBack%>">
            <input type="hidden" name="pageNum" value="1">
            <input type="submit" value="搜索一下">
        </form>
    </div>
</div>
<h5 class="result_info">供搜索到<span><%=totalHits %></span>条结果，耗时<span><%=Double.parseDouble(totalTime) / 1000.0 %></span>秒</h5>
<div class="newslist">
    <%
    if(newslist.size() > 0){
        Iterator<Map<String, Object>> iter = newslist.iterator();
        while(iter.hasNext()){
            Map<String, Object> news = iter.next();
            String content = news.get("content").toString();
            content = content.length() > 200 ? content.substring(0, 200) : content;
    %>
    <div class="news">
        <h4><a href="<%=news.get("url")%>" target="_blank"><%=news.get("title") %></a></h4>
        <p><%=content %></p>
    </div>
    <%
        }
    }
    %>
</div>
<div class="page">
    <ul>
        <%
        int begin = 1;
        int end = 10;
        int current = currentPage.intValue();
        if(pages > 10){
            if(current == pages){
                end = pages;
                begin = pages - 10 + 1;
            }else if(current == 1){
                begin = 1;
                end = 10;
            }else if(current - 5 < 1){
                begin = 1;
                end = 10;
            }else if(current + 5 > pages){
                end = pages;
                begin = end - 10 + 1;
            }else{
                begin = current - 5;
                end = current + 5;
            }
        }else{
            begin = 1;
            end = pages;
        }
        %>
        <li><a href="SearchNews?query=<%=queryBack%>&pageNum=<%=1%>">[&lt;&lt;]</a></li>
        <%if(current != 1){ %>
        <li><a href="SearchNews?query=<%=queryBack%>&pageNum=<%=currentPage.intValue()-1%>">[&lt;]</a></li>
        <%} %>
        <%for(int i=begin;i<=end;i++){ %>
        <li>
	        <a href="SearchNews?query=<%=queryBack%>&pageNum=<%=i%>">
	        <%if(i == current){ %>
	           [<%=i %>]
	        <%}else{ %>
	           <%=i %>
	        <%} %>
	        </a>
        </li>
        <%} %>
        <%if(current != pages){ %>
        <li><a href="SearchNews?query=<%=queryBack%>&pageNum=<%=currentPage.intValue()+1%>">[&gt;]</a></li>
        <%} %>
        <li><a href="SearchNews?query=<%=queryBack%>&pageNum=<%=pages%>">[&gt;&gt;]</a></li>
    </ul>
</div>
<div class="info">
    <p>新闻搜索项目实战 Powered by <b>Elasticsearch</b></p>
</div>
</body>
</html>