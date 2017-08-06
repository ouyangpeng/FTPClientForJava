package com.oyp.ftp.panel.local;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableStringConverter;

import com.oyp.ftp.FTPClientFrame;
import com.oyp.ftp.panel.FTPTableCellRanderer;
import com.oyp.ftp.panel.ftp.TableConverter;
import com.oyp.ftp.utils.DiskFile;
public class LocalPanel extends javax.swing.JPanel {

	Queue<Object[]> queue = new LinkedList<Object[]>();
	private UploadThread uploadThread = null;
	private Desktop desktop = null;
	private javax.swing.JButton createFolderButton;
	private javax.swing.JButton delButton;
	private javax.swing.JScrollPane scrollPane;
	private javax.swing.JToolBar.Separator jSeparator1;
	private javax.swing.JToolBar toolBar;
	private javax.swing.JComboBox localDiskComboBox;
	javax.swing.JTable localDiskTable;
	javax.swing.JLabel localSelFilePathLabel;
	private javax.swing.JButton renameButton;
	private javax.swing.JButton uploadButton;
	private TableRowSorter<TableModel> sorter;
	FTPClientFrame frame = null;

	public LocalPanel() {
		initComponents();
	}

	public LocalPanel(FTPClientFrame client_Frame) {
		frame = client_Frame;
		if (Desktop.isDesktopSupported()) {
			desktop = Desktop.getDesktop();
		}
		initComponents();
	}

	/**
	 * 界面布局与初始化方法
	 */
	private void initComponents() {
		ActionMap actionMap = getActionMap();
		actionMap.put("delAction", new DelFileAction(this, "删除", null));
		actionMap.put("renameAction", new RennameAction(this, "重命名", null));
		actionMap.put("createFolderAction", new CreateFolderAction(this,
				"新建文件夹", null));
		actionMap.put("uploadAction", new UploadAction(this, "上传", null));
		actionMap.put("refreshAction", new RefreshAction(this, "刷新", null));

		java.awt.GridBagConstraints gridBagConstraints;

		toolBar = new javax.swing.JToolBar();
		delButton = new javax.swing.JButton();
		renameButton = new javax.swing.JButton();
		createFolderButton = new javax.swing.JButton();
		uploadButton = new javax.swing.JButton();
		jSeparator1 = new javax.swing.JToolBar.Separator();
		localDiskComboBox = new javax.swing.JComboBox();
		localDiskComboBox.setPreferredSize(new Dimension(100, 25));
		scrollPane = new javax.swing.JScrollPane();
		localDiskTable = new javax.swing.JTable();
		localDiskTable.setDragEnabled(true);
		localSelFilePathLabel = new javax.swing.JLabel();
		/**
		 *  向现有边框添加一个标题，使其具有指定的位置和默认字体和文本颜色（由当前外观确定）。
		 *  TitledBorder.CENTER: 将标题文本置于边框线的中心。
		 *  TitledBorder.ABOVE_TOP: 将标题置于边框顶端线的上部。
		 */
		setBorder(javax.swing.BorderFactory.createTitledBorder(null, "本地",
				javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.ABOVE_TOP));
		setLayout(new java.awt.GridBagLayout());

		toolBar.setRollover(true);
		toolBar.setFloatable(false);

		delButton.setText("删除");
		delButton.setFocusable(false);
		delButton.setAction(actionMap.get("delAction"));
		toolBar.add(delButton);

		renameButton.setText("重命名");
		renameButton.setFocusable(false);
		renameButton.setAction(actionMap.get("renameAction"));
		toolBar.add(renameButton);

		createFolderButton.setText("新文件夹");
		createFolderButton.setFocusable(false);
		createFolderButton.setAction(actionMap.get("createFolderAction"));
		toolBar.add(createFolderButton);

		uploadButton.setText("上传");
		uploadButton.setFocusable(false);
		uploadButton.setAction(actionMap.get("uploadAction"));
		toolBar.add(uploadButton);

		JButton refreshButton = new JButton();
		refreshButton.setText("刷新");
		refreshButton.setFocusable(false);
		refreshButton.setAction(actionMap.get("refreshAction"));
		toolBar.add(refreshButton);
		toolBar.add(jSeparator1);
		
		//File.listRoots():列出可用的文件系统根。
		localDiskComboBox.setModel(new DefaultComboBoxModel(File.listRoots())); 
		localDiskComboBox.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				localDiskComboBoxItemStateChanged(evt);
			}
		});
		toolBar.add(localDiskComboBox);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(toolBar, gridBagConstraints);
		localDiskTable.setModel(new LocalTableModel());
		localDiskTable.setShowHorizontalLines(false);
		localDiskTable.setShowVerticalLines(false);
		localDiskTable.getTableHeader().setReorderingAllowed(false);
		localDiskTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				localDiskTableMouseClicked(evt);
			}
		});
		scrollPane.setViewportView(localDiskTable);
		scrollPane.getViewport().setBackground(Color.WHITE);
		//设置渲染本地资源和FTP资源表格组件的渲染器
		localDiskTable.getColumnModel().getColumn(0).setCellRenderer(
				FTPTableCellRanderer.getCellRanderer());
		//RowSorter 的一个实现，它使用 TableModel 提供排序和过滤操作。
		sorter = new TableRowSorter<TableModel>(localDiskTable.getModel());
		TableStringConverter converter = new TableConverter();
		//设置负责将值从模型转换为字符串的对象。
		sorter.setStringConverter(converter);
		//设置 RowSorter。RowSorter 用于提供对 JTable 的排序和过滤。 
		localDiskTable.setRowSorter(sorter);
		sorter.toggleSortOrder(0);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(scrollPane, gridBagConstraints);

		localSelFilePathLabel.setBorder(javax.swing.BorderFactory
				.createEtchedBorder());
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		add(localSelFilePathLabel, gridBagConstraints);
	}

	/**
	 * 本地磁盘下拉选择框的选项改变事件处理方法，由事件监听器调用
	 */
	private void localDiskComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
		if (evt.getStateChange() == ItemEvent.SELECTED) {
			Object item = evt.getItem(); // 获取选择的下拉列表的选项
			if (item instanceof File) { // 如果该选项是File类的实例对象
				File selDisk = (File) item; // 将该选项转换成File类
				// 调用listLocalFiles()方法，显示该File类指定的磁盘文件列表
				listLocalFiles(selDisk);
			}
		}
	}

	/**
	 * 刷新指定文件夹的方法
	 */
	void refreshFolder(File file) {
		listLocalFiles(file);
	}

	/**
	 * 刷新本地当前文件夹的方法
	 */
	public void refreshCurrentFolder() {
		final File file = getCurrentFolder(); // 获取当前文件夹
		Runnable runnable = new Runnable() { // 创建新的线程
			public void run() {
				listLocalFiles(file); // 重载当前文件夹的列表到表格中
			}
		};
		//导致 runnable 的 run 方法在 EventQueue 的指派线程上被调用。
		SwingUtilities.invokeLater(runnable); // 在事件线程中调用该线程对象
	}

	/**
	 * 获取当前文件夹
	 */
	public File getCurrentFolder() {
		// 使用路径标签的路径创建当前文件夹对象
		File file = new File(localSelFilePathLabel.getText());
		// 如果表格选择了文件夹，或选择的文件有真是的上级文件夹
		if (localDiskTable.getSelectedRow() > 1 && file.getParentFile() != null)
			file = file.getParentFile(); // 获取该上级文件夹
		return file; //  返回文件夹对象
	}

	/**
	 * 本地磁盘文件的表格单击和双击事件处理方法
	 */
	private void localDiskTableMouseClicked(java.awt.event.MouseEvent evt) {
		int selectedRow = localDiskTable.getSelectedRow(); // 获取选择的表格行号
		if (selectedRow < 0)
			return;
		// 获取表格中选择的当前行的第一个字段的值
		Object value = localDiskTable.getValueAt(selectedRow, 0);
		if (value instanceof DiskFile) { //  如果该值是DiskFile的实例对象
			DiskFile selFile = (DiskFile) value;
			// 设置状态栏的本地文件路径
			localSelFilePathLabel.setText(selFile.getAbsolutePath());
			if (evt.getClickCount() >= 2) { //  如果是双击鼠标
				if (selFile.isDirectory()) { // 并且选择的是文件夹
					listLocalFiles(selFile); // 显示该文件夹的内容列表
				} else if (desktop != null) { // 如果不是文件夹
					try {
						desktop.open(selFile); // 关联本地系统程序打开该文件
					} catch (IOException ex) {
						Logger.getLogger(FTPClientFrame.class.getName()).log(
								Level.SEVERE, null, ex);
					}
				}
			}
		} else { // 如果选择的表格内容不是DiskFile类的实例
			// 判断选择的是不是..选项
			if (evt.getClickCount() >= 2 && value.equals("..")) {
				// 创建当前选择文件的临时文件
				File tempFile = new File((localSelFilePathLabel.getText()));
				// 显示选择的文件的上级目录列表
				listLocalFiles(tempFile.getParentFile());
			}
		}
	}

	/**
	 * 读取本地文件到表格的方法
	 */
	private void listLocalFiles(File selDisk) {
		if (selDisk == null || selDisk.isFile()) {
			return;
		}
		localSelFilePathLabel.setText(selDisk.getAbsolutePath());
		File[] listFiles = selDisk.listFiles(); // 获取磁盘文件列表
		// 获取表格的数据模型
		DefaultTableModel model = (DefaultTableModel) localDiskTable.getModel();
		model.setRowCount(0); //  清除模型的内容
		model.addRow(new Object[] { ".", "<DIR>", "" }); // 创建.选项
		model.addRow(new Object[] { "..", "<DIR>", "" }); // 创建..选项
		if (listFiles == null) {
			JOptionPane.showMessageDialog(this, "该磁盘无法访问");
			return;
		}
		// 遍历磁盘根文件夹的内容，添加到表格中
		for (File file : listFiles) {
			File diskFile = new DiskFile(file); // 创建文件对象
			String length = file.length() + "B "; // 获取文件大小
			if (file.length() > 1000 * 1000 * 1000) { // 计算文件G单位
				length = file.length() / 1000000000 + "G ";
			}
			if (file.length() > 1000 * 1000) { // 计算文件M单位
				length = file.length() / 1000000 + "M ";
			}
			if (file.length() > 1000) {
				length = file.length() / 1000 + "K "; // 计算文件K单位
			}
			if (file.isDirectory()) { // 显示文件夹标志
				length = "<DIR>";
			}
			// 获取文件的最后修改日期
			String modifDate = new Date(file.lastModified()).toLocaleString();
			if (!file.canRead()) {
				length = "未知";
				modifDate = "未知";
			}
			// 将单个文件的信息添加到表格的数据模型中
			model.addRow(new Object[] { diskFile, length, modifDate });
		}
		localDiskTable.clearSelection(); // 取消表格的选择项
	}

	/**
	 * 停止文件上传线程的方法
	 */
	public void stopUploadThread() {
		if (uploadThread != null)
			uploadThread.stopThread();
	}

	public javax.swing.JComboBox getLocalDiskComboBox() {
		return localDiskComboBox;
	}

	/**
	 * 设置FTP连接，并启动上传队列线程的方法。
	 */
	public void setFtpClient(String server, int port, String userStr,
			String passStr) {
		if (uploadThread != null)
			uploadThread.stopThread();
		uploadThread = new UploadThread(this, server, port, userStr, passStr);
		uploadThread.start();
	}

	public Queue<Object[]> getQueue() {
		return queue;
	}
}
