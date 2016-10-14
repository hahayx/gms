<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglib.jsp"%>
<style type="text/css">
.allpag{padding:24px 0;height:22px;clear: both;}
.page{text-align:center;height:22px;}
.page div{display:inline-block;*display:inline;zoom:1;font-family:Tahoma;}
.c_page{height:20px;display:block;background:#fff;color:#000;text-decoration:none;float:left;line-height:20px;text-align:center;border:1px solid #91958c;padding:0 7px;}
.page div span{float:left;height:20px;line-height:20px;background:#63B12E;color:#fff;text-decoration:none;text-align:center;margin:0 2px;border:1px solid #45861e;padding:0 7px;}
.nextpage{float:left;width:50px;height:20px;line-height:20px;background:#f6feec;color:#000;text-align:center;border:1px solid #84a160;}
.page a{margin:0 2px;}
.page a:hover{border:1px solid #F60;color:#F60;}
.page div em{float:left;line-height:20px;margin:0 2px;}
</style>
<script type="text/javascript">
$(function(){
	$(".page a").click(function(){
		var pageNum=parseInt($(this).attr("pageNum"));
		var offset=(pageNum-1)*${limit};
		if(typeof toPageUrl=="function" ){
			toPageUrl(offset);
		}else{
			location.href="?offset="+offset;
		}
	});
});
</script>
<c:if test="${offset!=null }"><c:set var="curPageNum" value="${gms:getCurPage(offset,limit)}"/></c:if>
<c:if test="${showPageNum==null }"><c:set var="showPageNum" value="10"/></c:if>
<c:if test="${totalCount!=0}">
<c:set var="totalPageNum" value="${gms:getPageNum(totalCount,limit)}"/>
<!-- 从当前页面左右显示页面链接数 -->
<c:set var="leftShowPageNum" value="${(showPageNum-showPageNum%2)/2+(showPageNum%2==0?0:1)}"/>
<div class="allpag"><div class="page"><div>
<c:if test="${curPageNum!=1}"><a href="###" pageNum="1" class="nextpage">首页</a><a href="###" pageNum="${curPageNum-1 }" class="nextpage">上一页</a></c:if>
<c:set var="beginIndex" value="${curPageNum-leftShowPageNum>=1?curPageNum-leftShowPageNum:1 }"/>
<c:set var="endIndex" value="${beginIndex+showPageNum-1<=totalPageNum?beginIndex+showPageNum-1:totalPageNum}"/>
<c:set var="beginIndex" value="${endIndex-beginIndex+1<showPageNum?endIndex-showPageNum+1:beginIndex}"/>
<c:set var="beginIndex" value="${beginIndex>=1?beginIndex:1 }"/>

<c:forEach var="pageNumber" begin="${beginIndex}" end="${endIndex}">
	<c:choose>
		<c:when test="${curPageNum==pageNumber}">
			<span class="CurrentPage">${pageNumber}</span>
		</c:when>
		<c:otherwise>
			<a href="###" pageNum="${pageNumber }" class="c_page">${pageNumber}</a>
		</c:otherwise>
	</c:choose>
</c:forEach>

<c:if test="${curPageNum!=totalPageNum}"><a href="###" pageNum="${curPageNum+1 }" class="nextpage">下一页</a><a href="###" pageNum="${totalPageNum }" class="nextpage">尾页</a></c:if>
</div></div></div>
</c:if>

