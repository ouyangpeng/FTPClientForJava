package com.oyp.ftp.panel.manager;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.oyp.ftp.utils.SiteInfoBean;

/**
 *  创建维护FTP站点的对话框
 */
public class SiteDialog extends JDialog implements ActionListener {

	private FtpSiteDialog dialog; // 保存父窗体的引用对象
	private JTextField siteNameField; // 站点名称文本框组件
	private JTextField siteAddressField;// 站点地址文本框组件
	private JTextField portField; // 端口号文本框组件
	private JTextField loginUserField; // 登录用户文本框组件
	private SiteInfoBean siteBean; // 站点信息的JavaBean对象

	/**
	 * 创建添加FTP站点对话框
	 * 
	 * @param frame
	 *            对话框的父窗体
	 */
	public SiteDialog(FtpSiteDialog frame) {
		super(frame); // 调用父类的构造方法
		dialog = frame; // 赋值父窗体对象
		initComponents(); // 调用初始化对话框界面的方法
	}

	/**
	 * 创建编辑FTP站点对话框
	 * 
	 * @param frame
	 *            父窗体的引用
	 * @param siteBean
	 *            FTP站点信息的JavaBean
	 */
	public SiteDialog(FtpSiteDialog frame, SiteInfoBean siteBean) {
		this(frame);
		dialog = frame; // 赋值父窗体引用对象
		this.siteBean = siteBean; // 赋值FTP站点引用对象
		initInput(); // 初始化界面组件的内容
		setTitle("编辑FTP站点"); // 设置对话框的标题
	}

	/**
	 * 创建编辑FTP站点对话框
	 * 
	 * @param frame
	 *            父窗体的引用
	 * @param bean
	 *            FTP站点信息的JavaBean
	 */
	public SiteDialog(FtpLinkDialog frame, SiteInfoBean bean) {
		super(frame); // 调用父类构造方法
		this.siteBean = bean; // 赋值FTP站点信息的引用
		initComponents(); // 调用初始化程序界面的方法
		initInput(); // 初始化界面所有文本框组件的内容
		setReadOnly(); // 调用设置界面组件只读的方法
		setTitle("查看FTP站点"); // 设置对话框的标题
	}

	/**
	 * 设置窗体所有文本框组件只读的方法
	 */
	private void setReadOnly() {
		siteNameField.setEditable(false); // 设置站点名称文本框只读
		siteAddressField.setEditable(false); // 设置FTP地址只读
		loginUserField.setEditable(false); // 设置登录用户名只读
		portField.setEditable(false); // 设置端口号文本框只读
	}

	/**
	 * 初始化窗体界面的方法
	 */
	public void initComponents() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setTitle("添加FTP站点");
		JPanel content = new JPanel();
		content.setLayout(new GridLayout(0, 1, 4, 3));

		content.add(new JLabel("站点名称："));
		siteNameField = new JTextField();
		content.add(siteNameField);
		content.add(new JLabel("站点地址："));
		siteAddressField = new JTextField();
		content.add(siteAddressField);
		content.add(new JLabel("端口号："));
		portField = new JTextField();
		portField.setText("21");
		content.add(portField);
		content.add(new JLabel("登录用户："));
		loginUserField = new JTextField();
		content.add(loginUserField);

		JPanel panel = new JPanel();
		final FlowLayout layout = new FlowLayout(FlowLayout.CENTER);
		layout.setHgap(20);
		panel.setLayout(layout);
		final Insets insets = new Insets(0, 8, 0, 8);
		JButton okButton = new JButton("确定");
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		JButton cancelButton = new JButton("重置");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		cancelButton.setMargin(insets);
		okButton.setMargin(insets);
		panel.add(okButton);
		panel.add(cancelButton);
		content.add(panel);
		content.setBorder(new EmptyBorder(4, 6, 4, 6));
		add(content);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenSize.width - 200) / 2, (screenSize.height - 400) / 2,
				317, 383);
		setVisible(true);
	}

	/**
	 * 界面按钮的事件处理方法
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand(); // 获取按钮的command属性
		if (command.equals("ok")) { // 如果是确定按钮
			try {
				if (dialog == null) {
					dispose();
					return;
				}
				// 获取界面所有文本框的内容
				String siteName = siteNameField.getText().trim();
				String server = siteAddressField.getText().trim();
				String userName = loginUserField.getText().trim();
				String portStr = portField.getText().trim();
				// 判断是否填写了全部文本框
				if (siteName.isEmpty() || server.isEmpty()
						|| userName.isEmpty() || portStr.isEmpty()) {
					JOptionPane.showMessageDialog(this, "请填写全部信息");
					return;
				}
				int port = Integer.valueOf(portStr);
				// 创建FTP站点信息的JavaBean对象
				SiteInfoBean bean = new SiteInfoBean(siteName, server, port,
						userName);
				// 如果对话框的siteBean不为空
				if (siteBean != null)
					bean.setId(siteBean.getId()); // 设置FTP站点的ID编号
				dialog.addSite(bean); // 调用父窗体的 addSite方法添加站点
				dialog.loadSiteList(); // 调用父窗体的loadSiteList方法重载站点列表
				dispose();
			} catch (NullPointerException ex) {
				ex.printStackTrace();
				return;
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "请正确填写端口号信息");
				ex.printStackTrace();
				return;
			}
		}
		if (command.equals("cancel")) { // 如果是重置按钮
			if (siteBean == null) // 如果对话框的siteBean属性为空
				clearInput(); // 调用清除文本框内容的方法
			else
				// 否则
				initInput(); // 初始化界面文本框内容
		}
	}

	/**
	 * 清除界面所有文本框内容的方法
	 */
	private void clearInput() {
		siteNameField.setText(""); // 清除站点名称
		siteAddressField.setText(""); // 清除主机地址
		loginUserField.setText(""); // 清除登录用户
		portField.setText(""); // 清除端口号
	}

	/**
	 * 初始化界面组件内容的方法
	 */
	private void initInput() {
		siteNameField.setText(siteBean.getSiteName()); // 设置站点名称
		siteAddressField.setText(siteBean.getServer()); // 设置FTP地址
		loginUserField.setText(siteBean.getUserName()); // 设置登录用户
		portField.setText(siteBean.getPort() + ""); // 设置端口号
	}
}
