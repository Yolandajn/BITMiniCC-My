package me.entalent.minicc;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import bit.minisys.minicc.MiniCCCfg;
import bit.minisys.minicc.simulator.MIPSSimulator;

public class MiniCCompiler2 {
	// �����ļ�������
	HashMap<String, HashMap<String, String>> configData = new HashMap<>();
	// XML�ļ���ÿ������׶ε�����
	String[] phaseNames = {"pp", "scanning", "parsing", "semantic", "icgen", "optimizing", "codegen", "simulating"};
	//ÿ���׶ε������ļ���׺
	String[] phaseInputFileExt = {
			MiniCCCfg.MINICC_PP_INPUT_EXT,
			MiniCCCfg.MINICC_SCANNER_INPUT_EXT,
			MiniCCCfg.MINICC_PARSER_INPUT_EXT,
			MiniCCCfg.MINICC_SEMANTIC_INPUT_EXT,
			MiniCCCfg.MINICC_ICGEN_INPUT_EXT,
			MiniCCCfg.MINICC_OPT_INPUT_EXT,
			MiniCCCfg.MINICC_CODEGEN_INPUT_EXT,
	};
	//ÿ���׶�����ļ��ĺ�׺
	String[] phaseOutputFileExt = {
			MiniCCCfg.MINICC_PP_OUTPUT_EXT,
			MiniCCCfg.MINICC_SCANNER_OUTPUT_EXT,
			MiniCCCfg.MINICC_PARSER_OUTPUT_EXT,
			MiniCCCfg.MINICC_SEMANTIC_OUTPUT_EXT,
			MiniCCCfg.MINICC_ICGEN_OUTPUT_EXT,
			MiniCCCfg.MINICC_OPT_OUTPUT_EXT,
			MiniCCCfg.MINICC_CODEGEN_OUTPUT_EXT,
	};
	//ÿ���׶ε���������
	String[] attrNames = {"type", "path", "skip"};
	
	/**
	 * ��ȡ�����ļ�
	 * @throws Exception
	 */
	void readConfig() throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(".\\config.xml");
		NodeList nodeList = doc.getElementsByTagName("phase");
		for(int i = 0; i < nodeList.getLength(); i++){
			Element elem = (Element) nodeList.item(i);
			String phase = elem.getAttribute("name");
			HashMap<String, String> data = new HashMap<>();
			for(String attr : attrNames) {
				data.put(attr, elem.getAttribute(attr));
			}
			configData.put(phase, data);
		}
	}

	/**
	 * ���б���
	 * @param sourceFilePath Դ�ļ���·��
	 * @throws Exception
	 */
	public void run(String sourceFilePath) throws Exception {
		readConfig();
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		for(int i = 0; i < phaseNames.length - 1; i++){
			HashMap<String, String> phaseConfig = configData.get(phaseNames[i]);
			if(phaseConfig.get("skip").equals("true")) 
				continue;
			//ȷ�������ļ���������ļ���
			String inputFilePath = sourceFilePath.replace(MiniCCCfg.MINICC_PP_INPUT_EXT, phaseInputFileExt[i]),
					outputFilePath = sourceFilePath.replace(MiniCCCfg.MINICC_PP_INPUT_EXT, phaseOutputFileExt[i]);
			//System.out.println("input: " + inputFilePath);
			//System.out.println("output:" + outputFilePath);
			if(phaseConfig.get("type").equals("java")) {
				//ȡ���������
				Class<?> cls;
				try {
					cls = classLoader.loadClass(phaseConfig.get("path"));
				} catch (ClassNotFoundException e) {
					System.out.println("Failed to find the java class assigned for phase \"" + phaseNames[i] + "\"");
					return;
				}
				//���쵱ǰ�׶εĴ������ʵ�������������public���ε��޲ι��췽��
				Object obj;
				try {
					obj = cls.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					System.out.println("There should be a public default constructor defined in class " + cls.getName());
					return;
				}
				//����run���������е�ǰ�׶εķ���
				Method runMethod;
				try {
					runMethod = cls.getMethod("run", String.class, String.class);
					runMethod.invoke(obj, inputFilePath, outputFilePath);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
					//����û�ж���/����public/�����б���
					System.out.println("There should be a \'public void run(String inputFile, String outputFile)\' method defined in class " + cls.getName());
					return;
				} catch (InvocationTargetException e) {
					//�����õķ����׳��쳣
					e.printStackTrace();
					return;
				}
			} else {
				String command = phaseConfig.get("path") + " " + inputFilePath + " " + outputFilePath;
				try {
					Runtime.getRuntime().exec(command);
				} catch (IOException e) {
					System.out.println("Failed to execute the binary file in phase \"" + phaseNames[i] + "\"");
					System.out.println("The command is:\n" + command);
					return;
				}
			}
		}
		HashMap<String, String> phaseConfig = configData.get(phaseNames[7]);
		if(!phaseConfig.get("skip").equals("true")) {
			MIPSSimulator m = new MIPSSimulator();
			m.run(phaseOutputFileExt[6]);
		}
	}
}
