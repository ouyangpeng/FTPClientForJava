package com.oyp.ftp.panel.ftp;

import javax.swing.table.TableModel;
import javax.swing.table.TableStringConverter;

import com.oyp.ftp.utils.FileInterface;
/**
 *  用于将对象从模型转换为字符串。
 *  当模型返回不具有有意义的 toString 实现的对象时，此类用于过滤和搜索操作。 
 */
public class TableConverter extends TableStringConverter {

	@Override
	public String toString(TableModel model, int row, int column) {
		Object value = model.getValueAt(row, column);
		if (value instanceof FileInterface) {
			FileInterface file = (FileInterface) value;
			if (file.isDirectory())
				return "!" + file.toString();
			else
				return "Z" + file.toString();
		}
		if (value.equals(".") || value.equals(".."))
			return "!!";
		return value.toString();
	}

}
