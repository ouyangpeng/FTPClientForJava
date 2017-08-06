package com.oyp.ftp.panel.local;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import com.oyp.ftp.utils.DiskFile;

/**
 * 删除本地文件的动作处理器
 */
class DelFileAction extends AbstractAction {
	private LocalPanel localPanel; // 本地资源管理面板的引用对象

	/**
	 * 动作处理器的构造方法
	 * 
	 * @param localPanel
	 *            本地资源管理面板
	 * @param name
	 *            动作名称
	 * @param icon
	 *            动作的图标
	 */
	public DelFileAction(LocalPanel localPanel, String name, Icon icon) {
		super(name, icon); // 调用父类的构造方法
		this.localPanel = localPanel; // 赋值本地资源管理面板的引用
	}

	/**
	 * 删除本地文件的动作处理器的处理动作事件的方法
	 */
	public void actionPerformed(ActionEvent e) {
		// 获取表格选择的所有行
		final int[] selRows = this.localPanel.localDiskTable.getSelectedRows();
		if (selRows.length < 1) // 如果没有选择表格内容
			return; // 结束该方法
		int confirmDialog = JOptionPane.showConfirmDialog(localPanel,
				"确定要执行删除吗？"); // 用户确认是否删除
		if (confirmDialog == JOptionPane.YES_OPTION) { // 如果用于同意删除
			Runnable runnable = new Runnable() { // 创建线程
				/**
				 * 删除文件的递归方法
				 * 
				 * @param file
				 *            要删除的文件对象
				 */
				private void delFile(File file) {
					try {
						if (file.isFile()) { // 如果删除的是文件
							boolean delete = file.delete(); // 调用删该文件的方法
							if (!delete) {
								JOptionPane.showMessageDialog(localPanel, file
										.getAbsoluteFile()
										+ "文件无法删除。", "删除文件",
										JOptionPane.ERROR_MESSAGE);
								return;
							}
						} else if (file.isDirectory()) { // 如果删除的是文件夹
							File[] listFiles = file.listFiles();// 获取该文件夹的文件列表
							if (listFiles.length > 0) {
								for (File subFile : listFiles) {
									delFile(subFile); // 调用递归方法删除该列表的所有文件或文件夹
								}
							}
							boolean delete = file.delete();// 最后删除该文件夹
							if (!delete) { // 如果成功删除
								JOptionPane.showMessageDialog(localPanel, file
										.getAbsoluteFile()
										+ "文件夹无法删除。", "删除文件",
										JOptionPane.ERROR_MESSAGE);
								return; // 返回方法的调用处
							}
						}
					} catch (Exception ex) {
						Logger.getLogger(LocalPanel.class.getName()).log(
								Level.SEVERE, null, ex);
					}
				}

				/**
				 * 线程的主体方法
				 * 
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					File parent = null;
					// 遍历表格的选择内容
					for (int i = 0; i < selRows.length; i++) {
						// 获取每个选择行的第一列单元内容
						Object value = DelFileAction.this.localPanel.localDiskTable
								.getValueAt(selRows[i], 0);
						// 如果该内容不是DiskFile类的实例对象
						if (!(value instanceof DiskFile))
							continue; // 结束本次循环
						DiskFile file = (DiskFile) value;
						if (parent == null)
							parent = file.getParentFile(); // 获取选择文件的上级文件夹
						if (file != null) {
							delFile(file); // 调用递归方法删除选择内容
						}
					}
					// 调用refreshFolder方法刷新当前文件夹
					DelFileAction.this.localPanel.refreshFolder(parent);
					JOptionPane.showMessageDialog(localPanel, "删除成功。");
				}
			};
			new Thread(runnable).start(); // 创建并启动这个线程
		}
	}
}