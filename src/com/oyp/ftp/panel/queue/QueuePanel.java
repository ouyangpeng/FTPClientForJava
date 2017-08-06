package com.oyp.ftp.panel.queue;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import com.oyp.ftp.FTPClientFrame;
import com.oyp.ftp.utils.FtpClient;
import com.oyp.ftp.utils.FtpFile;

/**
 * 任务队列控制面板
 */
public class QueuePanel extends JPanel implements ActionListener {
	private JTable queueTable = new JTable(); // 显示任务队列的表格组件
	private JScrollPane scrollPane = new JScrollPane();
	private FTPClientFrame frame; // 主窗体的引用对象
	private String[] columns;
	private FtpClient ftpClient; 	// FTP协议的控制类
	private Timer queueTimer; 		// 队列的定时器
	private LinkedList<Object[]> localQueue; 	// 本地面板的上传队列
	private LinkedList<Object[]> ftpQueue; 		// FTP面板的下载队列
	private JToggleButton stopButton;
	private boolean stop = false; // 队列的控制变量

	/**
	 * 默认的构造方法
	 */
	public QueuePanel() {
		initComponent();
	}

	/**
	 * 自定义的构造方法
	 * 
	 * @param frame
	 *            主窗体
	 */
	public QueuePanel(FTPClientFrame frame) {
		this.frame = frame;
		// 从主窗体获取本地面板的上传队列
		localQueue = (LinkedList<Object[]>) frame.getLocalPanel().getQueue();
		// 从主窗体获取FTP面板的下载队列
		ftpQueue = (LinkedList<Object[]>) frame.getFtpPanel().getQueue();
		initComponent(); // 初始化窗体界面
		// 创建定时器，每间隔1秒执行队列刷新任务
		queueTimer = new Timer(1000, new ActionListener() {
			/**
			 * 定时器的事件处理方法
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				if (localQueue.size() + ftpQueue.size() == queueTable
						.getRowCount()) // 如果队列大小没有改变
					return; // 结束本方法，不做任何操作
				refreshQueue(); // 否则刷新显示队列信息的表格数据
			}
		});
	}

	private void initComponent() {
		BorderLayout cardLayout = new BorderLayout();
		setLayout(cardLayout);
		columns = new String[] { "任务名称", "方向", "主机", "执行状态" };
		queueTable.setModel(new DefaultTableModel(new Object[][] {}, columns));
		queueTable.getTableHeader().setReorderingAllowed(false);
		scrollPane.setViewportView(queueTable);
		cardLayout.layoutContainer(scrollPane);
		add(scrollPane, CENTER);

		JToolBar controlTool = new JToolBar(JToolBar.VERTICAL);
		controlTool.setRollover(true);
		controlTool.setFloatable(false);
		JButton upButton = new JButton("上移");
		upButton.setActionCommand("up");
		upButton.addActionListener(this);
		JButton downButton = new JButton("下移");
		downButton.setActionCommand("down");
		downButton.addActionListener(this);
		stopButton = new JToggleButton("暂停");
		stopButton.setActionCommand("stop&start");
		stopButton.addActionListener(this);
		JButton delButton = new JButton("删除");
		delButton.setActionCommand("del");
		delButton.addActionListener(this);
		JButton clearButton = new JButton("清空");
		clearButton.setActionCommand("clear");
		clearButton.addActionListener(this);
		controlTool.setLayout(new BoxLayout(controlTool, BoxLayout.Y_AXIS));
		controlTool.add(upButton);
		controlTool.add(downButton);
		controlTool.add(stopButton);
		controlTool.add(delButton);
		controlTool.add(clearButton);
		add(controlTool, EAST);
	}

	public void startQueue() {
		ftpClient = frame.getFtpClient();
		queueTimer.start();
	}

	/**
	 * 界面上按钮的事件处理方法
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		final String command = e.getActionCommand();
		if (command.equals("stop&start")) {// 处理暂停按钮事件
			if (stopButton.isSelected()) {
				stop = true;
				stopButton.setText("继续");
			} else {
				stop = false;
				stopButton.setText("暂停");
			}
		}
		// 处理上移和下移按钮事件
		if (command.equals("up") || command.equals("down")) {
			up_Down_Action(command); // 调用处理上移和下移动作的方法
		}
		if (command.equals("del")) {// 处理删除按钮的事件
			int row = queueTable.getSelectedRow(); // 获取显示队列的表格的当前选择行
			if (row < 0)
				return;
			// 获取选择行的第一个表格单元值
			Object valueAt = queueTable.getValueAt(row, 0);
			// 如果选择内容是File类的对象
			if (valueAt instanceof File) {
				File file = (File) valueAt;
				int size = localQueue.size(); // 获取上传队列大小
				for (int i = 0; i < size; i++) { // 遍历上传队列
					if (localQueue.get(i)[0].equals(file)) {
						localQueue.remove(i); // 从上传队列中删除文件对象
					}
				}
			} else if (valueAt instanceof String) { // 如果选择的是字符串对象
				String fileStr = (String) valueAt;
				int size = ftpQueue.size(); // 获取上传队列的大小
				for (int i = 0; i < size; i++) { // 遍历上传队列
					// 获取上传队列中的文件对象
					FtpFile ftpFile = (FtpFile) ftpQueue.get(i)[0];
					if (ftpFile.getAbsolutePath().equals(fileStr)) {
						ftpQueue.remove(i); // 从上传队列中删除该文件对象
					}
				}
			}
			refreshQueue(); // 刷新队列列表
		}
		if (command.equals("clear")) { // 处理清空按钮的事件
			localQueue.clear(); // 调用本地面板的队列的clear()方法
			ftpQueue.clear(); // 调用FTP面板的队列的clear()方法
		}
	}

	/**
	 * 队列任务的上移和下移动作处理方法
	 * 
	 * @param command
	 *            上移或下移命令
	 */
	private void up_Down_Action(final String command) {
		int row = queueTable.getSelectedRow(); // 获取队列表格的当前选择行号
		if (row < 0)
			return;
		// 获取当前选择行的第一个单元值
		Object valueAt = queueTable.getValueAt(row, 0);
		// 获取当前选择行的第二个单元值作为判断上传或下载方向的依据
		String orientation = (String) queueTable.getValueAt(row, 1);
		if (orientation.equals("上传")) { // 如果是上传任务
			String value = ((File) valueAt).getAbsolutePath();
			int size = localQueue.size();
			for (int i = 0; i < size; i++) { // 遍历上传队列
				Object[] que = localQueue.get(i);
				File file = (File) que[0];
				// 从队列中，遍历到选择的上传任务的文件对象
				if (file.getAbsolutePath().equals(value)) {
					ListSelectionModel selModel = queueTable
							.getSelectionModel(); // 获取表格的选择模型
					selModel // 设置选择模型的单选模式
							.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					int dsize = localQueue.size(); // 获取本地上传队列的大小
					int drow = queueTable.getSelectedRow();// 获取队列表格的当前选择行号
					int queueVal = localQueue.size() - dsize;

					int next = -1;
					int selIndex = -1;
					if (command.equals("up")) {
						if (i < 1) // 限制选择范围
							return;
						selIndex = drow - queueVal - 1;
						next = i - 1;
					} else {
						if (i >= size - 1) // 限制选择范围
							return;
						selIndex = drow - queueVal + 1;
						next = i + 1;
					}
					// 交换连个队列位置的任务
					Object[] temp = localQueue.get(next);
					localQueue.set(next, que);
					localQueue.set(i, temp);
					refreshQueue(); // 刷新队列表格的列表
					// 设置表格选择第一行
					selModel.setSelectionInterval(0, selIndex);
					break;
				}
			}
		} else if (orientation.equals("下载")) { // 如果是下载任务
			String value = (String) valueAt;
			int size = ftpQueue.size(); // 获取FTP下载队列的大小
			for (int i = 0; i < size; i++) { // 遍历下载队列
				Object[] que = ftpQueue.get(i);
				FtpFile file = (FtpFile) que[0]; // 获取每个下载任务的FTP文件对象
				if (file.getAbsolutePath().equals(value)) {// 
					ListSelectionModel selModel = queueTable
							.getSelectionModel(); // 获取任务队列表格的选择模型
					// 设置模型使用单选模式
					selModel.setSelectionMode(SINGLE_SELECTION);
					int dsize = ftpQueue.size();
					int drow = queueTable.getSelectedRow();
					int queueVal = ftpQueue.size() - dsize;

					int next = -1;
					int selIndex = -1;
					if (command.equals("up")) {
						if (i < 1) // 限制选择范围
							return;
						selIndex = drow - queueVal - 1;
						next = i - 1;
					} else {
						if (i >= size - 1) // 限制选择范围
							return;
						selIndex = drow - queueVal + 1;
						next = i + 1;
					}
					// 交换连个队列位置的任务内容
					Object[] temp = ftpQueue.get(next);
					ftpQueue.set(next, que);
					ftpQueue.set(i, temp);
					refreshQueue(); // 刷新任务队列的表格列表
					// 选择表格的第一行
					selModel.setSelectionInterval(0, selIndex);
					break;
				}
			}
		}
	}

	/**
	 * 刷新队列的方法
	 */
	private synchronized void refreshQueue() {
		// 如果自动关机按钮被按下并且上传和下载的队列都有任务
		if (frame.getShutdownButton().isSelected() && localQueue.isEmpty()
				&& ftpQueue.isEmpty()) {
			try {
				// 执行系统关机命令，延迟30秒钟
				Runtime.getRuntime().exec("shutdown -s -t 30");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 创建表格的数据模型对象
		DefaultTableModel model = new DefaultTableModel(columns, 0);
		// 获取本地上传队列中的任务
		Object[] localQueueArray = localQueue.toArray();
		// 遍历本地上传任务
		for (int i = 0; i < localQueueArray.length; i++) {
			Object[] queueValue = (Object[]) localQueueArray[i];
			if (queueValue == null)
				continue;
			File localFile = (File) queueValue[0];
			// 把上传队列的任务添加到表格组件的数据模型中
			model.addRow(new Object[] { localFile.getAbsoluteFile(), "上传",
					ftpClient.getServer(), i == 0 ? "正在上传" : "等待上传" });
		}
		// 获取下载队列的任务
		Object[] ftpQueueArray = ftpQueue.toArray();
		// 遍历下载队列
		for (int i = 0; i < ftpQueueArray.length; i++) {
			Object[] queueValue = (Object[]) ftpQueueArray[i];
			if (queueValue == null)
				continue;
			FtpFile ftpFile = (FtpFile) queueValue[0];
			// 把下载队列的任务添加到表格组件的数据模型中
			model.addRow(new Object[] { ftpFile.getAbsolutePath(), "下载",
					ftpClient.getServer(), i == 0 ? "正在下载" : "等待下载" });
		}
		queueTable.setModel(model); // 设置表格使用本方法的表格数据模型
	}

	public boolean isStop() {
		return stop;
	}
}
