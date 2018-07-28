package com.mall;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.sql.SQLException;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class StoreTableKeyListener implements KeyListener {

	private final StoreOwnerPanel panel;

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case 127:
				int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this store?", "Confirm delete", JOptionPane.YES_NO_OPTION);
				if (n == 0) {
					int selected = panel.getStoreTable().getSelectedRow();
					try {
						panel.deleteStoreFromBase();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
					panel.getEmployeeIds().remove(selected);
					panel.getEmployeeFirstNames().remove(selected);
					panel.getEmployeeLastNames().remove(selected);
					panel.getEmployeeExperience().remove(selected);
					panel.getEmployeeJobs().remove(selected);
					panel.getEmployeeSalary().remove(selected);
					((DefaultTableModel) panel.getStoreTable().getModel()).removeRow(panel.getStoreTable().getSelectedRow());
					JOptionPane.showMessageDialog(null, "Store deleted");
					panel.deleteRows(panel.getEmployeeTable());
					panel.fillEmployeeTable((ArrayList) panel.getEmployeeFirstNames().get(0), (ArrayList) panel.getEmployeeLastNames().get(0), (ArrayList) panel.getEmployeeExperience().get(0), (ArrayList) panel.getEmployeeJobs().get(0), (ArrayList) panel.getEmployeeSalary().get(0));
				}
			case 40:
			case 38:
				panel.deleteRows(panel.getEmployeeTable());
				panel.fillEmployeeTable((ArrayList) panel.getEmployeeFirstNames().get(0), (ArrayList) panel.getEmployeeLastNames().get(0), (ArrayList) panel.getEmployeeExperience().get(0), (ArrayList) panel.getEmployeeJobs().get(0), (ArrayList) panel.getEmployeeSalary().get(0));
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	public StoreTableKeyListener(StoreOwnerPanel panel) {
		this.panel = panel;
	}
}