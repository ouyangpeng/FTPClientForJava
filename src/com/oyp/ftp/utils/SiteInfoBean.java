package com.oyp.ftp.utils;

/**
 * 站点信息的JavaBean类，用于存储FTP站点信息
 */
public class SiteInfoBean {
	private String siteName; // 站点名称
	private String server; // 服务器地址
	private String userName; // 登录用户名
	private int port; // FTP服务器端口
	private String id; // ID编号

	/**
	 * 为各种变量赋值的构造方法
	 * 
	 * @param siteName
	 *            站点名称
	 * @param server
	 *            服务器地址
	 * @param port
	 *            端口号
	 * @param userName
	 *            用户名
	 */
	public SiteInfoBean(String siteName, String server, int port,
			String userName) {
		id = System.currentTimeMillis() + ""; // 赋值ID编号
		this.siteName = siteName; // 赋值服务器名称
		this.server = server; // 赋值服务器地址
		this.port = port; // 赋值端口号
		this.userName = userName; // 赋值用户名
	}

	/**
	 * 解析属性字符串进行赋值的构造方法
	 * 
	 * @param id
	 *            编号
	 * @param info
	 *            属性信息字符串
	 */
	public SiteInfoBean(String id, String info) {
		this.id = id; // 赋值ID编号
		String[] infos = info.split(","); // 解析属性信息字符串
		this.siteName = infos[0]; // 解析站点名称
		this.server = infos[1]; // 解析服务器地址
		this.port = Integer.valueOf(infos[2]); // 解析端口号
		this.userName = infos[3]; // 解析用户名
	}

	/**
	 * 获取站点名称的方法
	 * 
	 * @return 站点名称
	 */
	public String getSiteName() {
		return siteName;
	}

	/**
	 * 设置站点名称的方法
	 * 
	 * @param siteName
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * 重写父类toString()方法
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return siteName; // 这个返回值将显示在列表组件中
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
