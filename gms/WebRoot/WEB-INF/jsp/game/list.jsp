<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/common/taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<title>指挥中心</title>
<%@ include file="/common/pubScript.jsp"%>
<style>

</style>
</head>
	<body>
		<a class="btn" href="/game/game.html">添加</a>
	<table>
		<tr>
			<td>游戏名</td>
			<td>logo</td>
			<td>类别</td>
			<td>权重</td>
			<td>操作</td>
		</tr>
		<c:forEach items="${list}" var="item" varStatus="status">
		<tr>
			<td>${item.gameName}</td>
			<td><img src="${item.logo}" alt="" width="300" /></td>
			<td>${item.cid}</td>
			<td>${item.power}</td>
			<td><a href="/game/game.html?gameId=${item.gameId}">编辑</a></td>
		</tr>
		</c:forEach>
	</table>

	<%@ include file="/common/pager.jsp"%>
	</body>	
</html>