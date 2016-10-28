<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/common/taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<title>指挥中心</title>
<%@ include file="/common/pubScript.jsp"%>
<style>
th,td {
	/*borders and padding to make the table readable*/
	border:1px solid #000;
	padding:.5em;
}
label{
	margin:0px -5px 0px 20px
}
table {border-collapse: collapse;border-spacing: 0;}
input[type="text"] {
    width: 95%;
}
</style>
<script>
$(function(){
	$("#cidT input").each(function(){
		var cid = $("#cid").val();
		var v = $(this).val();
		if (1<<v & cid) {
			$(this).attr("checked","checked")
		};
	});
	$("#f").ajaxForm(function(data){
		if(data.code<0){
			alert(data.info);
		}else{
			alert("操作成功");
			location.reload();
		}
	});
	$("#cidT input").change(function(){
		var v = 0;
		$("#cidT input").each(function(){
			if ($(this).attr("checked")) {
				v = setBitIndexToOne($(this).val(),v);
			}
		});
		$("#cid").val(v);

	});

	function setBitIndexToOne(bitIndex,  value) {
		return (1 << bitIndex ) | value;
	}

});
</script>

</head>
	<body>
	<form id="f" action="/game/insertOrUpdate.json" method="post">
	<input type="hidden" id="gameId" name="gameId" value="${game.gameId}" />
	<table>
		<tr>
			<td>游戏名</td>
			<td>
				<input type="text" name="gameName" value="${game.gameName}" />
			</td>
		</tr>
		<tr>
			<td>logo</td>
			<td>
				<input type="text" name="logo" value="${game.logo}" />
			</td>
		</tr>
		<tr>
			<td>图片（多张逗号隔开）</td>
			<td>
				<input type="text" name="imgs" value="${game.imgs}" />
			</td>
		</tr>
		<tr>
			<td>游戏play页</td>
			<td>
				<input type="text" name="playUrl" value="${game.playUrl}" />
			</td>
		</tr>
		<tr>
			<td>权重</td>
			<td>
				<input type="text" name="power" value="${game.power}" />
			</td>
		</tr>
		<tr>
			<td>评分</td>
			<td>
				<input type="text" name="mark" value="${game.mark}" />
			</td>
		</tr>
		<tr>
			<td>类别</td>
			<td id="cidT">
				<input type="hidden" id="cid" name="cid" value="${game.cid}" />
				<label for="ns">女生：</label><input type="checkbox" value="1" id="ns" />
				<label for="gx">搞笑：</label><input type="checkbox" value="2" id="gx" />
				<label for="xx">休闲：</label><input type="checkbox" value="3" id="xx" />
				<label for="dz">动作：</label><input type="checkbox" value="4" id="dz" />
				<label for="yz">益智：</label><input type="checkbox" value="5" id="yz" />
				<label for="sj">射击：</label><input type="checkbox" value="6" id="sj" />
				<label for="zb">装扮：</label><input type="checkbox" value="7" id="zb" />
				<label for="et">儿童：</label><input type="checkbox" value="8" id="et" />
			</td>
		</tr>
		<tr>
			<td>游戏介绍</td>
			<td>
				<textarea name="intro" id="" cols="30" rows="10" style="width: 95%;">${game.intro}</textarea>
			</td>
		</tr>
	</table>	
	<input type="submit" value="更新" style="margin:15px;" />
	</form>
	
	</body>	
</html>