package com.xt;

import java.io.File;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import com.gexin.fastjson.JSON;

import jetbrick.template.JetEngine;
import jetbrick.template.JetTemplate;

public class Start {
	/**
	 * 启动
	 * @throws Exception
	 */
	public void start() throws Exception{
		NutMap res = this.getDbTableInfo();
		String packageStr = res.getString("packageName");
		File folder = new File(packageStr.replaceAll("\\.", "/"));
		System.out.println(folder.getAbsolutePath());
		boolean fm = Files.makeDir(folder);
		if(fm) {
			System.out.println("文件目录创建成功");
		}else {
			System.out.println("文件创建失败,请删除生产的目录后在生成");
			return;
		}
		
		JetEngine engine = JetEngine.create();
		JetTemplate template = engine.getTemplate("model.jetx");
		List<NutMap> tables = res.getAsList("tables", NutMap.class);
		for(NutMap table : tables) {
			String tablename = table.getString("tablename");
			String tableModelName = table.getString("tableModelName");
			File modeFile = new File(folder, tableModelName + ".java");
			System.out.println(JSON.toJSON(table));
			StringWriter writer = new StringWriter();
	        //模板转换
	        template.render(table, writer);
	        Files.write(modeFile, writer.toString());
		}
	}
	/**
	 * 获取数据表信息
	 * {
	 * 	packageName: 'xxx',
	 * 	tables: [
	 * 		{
	 * 			packageName: '',
	 * 			tablename: 'user_info',
	 * 			tableModelName: 'UserInfo',
	 * 			columns: [
	 * 				{
	 * 					id: 0 //0: false 1 true
	 * 					name: 0 //0: false, 1 true 
	 * 					isAuto: false,
	 * 					columnname: 'user_name',
	 * 					fieldname: 'userName',
	 * 					notNull: false,
	 * 					type: 'ColType.AUTO',
	 * 					filedType: 'int'
	 * 					width: 234,
	 * 					comment: ''
	 * 				}...
	 * 			]
	 * 		}...
	 * 	]
	 * }
	 * @return
	 * @throws Exception
	 */
	public NutMap getDbTableInfo() throws Exception {
		Connection con = Db.getDbConnect();
		DatabaseMetaData data = con.getMetaData();
		ResultSet rs = data.getTables(null, null, null, new String[] { "TABLE" });
		NutMap res = NutMap.NEW().addv("packageName", Util.conf.get("gen.package"));
		List<NutMap> tables = new ArrayList<>();
		while (rs.next()) {
			NutMap table = NutMap.NEW().addv("tablename", rs.getString("TABLE_NAME"));
			table.addv("packageName", Util.conf.get("gen.package"))
				.addv("tableModelName", Strings.line2Hump(rs.getString("TABLE_NAME")));
			
			//获取主键
			NutMap primaryKey = NutMap.NEW();
			ResultSet primaryKeyResultSet = data.getPrimaryKeys(null, null, rs.getString("TABLE_NAME"));  
			while(primaryKeyResultSet.next()){
			    String primaryKeyColumnName = primaryKeyResultSet.getString("COLUMN_NAME");
			    primaryKey.addv(primaryKeyColumnName, true);
			}
			//获取列
			ResultSet rsColimns = data.getColumns(null, "%", rs.getString("TABLE_NAME"), "%");
			List<NutMap> columns = new ArrayList();
			while (rsColimns.next()) {
				String filedName = Util.toHump1(rsColimns.getString("COLUMN_NAME"));
				String isAuto =rsColimns.getString("IS_AUTOINCREMENT");
				NutMap column = NutMap.NEW()
						.addv("isAuto", isAuto == "YES"? true: false)
						.addv("columnname", rsColimns.getString("COLUMN_NAME"))
						.addv("fieldname", filedName)
						.addv("setMethod", Util.getMethod(filedName, "set"))
						.addv("getMethod", Util.getMethod(filedName, "get"))
						.addv("notNull", rsColimns.getInt("NULLABLE"))
						.addv("type", Util.typeDb2Nutz(rsColimns.getString("TYPE_NAME")))
						.addv("filedType", Util.typeDb2Java(rsColimns.getString("TYPE_NAME")))
						.addv("width", rsColimns.getString("COLUMN_SIZE"))
						.addv("comment", rsColimns.getString("REMARKS"));
				column.addv("id", 0).addv("name", 0);
				if(primaryKey.getBoolean(rsColimns.getString("COLUMN_NAME"))) {
					//int,long,short,byte => @Id 其他 @Name
					if(Strings.equalsIgnoreCase(rsColimns.getString("TYPE_NAME"), "INT")
						|| Strings.equalsIgnoreCase(rsColimns.getString("TYPE_NAME"), "LONE")
						|| Strings.equalsIgnoreCase(rsColimns.getString("TYPE_NAME"), "SHORT")
						|| Strings.equalsIgnoreCase(rsColimns.getString("TYPE_NAME"), "BYTE")) {
						column.put("id", 1);
					}else {
						column.put("name", 1);
					}
				}
				columns.add(column);
			}
			table.addv("columns", columns);
			tables.add(table);
		}
		res.addv("tables", tables);
		return res;
	}
}