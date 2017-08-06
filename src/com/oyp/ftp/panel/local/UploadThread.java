
package com.oyp.ftp.panel.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Queue;

import com.oyp.ftp.panel.ftp.FtpPanel;
import com.oyp.ftp.panel.queue.UploadPanel;
import com.oyp.ftp.utils.FtpClient;
import com.oyp.ftp.utils.FtpFile;
import com.oyp.ftp.utils.ProgressArg;

/**
 * FTP文件管理模块的本地文件上传队列的线程
 */
class UploadThread extends Thread {
	private LocalPanel localPanel;
	String path = "";// 上传文件的本地相对路径
	String selPath;// 选择的本地文件的路径
	private boolean conRun = true; // 线程的控制变量
	private FtpClient ftpClient; // FTP控制类
	private Object[] queueValues; // 队列任务数组

	/**
	 * 创建上传队列线程的构造方法
	 * 
	 * @param localPanel
	 *            - 本地资源管理面板
	 * @param server
	 *            - FTP服务器地址
	 * @param port
	 *            - FTP服务器端口号
	 * @param userStr
	 *            - 登录FTP服务器的用户名
	 * @param passStr
	 *            - 登录FTP服务器的密码
	 */
	public UploadThread(LocalPanel localPanel, String server, int port,
			String userStr, String passStr) {
		try {
			ftpClient = new FtpClient(server, port);
			ftpClient.login(userStr, passStr);
			ftpClient.binary();
			path = ftpClient.pwd();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.localPanel = localPanel;
		new Thread() { // 创建保持服务器通讯的线程
			public void run() {
				while (conRun) {
					try {
						Thread.sleep(30000);
						// 定时向服务器发送消息，保持连接
						UploadThread.this.ftpClient.noop();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void stopThread() { // 停止线程的方法
		conRun = false;
	}
	
	/**
	 * 上传线程的递归方法，上传文件夹的所有子文件夹和内容
	 * @param file
	 *            - FTP文件对象
	 * @param localFolder
	 *            - 本地文件夹对象
	 */
	private void copyFile(File file, FtpFile ftpFile) { // 递归遍历文件夹的方法
		// 判断队列面板是否执行暂停命令
		while (localPanel.frame.getQueuePanel().isStop()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		Object[] args = localPanel.queue.peek();
		// 判断队列顶是不是上一个处理的任务。
		if (queueValues == null || args == null
				|| !queueValues[0].equals(args[0]))
			return;
		try {
//			System.out.println("selPath："+selPath);
			path = file.getParentFile().getPath().replace(selPath, "");
//			System.out.println("path："+path);
			ftpFile.setName(path.replace("\\", "/"));
			path = ftpFile.getAbsolutePath();
//			System.out.println("ftpFile.getAbsolutePath()："+path);
			if (file.isFile()) {
				UploadPanel uploadPanel = localPanel.frame.getUploadPanel();//上传面板
				String remoteFile = path + "/" + file.getName(); // 远程FTP的文件名绝对路径
//				System.out.println("remoteFile:" + remoteFile);
				double fileLength = file.length() / Math.pow(1024, 2);
				ProgressArg progressArg = new ProgressArg(
						(int) (file.length() / 1024), 0, 0);//进度参数
				String size = String.format("%.4f MB", fileLength);
				Object[] row = new Object[] { file.getAbsoluteFile(), size,
						remoteFile, ftpClient.getServer(), progressArg };
				uploadPanel.addRow(row); //添加列
				OutputStream put = ftpClient.put(remoteFile); // 获取服务器文件的输出流
				FileInputStream fis = null; // 本地文件的输入流
				try {
					fis = new FileInputStream(file); // 初始化文件的输入流
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				int readNum = 0;
				byte[] data = new byte[1024]; // 缓存大小
				while ((readNum = fis.read(data)) > 0) { // 读取本地文件到缓存
					Thread.sleep(0, 30); // 线程休眠
					put.write(data, 0, readNum); // 输出到服务器
					progressArg.setValue(progressArg.getValue() + 1);// 累加进度条
				}
				progressArg.setValue(progressArg.getMax()); // 结束进度条
				fis.close(); // 关闭文件输入流
				put.close(); // 关闭服务器输出流
			} else if (file.isDirectory()) {
				path = file.getPath().replace(selPath, "");
				ftpFile.setName(path.replace("\\", "/"));
//				System.out.println("Dirpath："+path);
				/**将目录切换到当前FTP服务器的当前目录*/
				ftpClient.cd(this.localPanel.frame.getFtpPanel().getPwd());     //  /media目录
				/**
				 * 如果有创建文件夹的权限，则在当前FTP服务器的当前目录下创建文件夹
				 * 必须要有创建文件夹的权限，否则会报错
				 * 		path：audio
						ftpFile.getAbsolutePath()：/media/audio
						remoteFile:/media/audio/梁静茹-会呼吸的痛Live.mp3
				 */
				ftpClient.sendServer("MKD " + path + "\r\n");   //创建  /media/audio 目录
				ftpClient.readServerResponse();
				
				/***********************************************************
				 * 如果没有有创建文件夹的权限，则创建文件夹，因此FTP服务器的当前路径下不存在
				 * 那么将文件上传到此FTP服务器的当前路径下
				 * 
				 * 		如要上传C://audio目录（目录中有 梁静茹-会呼吸的痛Live.mp3 和 林宥嘉-心酸.mp3 两个文件）
				 * 		到 FTP服务器上的  /media/ 目录下
				 * 		因为FTP服务器上没有 /media/audio 目录，并且FTP服务器当前的目录为 /media
				 * 		所以将 C://audio目录下的文件上传到了 /media目录下
				 * 		ftpFile.getAbsolutePath()：/media/audio
						remoteFile:/media/梁静茹-会呼吸的痛Live.mp3
						remoteFile:/media/林宥嘉-心酸.mp3
				 */
				//创建一个文件夹对象，检查该文件是否存在
				File fileRemote=new File(this.localPanel.frame.getFtpPanel().getPwd()+path);  //path：audio
				//该目录不存在
				if (!fileRemote.exists()) {
					path=this.localPanel.frame.getFtpPanel().getPwd();
				}
				/***********************************************************/
				
				File[] listFiles = file.listFiles();
				for (File subFile : listFiles) {
					Thread.sleep(0, 50);
					copyFile(subFile, ftpFile);
				}
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.exit(0);
			// JOptionPane.showMessageDialog(localPanel, e1.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 线程的主体方法
	 */
	public void run() { // 线程的主体方法
		while (conRun) {
			try {
				Thread.sleep(1000); // 线程休眠1秒
				Queue<Object[]> queue = localPanel.queue; // 获取本地面板的队列对象
				queueValues = queue.peek(); // 获取队列首的对象
				if (queueValues == null) { // 如果该对象为空
					continue; // 进行下一次循环
				}
				File file = (File) queueValues[0]; // 获取队列中的本队文件对象
				FtpFile ftpFile = (FtpFile) queueValues[1]; // 获取队列中的FTP文件对象
				if (file != null) {
					selPath = file.getParent();
					copyFile(file, ftpFile); // 调用递归方法上传文件
					FtpPanel ftpPanel = localPanel.frame.getFtpPanel();
					ftpPanel.refreshCurrentFolder(); // 刷新FTP面板中的资源
				}
				Object[] args = queue.peek();
				// 判断队列顶是否为处理的上一个任务。
				if (queueValues == null || args == null
						|| !queueValues[0].equals(args[0])) {
					continue;
				}
				queue.remove(); // 移除队列首元素
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}