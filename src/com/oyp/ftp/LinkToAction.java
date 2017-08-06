package com.oyp.ftp;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.oyp.ftp.panel.manager.FtpLinkDialog;

/**
 * 连接到按钮的动作类
 */
class LinkToAction extends AbstractAction {
	private final FTPClientFrame client_Frame;

	/**
	 * 构造方法
	 * 
	 * @param frame
	 *            主窗体的引用
	 * @param string
	 *            动作的名称
	 * @param icon
	 *            动作的图标
	 */
	public LinkToAction(FTPClientFrame frame, String string, Icon icon) {
		super(string, icon); // 调用父类构造方法
		client_Frame = frame; // 赋值主窗体的引用
	}

	/**
	 * 处理动作的事件方法
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// 创建连接到FTP站点对话框
		FtpLinkDialog dialog = new FtpLinkDialog(this.client_Frame);
	}
}