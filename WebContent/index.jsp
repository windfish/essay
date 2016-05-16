<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商品交易管理系统</title>
<script type="text/javascript" src="//code.jquery.com/jquery-1.11.3.min.js"></script>
<script type="text/javascript">

	var index = 1; //标识
	
	//增加颜色尺码输入域
	function addSizeArea(){
		var firstTR = $('#firstTR');
		var tr = document.createElement('tr');
		var td1 = document.createElement('td');
		td1.innerHTML='颜色:<input type="text" id="color'+index+'" name="color'+index+'">';
		var td2 = document.createElement('td');
		td2.innerHTML='尺码:<input type="text" id="size'+index+'" name="size'+index+'">';
		var td3 = document.createElement('td');
		td3.innerHTML='数量:<input type="text" id="number'+index+'" name="number'+index+'">';
		tr.appendChild(td1);
		tr.appendChild(td2);
		tr.appendChild(td3);
		firstTR.after(tr);
		index++;
	}
	
	//新增商品
	function saveStock(){
		var name = $('#name').val();
		var type = $('#type').val();
		var purchase_price = $('#purchase_price').val();
		var hope_price = $('#hope_price').val();
		var discount_price = $('#discount_price').val();
		var shop = $('#shop').val();
		
		var datas = new Array();
		for(var i=1;i<index;i++){
			datas.push({
					name: name,
					type: type,
					color: $('#color'+i).val(),
					size: $('#size'+i).val(),
					number: $('#number'+i).val(),
					purchasePrice: purchase_price,
					hopePrice: hope_price,
					discountPrice: discount_price,
					shopId: shop
			});
		}
		
		$.ajax('stock/addStock', {
			type: 'POST',
			contentType : 'application/x-www-form-urlencoded',
		    data: {
		    	stocks: JSON.stringify(datas)
		    }
		}).done(function (data) {
		    alert('成功'+data);
		}).fail(function (xhr, status) {
			alert('失败: ' + xhr.status + ', 原因: ' + status);
		});
	}
	
	//关闭新增商品区域
	function closeAddStockArea(){
		index = 1;
		$('#addStockArea').attr("style","display:none;");
	}
	
</script>
</head>
<body>
  <div id="queryArea">
    <form id="queryForm" action="" method="post" >
    	
    </form>
  </div>
  <div id="listArea" style="display: none;">
    <table id="list" border="1" style="cellspacing:0;cellpadding:5;">
    	<caption>商品交易信息</caption>
    	<tr align="center">
    		<td>商品名称</td>
    		<td>颜色</td>
    		<td>尺码</td>
    		<td>进价</td>
    		<td>售价</td>
    		<td>利润</td>
    		<td>利润率</td>
    		<td>快递费</td>
    		<td>快递单号</td>
    		<td>交易时间</td>
    		<td>交易状态</td>
    	</tr>
    	<tr>
    		<td>商品名称</td>
    		<td>颜色</td>
    		<td>尺码</td>
    		<td>进价</td>
    		<td>售价</td>
    		<td>利润</td>
    		<td>利润率</td>
    		<td>快递费</td>
    		<td>快递单号</td>
    		<td>交易时间</td>
    		<td>交易状态</td>
    	</tr>
    </table>
  </div>
  <div id="addStockArea">
  	<fieldset>
  		<legend>商品新增</legend>
    <!-- <form id="addStockForm" action="/stock/addStock" method="post"> -->
    	<table>
    		<tr id="firstTR">
    			<td>商品名称:<input type="text" id="name" name="name"></td>
    			<td>商品类别:<select name="type" id="type" style="width: 70%;"><option value="">请选择</option></select></td>
    			<td><input type="button" id="addStockProp" onclick="addSizeArea();" value="增加颜色尺码输入域"></td>
    		</tr>
    		<!-- 颜色、尺码、数量  动态添加 -->
    		<tr>
    			<td>商品进价:<input type="number" id="purchase_price" name="purchase_price"></td>
    			<td>估计售价:<input type="number" id="hope_price" name="hope_price" onblur=""></td>
    			<td>折扣售价:<input type="number" id="discount_price" name="discount_price">(默认9折)</td>
    		</tr>
    		<tr>
    			<td>店铺信息:<select name="shop" id="shop" style="width: 70%;"><option value="">请选择</option></select></td>
    		</tr>
    	</table>
    	<div>
    		<input type="button" onclick="saveStock();" value="确定">
    		<input type="button" onclick="closeAddStockArea();" value="取消">
    	</div>
    <!-- </form> -->
  	</fieldset>
  </div>
</body>
</html>