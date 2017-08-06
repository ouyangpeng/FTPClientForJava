package com.oyp.ftp.utils;

import java.io.IOException;

import sun.net.TelnetInputStream;
import sun.net.TelnetOutputStream;

public class FtpClient extends sun.net.ftp.FtpClient {
	private String server;
	private int port;
	private String name;
	private String pass;

	public int getPort() {
		return port;
	}

	public String getName() {
		return name;
	}

	public String getPass() {
		return pass;
	}

	/**
	  * 设置连接编码
	  * @param encodingStr
	  */
	public static void setEncoding(String encodingStr) {
	  sun.net.NetworkClient.encoding = encodingStr;
	}
	
	public FtpClient(String server, int port) throws IOException {
		super(server, port);
		this.server = server;
		/*设置连接编码*/
		setEncoding(System.getProperty("file.encoding"));
	}

	public FtpClient() {
		super();
		/*设置连接编码*/
		setEncoding(System.getProperty("file.encoding"));
	}

	@Override
	public synchronized void binary() throws IOException {
		super.binary();
	}

	@Override
	public synchronized void cd(String arg0) throws IOException {
		super.cd(arg0);
	}

	@Override
	public synchronized void cdUp() throws IOException {
		super.cdUp();
	}

	@Override
	public synchronized TelnetInputStream get(String file) throws IOException {
		return super.get(file);
	}

	@Override
	public synchronized TelnetInputStream list() throws IOException {
		return super.list();
	}

	@Override
	public synchronized void login(String arg0, String arg1) throws IOException {
		name = arg0;
		pass = arg1;
		super.login(arg0, arg1);
	}

	@Override
	public synchronized void noop() throws IOException {
		super.noop();
	}

	@Override
	public synchronized void openServer(String arg0, int port)
			throws IOException {
		this.server = arg0;
		this.port = port;
		super.openServer(server, port);
	}

	@Override
	public synchronized TelnetOutputStream put(String arg0) throws IOException {
		return super.put(arg0);
	}

	@Override
	public synchronized String pwd() throws IOException {
		String pwd = super.pwd();
//		pwd = new String(pwd.getBytes("iso-8859-1"), "GBK");
		pwd = new String(pwd.getBytes(System.getProperty("file.encoding")));/*设置连接编码*/
		return pwd;
	}

	@Override
	public synchronized int readServerResponse() throws IOException {
		return super.readServerResponse();
	}

	@Override
	public synchronized void sendServer(String command) {
		super.sendServer(command);
	}

	@Override
	public synchronized boolean serverIsOpen() {
		return super.serverIsOpen();
	}

	public String getServer() {
		return server;
	}
}
