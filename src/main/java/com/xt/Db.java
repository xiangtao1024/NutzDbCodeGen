package com.xt;

import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.mvc.NutConfig;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

public class Db {
	public static DruidPooledConnection getDbConnect() throws Exception {
		DruidDataSource dataSource = null;
		try {
			dataSource = new DruidDataSource();
			// 设置连接参数 (***自己定义传递的参数***)
			dataSource.setUrl(Util.conf.get("db.url"));
			dataSource.setDriverClassName(Util.conf.get("db.driverClassName"));
			dataSource.setUsername(Util.conf.get("db.username"));
			dataSource.setPassword(Util.conf.get("db.password"));
			// 配置初始化大小、最小、最大
			dataSource.setInitialSize(1);
			dataSource.setMinIdle(1);
			dataSource.setMaxActive(20);
			// 连接泄漏监测
			dataSource.setRemoveAbandoned(true);
			dataSource.setRemoveAbandonedTimeout(30);
			// 配置获取连接等待超时的时间
			dataSource.setMaxWait(20000);
			// 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
			dataSource.setTimeBetweenEvictionRunsMillis(20000);
			// 防止过期
			dataSource.setValidationQuery("SELECT 'x'");
			dataSource.setTestWhileIdle(true);
			dataSource.setTestOnBorrow(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 建立了连接
		DruidPooledConnection con = dataSource.getConnection();
		return con;
	}
}
