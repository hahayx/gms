package com.hh.common.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class XmlUtil {
	public static interface CallBack<T,K> {
		T execute(K obj);
	}
	private final static CallBack<Boolean,Node> DefaultFilter=new CallBack<Boolean,Node>(){
		public Boolean execute(Node node) {
			return true;
		}
	};
	private final static class TagNameFilter implements CallBack<Boolean,Node>{
		private String tagName;
		private TagNameFilter(String tagName) {
			this.tagName = tagName;
		}
		public Boolean execute(Node node) {
			return node.getNodeName().equals(tagName);
		}
	}
	public static Document getDoc(String file) throws Exception{
		if(!new File(file).exists()){return null;}
		return getDoc(new FileInputStream(file));
	}
	public static Document getDocByXmlStr(String hostNodeStr) throws Exception {
		return getDoc(new ByteArrayInputStream(hostNodeStr.getBytes()));
	}
	public static Document getDoc(InputStream is) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setNamespaceAware(false);
		dbf.setIgnoringElementContentWhitespace(true);
		dbf.setIgnoringComments(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(is);
		document.getDocumentElement().normalize();
		return document;
	}
	public static Node getRootNode(Document document){
		NodeList nl = document.getChildNodes();
		return nl==null?null:nl.item(0);
	}
	public static void setAttr(Node n, String attr, String value) {
		Node attrNode = getAttrNode(n, attr);
		if(attrNode!=null){attrNode.setNodeValue(value);}
	}
	public static String getAttr(Node node,String attName){
		return getAttr(node,attName,null);
	}
	public static String getAttr(Node node,String attName,String defaultValue){
		Node tmNode = getAttrNode(node, attName);
		return tmNode==null?defaultValue:tmNode.getNodeValue() ;
	}
	private static Node getAttrNode(Node node, String attName) {
		Node tmNode = node==null?null:node.getAttributes().getNamedItem(attName);
		return tmNode;
	}
	public static String getFirstChildNodeAttr(Node node,String tagname,String attName){
		return getFirstChildNodeAttr(node, tagname, attName,null);
	}
	public static String getFirstChildNodeAttr(Node node,String tagname,String attName,String defaultValue){
		Node childNode = getFirstChildNode(node, tagname);
		if(childNode == null){return defaultValue;}
		String value = getAttr(childNode, attName);
		return value == null?defaultValue:value;
	}
	public static List<Node> getChildNodes(Node node){
		return getChildNodes(node,DefaultFilter);
	}
	public static List<Node> getChildNodes(Node pNode,final String tagName){
		if(tagName == null || tagName.length()==0){
			return Collections.emptyList();
		}
		return getChildNodes(pNode,new TagNameFilter(tagName));
	}
	public static List<Node> getChildNodes(Document doc,String pTagName){
		Node pNode = getFirstByTagName(doc, pTagName,DefaultFilter);
		return getChildNodes(pNode);
	}
	public static List<Node> getChildNodes(Document doc,String pTagName,CallBack<Boolean,Node>  filter){
		Node pNode = getFirstByTagName(doc, pTagName,filter);
		return getChildNodes(pNode);
	}
	public static Node getFirstChildNode(Node pNode,String tagName){
		List<Node> res = getChildNodes(pNode,new TagNameFilter(tagName),true);
		return res.size()==0?null:res.get(0);
	}
	public static List<Node> getChildNodes(Node pNode,CallBack<Boolean,Node>  filter){
		return getChildNodes(pNode, filter, false);
	}
	public static List<Node> getChildNodes(Node pNode,CallBack<Boolean,Node>  filter,boolean returnOne){
		if(pNode == null || !pNode.hasChildNodes()){
			return Collections.emptyList();
		}
		return getNodeList(pNode.getChildNodes(),filter,returnOne);
	}
	public static Node getFirstByTagName(Document doc,String tagName,CallBack<Boolean,Node>  filter) {
		List<Node> res =getByTagName(doc, tagName, filter, true);
		return res.size()==0?null:res.get(0);
	}
	public static List<Node> getByTagName(Document doc,String tagName) {
		return getNodeList(doc.getElementsByTagName(tagName),DefaultFilter,false);
	}
	public static List<Node> getByTagName(Document doc,String tagName,CallBack<Boolean,Node>  filter) {
		return getNodeList(doc.getElementsByTagName(tagName),filter,false);
	}
	public static List<Node> getByTagName(Document doc,String tagName,CallBack<Boolean,Node>  filter,boolean returnOne) {
		return getNodeList(doc.getElementsByTagName(tagName),filter,returnOne);
	}
	private static List<Node> getNodeList(NodeList nodeList,CallBack<Boolean, Node> filter, boolean returnOne) {
		List<Node> resList = new ArrayList<Node>();
		for (int i = 0,len=nodeList.getLength(); i < len; i++) {
			Node cnode = nodeList.item(i);
			if(isElementNode(cnode)&&filter.execute(cnode)){
				resList.add(cnode);
				if(returnOne){break;}
			}
		}
		return resList;
	}
	public static Node getNextSibling(Node node) {
		if(node==null){return null;}
		Node n = node.getNextSibling();
		if(n==null){return null;}
		while(!isElementNode(n)){
			n = n.getNextSibling();
			if(n==null){return null;}
		}
		return n;
	}
	private static boolean isElementNode(Node n) {
		return n.getNodeType()==Node.ELEMENT_NODE;
	}
}
