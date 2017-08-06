package com.oyp.ftp;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import com.oyp.ftp.panel.ftp.FtpPanel;
import com.oyp.ftp.panel.local.LocalPanel;
import com.oyp.ftp.panel.manager.FtpSiteDialog;
import com.oyp.ftp.panel.queue.DownloadPanel;
import com.oyp.ftp.panel.queue.QueuePanel;
import com.oyp.ftp.panel.queue.UploadPanel;
import com.oyp.ftp.utils.FtpClient;
import com.oyp.ftp.utils.SiteInfoBean;
import com.sun.java.swing.plaf.nimbus.*;

public class FTPClientFrame extends javax.swing.JFrame {
	FtpClient ftpClient;
	private JPasswordField PassField;
	private JButton cutLinkButton;
	FtpPanel ftpPanel;
	LocalPanel localPanel;
	private JTextField portTextField;
	private JTextField serverTextField;
	private JTextField userTextField;
	private QueuePanel queuePanel;
	private UploadPanel uploadPanel;
	private DownloadPanel downloadPanel;
	private JSplitPane jSplitPane1;
	private JButton linkButton;
	private final LinkToAction LINK_TO_ACTION; // 连接到 按钮的动作处理器
	private final CutLinkAction CUT_LINK_ACTION; // 断开 按钮的动作处理器
	private SystemTray systemTray;
	private JToggleButton shutdownButton;
	private final ImageIcon icon = new ImageIcon(getClass().getResource(
			"/com/oyp/ftp/res/trayIcon.png"));

	public FTPClientFrame() {
		LINK_TO_ACTION = new LinkToAction(this, "连接到", null);
		CUT_LINK_ACTION = new CutLinkAction(this, "断开", null);
		initComponents();
		initSystemTray();
	}

	/**
	 * 初始化系统托盘的方法
	 */
	private void initSystemTray() {
		if (SystemTray.isSupported())
			systemTray = SystemTray.getSystemTray();
		TrayIcon trayIcon = new TrayIcon(icon.getImage());
		PopupMenu popupMenu = new PopupMenu("托盘菜单");

		// 创建显示主窗体菜单项
		MenuItem showMenuItem = new MenuItem("显示主窗体");
		showMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FTPClientFrame.this.setExtendedState(JFrame.NORMAL);
				FTPClientFrame.this.setVisible(true);
			}
		});

		// 创建退出菜单项
		MenuItem exitMenuItem = new MenuItem("退出");
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		popupMenu.add(showMenuItem);
		popupMenu.addSeparator();
		popupMenu.add(exitMenuItem);
		trayIcon.setPopupMenu(popupMenu);
		try {
			systemTray.add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化程序界面的方法
	 */
	private void initComponents() {
		setIconImage(icon.getImage());
		java.awt.GridBagConstraints gridBagConstraints;

		JPanel jPanel1 = new JPanel();
		JToolBar jToolBar1 = new JToolBar();
		JButton linkTo = new JButton();
		cutLinkButton = new JButton();
		JPanel jPanel4 = new JPanel();
		JLabel jLabel1 = new JLabel();
		serverTextField = new JTextField();
		JLabel jLabel2 = new JLabel();
		userTextField = new JTextField();
		JLabel jLabel3 = new JLabel();
		PassField = new JPasswordField();
		JLabel jLabel6 = new JLabel();
		portTextField = new JTextField();
		linkButton = new JButton();
		JSplitPane jSplitPane2 = new JSplitPane();
		jSplitPane1 = new JSplitPane();
		ftpPanel = new FtpPanel(this); // 初始化FTP远程资源面板
		localPanel = new LocalPanel(this); // 初始化本地资源管理面板
		uploadPanel = new UploadPanel(); // 初始化上传队列面板
		downloadPanel = new DownloadPanel(); // 初始化下载队列面板
		queuePanel = new QueuePanel(this); // 初始化队列面板

		JTabbedPane jTabbedPane1 = new JTabbedPane();
		JMenuBar MenuBar = new JMenuBar();
		JMenu fileMenu = new JMenu();
		JMenuItem ftpManageMenuItem = new JMenuItem();
		JSeparator jSeparator1 = new JSeparator();
		JMenuItem linkToMenuItem = new javax.swing.JMenuItem();
		JMenuItem cutMenuItem = new javax.swing.JMenuItem();
		JSeparator jSeparator2 = new javax.swing.JSeparator();
		JMenuItem exitMenuItem = new javax.swing.JMenuItem();
		JMenuItem uploadMenuItem = new javax.swing.JMenuItem();
		JSeparator jSeparator3 = new javax.swing.JSeparator();
		JMenuItem createFolderMenuItem = new javax.swing.JMenuItem();
		JMenuItem renameMenuItem = new javax.swing.JMenuItem();
		JMenuItem delMenuItem = new javax.swing.JMenuItem();
		JMenu ftpMenu = new javax.swing.JMenu();
		JMenuItem downMenuItem = new javax.swing.JMenuItem();
		JSeparator jSeparator6 = new javax.swing.JSeparator();
		JMenuItem ftpDelMenuItem = new javax.swing.JMenuItem();
		JMenuItem ftpRenameMenuItem = new javax.swing.JMenuItem();
		JMenuItem newFolderMenuItem = new javax.swing.JMenuItem();
		JMenu helpMenu = new javax.swing.JMenu();
		JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
		JMenuItem bugMenuItem = new javax.swing.JMenuItem();

//		setTitle("基于Socket的FTP软件Java实现");
		setTitle("Java语言实现简单FTP软件__欧阳鹏设计");
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				formWindowOpened(evt);
			}

			public void windowIconified(final WindowEvent e) {
				setVisible(false);
			}
		});
		addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(java.awt.event.ComponentEvent evt) {
				formComponentResized(evt);
			}
		});
		getContentPane().setLayout(new java.awt.GridBagLayout());

		jPanel1.setLayout(new java.awt.GridLayout(0, 1));

		jToolBar1.setRollover(true);
		jToolBar1.setFloatable(false);

		linkTo.setText("连接到");
		linkTo.setFocusable(false);
		linkTo.setAction(LINK_TO_ACTION);
		jToolBar1.add(linkTo);

		cutLinkButton.setText("断开");
		cutLinkButton.setEnabled(false);
		cutLinkButton.setFocusable(false);
		cutLinkButton.setAction(CUT_LINK_ACTION);
		jToolBar1.add(cutLinkButton);

		jPanel1.add(jToolBar1);
		
		shutdownButton = new JToggleButton();
		shutdownButton.setText("自动关机");
		shutdownButton.setToolTipText("队列完成后，自动关闭计算机");
		shutdownButton.setFocusable(false);
		jToolBar1.add(shutdownButton);

		jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4,
				javax.swing.BoxLayout.LINE_AXIS));

		jLabel1.setText("主机地址：");
		jPanel4.add(jLabel1);

		serverTextField.setText("192.168.1.100");
		serverTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				LinkFTPKeyPressed(evt);
			}
		});
		jPanel4.add(serverTextField);

		jLabel2.setText("用户名：");
		jPanel4.add(jLabel2);

		userTextField.setText("oyp");
		userTextField.setMaximumSize(new java.awt.Dimension(200, 2147483647));
		userTextField.setPreferredSize(new java.awt.Dimension(100, 21));
		userTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				LinkFTPKeyPressed(evt);
			}
		});
		jPanel4.add(userTextField);

		jLabel3.setText("密码：");
		jPanel4.add(jLabel3);

		PassField.setText("oyp");
		PassField.setMaximumSize(new java.awt.Dimension(200, 2147483647));
		PassField.setPreferredSize(new java.awt.Dimension(100, 21));
		PassField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				LinkFTPKeyPressed(evt);
			}
		});
		jPanel4.add(PassField);

		jLabel6.setText("端口：");
		jPanel4.add(jLabel6);

		portTextField.setText("21");
		portTextField.setMaximumSize(new java.awt.Dimension(100, 2147483647));
		portTextField.setPreferredSize(new java.awt.Dimension(50, 21));
		portTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				LinkFTPKeyPressed(evt);
			}
		});
		jPanel4.add(portTextField);

		linkButton.setText("连接");
		linkButton.setFocusable(false);
		linkButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		linkButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		linkButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				linkButtonActionPerformed(evt);
			}
		});
		jPanel4.add(linkButton);

		jPanel1.add(jPanel4);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;	//指定包含组件的显示区域开始边的单元格，其中行的第一个单元格为 gridx=0。
		gridBagConstraints.gridy = 0;	//指定位于组件显示区域的顶部的单元格，其中最上边的单元格为 gridy=0。
		//当组件的显示区域大于它所请求的显示区域的大小时使用此字段。
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;   //在水平方向而不是垂直方向上调整组件大小。
		gridBagConstraints.weightx = 1.0;	//指定如何分布额外的水平空间。
		getContentPane().add(jPanel1, gridBagConstraints);

		jSplitPane2.setBorder(null);
		jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
		jSplitPane2.setResizeWeight(1.0);
		jSplitPane2.setContinuousLayout(true);

		jSplitPane1.setDividerLocation(400);
		jSplitPane1.setDividerSize(10);
		jSplitPane1.setOneTouchExpandable(true);
		jSplitPane1.setRightComponent(ftpPanel);
		jSplitPane1.setLeftComponent(localPanel);

		jSplitPane2.setLeftComponent(jSplitPane1);

		jTabbedPane1.setMinimumSize(new java.awt.Dimension(40, 170));

		jTabbedPane1.addTab("队列", queuePanel);// 添加队列面板
		jTabbedPane1.addTab("上传队列", uploadPanel);// 添加上传面板
		jTabbedPane1.addTab("下载队列", downloadPanel);// 添加下载面板

		jSplitPane2.setBottomComponent(jTabbedPane1);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;	//在水平方向和垂直方向上同时调整组件大小。
		gridBagConstraints.weightx = 1.0;	//指定如何分布额外的水平空间。
		gridBagConstraints.weighty = 1.0;	//指定如何分布额外的垂直空间。
		getContentPane().add(jSplitPane2, gridBagConstraints);

		fileMenu.setMnemonic('f');
		fileMenu.setText("站点(F)");

		ftpManageMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_S,
				java.awt.event.InputEvent.CTRL_MASK));
		ftpManageMenuItem.setText("FTP站点管理(S)");
		ftpManageMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				System.out.println("action");
				FtpSiteDialog dialog = new FtpSiteDialog(FTPClientFrame.this);
				dialog.setVisible(true);
			}
		});
		fileMenu.add(ftpManageMenuItem);
		fileMenu.add(jSeparator1);

		linkToMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_C,
				java.awt.event.InputEvent.CTRL_MASK));
		linkToMenuItem.setText("连接到...(C)");
		linkToMenuItem.setAction(LINK_TO_ACTION);
		fileMenu.add(linkToMenuItem);

		cutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_Z,
				java.awt.event.InputEvent.CTRL_MASK));
		cutMenuItem.setText("断开(Z)");
		cutMenuItem.setAction(CUT_LINK_ACTION);
		fileMenu.add(cutMenuItem);
		fileMenu.add(jSeparator2);

		exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_X,
				java.awt.event.InputEvent.CTRL_MASK));
		exitMenuItem.setText("退出(X)");
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(exitMenuItem);

		MenuBar.add(fileMenu);

		JMenu localMenu = new JMenu();
		localMenu.setMnemonic('l');
		localMenu.setText("本地(L)");

		uploadMenuItem.setMnemonic('U');
		uploadMenuItem.setText("上传(U)");
		uploadMenuItem.setAction(localPanel.getActionMap().get("uploadAction"));

		localMenu.add(uploadMenuItem);
		localMenu.add(jSeparator3);

		createFolderMenuItem.setMnemonic('C');
		createFolderMenuItem.setText("新建文件夹(C)");
		createFolderMenuItem.setAction(localPanel.getActionMap().get(
				"createFolderAction"));
		localMenu.add(createFolderMenuItem);

		renameMenuItem.setMnemonic('R');
		renameMenuItem.setText("重命名(R)");
		renameMenuItem.setAction(localPanel.getActionMap().get("renameAction"));
		localMenu.add(renameMenuItem);

		delMenuItem.setMnemonic('D');
		delMenuItem.setText("删除(D)");
		delMenuItem.setAction(localPanel.getActionMap().get("delAction"));
		localMenu.add(delMenuItem);

		JMenuItem localrefreshMenuItem = new JMenuItem();
		localrefreshMenuItem.setMnemonic('R');
		localrefreshMenuItem.setText("刷新(R)");
		localrefreshMenuItem.setAction(localPanel.getActionMap().get(
				"refreshAction"));
		localMenu.add(localrefreshMenuItem);

		MenuBar.add(localMenu);

		ftpMenu.setMnemonic('r');
		ftpMenu.setText("远程(R)");

		downMenuItem.setMnemonic('U');
		downMenuItem.setText("下载(U)");
		downMenuItem.setAction(ftpPanel.getActionMap().get("downAction"));
		ftpMenu.add(downMenuItem);
		ftpMenu.add(jSeparator6);

		ftpDelMenuItem.setMnemonic('D');
		ftpDelMenuItem.setText("删除(D)");
		ftpDelMenuItem.setAction(ftpPanel.getActionMap().get("delAction"));
		ftpMenu.add(ftpDelMenuItem);

		ftpRenameMenuItem.setMnemonic('R');
		ftpRenameMenuItem.setText("重命名(R)");
		ftpRenameMenuItem
				.setAction(ftpPanel.getActionMap().get("renameAction"));
		ftpMenu.add(ftpRenameMenuItem);

		newFolderMenuItem.setMnemonic('C');
		newFolderMenuItem.setText("新建文件夹(C)");
		newFolderMenuItem.setAction(ftpPanel.getActionMap().get(
				"createFolderAction"));
		ftpMenu.add(newFolderMenuItem);

		JMenuItem refreshMenuItem = new JMenuItem();
		refreshMenuItem.setMnemonic('R');
		refreshMenuItem.setText("刷新(R)");
		refreshMenuItem.setAction(ftpPanel.getActionMap().get("refreshAction"));
		ftpMenu.add(refreshMenuItem);

		MenuBar.add(ftpMenu);

		helpMenu.setText("帮助(H)");
		aboutMenuItem.setMnemonic('a');
		aboutMenuItem.setText("关于(A)");
		aboutMenuItem.addActionListener(new AboutItemAction(this));
		helpMenu.add(aboutMenuItem);

		bugMenuItem.setMnemonic('u');
		bugMenuItem.setText("错误报告(U)");
		bugMenuItem.addActionListener(new BugItemAction());
		helpMenu.add(bugMenuItem);

		MenuBar.add(helpMenu);

		setJMenuBar(MenuBar);

		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit()
				.getScreenSize();
		setBounds((screenSize.width - 800) / 2, (screenSize.height - 600) / 2,
				800, 700);
	}

	public JToggleButton getShutdownButton() {
		return shutdownButton;
	}

	/**
	 * 窗体装载的事件处理方法
	 */
	private void formWindowOpened(java.awt.event.WindowEvent evt) {
		jSplitPane1.setDividerLocation(0.50);
		localPanel.getLocalDiskComboBox().setSelectedIndex(1);
		localPanel.getLocalDiskComboBox().setSelectedIndex(0);
	}

	/**
	 * 窗体大小调整的事件处理方法
	 */
	private void formComponentResized(java.awt.event.ComponentEvent evt) {
		jSplitPane1.setDividerLocation(0.50);
	}

	/**
	 * 连接按钮的事件处理方法
	 */
	private void linkButtonActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			String server = serverTextField.getText(); // 获取服务器地址
			if (server == null) {
				return;
			}
			String portStr = portTextField.getText(); // 获取端口号
			if (portStr == null) {
				portStr = "21";
			}
			int port = Integer.parseInt(portStr.trim());
			String userStr = userTextField.getText(); // 获取用户名
			userStr = userStr == null ? "" : userStr.trim();
			String passStr = PassField.getText(); // 获取密码
			passStr = passStr == null ? "" : passStr.trim();
			cutLinkButton.doClick();
			ftpClient = new FtpClient();
			ftpClient.openServer(server.trim(), port); // 连接服务器
			ftpClient.login(userStr, passStr); // 登录服务器
			ftpClient.binary(); // 使用二进制传输模式
			if (ftpClient.serverIsOpen()) { // 如果连接成功
				CUT_LINK_ACTION.setEnabled(true); // 设置断开按钮可用
			} else { // 否则
				CUT_LINK_ACTION.setEnabled(false); // 设置断开按钮不可用
				return; // 并结束事件处理
			}
			// 设置本地资源管理面板的FTP连接信息
			localPanel.setFtpClient(server, port, userStr, passStr);
			// 设置上传按钮可用
			localPanel.getActionMap().get("uploadAction").setEnabled(true);
			ftpPanel.setFtpClient(ftpClient);// 设置FTP资源管理面板的FTP连接信息
			// 设置下载按钮可用
			ftpPanel.getActionMap().get("downAction").setEnabled(true);
			ftpPanel.refreshCurrentFolder();// 刷新FTP资源管理面板的当前文件夹
			queuePanel.startQueue(); // 启动任务队列线程
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 连接FTP相关的文本框 和密码框的回车事件
	 */
	private void LinkFTPKeyPressed(java.awt.event.KeyEvent evt) {
		if (evt.getKeyChar() == '\n') {
			linkButton.doClick();
		}
	}

	public LocalPanel getLocalPanel() {
		return localPanel;
	}

	public FtpPanel getFtpPanel() {
		return ftpPanel;
	}

	public QueuePanel getQueuePanel() {
		return queuePanel;
	}

	public UploadPanel getUploadPanel() {
		return uploadPanel;
	}

	public DownloadPanel getDownloadPanel() {
		return downloadPanel;
	}

	public FtpClient getFtpClient() {
		return ftpClient;
	}

	/**
	 * 设置FTP连接信息的方法，由FTP站点管理器调用
	 */
	public void setLinkInfo(SiteInfoBean bean) {
		serverTextField.setText(bean.getServer()); // 设置主机地址
		portTextField.setText(bean.getPort() + ""); // 设置端口号
		userTextField.setText(bean.getUserName()); // 设置用户名
		PassField.setText(""); // 密码清空
		PassField.requestFocus(); // 密码框请求焦点
	}
}
