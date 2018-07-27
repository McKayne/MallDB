package com.mall;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JOptionPane;

public class PurchaseTableKeyListener implements KeyListener {

	private final CashierPanel panel;

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case 127:
				int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this item from purchase?", "Confirm delete", JOptionPane.YES_NO_OPTION);
				if (n == 0) {
					panel.deleteItem();
				}
				break;
			case 40:
				break;
			case 38:
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	public PurchaseTableKeyListener(CashierPanel panel) {
		this.panel = panel;
	}
}