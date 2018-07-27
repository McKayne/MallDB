package com.mall;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

public class CustomerTableMouseListener implements MouseListener {

	private final CashierPanel panel;

	@Override
	public void mouseClicked(MouseEvent e) {
		int selected = panel.getCustomerTable().getSelectedRow();
		if (selected == -1) {
			selected = 0;
		}
		DefaultTableModel model = (DefaultTableModel) panel.getPurchasedTable().getModel();
		int rowCount = model.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
			model.removeRow(i);
		}
		panel.updatePurchaseInfo(panel.getPurchaseNumber(), panel.getPurchaseDate());
		panel.fillPurchasedWaresTable((ArrayList) panel.getPurchasedName().get(selected), (ArrayList) panel.getPurchasedPrice().get(selected), (ArrayList) panel.getPurchasedQuantity().get(selected));
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	public CustomerTableMouseListener(CashierPanel panel) {
		this.panel = panel;
	}
}