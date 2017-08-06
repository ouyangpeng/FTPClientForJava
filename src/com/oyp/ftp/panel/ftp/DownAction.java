package com.oyp.ftp.panel.ftp;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.oyp.ftp.utils.FtpFile;

/**
 * 下载按钮的动作处理器
 */
class DownAction extends AbstractAction {
	private final FtpPanel ftpPanel;

	/**
	 * 构造方法
	 * 
	 * @param ftpPanel
	 *            - FTP资源管理面板
	 * @param name
	 *            - 动作的名称
	 * @param icon
	 *            - 动作的图标
	 */
	public DownAction(FtpPanel ftpPanel, String name, Icon icon) {
		super(name, icon); // 调用父类构造方法
		this.ftpPanel = ftpPanel; // 赋值FTP资源管理面板的引用
		setEnabled(false); // 设置动作不可用
	}

	/**
	 * 下载按钮的动作处理器动作的事件处理方法
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// 获取FTP资源表格的所有选择行
		final int[] selRows = ftpPanel.ftpDiskTable.getSelectedRows();
		if (selRows.length < 1)
			return;
		// 遍历表格的所有选择行
		for (int i = 0; i < selRows.length; i++) {
			// 获取每行的第一个单元值并转换成FtpFile类的对象
			final FtpFile file = (FtpFile) ftpPanel.ftpDiskTable.getValueAt(
					selRows[i], 0);
			if (file != null) {
				// 获取本地资源管理面板的当前文件夹
				File currentFolder = ftpPanel.frame.getLocalPanel()
						.getCurrentFolder();
				// 把FTP文件对象和本地当前文件夹对象定义成数组添加到下载队列中
				ftpPanel.queue.offer(new Object[] { file, currentFolder });
			}
		}
	}
}