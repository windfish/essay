<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>新闻搜索</title>
<link type="text/css" rel="stylesheet" href="news.css" >
</head>
<body>
<div class="box">
    <h1>Elasticsearch 新闻搜索</h1>
    <div class="searchbox">
        <form action="SearchNews" method="get">
            <input type="text" name="query">
            <input type="hidden" name="pageNum" value="1">
            <input type="submit" value="搜索一下">
        </form>
    </div>
</div>
</body>
</html>