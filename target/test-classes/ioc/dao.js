var ioc = {
	conf : {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		fields : {
			paths : [ "custom/" ]
		}
	},
	dataSource : {
		factory : "$conf#make",
		args : [ "com.alibaba.druid.pool.DruidDataSource", "db." ],
		type : "com.alibaba.druid.pool.DruidDataSource",
		events : {
			create : "init",
			depose : 'close'
		}
	},
	dao : {
		type : "org.nutz.dao.impl.NutDao",
		args : [ {
			refer : "dataSource"
		} ]
	},
	//文件上传配置
	tmpFilePool : {
		type : 'org.nutz.filepool.NutFilePool',
		// 临时文件最大个数为 1000 个
		args : [ "~/nutz/blog/upload/tmps", 1000 ]
	},
	uploadFileContext : {
		type : 'org.nutz.mvc.upload.UploadingContext',
		singleton : false,
		args : [ {
			refer : 'tmpFilePool'
		} ],
		fields : {
			// 是否忽略空文件, 默认为 false
			ignoreNull : true,
			// 单个文件最大尺寸(单位为字节)
			maxFileSize : 209715200, // 200M
			// 正则表达式匹配可以支持的文件名
			nameFilter : '^(.+[.])(gif|jpg|png|mp4|exe)$'
		}
	},
	myUpload : {
		type : 'org.nutz.mvc.upload.UploadAdaptor',
		singleton : false,
		args : [ {
			refer : 'uploadFileContext'
		} ]
	}
};