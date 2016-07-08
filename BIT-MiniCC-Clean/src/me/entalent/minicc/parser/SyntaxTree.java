package me.entalent.minicc.parser;

import java.util.ArrayList;

import me.entalent.minicc.icgen.NodeProperty;
import me.entalent.minicc.scanner.Scanner.Token;

/**
 * �﷨��
 * @author ental
 *
 */
public class SyntaxTree {
	//���ڵ㣬���ڵ�ĸ��ڵ�Ϊnull
	public SyntaxTree parent;
	
	//�ڵ��ϵ�����
	public Object content;
	
	public NodeProperty prop;
	public boolean processed;
	
	//��������
	public ArrayList<SyntaxTree> children;
	
	public SyntaxTree(Object rootContent) {
		this.content = rootContent;
		this.parent = null;
		this.prop = new NodeProperty();
	}
	
	//�������
	public boolean addChild(SyntaxTree syntaxTree) {
		if(this.children == null)
			this.children = new ArrayList<>();
		syntaxTree.parent = this;
		return this.children.add(syntaxTree);
	}
	
	public boolean addChild(String childContent) {
		return addChild(new SyntaxTree(childContent));
	}
	
	public ArrayList<SyntaxTree> getChild(String key) {
		ArrayList<SyntaxTree> childs = new ArrayList<>();
		for(int i = this.children.size() - 1; i >= 0; i--) {
			if(children.get(i).content.equals(key)) {
				childs.add(this.children.get(i));
			}
		}
		return childs;
	}

	@Override
	public String toString() {
		if(this.children == null) {
			return (String) this.content + "\n";
		}
		String str = "<" + content.toString() + ">" + "\n";
		if(this.children != null) {
			for(int i = this.children.size() - 1; i >= 0; i--) {
				str += this.children.get(i).toString();
			}
		}
		str += "</" + content.toString() + ">\n";
		return str;
	}
	
	public static String getLeafNodeContent(String tagName, String value) {
		return "<" + tagName + ">" + value + "</" + tagName + ">";
	}
	
}
