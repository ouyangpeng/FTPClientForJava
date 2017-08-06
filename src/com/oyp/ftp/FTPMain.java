package com.oyp.ftp;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;

public class FTPMain {
	/**
	 * 本应用的程序入口
	 */
	public static void main(String args[]) {
		//导致 runnable 的 run 方法在 EventQueue 的指派线程上被调用。
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//使用 LookAndFeel 对象设置当前的默认外观。 
					UIManager.setLookAndFeel(new NimbusLookAndFeel());//设置一个非常漂亮的外观
//					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					FTPClientFrame client_Frame = new FTPClientFrame();
					client_Frame.setVisible(true);
				} catch (Exception ex) {
					Logger.getLogger(FTPClientFrame.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
		});
	}
}
