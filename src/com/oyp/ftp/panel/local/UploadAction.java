package com.oyp.ftp.panel.local;

import java.util.Queue;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import com.oyp.ftp.utils.DiskFile;
import com.oyp.ftp.utils.FtpFile;

/**
 * 上传文件的动作处理器
 */
class UploadAction extends AbstractAction {
	private LocalPanel localPanel; // 本地资源管理面板的引用

	/**
	 * 构造方法
	 * 
	 * @param localPanel
	 *            本地资源管理面板
	 * @param name
	 *            动作的名称
	 * @param icon
	 *            动作的图标
	 */
	public UploadAction(LocalPanel localPanel, String name, Icon icon) {
		super(name, icon); // 调用父类构造方法
		this.localPanel = localPanel; // 赋值本地资源管理面板的引用
		setEnabled(false); // 设置按钮不可用
	}

	/**
	 * 上传文件动作的事件处理方法
	 */
	public void actionPerformed(java.awt.event.ActionEvent evt) {
		// 获取用户选择的多个文件或文件夹
		int[] selRows = this.localPanel.localDiskTable.getSelectedRows();
		if (selRows.length < 1) {
			JOptionPane.showMessageDialog(this.localPanel, "请选择上传的文件或文件夹");
			return;
		}
		// 获取FTP服务器的当前路径
		String pwd = this.localPanel.frame.getFtpPanel().getPwd();
		// 创建FTP当前路径的文件夹对象
		FtpFile ftpFile = new FtpFile("", pwd, true);
		// 遍历本地资源的表格
		for (int i = 0; i < selRows.length; i++) {
			Object valueAt = this.localPanel.localDiskTable.getValueAt(
					selRows[i], 0); // 获取表格选择行的第一列数据
			if (valueAt instanceof DiskFile) {
				final DiskFile file = (DiskFile) valueAt;
				// 获取本地面板类中的队列，该队列是LinkedList类的实例对象
				Queue<Object[]> queue = this.localPanel.queue;
				queue.offer(new Object[] { file, ftpFile });// 执行offer方法向队列尾添加对象
			}
		}
	}
}