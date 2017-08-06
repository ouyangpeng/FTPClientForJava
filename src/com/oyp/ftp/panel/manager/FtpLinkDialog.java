package com.oyp.ftp.panel.manager;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import com.oyp.ftp.FTPClientFrame;
import com.oyp.ftp.utils.SiteInfoBean;

/**
 *  连接到FTP站点的对话框
 */
public class FtpLinkDialog extends JDialog implements ActionListener {
	private Properties siteInfo = new Properties();
	private JList list;
	private FTPClientFrame frame;
	private static final File FILE = new File("data/siteInfo.data");

	public FtpLinkDialog() {
		super();
		initComponents();
	}

	public FtpLinkDialog(FTPClientFrame frame) {
		super(frame);
		this.frame = frame;
		initComponents();
	}

	/**
	 * 初始化窗体界面的方法
	 */
	public void initComponents() {
		loadSiteProperties();   //装载站点数据文件
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setTitle("连接到FTP站点对话框");
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
		JButton linkButton = new JButton("连接");
		linkButton.setActionCommand("link");
		linkButton.addActionListener(this);
		JButton lookButton = new JButton("查看");
		lookButton.setActionCommand("look");
		lookButton.addActionListener(this);
		controlPanel.add(linkButton);
		controlPanel.add(lookButton);
		add(controlPanel, EAST);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenSize.width - 200) / 2, (screenSize.height - 400) / 2,
				200, 400);
		setVisible(true);
	}

	/**
	 * 装载站点数据的方法
	 */
	public void loadSiteList() {
		Enumeration<Object> keys = siteInfo.keys();
		DefaultListModel model = new DefaultListModel();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = siteInfo.getProperty(key);
			model.addElement(new SiteInfoBean(key, value));
		}
		list.setModel(model);
	}

	/**
	 * 装载站点数据文件的方法
	 */
	private void loadSiteProperties() {
		try {
			if (!FILE.exists()) {
				FILE.getParentFile().mkdirs();
				FILE.createNewFile();
			}
			InputStream is = new FileInputStream(FILE);
			siteInfo.load(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		SiteInfoBean bean = (SiteInfoBean) list.getSelectedValue();
		if (bean == null) {
			return;
		}
		if (command.equals("link")) {
			frame.setLinkInfo(bean); //设置FTP连接信息的方法，由FTP站点管理器调用
			dispose();
		}
		if (command.equals("look")) {
			SiteDialog dialog = new SiteDialog(this, bean);
		}
	}
}
