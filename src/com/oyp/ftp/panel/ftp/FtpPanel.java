
package com.oyp.ftp.panel.ftp;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ActionMap;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableStringConverter;


import sun.net.TelnetInputStream;
import com.oyp.ftp.FTPClientFrame;
import com.oyp.ftp.panel.FTPTableCellRanderer;
import com.oyp.ftp.utils.FtpClient;
import com.oyp.ftp.utils.FtpFile;

public class FtpPanel extends javax.swing.JPanel {

	FtpClient ftpClient;
	private javax.swing.JButton createFolderButton;
	private javax.swing.JButton delButton;
	private javax.swing.JButton downButton;
	javax.swing.JTable ftpDiskTable;
	private javax.swing.JLabel ftpSelFilePathLabel;
	private javax.swing.JScrollPane scrollPane;
	private javax.swing.JToolBar toolBar;
	private javax.swing.JButton refreshButton;
	private javax.swing.JButton renameButton;
	FTPClientFrame frame = null;
	Queue<Object[]> queue = new LinkedList<Object[]>();
	private DownThread thread;

	public FtpPanel() {
		initComponents();
	}

	public FtpPanel(FTPClientFrame client_Frame) {
		frame = client_Frame;
		initComponents();
	}

	private void initComponents() {
		ActionMap actionMap = getActionMap();
		actionMap.put("createFolderAction", new CreateFolderAction(this,
				"创建文件夹", null));
		actionMap.put("delAction", new DelFileAction(this, "删除", null));
		actionMap.put("refreshAction", new RefreshAction(this, "刷新", null));
		actionMap.put("renameAction", new RenameAction(this, "重命名", null));
		actionMap.put("downAction", new DownAction(this, "下载", null));

		java.awt.GridBagConstraints gridBagConstraints;

		toolBar = new javax.swing.JToolBar();
		delButton = new javax.swing.JButton();
		renameButton = new javax.swing.JButton();
		createFolderButton = new javax.swing.JButton();
		downButton = new javax.swing.JButton();
		refreshButton = new javax.swing.JButton();
		scrollPane = new JScrollPane();
		ftpDiskTable = new JTable();
		ftpDiskTable.setDragEnabled(true);
		ftpSelFilePathLabel = new javax.swing.JLabel();

		setBorder(javax.swing.BorderFactory.createTitledBorder(null, "远程",
				javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.ABOVE_TOP));
		setLayout(new java.awt.GridBagLayout());

		toolBar.setRollover(true);
		toolBar.setFloatable(false);

		delButton.setText("删除");
		delButton.setFocusable(false);
		delButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		delButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		delButton.setAction(actionMap.get("delAction"));
		toolBar.add(delButton);

		renameButton.setText("重命名");
		renameButton.setFocusable(false);
		renameButton.setAction(actionMap.get("renameAction"));
		toolBar.add(renameButton);

		createFolderButton.setText("新文件夹");
		createFolderButton.setFocusable(false);
		createFolderButton.setAction(actionMap.get("createFolderAction"));
		toolBar.add(createFolderButton);

		downButton.setText("下载");
		downButton.setFocusable(false);
		downButton.setAction(actionMap.get("downAction"));
		toolBar.add(downButton);

		refreshButton.setText("刷新");
		refreshButton.setFocusable(false);
		refreshButton.setAction(actionMap.get("refreshAction"));
		toolBar.add(refreshButton);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(toolBar, gridBagConstraints);

		ftpDiskTable.setModel(new FtpTableModel());
		ftpDiskTable.setShowHorizontalLines(false);
		ftpDiskTable.setShowVerticalLines(false);
		ftpDiskTable.getTableHeader().setReorderingAllowed(false);
		ftpDiskTable.setDoubleBuffered(true);
		ftpDiskTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				ftpDiskTableMouseClicked(evt);
			}
		});
		scrollPane.setViewportView(ftpDiskTable);
		scrollPane.getViewport().setBackground(Color.WHITE);
		//设置渲染本地资源和FTP资源表格组件的渲染器
		ftpDiskTable.getColumnModel().getColumn(0).setCellRenderer(
				FTPTableCellRanderer.getCellRanderer());
		//RowSorter 的一个实现，它使用 TableModel 提供排序和过滤操作。
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
				ftpDiskTable.getModel());
		TableStringConverter converter = new TableConverter();
		//设置负责将值从模型转换为字符串的对象。
		sorter.setStringConverter(converter);
		//设置 RowSorter。RowSorter 用于提供对 JTable 的排序和过滤。 
		ftpDiskTable.setRowSorter(sorter);
		/**
		 * 颠倒指定列的排序顺序。调用此方法时，由子类提供具体行为。
		 * 通常，如果指定列已经是主要排序列，则此方法将升序变为降序（或将降序变为升序）；
		 * 否则，使指定列成为主要排序列，并使用升序排序顺序。如果指定列不可排序，则此方法没有任何效果。 
		 */
		sorter.toggleSortOrder(0);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(scrollPane, gridBagConstraints);

		ftpSelFilePathLabel.setBorder(javax.swing.BorderFactory
				.createEtchedBorder());
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		add(ftpSelFilePathLabel, gridBagConstraints);
	}

	/**
	 * 表格单击或双击事件的处理方法。
	 */
	private void ftpDiskTableMouseClicked(java.awt.event.MouseEvent evt) {
		int selectedRow = ftpDiskTable.getSelectedRow();
		Object value = ftpDiskTable.getValueAt(selectedRow, 0);
		if (value instanceof FtpFile) {
			FtpFile selFile = (FtpFile) value;
			ftpSelFilePathLabel.setText(selFile.getAbsolutePath());
			if (evt.getClickCount() >= 2) { //双击鼠标
				if (selFile.isDirectory()) {
					try {
						ftpClient.cd(selFile.getAbsolutePath());
						listFtpFiles(ftpClient.list());
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 读取FTP文件到表格的方法
	 * @param list
	 *            读取FTP服务器资源列表的输入流
	 */
	public synchronized void listFtpFiles(final TelnetInputStream list) {
		// 获取表格的数据模型
		final DefaultTableModel model = (DefaultTableModel) ftpDiskTable
				.getModel();
		model.setRowCount(0);
		// 创建一个线程类
		Runnable runnable = new Runnable() {
			public synchronized void run() {
				ftpDiskTable.clearSelection();
				try {
					String pwd = getPwd(); // 获取FTP服务器的当前文件夹
					model.addRow(new Object[] { new FtpFile(".", pwd, true),
							"", "" }); // 添加“.”符号
					model.addRow(new Object[] { new FtpFile("..", pwd, true),
							"", "" }); // 添加“..”符号
					/*
					byte[]names=new byte[2048];
					int bufsize=0;
					bufsize=list.read(names, 0, names.length);
//					list.close();
					int i=0,j=0;
					while(i<bufsize){
						char bc=(char)names[i];
						System.out.print(i+" "+bc+" ");
						//文件名在数据中开始做坐标为j,i-j为文件名的长度，文件名在数据中的结束下标为i-1
						if (names[i]==13) {
//							System.out.println("j:"+j+" i:"+i+ " i-j:"+(i-j));
							String temName=new String(names,j,i-j);
							System.out.println("temName="+temName);
							j=i+2;
						}
						i=i+1;
					}
					*/
					/* 	其中格式应满足如下格式的字符串	结果为:
						0 -: 1 r: 2 w: 3 x: 4 -: 5 -: 6 -: 7 -: 8 -: 9 -: 10  : 11 1: 12  : 13 u: 14 s: 15 e: 16 r: 17  : 18 g: 19 r: 20 o: 21 u: 22 p: 23  : 24  : 25  : 26  : 27  : 28  : 29  : 30  : 31  : 32 6: 33 7: 34 8: 35 4: 36 3: 37 0: 38  : 39 A: 40 p: 41 r: 42  : 43 1: 44 6: 45  : 46 2: 47 1: 48 :: 49 4: 50 6: 51  : 52 F: 53 T: 54 P: 55 ?: 56 ?: 57 ?: 58 ?: 59 ?: 60 ?: 61 ?: 62 ?: 63 ?: 64 ?: 65 ?: 66 ?: 67 ?: 68 ?: 69 ?: 70 ?: 71 ?: 72 ?: 73 .: 74 p: 75 d: 76 f: 77 
						 
						  -rwx------ 1 user group         678430 Apr 16 21:46 FTP客户端的设计与实现.pdf
						  -rwx------ 1 user group       87504927 Apr 18 22:50 VC.深入详解(孙鑫)[www.xuexi111.com].pdf
						  -rwx------ 1 user group          57344 Apr 18 05:32 腾讯电商2013实习生招聘TST推荐模板.xls
						
						 *<br>d			表示目录	
						 * <br>-			表示文件
						 * <br>rw-rw-rw-	表示权限设置
						
						dateStr:39-51
						sizeOrDir：23-38
						fileName:52-^
					*/
					
					/*********************************************************/
					byte[]names=new byte[2048];
					int bufsize=0;
					bufsize=list.read(names, 0, names.length);
					int i=0,j=0;
					while(i<bufsize){
						//字符模式为10，二进制模式为13
//						if (names[i]==10) {
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
								/**文件日期*/
								String dateStr =fileMessage.split("\\s+")[5] +" "+fileMessage.split("\\s+")[6]+" " +fileMessage.split("\\s+")[7];
//								System.out.println("sizeOrDir="+sizeOrDir);
//								System.out.println("fileName="+fileName); 
//								System.out.println("dateStr="+dateStr);
								
								FtpFile ftpFile = new FtpFile();
								// 将FTP目录信息初始化到FTP文件对象中
								ftpFile.setLastDate(dateStr);
								ftpFile.setSize(sizeOrDir);
								ftpFile.setName(fileName);
								ftpFile.setPath(pwd);
								// 将文件信息添加到表格中
								model.addRow(new Object[] { ftpFile, ftpFile.getSize(),
										dateStr });
							}
							
//							j=i+1;//上一次位置为字符模式
							j=i+2;//上一次位置为二进制模式
						}
						i=i+1;
					}
					list.close();
					
					/**********************************************************************
					//下面的方法太死了,不够灵活
					BufferedReader br = new BufferedReader(
							new InputStreamReader(list)); // 创建字符输入流
					String data = null;
					// 读取输入流中的文件目录
					while ((data = br.readLine()) != null) {
						// 创建FTP文件对象
						FtpFile ftpFile = new FtpFile();
						// 获取FTP服务器目录信息
						    String dateStr = data.substring(39, 51).trim();
							String sizeOrDir = data.substring(23, 38).trim();
							String fileName = data.substring(52, data.length())
									.trim();
							// 将FTP目录信息初始化到FTP文件对象中
							ftpFile.setLastDate(dateStr);
							ftpFile.setSize(sizeOrDir);
							ftpFile.setName(fileName);
							ftpFile.setPath(pwd);
							// 将文件信息添加到表格中
							model.addRow(new Object[] { ftpFile, ftpFile.getSize(),
									dateStr });
						}
						br.close(); // 关闭输入流
					**********************************************************************/
					
				} catch (IOException ex) {
					Logger.getLogger(FTPClientFrame.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
		};
		if (SwingUtilities.isEventDispatchThread()) // 启动线程对象
			runnable.run();
		else
			SwingUtilities.invokeLater(runnable);
	}

	
	/**
	 * 设置FTP连接，并启动下载队列线程的方法
	 */
	public void setFtpClient(FtpClient ftpClient) {
		this.ftpClient = ftpClient;
		// 以30秒为间隔与服务器保持通讯
		final Timer timer = new Timer(3000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					final FtpClient ftpClient = FtpPanel.this.ftpClient;
					if (ftpClient != null && ftpClient.serverIsOpen()) {
						ftpClient.noop();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		timer.start();
		startDownThread();
	}

	/**
	 * 刷新FTP资源管理面板的当前文件夹
	 */
	public void refreshCurrentFolder() {
		try {
			 // 获取服务器文件列表
			TelnetInputStream list = ftpClient.list();
			listFtpFiles(list); // 调用解析方法
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开始下载队列线程
	 */
	private void startDownThread() {
		if (thread != null)
			thread.stopThread();
		thread = new DownThread(this);
		thread.start();
	}

	/**
	 * 停止下载队列线程
	 */
	public void stopDownThread() {
		if (thread != null) {
			thread.stopThread();
			ftpClient = null;
		}
	}

	public String getPwd() {
		String pwd = null;
		try {
			pwd = ftpClient.pwd();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pwd;
	}

	public Queue<Object[]> getQueue() {
		return queue;
	}

	/**
	 * 清除FTP资源表格内容的方法
	 */
	public void clearTable() {
		FtpTableModel model = (FtpTableModel) ftpDiskTable.getModel();
		model.setRowCount(0);
	}
}
