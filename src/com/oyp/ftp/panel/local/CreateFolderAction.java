package com.oyp.ftp.panel.local;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

/**
 * 创建文件夹按钮的动作处理器
 */
class CreateFolderAction extends AbstractAction {
	private LocalPanel localPanel; // 本地资源管理面板的引用

	/**
	 * 构造方法
	 * 
	 * @param localPanel
	 *            本地资源面板
	 * @param name
	 *            动作的名称
	 * @param icon
	 *            动作的图标
	 */
	public CreateFolderAction(LocalPanel localPanel, String name, Icon icon) {
		super(name, icon); // 调用父类构造方法
		this.localPanel = localPanel; // 赋值本地资源管理面板的引用
	}

	/**
	 * 创建文件夹按钮的动作处理器的动作事件的方法
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// 使用输入对话框接收用户输入的文件夹名称
		String folderName = JOptionPane.showInputDialog("请输入文件夹名称：");
		if (folderName == null)
			return;
		File curFolder = null;
		// 获取本地资源表格的当前选择行号
		int selRow = localPanel.localDiskTable.getSelectedRow();
		if (selRow < 0) {
			// 创建当前文件夹对象
			curFolder = new File(localPanel.localSelFilePathLabel.getText());
		} else {
			// 获取表格选择行的第一个单元值
			Object value = localPanel.localDiskTable.getValueAt(selRow, 0);
			if (value instanceof File) { // 如果单元值是文件，则获取文件的上级文件夹
				curFolder = (File) value;
				if (curFolder.getParentFile() != null)
					curFolder = curFolder.getParentFile();
			} else
				// 否则根据界面的路径标签创建当前文件夹对象
				curFolder = new File(localPanel.localSelFilePathLabel.getText());
		}
		// 创建当前文件夹下的新文件夹对象
		File tempFile = new File(curFolder, folderName);
		if (tempFile.exists()) {// 如果存在相同文件或文件夹
			JOptionPane.showMessageDialog(localPanel, folderName
					+ "创建失败，已经存在此名称的文件夹或文件。", "创建文件夹",
					JOptionPane.ERROR_MESSAGE);// 提示用户名称已存在
			return; // 结束本方法
		}
		if (tempFile.mkdir()) // 创建文件夹
			JOptionPane.showMessageDialog(localPanel, folderName + "文件夹，创建成功。",
					"创建文件夹", JOptionPane.INFORMATION_MESSAGE);
		else
			JOptionPane.showMessageDialog(localPanel, folderName + "文件夹无法被创建。",
					"创建文件夹", JOptionPane.ERROR_MESSAGE);
		this.localPanel.refreshFolder(curFolder);// 刷新文件夹
	}
}