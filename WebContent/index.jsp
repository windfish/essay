<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商品交易管理系统</title>
<script type="text/javascript" src="//code.jquery.com/jquery-1.11.3.min.js"></script>
<script type="text/javascript">

	var index = 1; //标识
	
	//新增商品
	function addStock(){
		$.ajax({
			url: 'type/queryTypes',
			type: 'POST'
		}).done(function (data) {
		    var typeObj = document.getElementById('type');
		    for(var i=0;i<data.length;i++){
		    	var opt = document.createElement('option');
		    	opt.value = data[i].typeId;
		    	opt.text = data[i].name;
		    	typeObj.appendChild(opt);
		    }
		}).fail(function (xhr, status) {
			alert('失败: ' + xhr.status + ', 原因: ' + status);
		});
		
		$.ajax({
			url: 'shop/queryShops',
			type: 'POST'
		}).done(function(data){
			var shopObj = document.getElementById('shop');
			for(var i=0;i<data.length;i++){
				var opt = document.createElement('option');
				opt.value = data[i].shopId;
				opt.text = data[i].name;
				shopObj.appendChild(opt);
			}
		}).fail(function (xhr, status) {
			alert('失败: ' + xhr.status + ', 原因: ' + status);
		});
		
		addSizeArea();
		$('#addStockArea').attr('style','display:inline;');
		
		closeAddShopArea();
		closeAddTypeArea();
	}
	
	//增加颜色尺码输入域
	function addSizeArea(){
		var firstTR = $('#firstTR');
		var tr = document.createElement('tr');
		tr.id = "id"+index;
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
	
	//保存商品
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
		    data: {
		    	stocks: JSON.stringify(datas)
		    }
		}).done(function (data) {
		    alert('成功'+data);
		    closeAddStockArea();
		}).fail(function (xhr, status) {
			alert('失败: ' + xhr.status + ', 原因: ' + status);
		});
	}
	
	//关闭新增商品区域
	function closeAddStockArea(){
		for(var i=1;i<index;i++){
			$('#id'+i).remove();
		}
		document.getElementById('name').value = "";
		document.getElementById('type').innerHTML = '<option value="">请选择</option>';
		document.getElementById('purchase_price').value = "";
		document.getElementById('hope_price').value = "";
		document.getElementById('discount_price').value = "";
		document.getElementById('shop').innerHTML = '<option value="">请选择</option>';
		index = 1;
		$('#addStockArea').attr("style","display:none;");
	}
	
	//自动计算折扣后价格
	function showDiscount(){
		var hope_price = $('#hope_price').val();
		if(hope_price != null){
			document.getElementById('discount_price').value = accMul(hope_price,0.9);
		}
	}
	
	/**
	 ** 乘法函数，用来得到精确的乘法结果
	 ** 说明：javascript的乘法结果会有误差，在两个浮点数相乘的时候会比较明显。这个函数返回较为精确的乘法结果。
	 ** 调用：accMul(arg1,arg2)
	 ** 返回值：arg1乘以 arg2的精确结果
	 **/
	function accMul(arg1, arg2) {
	    var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
	    try {
	        m += s1.split(".")[1].length;
	    }
	    catch (e) {
	    }
	    try {
	        m += s2.split(".")[1].length;
	    }
	    catch (e) {
	    }
	    return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
	}
	
	//新增类别
	function addType(){
		document.getElementById('new_type').value = "";
		
		$('#addTypeArea').attr('style','display:inline;');
		
		closeAddStockArea();
		closeAddShopArea();
	}
	
	//关闭新增类别区域
	function closeAddTypeArea(){
		$('#addTypeArea').attr('style','display:none;');
	}
	
	//保存类别
	function saveTpye(){
		$.ajax({
			url: 'type/addType',
			type: 'POST',
		    data: {
		    	name: $('#new_type').val()
		    }
		}).done(function (data) {
		    if(data === 'ok'){
		    	alert("保存成功");
		    	//$('#new_type').attr('value','');
		    	document.getElementById('new_type').value = "";
		    }else{
		    	alert("保存失败");
		    }
		}).fail(function (xhr, status) {
			alert('失败: ' + xhr.status + ', 原因: ' + status);
		});
	}
	
	function addShop(){
		document.getElementById('new_shop_name').value = "";
    	document.getElementById('new_shop_addr').value = "";
    	document.getElementById('new_shop_phone').value = "";
    	document.getElementById('new_shop_wechat').value = "";
    	
		$('#addShopArea').attr('style','display:inline;');
		
		closeAddStockArea();
		closeAddTypeArea();
	}
	function closeAddShopArea(){
		$('#addShopArea').attr('style','display:none;');
	}
	function saveShop(){
		$.ajax({
			url: 'shop/addShop',
			type: 'POST',
		    data: {
		    	name: $('#new_shop_name').val(),
		    	addr: $('#new_shop_addr').val(),
		    	phone: $('#new_shop_phone').val(),
		    	wechat: $('#new_shop_wechat').val()
		    }
		}).done(function (data) {
	    	alert("保存成功");
	    	document.getElementById('new_shop_name').value = "";
	    	document.getElementById('new_shop_addr').value = "";
	    	document.getElementById('new_shop_phone').value = "";
	    	document.getElementById('new_shop_wechat').value = "";
		}).fail(function (xhr, status) {
			alert('失败: ' + xhr.status + ', 原因: ' + status);
		});
	}
	
	function queryStock(){
		$.ajax({
			url: 'stock/queryStocks',
			type: 'POST'
		}).done(function (data) {
			var table = document.getElementById('stockList');
	    	for(var i=0;i<data.length;i++){
	    		var tr = document.createElement('tr');
	    		var name = document.createElement('td');
	    		name.innerHTML = data[i].name;
	    		tr.appendChild(name);
	    		
	    		var color = document.createElement('td');
	    		color.innerHTML = data[i].color;
	    		tr.appendChild(color);
	    		
	    		var size = document.createElement('td');
	    		size.innerHTML = data[i].size;
	    		tr.appendChild(size);
	    		
	    		var purchase_price = document.createElement('td');
	    		purchase_price.innerHTML = data[i].purchasePrice;
	    		tr.appendChild(purchase_price);
	    		
	    		var hope_price = document.createElement('td');
	    		hope_price.innerHTML = data[i].hopePrice;
	    		tr.appendChild(hope_price);
	    		
	    		var discount_price = document.createElement('td');
	    		discount_price.innerHTML = data[i].discountPrice;
	    		tr.appendChild(discount_price);
	    		
	    		var number = document.createElement('td');
	    		number.innerHTML = data[i].number;
	    		tr.appendChild(number);
	    		
	    		var type_name = document.createElement('td');
	    		type_name.innerHTML = data[i].type_name;
	    		tr.appendChild(type_name);
	    		
	    		var shop_name = document.createElement('td');
	    		shop_name.innerHTML = data[i].shop_name;
	    		tr.appendChild(shop_name);
	    		
	    		var func = document.createElement('td');
	    		func.innerHTML = '<input type="button" value="出售" id="sell"'+i+'>';
	    		document.getElementById('sell'+i).onClick = function (){
	    			sellStock(data[i]);
	    		};
	    		tr.appendChild(func);
	    		
	    		table.appendChild(tr);
	    	}
		}).fail(function (xhr, status) {
			alert('失败: ' + xhr.status + ', 原因: ' + status);
		});
		
		$('#stockListArea').attr('style','display:inline;');
	}
	
	function sellStock(stock){
		alert(stock);
	}
	
</script>
</head>
<body>
  <div id="queryArea">
    <fieldset>
    	<legend>商品查询</legend>
    	<form action="" method="post">
    	<table>
    		<tr>
    			<td>商品名称:<input type="text" id="query_name" name="name"></td>
    			<td>交易时间:<input type="date" id="query_time" name="time"></td>
    		</tr>
    	</table>
    	<div>
    		<input type="button" value="新增商品" onclick="addStock();">
    		<input type="button" value="新增商品类别" onclick="addType();">
    		<input type="button" value="新增商铺" onclick="addShop();">
    		<input type="button" value="查询交易信息" onclick="queryDeal();">
    		<input type="button" value="查询商品信息" onclick="queryStock();">
    	</div>
    	</form>
    </fieldset>
  </div>
  <div id="stockListArea" style="display: none;">
  	<br>
    <fieldset>
    <table id="stockList" border="1" style="cellspacing:0;cellpadding:5;">
    	<caption>商品库存信息</caption>
    	<tr align="center">
    		<td>商品名称</td>
    		<td>颜色</td>
    		<td>尺码</td>
    		<td>进价</td>
    		<td>估计售价</td>
    		<td>折扣售价</td>
    		<td>库存量</td>
    		<td>商品类别</td>
    		<td>店铺</td>
    		<td>操作</td>
    	</tr>
    </table>
    </fieldset>
  </div>
  <div id="dealListArea" style="display: none;">
    <table id="dealList" border="1" style="cellspacing:0;cellpadding:5;">
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
    </table>
  </div>
  <div id="addStockArea" style="display: none;">
  	<fieldset>
  		<legend>商品新增</legend>
    	<table>
    		<tr id="firstTR">
    			<td>商品名称:<input type="text" id="name" name="name"></td>
    			<td>商品类别:<select name="type" id="type" style="width: 70%;"><option value="">请选择</option></select></td>
    			<td><input type="button" id="addStockProp" onclick="addSizeArea();" value="增加颜色尺码输入域"></td>
    		</tr>
    		<!-- 颜色、尺码、数量  动态添加 -->
    		<tr>
    			<td>商品进价:<input type="number" id="purchase_price" name="purchase_price"></td>
    			<td>估计售价:<input type="number" id="hope_price" name="hope_price" onblur="showDiscount();"></td>
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
  	</fieldset>
  </div>
  <div id="addTypeArea" style="display: none;">
  	<fieldset>
  		<legend>类别新增</legend>
    	<table>
    		<tr>
    			<td>商品类别:<input type="text" name="new_type" id="new_type" value=""></td>
    		</tr>
    	</table>
    	<div>
    		<input type="button" onclick="saveTpye();" value="确定">
    		<input type="button" onclick="closeAddTypeArea();" value="取消">
    	</div>
  	</fieldset>
  </div>
  <div id="addShopArea" style="display: none;">
  	<fieldset>
  		<legend>商铺新增</legend>
    	<table>
    		<tr>
    			<td>商铺名称:<input type="text" id="new_shop_name" value=""></td>
    			<td>商铺地址:<input type="text" id="new_shop_addr" value=""></td>
    			<td>商铺电话:<input type="text" id="new_shop_phone" value=""></td>
    			<td>商铺微信:<input type="text" id="new_shop_wechat" value=""></td>
    		</tr>
    	</table>
    	<div>
    		<input type="button" onclick="saveShop();" value="确定">
    		<input type="button" onclick="closeAddShopArea();" value="取消">
    	</div>
  	</fieldset>
  </div>
</body>
</html>