package com.xt;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

public class Util {
	public static PropertiesProxy conf = new PropertiesProxy("custom");
	
	public static void main(String[] args) {
		
	}
	
	/**
	 * 将蛇形字符串转为驼峰且第一个字母变小写
	 * @param str
	 * @return
	 */
	public static String toHump1(String str){
		str = Strings.line2Hump(str);
		String s = str.substring(0,  1);
		str = str.replaceFirst(s, s.toLowerCase());
		return str;
	}
	/**
	 * 将数据库的类型转为Nutz的数据库字段类型 VARCHAR => ColType.VARCHAR
	 * @param s
	 * @return
	 */
	public static String typeDb2Nutz(String s){
		String res = "ColType.AUTO";
		switch(s.toUpperCase()){
			case "VARCHAR": case "BOOLEAN": case "CHAR": case "TEXT": case "BINARY":
			case "TIMESTAMP": case "DATETIME": case "DATE": case "TIME": case "INT":
			case "FLOAT": case "LONG":	 
				res = "ColType." + s;
				break;
		}
		return res;
	}
	/**
	 * 将数据库的类型转为java类型 VARCHAR => String
	 * @param s
	 * @return
	 */
	public static String typeDb2Java(String s){
		String res = "String";
		switch(s.toUpperCase()){
			case "VARCHAR": case "TEXT": case "BINARY":
				res = "String";
				break;
			case "BOOLEAN": 
				res = "boolean";
				break;
			case "CHAR": 
				res = "char";
				break;
			case "TIMESTAMP": case "DATETIME": case "DATE": case "TIME": 
				res = "Date";
				break;
			case "INT":
				res = "int";
				break;
			case "FLOAT":  
				res = "float";
				break;
		}
		return res;
	}
	/**
	 * 获取字段的方法名 userName => getUserName / setUserName
	 * @param filedName 字段名
	 * @param m get/set
	 * @return 
	 */
	public static String getMethod(String filedName, String m) {
		String s = filedName.substring(0,  1);
		filedName = filedName.replaceFirst(s, s.toUpperCase());
		return m + filedName;
	}
	
}
