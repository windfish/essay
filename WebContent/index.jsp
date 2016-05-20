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
	
	$(document).ready(function(){
		//初始化下拉列表数据
		initSelect();
		
	});
	
	function initSelect(){
		$.ajax({
			url: 'type/queryTypes',
			type: 'POST'
		}).done(function (data) {
		    var typeObj = document.getElementById('type');
		    typeObj.innerHTML = '<option value="">请选择</option>'
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
			shopObj.innerHTML = '<option value="">请选择</option>'
			for(var i=0;i<data.length;i++){
				var opt = document.createElement('option');
				opt.value = data[i].shopId;
				opt.text = data[i].name;
				shopObj.appendChild(opt);
			}
		}).fail(function (xhr, status) {
			alert('失败: ' + xhr.status + ', 原因: ' + status);
		});
		
		//TODO  买方信息初始化到下拉列表
		
	}
	
	//新增商品
	function addStock(){
		$('#type').val('');
		$('#shop').val('');
		
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
		//document.getElementById('type').innerHTML = '<option value="">请选择</option>';
		document.getElementById('purchase_price').value = "";
		document.getElementById('hope_price').value = "";
		document.getElementById('discount_price').value = "";
		//document.getElementById('shop').innerHTML = '<option value="">请选择</option>';
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
		    	
		    	initSelect();
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
	    	
	    	initSelect();
		}).fail(function (xhr, status) {
			alert('失败: ' + xhr.status + ', 原因: ' + status);
		});
	}
	//查询商品库存信息
	function queryStock(){
		$.ajax({
			url: 'stock/queryStocks',
			type: 'POST'
		}).done(function (data) {
			var table_tbody = document.getElementById('stockList_tbody');
			table_tbody.innerHTML = "";
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
	    		
	    		var sellFunc = document.createElement('td');
	    		sellFunc.innerHTML = '<a href="#" onclick="sellStock(\''+data[i].stockId+'\',\''+data[i].name+'\',\''+data[i].purchasePrice+'\',\''+data[i].number+'\')">出售</a>'
	    						   + '<a href="#" onclick="stockAdd(\''+data[i].stockId+'\',\''+data[i].name+'\',\''+data[i].purchasePrice+'\')">补货</a>';
	    		tr.appendChild(sellFunc);
	    		
	    		table_tbody.appendChild(tr);
	    	}
	    	
	    	closeAddStockArea();
	    	closeAddTypeArea();
	    	closeAddShopArea();
	    	closeSellStockArea();
	    	closeBHArea();
		}).fail(function (xhr, status) {
			alert('失败: ' + xhr.status + ', 原因: ' + status);
		});
		
		$('#stockListArea').attr('style','display:inline;');
	}
	//商品出售
	function sellStock(stockId, name, purchasePrice, number){
		if(number === 0){
			alert('该商品库存不足，不能出售');
			return;
		}
		$('#buyer_stock_id').val(stockId);
		$('#buyer_stock_name').val(name);
		$('#buyer_stock_purchase_price').val(purchasePrice);
		
		$('#buyer').val('');
		$('#status').val('');
		
		$('#price').val('');
		$('#express_no').val('');
		$('#express_charge').val('');
		$('#buyer_name').val('');
		$('#buyer_addr').val('');
		$('#buyer_phone').val('');
		
		$('#sellStockArea').attr('style','display:inline;');
		
		closeBHArea();
	}
	function closeSellStockArea(){
		$('#sellStockArea').attr('style','display:none;');
	}
	//保存交易信息
	function saveSell(){
		var data = {
				stockId: $('#buyer_stock_id').val(),
				price: $('#price').val(),
				expressNo: $('#express_no').val(),
				expressCharge: $('#express_charge').val(),
				status: $('#status').val(),
				buyerId: $('#buyer').val(),
				name: $('#buyer_name').val(),
				addr: $('#buyer_addr').val(),
				phone: $('#buyer_phone').val()
		};
		$.ajax({
			url: 'deal/addDeal',
			type: 'POST',
			data: {
				data: JSON.stringify(data)
			}
		}).done(function (data) {
			alert('出售成功');
			
			queryStock(); //重新查询
			queryDeal();
		}).fail(function (xhr, status) {
			alert('失败: ' + xhr.status + ', 原因: ' + status);
		});
	}
	
	//商品补货
	function stockAdd(stockId, name, purchasePrice){
		$('#bh_stock_name').val(name);
		$('#bh_price').val(purchasePrice);
		$('#bh_stock_id').val(stockId);
		$('#bh_number').val('');
		
		$('#stockBHArea').attr('style','display:inline');
		
		closeSellStockArea();
	}
	function bh(){
		$.ajax({
			url: 'stock/bhStock',
			type: 'POST',
			data: {
				stockId: $('#bh_stock_id').val(),
				number: $('#bh_number').val()
			}
		}).done(function (data) {
			alert('补货成功');
			
			queryStock(); //重新查询
		}).fail(function (xhr, status) {
			alert('失败: ' + xhr.status + ', 原因: ' + status);
		});
	}
	function closeBHArea(){
		$('#stockBHArea').attr('style','display:none;');
	}
	
	//查询商品交易信息
	function queryDeal(){
		$.ajax({
			url: 'stock/queryStockDeals',
			type: 'POST',
			date: {
				name: $('#query_name').val(),
				time: $('#query_time').val()
			}
		}).done(function (data) {
			var dealList_tbody = $('#dealList_tbody');
			dealList_tbody.html('');
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
	    		
	    		var price = document.createElement('td');
	    		price.innerHTML = data[i].price;
	    		tr.appendChild(price);
	    		
	    		var profit = document.createElement('td');
	    		profit.innerHTML = data[i].profit;
	    		tr.appendChild(profit);
	    		
	    		var express_charge = document.createElement('td');
	    		express_charge.innerHTML = data[i].expressCharge;
	    		tr.appendChild(express_charge);
	    		
	    		var express_no = document.createElement('td');
	    		express_no.innerHTML = data[i].expressNo;
	    		tr.appendChild(express_no);
	    		
	    		var time = document.createElement('td');
	    		time.innerHTML = (new Date(data[i].time)).toLocaleDateString();
	    		tr.appendChild(time);
	    		
	    		var status = document.createElement('td');
	    		status.innerHTML = data[i].status;
	    		tr.appendChild(status);
	    		
	    		dealList_tbody.append(tr);
			}
			
			$('#dealListArea').attr('style','display:inline');
			
			closeAddStockArea();
	    	closeAddTypeArea();
	    	closeAddShopArea();
	    	closeSellStockArea();
	    	closeBHArea();
			
		}).fail(function (xhr, status) {
			alert('失败: ' + xhr.status + ', 原因: ' + status);
		});
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
    	<thead>
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
    	</thead>
    	<tbody id="stockList_tbody"></tbody>
    </table>
    </fieldset>
  </div>
  <div id="sellStockArea" style="display: none;">
    <fieldset>
      <legend>出售商品</legend>
      <table>
      	<tr>
      		<td>商品名称:<input id="buyer_stock_name"></td>
      		<td>商品进价:<input id="buyer_stock_purchase_price"></td>
      		<td><input type="hidden" id="buyer_stock_id"></td>
      	</tr>
      	<tr>
      		<td>售价:<input id="price"></td>
      		<td>快递单号:<input id="express_no"></td>
      		<td>快递费:<input id="express_charge"></td>
      	</tr>
      	<tr>
      		<td>买家:<select id="buyer" style="width: 70%;"><option value="">请选择</option></select></td>
      		<td>订单状态:<select id="status" style="width: 70%;">
      			<option value="已预定" selected="selected">已预定</option>
      			<option value="已付款">已付款</option>
      			<option value="已发货">已发货</option>
      			<option value="已完成">已完成</option>
      			<option value="信用代付">信用代付</option>
      		</select></td>
      		<td></td>
      	</tr>
      	<tr id="buyerTr">
      		<td>买家姓名:<input id="buyer_name"></td>
      		<td>买家地址:<input id="buyer_addr"></td>
      		<td>买家电话:<input id="buyer_phone"></td>
      	</tr>
      </table>
      <div>
      	<input type="button" onclick="saveSell();" value="确定">
    	<input type="button" onclick="closeSellStockArea();" value="取消">
      </div>
    </fieldset>
  </div>
  <div id="stockBHArea" style="display:none;">
  	<fieldset>
  	  <legend>补货</legend>
      <table>
      	<tr>
      		<td>商品名称:<input id="bh_stock_name"></td>
      		<td>商品进价:<input id="bh_price"></td>
      		<td>补货数量:<input id="bh_number"><input type="hidden" id="bh_stock_id"></td>
      	</tr>
      </table>
      <div>
      	<input type="button" onclick="bh();" value="确定">
    	<input type="button" onclick="closeBHArea();" value="取消">
      </div>
  	</fieldset>
  </div>
  <div id="dealListArea" style="display: none;">
  	<br>
  	<fieldset>
    <table id="dealList" border="1" style="cellspacing:0;cellpadding:5;">
    	<caption>商品交易信息</caption>
    	<thead>
    	<tr align="center">
    		<td>商品名称</td>
    		<td>颜色</td>
    		<td>尺码</td>
    		<td>进价</td>
    		<td>售价</td>
    		<td>利润</td>
    		<td>快递费</td>
    		<td>快递单号</td>
    		<td>交易时间</td>
    		<td>交易状态</td>
    	</tr>
    	</thead>
    	<tbody id="dealList_tbody"></tbody>
    </table>
    </fieldset>
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