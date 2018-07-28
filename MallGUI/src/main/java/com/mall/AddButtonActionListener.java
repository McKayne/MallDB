package com.mall;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddButtonActionListener implements ActionListener {

	private final Object panel;

	@Override
	public void actionPerformed(ActionEvent e) {
		if (panel instanceof CashierPanel) {
			CashierPanel cp = (CashierPanel) panel;
			cp.showInsertFrame();
		} else {
			StoreOwnerPanel sop = (StoreOwnerPanel) panel;
			sop.showInsertFrame();
		}
	}

	public AddButtonActionListener(StoreOwnerPanel panel) {
		this.panel = panel;
	}
	
	public AddButtonActionListener(CashierPanel panel) {
		this.panel = panel;
	}
}