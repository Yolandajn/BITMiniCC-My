package me.entalent.minicc.util.xml;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
/**
 * ��ʾһ��XML��ǩ�����е��ı��������Լ������ӱ�ǩ
 * ��װ�˽�XML��ǩת��Ϊ�ַ������Լ�ֱ�Ӵ�ӡ���ļ��ķ���
 * @author Wentian Zhao
 *
 */
public final class XmlNode {
	//��ǩ����
	String tagName;
	//��ǩ����������
	HashMap<String, String> attributes;
	//�ı�����
	String textContent;
	//�ӱ�ǩ
	ArrayList<XmlNode> children;
	
	//���ʱ�������ո���
	private static int indentNum = 4;
	
	/**
	 * ���ݱ�ǩ������
	 * @param tagName
	 */
	public XmlNode(String tagName) {
		this.tagName = tagName;
	}
	
	public static void setIndentNum(int indent) {
		indentNum = indent;
	}
	
	private static XmlNode fromNode(Node node) {
		XmlNode xmlNode = new XmlNode(node.getNodeName());
		xmlNode.textContent = node.getTextContent();
		NamedNodeMap map = node.getAttributes();
		for(int i = 0; i < map.getLength(); i++){
			xmlNode.attribute(map.item(i).getNodeName(), map.item(i).getNodeValue());
		}
		NodeList nodeList = node.getChildNodes();
		for(int i = 0; i < nodeList.getLength(); i++){
			xmlNode.addChild(XmlNode.fromNode(nodeList.item(i)));
		}
		return xmlNode;
	}
	
	/**
	 * ���ÿ���ǩ�ͱձ�ǩ֮����ı�
	 * @param textContent Ҫ���õ��ı�
	 * @return ��������
	 * eg. new XmlNode("tag").textContent("text");
	 */
	public XmlNode textContent(String textContent) {
		this.textContent = textContent;
		return this;
	}
	
	public String getTagName() {
		return this.tagName;
	}
	
	public String getTextContent() {
		return this.textContent;
	}
	
	public String getAttribute(String key) {
		return this.attributes.get(key);
	}
	
	public HashMap<String, String> getAllAttributes() {
		return this.attributes;
	}
	
	/**
	 * ������ǩ����һ������
	 * @param key ������
	 * @param value ����ֵ
	 * @return ��������
	 * eg. new XmlNode("tag").attribute("key", "value");
	 */
	public XmlNode attribute(String key, String value) {
		if(this.attributes == null) 
			this.attributes = new HashMap<>();
		this.attributes.put(key, value);
		return this;
	}
	
	/**
	 * ���һ���ӱ�ǩ
	 * @param node Ҫ��ӵı�ǩ
	 * @return �����Ƿ���ӳɹ�
	 */
	public synchronized boolean addChild(XmlNode node) {
		if(children == null) 
			this.children = new ArrayList<>();
		return this.children.add(node);
	}
	
	/**
	 * �ӱ�ǩ��
	 * @return �ӱ�ǩ�����������ӱ�ǩ���ӱ�ǩ��
	 */
	public synchronized int getChildCount() {
		if(children == null) 
			return 0;
		return this.children.size();
	}
	
	/**
	 * ����������ȡ�ӽڵ�
	 * @param index ����
	 * @return ���ظ�λ�õ��ӽڵ㡣��û�к��ӻ������Ƿ�������null
	 */
	public synchronized XmlNode getChild(int index) {
		if(children == null || index < 0 || index > children.size() - 1)
			return null;
		return this.children.get(index);
	}
	
	/**
	 * ���ݱ�ǩ����ȡ�ӽڵ�
	 * @param tagName ��ǩ��
	 * @return ���ذ������б�ǩ������Ҫ����ӽڵ��List��
	 */
	public synchronized List<XmlNode> getChild(String tagName) {
		ArrayList<XmlNode> list = new ArrayList<>();
		if(children != null) {
			for(XmlNode i : children) {
				if(i.tagName.equals(tagName))
					list.add(i);
			}
		}
		return list;
	}

	/**
	 * ˽�з��� �ݹ齫��ǩ����д��Document
	 * @param document Ҫд���Document����
	 * @param parent ���ڵ�
	 */
	private void genXml(Document document, Element parent) {
		Element node = document.createElement(this.tagName);
		node.setTextContent(this.textContent);
		if(attributes != null)
			for(Entry<String, String> e : attributes.entrySet()) {
				node.setAttribute(e.getKey(), e.getValue());
			}
		if(parent != null) {
			parent.appendChild(node);
		} else {
			document.appendChild(node);
		}
		if(this.getChildCount() > 0) {
			for(XmlNode child : children)
				child.genXml(document, node);
		}
	}
	
	/**
	 * ��XML�ļ���ȡ�������ɶ�Ӧ��XmlNode����
	 * @param file Ҫ��ȡ���ļ�������ָ��һ�������ҿɶ����ļ�
	 * @return ���ɵ�XmlNode����
	 * @throws Exception
	 */
	public static synchronized XmlNode readFromFile(File file) throws Exception {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
		return XmlNode.fromNode(doc.getDocumentElement());
	}
	
	/**
	 * �������Լ������ӽڵ�ת��Ϊ�ַ���
	 * @return XML��ǩ���ַ�����ʾ
	 * @throws Exception
	 */
	public synchronized String toXmlString() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();
		genXml(document, null);
		OutputFormat format = new OutputFormat(document);
		format.setIndenting(true);
		format.setIndent(indentNum);
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		XMLSerializer serializer = new XMLSerializer(bos, format);
		serializer.serialize(document);
		return bos.toString();
	}
	
	/**
	 * �������Լ������ӱ�ǩ������д���ļ�
	 * @param file Ҫд����ļ��������������ָ��һ�������ҿ�д���ļ�
	 * @throws Exception
	 */
	public synchronized void writeXmlToFile(File file) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();
		genXml(document, null);
		OutputFormat format = new OutputFormat(document);
		format.setIndenting(true);
		format.setIndent(indentNum);
		Writer output = new BufferedWriter(new FileWriter(file));
		XMLSerializer serializer = new XMLSerializer(output, format);
		serializer.serialize(document);
	}
}
