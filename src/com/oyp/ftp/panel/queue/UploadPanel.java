package com.oyp.ftp.panel.queue;

import java.awt.CardLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.oyp.ftp.panel.QueueTableCellRanderer;
import com.oyp.ftp.utils.ProgressArg;

public class UploadPanel extends JPanel {
	private JTable uploadTable = new JTable(); // 表格组件
	private JScrollPane scrollPane = new JScrollPane();
	private DefaultTableModel model; // 表格的数据模型

	/**
	 * 构造方法，用于初始化程序界面
	 */
	public UploadPanel() {
		CardLayout cardLayout = new CardLayout();
		setLayout(cardLayout);
		ProgressArg progressArg = new ProgressArg(-1, -1, -1);
		model = new DefaultTableModel(new Object[][] { new Object[] { "", "",
				"", "", progressArg } }, new String[] { "文件名", "大小", "远程文件名",
				"主机", "状态" });
		uploadTable.setModel(model);
		uploadTable.getTableHeader().setReorderingAllowed(false);
		uploadTable.setRowSelectionAllowed(false);
		TableColumn column = uploadTable.getColumn("状态");
		column.setCellRenderer(new QueueTableCellRanderer());
		scrollPane.setViewportView(uploadTable);
		cardLayout.layoutContainer(scrollPane);
		add(scrollPane, "queue");
	}

	/**
	 * 向上传队列的表格组件添加新任务的方法
	 * 
	 * @param values
	 *            - 添加到表格的一行数据的数组对象
	 */
	public void addRow(final Object[] values) {
		Runnable runnable = new Runnable() {
			public void run() {
				model.insertRow(0, values); // 向表格的数据模型添加数据
			}
		};
		if (SwingUtilities.isEventDispatchThread())
			runnable.run(); // 在事件队列执行
		else
			SwingUtilities.invokeLater(runnable); // 或有事件队列调用
	}
}
