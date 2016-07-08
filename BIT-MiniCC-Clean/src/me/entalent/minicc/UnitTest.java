package me.entalent.minicc;

import java.io.File;

import org.junit.Test;

import me.entalent.minicc.util.xml.XmlNode;

/**
 * ��Ԫ�����õ��࣬��������޹�
 * @author ental
 *
 */
public class UnitTest {
	@Test 
	public void test() throws Exception {
		XmlNode node = new XmlNode("root");
		XmlNode son = new XmlNode("son");
		node.addChild(son);
		son.addChild(new XmlNode("grandson"));
		System.out.println(node.toXmlString());
	}
}
