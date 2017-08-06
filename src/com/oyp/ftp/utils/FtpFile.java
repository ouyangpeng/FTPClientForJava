package com.oyp.ftp.utils;

/**
 *  FTP文件的JavaBean类
 */
public class FtpFile implements FileInterface {
	private String name = ""; // 文件名称
	private String path = ""; // 路径
	protected boolean directory; // 是否文件夹
	private boolean file; // 是否文件
	private String lastDate; // 最后修改日期
	private String size; // 文件大小
	private long longSize; // 文件大小的长整型类型
	private final int GB = (int) Math.pow(1024, 3); // GB单位数值
	private final int MB = (int) Math.pow(1024, 2); // MB单位数值
	private final int KB = 1024; // KB单位数值

	/**
	 * 默认的构造方法
	 */
	public FtpFile() {
	}

	/**
	 * 自定义的构造方法
	 * 
	 * @param name
	 *            文件名
	 * @param path
	 *            路径
	 * @param directory
	 *            是否文件夹
	 */
	public FtpFile(String name, String path, boolean directory) {
		this.name = name; // 初始化相关属性
		this.path = path;
		this.directory = directory;
	}

	public String getSize() {
		return size;
	}

	/**
	 * 设置文件大小的方法
	 * 
	 * @param nsize
	 *            文件大小的字符串表示
	 */
	public void setSize(String nsize) {
		if (nsize.indexOf("DIR") != -1) { // 如果文件大小包含DIR字符串
			this.size = "<DIR>";
			directory = true; // 设置该类成为一个文件夹对象
			file = false;
		} else { // 否则
			file = true; // 设置该类成为一个文件对象
			directory = false;
			this.size = nsize.trim(); // 计算文件的大小单位
			longSize = Long.parseLong(size);
			if (longSize > GB) {
				size = longSize / GB + "G ";
			}
			if (longSize > MB) {
				size = longSize / MB + "M ";
			}
			if (longSize > KB) {
				size = longSize / KB + "K ";
			}
		}
	}

	public String getLastDate() {
		return lastDate;
	}

	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}

	public boolean isFile() {
		return file;
	}

	public boolean isDirectory() {
		return directory;
	}

	/**
	 * 获得该文件的绝对路径
	 * 
	 * @return 路径
	 */
	public String getAbsolutePath() {
		if (path.lastIndexOf('/') == path.length() - 1)
			return path + name;
		else
			return path + "/" + name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 重写toString方法，它决定了该类在组件中显示的内容
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	public long getLongSize() {
		return longSize;
	}
}
