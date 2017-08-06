package com.oyp.ftp.panel.ftp;

import java.io.*;

import javax.swing.*;

import sun.net.*;

import com.oyp.ftp.panel.queue.*;
import com.oyp.ftp.utils.*;

/**
 * FTP文件管理模块的FTP文件下载队列的线程
 */
public class DownThread extends Thread {
	private final FtpPanel ftpPanel; // FTP资源管理面板
	private final FtpClient ftpClient; // FTP控制类
	private boolean conRun = true; // 线程的控制变量
	private String path; // FTP的路径信息
	private Object[] queueValues; // 下载任务的数组

	/**
	 * 构造方法
	 * 
	 * @param ftpPanel
	 *            - FTP资源管理面板
	 */
	public DownThread(FtpPanel ftpPanel) {
		this.ftpPanel = ftpPanel;
		ftpClient = new FtpClient(); // 创建新的FTP控制对象
		FtpClient ftp = ftpPanel.ftpClient;
		try {
			// 连接到FTP服务器
			ftpClient.openServer(ftp.getServer(), ftp.getPort());
			ftpClient.login(ftp.getName(), ftp.getPass()); // 登录服务器
			ftpClient.binary(); // 使用二进制传输
			ftpClient.noop();
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread() { // 创建保持服务器通讯的线程
			public void run() {
				while (conRun) {
					try {
						Thread.sleep(30000);
						ftpClient.noop(); // 定时向服务器发送消息，保持连接
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void stopThread() {// 停止线程的方法
		conRun = false;
	}

	/**
	 * 下载线程的递归方法，用户探索FTP下载文件夹的所有子文件夹和内容
	 * @param file  FTP文件对象
	 * @param localFolder  本地文件夹对象
	 */
	private void downFile(FtpFile file, File localFolder) {
		// 判断队列面板是否执行暂停命令
		while (ftpPanel.frame.getQueuePanel().isStop()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Object[] args = ftpPanel.queue.peek();
		// 判断队列顶是否为处理的上一个任务。
		if (queueValues == null || args == null
				|| !queueValues[0].equals(args[0]))
			return;
		try {
			String ftpFileStr = file.getAbsolutePath().replaceFirst(path + "/",
					"");
			if (file.isFile()) {
				// 获取服务器指定文件的输入流
				TelnetInputStream ftpIs = ftpClient.get(file.getName());
				if (ftpIs == null) {
					JOptionPane.showMessageDialog(this.ftpPanel, file.getName()
							+ "无法下载");
					return;
				}
				// 创建本地文件对象
				File downFile = new File(localFolder, ftpFileStr);
				// 创建本地文件的输出流
				FileOutputStream fout = new FileOutputStream(downFile, true);
				// 计算文件大小
				double fileLength = file.getLongSize() / Math.pow(1024, 2);
				ProgressArg progressArg = new ProgressArg((int) (file
						.getLongSize() / 1024), 0, 0); //进度参数
				String size = String.format("%.4f MB", fileLength);
				//"文件名", "大小", "本地文件名","主机", "状态"
				Object[] row = new Object[] { ftpFileStr, size,
						downFile.getAbsolutePath(), ftpClient.getServer(),
						progressArg };
				DownloadPanel downloadPanel = ftpPanel.frame.getDownloadPanel(); //下载队列面板
				downloadPanel.addRow(row);  //添加列
				byte[] data = new byte[1024]; // 定义缓存
				int read = -1;
				while ((read = ftpIs.read(data)) > 0) { // 读取FTP文件内容到缓存
					Thread.sleep(0, 30); // 线程休眠
					fout.write(data, 0, read); // 将缓存数据写入本地文件
					// 累加进度条
					progressArg.setValue(progressArg.getValue() + 1);
				}
				progressArg.setValue(progressArg.getMax());// 结束进度条
				fout.close(); // 关闭文件输出流
				ftpIs.close(); // 关闭FTP文件输入流
			} else if (file.isDirectory()) { // 如果下载的是文件夹
				// 创建本地文件夹对象
				File directory = new File(localFolder, ftpFileStr);
				directory.mkdirs(); // 创建本地的文件夹
				ftpClient.cd(file.getName()); // 改变FTP服务器的当前路径
				// 获取FTP服务器的文件列表信息
				TelnetInputStream telnetInputStream=ftpClient.list();
				byte[]names=new byte[2048];
				int bufsize=0;
				bufsize=telnetInputStream.read(names, 0, names.length);
				int i=0,j=0;
				while(i<bufsize){
					//字符模式为10，二进制模式为13
//					if (names[i]==10) {
					if (names[i]==13) {
						//获取字符串 -rwx------ 1 user group          57344 Apr 18 05:32 腾讯电商2013实习生招聘TST推荐模板.xls
						//文件名在数据中开始做坐标为j,i-j为文件名的长度，文件名在数据中的结束下标为i-1
						String fileMessage = new String(names,j,i-j);
						if(fileMessage.length() == 0){
							System.out.println("fileMessage.length() == 0");
							break;
						}
						//按照空格将fileMessage截为数组后获取相关信息
						// 正则表达式  \s表示空格，｛1，｝表示1一个以上 
						if(!fileMessage.split("\\s+")[8].equals(".") && !fileMessage.split("\\s+")[8].equals("..")){
							/**文件大小*/
							String sizeOrDir="";
							if (fileMessage.startsWith("d")) {//如果是目录
								sizeOrDir="<DIR>";
							}else if (fileMessage.startsWith("-")) {//如果是文件
								sizeOrDir=fileMessage.split("\\s+")[4];
							}
							/**文件名*/
							String fileName=fileMessage.split("\\s+")[8];
							FtpFile ftpFile = new FtpFile();
							// 将FTP目录信息初始化到FTP文件对象中
							ftpFile.setSize(sizeOrDir);
							ftpFile.setName(fileName);
							ftpFile.setPath(file.getAbsolutePath());
							// 递归执行子文件夹的下载
							downFile(ftpFile, localFolder); 
						}
//						j=i+1;//上一次位置为字符模式
						j=i+2;//上一次位置为二进制模式
					}
					i=i+1;
				}
				ftpClient.cdUp(); // 返回FTP上级路径
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void run() { // 线程业务方法
		while (conRun) {
			try {
				Thread.sleep(1000);
				ftpClient.noop();
				queueValues = ftpPanel.queue.peek();
				if (queueValues == null) {
					continue;
				}
				FtpFile file = (FtpFile) queueValues[0];
				File localFolder = (File) queueValues[1];
				if (file != null) {
					path = file.getPath();
					ftpClient.cd(path);
					downFile(file, localFolder);
					path = null;
					ftpPanel.frame.getLocalPanel().refreshCurrentFolder();
				}
				Object[] args = ftpPanel.queue.peek();
				// 判断队列顶是否为处理的上一个任务。
				if (queueValues == null || args == null
						|| !queueValues[0].equals(args[0]))
					continue;
				ftpPanel.queue.poll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}