package com.oyp.ftp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
/**
 * 点击  帮助(H)-->关于(A) 响应显示一个带图片的面板
 */
class AboutItemAction implements ActionListener {
	private final FTPClientFrame frame;
	private JLabel imgLabel;
	private JPanel topPane;

	AboutItemAction(FTPClientFrame client_Frame) {
		frame = client_Frame;
		URL url = frame.getClass().getResource("/com/oyp/ftp/res/about.jpg");
		ImageIcon aboutImage = new ImageIcon(url);
		imgLabel = new JLabel(aboutImage);
		topPane = (JPanel) frame.getRootPane().getGlassPane();
		topPane.setLayout(new BorderLayout());
		topPane.add(imgLabel, BorderLayout.CENTER);
		topPane.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				topPane.setVisible(false);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		topPane.setVisible(true);
	}
}