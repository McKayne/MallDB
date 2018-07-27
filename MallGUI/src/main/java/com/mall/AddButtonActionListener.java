package com.mall;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddButtonActionListener implements ActionListener {

	private final CashierPanel panel;

	@Override
	public void actionPerformed(ActionEvent e) {
		panel.showInsertFrame();
	}
	
	public AddButtonActionListener(CashierPanel panel) {
		this.panel = panel;
	}
}