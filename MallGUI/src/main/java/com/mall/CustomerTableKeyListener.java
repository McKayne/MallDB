package com.mall;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.sql.SQLException;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import javax.swing.table.DefaultTableModel;

public class CustomerTableKeyListener implements KeyListener {

	private final CashierPanel panel;

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case 127:
				int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this customer?", "Confirm delete", JOptionPane.YES_NO_OPTION);
				if (n == 0) {
					try {
						panel.deleteCustomerFromBase();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
					panel.getCustomerFirstNames().remove(panel.getCustomerTable().getSelectedRow());
					panel.getCustomerLastNames().remove(panel.getCustomerTable().getSelectedRow());
					panel.getDates().remove(panel.getCustomerTable().getSelectedRow());
					panel.getPurchasedName().remove(panel.getCustomerTable().getSelectedRow());
					panel.getPurchasedPrice().remove(panel.getCustomerTable().getSelectedRow());
					panel.getPurchasedQuantity().remove(panel.getCustomerTable().getSelectedRow());
					panel.getPurchaseNumber().remove(panel.getCustomerTable().getSelectedRow());
					panel.getPurchaseDate().remove(panel.getCustomerTable().getSelectedRow());
					((DefaultTableModel) panel.getCustomerTable().getModel()).removeRow(panel.getCustomerTable().getSelectedRow());
					JOptionPane.showMessageDialog(null, "Customer deleted");
					panel.deleteRows(panel.getPurchasedTable());
					panel.fillPurchasedWaresTable((ArrayList) panel.getPurchasedName().get(0), (ArrayList) panel.getPurchasedPrice().get(0), (ArrayList) panel.getPurchasedQuantity().get(0));
				}
				break;
			case 38:
			case 40:
				int selected = panel.getCustomerTable().getSelectedRow();
				panel.deleteRows(panel.getPurchasedTable());
				panel.updatePurchaseInfo(panel.getPurchaseNumber(), panel.getPurchaseDate());
				panel.fillPurchasedWaresTable((ArrayList) panel.getPurchasedName().get(selected), (ArrayList) panel.getPurchasedPrice().get(selected), (ArrayList) panel.getPurchasedQuantity().get(selected));
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	public CustomerTableKeyListener(CashierPanel panel) {
		this.panel = panel;
	}
}