package com.oyp.ftp.panel.manager;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.BevelBorder;

import com.oyp.ftp.FTPClientFrame;
import com.oyp.ftp.utils.SiteInfoBean;

public class FtpSiteDialog extends JDialog implements ActionListener {
	private Properties siteInfo = new Properties(); // 站点属性对象
	private JList list; // 显示站点的列表组件
	private FTPClientFrame frame; // 主窗体的引用对象
	private static final File FILE = new File("data/siteInfo.data");// 存储属性的文件对象

	public FtpSiteDialog() {
		super();
		initComponents();
	}

	public FtpSiteDialog(FTPClientFrame frame) {
		super(frame);
		this.frame = frame;
		initComponents();
	}

	/**
	 * 初始化窗体界面的方法
	 */
	public void initComponents() {
		loadSiteProperties();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setTitle("FTP站点管理");
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setHgap(5);
		setLayout(borderLayout);

		list = new JList();
		final BevelBorder bevelBorder = new BevelBorder(BevelBorder.LOWERED);
		list.setBorder(bevelBorder);
		loadSiteList(); // 装载站点数据
		JScrollPane scrollPanel = new JScrollPane(list);
		add(scrollPanel, CENTER);

		JPanel controlPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(controlPanel, BoxLayout.Y_AXIS);
		controlPanel.setLayout(boxLayout);
		JButton addButton = new JButton("添加");
		addButton.setActionCommand("add");
		addButton.addActionListener(this);
		JButton editButton = new JButton("编辑");
		editButton.setActionCommand("edit");
		editButton.addActionListener(this);
		JButton delButton = new JButton("删除");
		delButton.setActionCommand("del");
		delButton.addActionListener(this);
		JButton linkButton = new JButton("连接");
		linkButton.setActionCommand("link");
		linkButton.addActionListener(this);
		controlPanel.add(linkButton);
		controlPanel.add(addButton);
		controlPanel.add(editButton);
		controlPanel.add(delButton);
		add(controlPanel, EAST);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenSize.width - 200) / 2, (screenSize.height - 430) / 2,
				200, 430);
		setVisible(true);
	}

	/**
	 * 装载站点数据的方法
	 */
	public void loadSiteList() {
		Enumeration<Object> keys = siteInfo.keys(); // 获取属性集合的键值集合
		DefaultListModel model = new DefaultListModel(); // 创建列表组件数据模型
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement(); // 获取每个键值
			String value = siteInfo.getProperty(key); // 获取每个键值的内容
			// 使用键值和内容创建站点信息Bean
			SiteInfoBean siteInfoBean = new SiteInfoBean(key, value);
			model.addElement(siteInfoBean); // 将站点信息对象添加到列表组件的模型中
		}
		list.setModel(model); // 设置列表组件使用创建的模型
	}

	/**
	 * 装载站点属性文件的方法
	 */
	private void loadSiteProperties() {
		try {
			if (!FILE.exists()) { // 如果属性文件不存在
				FILE.getParentFile().mkdirs(); // 创建属性文件的文件夹
				FILE.createNewFile(); // 创建新的属性文件
			}
			InputStream is = new FileInputStream(FILE); // 创建文件输入流
			siteInfo.load(is); // 装载属性文件
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 4个维护按钮的事件处理方法
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand(); // 获取单击的维护按钮
		if (command.equals("add")) { // 如果是添加按钮
			SiteDialog dialog = new SiteDialog(this); // 显示站点对话框
		}
		// 获取列表组件选择的站点JavaBean对象
		SiteInfoBean bean = (SiteInfoBean) list.getSelectedValue();
		if (bean == null) { // 如果站点对象为NULL
			return; // 结束方法执行
		}
		if (command.equals("link")) { // 如果单击的是连接按钮
			frame.setLinkInfo(bean); // 调用setLinkInfo()方法
			dispose(); // 关闭FTP站点管理对话框
		}
		if (command.equals("edit")) { // 如果是编辑按钮
			SiteDialog dialog = new SiteDialog(this, bean); // 显示站点对话框进行编辑
		}
		if (command.equals("del")) { // 如果是删除按钮
			delSite(bean); // 调用delSite()方法
		}
	}

	/**
	 * 添加站点信息的方法
	 */
	public void addSite(SiteInfoBean bean) {
		siteInfo.setProperty(bean.getId() + "", bean.getSiteName() + ","
				+ bean.getServer() + "," + bean.getPort() + ","
				+ bean.getUserName());
		try {
			FileOutputStream out = new FileOutputStream(FILE);
			siteInfo.store(out, "FTP站点数据");
			loadSiteList();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除FTP站点的方法
	 */
	public void delSite(SiteInfoBean bean) {
		// 从站点属性集合对象中移除指定ID编号的站点属性
		siteInfo.remove(bean.getId());
		try {
			// 获取站点属性文件的输出流
			FileOutputStream out = new FileOutputStream(FILE);
			siteInfo.store(out, "FTP站点数据"); // 调用store方法存储站点属性
			loadSiteList(); // 重新装载站点列表
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
