/**
 * 
 */
package com.oyp.ftp.panel.ftp;

import javax.swing.table.DefaultTableModel;
/***
 * FTP表格模型
 */
class FtpTableModel extends DefaultTableModel {
	Class[] types = new Class[] { java.lang.Object.class,
			java.lang.String.class, java.lang.String.class };
	boolean[] canEdit = new boolean[] { false, false, false };

	FtpTableModel() {
		super(new Object[][] {}, new String[] { "文件名", "大小", "日期" });
	}

	public Class getColumnClass(int columnIndex) {
		return types[columnIndex];
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return canEdit[columnIndex];
	}
}