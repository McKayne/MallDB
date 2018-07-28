package com.mall;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

public class StoreTableMouseListener implements MouseListener {

	private final StoreOwnerPanel panel;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		int selected = panel.getStoreTable().getSelectedRow();
		if (selected == -1) {
			selected = 0;
		}
		DefaultTableModel model = (DefaultTableModel) panel.getEmployeeTable().getModel();
		int rowCount = model.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
			model.removeRow(i);
		}
		panel.fillEmployeeTable((ArrayList) panel.getEmployeeFirstNames().get(panel.getStoreTable().getSelectedRow()), (ArrayList) panel.getEmployeeLastNames().get(panel.getStoreTable().getSelectedRow()), (ArrayList) panel.getEmployeeExperience().get(panel.getStoreTable().getSelectedRow()), (ArrayList) panel.getEmployeeJobs().get(panel.getStoreTable().getSelectedRow()), (ArrayList) panel.getEmployeeSalary().get(panel.getStoreTable().getSelectedRow()));
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

	public StoreTableMouseListener(StoreOwnerPanel panel) {
		this.panel = panel;
	}
}