package com.wileyedge.flooring.controller;

import com.wileyedge.flooring.dao.DaoException;
import com.wileyedge.flooring.model.Order;
import com.wileyedge.flooring.service.FlooringService;
import com.wileyedge.flooring.service.ValidationException;
import com.wileyedge.flooring.view.FlooringView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FlooringController {

    private final FlooringView view;
    private final FlooringService service;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy");


    public FlooringController(FlooringView view, FlooringService service) {
        this.view = view; this.service = service;
    }

    public void run() {
        boolean running = true;
        while (running) {
            int choice = view.printMenuAndGetSelection();
            try {
                switch (choice) {
                    case 1 -> displayOrders();
                    case 2 -> addOrder();
                    case 3 -> editOrder();
                    case 4 -> removeOrder();
                    case 5 -> exportAll();
                    case 6 -> running = false;
                }
            } catch (Exception e) {
                view.showMessage("Error: " + e.getMessage());
            }
        }
    }

    private void displayOrders() throws DaoException {
        LocalDate d = view.readOrderDate();
        List<Order> orders = service.getOrdersByDate(d);
        view.displayOrders(orders);
    }

    private void addOrder() throws DaoException {
        LocalDate d = view.readOrderDate();
        Order o = view.readNewOrder(d);
        try {
            Order placed = service.createOrder(o);
            view.showMessage("Order placed: " + placed.getOrderNumber());
        } catch (ValidationException ve) { view.showMessage("Validation: " + ve.getMessage()); }
    }

    private void editOrder() throws DaoException {
        LocalDate d = view.readOrderDate();
        int num = view.readOrderNumber();
        List<Order> orders = service.getOrdersByDate(d);
        Order target = orders.stream().filter(o -> o.getOrderNumber() == num).findFirst().orElse(null);

        if (target == null) { view.showMessage("Order not found"); return; }

        // Simple edit flow: re-read allowed fields
        view.showMessage("Leave blank to keep existing value.");
        String name = view.readString("Enter customer name ("+target.getCustomerName()+"): ");

        if (!name.trim().isEmpty()) target.setCustomerName(name);

        String state = view.readString("Enter state ("+target.getState()+"): ");

        if (!state.trim().isEmpty()) target.setState(state);

        String prod = view.readString("Enter product type ("+target.getProductType()+"): ");

        if (!prod.trim().isEmpty()) target.setProductType(prod);

        String areaS = view.readString("Enter area ("+target.getArea()+"): ");

        if (!areaS.trim().isEmpty()) target.setArea(new java.math.BigDecimal(areaS));
        try {

            if (view.confirm("Save changes?")) {
                service.editOrder(d, target);
                view.showMessage("Order updated.");
            }

        } catch (ValidationException ve) { view.showMessage("Validation: " + ve.getMessage()); }
    }

    private void removeOrder() throws DaoException {
        LocalDate d = view.readOrderDate();
        int num = view.readOrderNumber();
        List<Order> orders = service.getOrdersByDate(d);
        Order target = orders.stream().filter(o -> o.getOrderNumber() == num).findFirst().orElse(null);

        if (target == null) { view.showMessage("Order not found"); return; }

        if (view.confirm("Are you sure you want to remove order " + num + "?")) {
            service.removeOrder(d, num);
            view.showMessage("Order removed.");
        }
    }

    private void exportAll() throws DaoException {
        String path = "Backup/DataExport.txt";
        service.exportAll(path);
        view.showMessage("Exported to " + path);
    }
}
