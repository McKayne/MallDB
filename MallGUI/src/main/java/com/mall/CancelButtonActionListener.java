package com.mall;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class CancelButtonActionListener implements ActionListener {

	private final JFrame insertFrame;

	@Override
	public void actionPerformed(ActionEvent e) {
		insertFrame.setVisible(false);
	}
	
	public CancelButtonActionListener(JFrame insertFrame) {
		this.insertFrame = insertFrame;
	}
}