package com.oyp.ftp.panel.local;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

/**
 * 重命名按钮的动作处理器
 */
class RennameAction extends AbstractAction {
	private LocalPanel localPanel; // 本地资源管理面板的引用

	/**
	 * 构造方法
	 * 
	 * @param localPanel
	 *            本地资源管理面板
	 * @param name
	 *            动作处理器的名称
	 * @param icon
	 *            动作处理器的图标
	 */
	public RennameAction(LocalPanel localPanel, String name, Icon icon) {
		super(name, icon); // 调用父类的构造方法
		this.localPanel = localPanel; // 赋值本地资源管理面板的引用
	}

	/**
	 * 重命名动作的事件处理方法
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// 获取本地资源表格的选择行号
		int selRow = this.localPanel.localDiskTable.getSelectedRow();
		if (selRow < 0)
			return;
		// 获取选择行的第一个单元值
		Object value = this.localPanel.localDiskTable.getValueAt(selRow, 0);
		if (!(value instanceof File))
			return;
		// 将该单元值转换为File类的对象
		File file = (File) value;
		// 使用对话框接收用户如入的新文件名
		String fileName = JOptionPane
				.showInputDialog("请输入新文件名", file.getName());
		if (fileName == null)
			return;
		// 创建新名称的文件
		File renFile = new File(file.getParentFile(), fileName);
		boolean isRename = file.renameTo(renFile); // 将原文件重命名
		// 刷新文件夹
		this.localPanel.refreshFolder(file.getParentFile());
		if (isRename) {
			JOptionPane.showMessageDialog(this.localPanel, "重命名为" + fileName
					+ "成功。");
		} else {
			JOptionPane.showMessageDialog(this.localPanel, "无法重命名为" + fileName
					+ "。", "文件重命名", JOptionPane.ERROR_MESSAGE);
		}
	}
}