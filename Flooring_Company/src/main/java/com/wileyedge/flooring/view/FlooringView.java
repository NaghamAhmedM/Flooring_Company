package com.wileyedge.flooring.view;

import com.wileyedge.flooring.model.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class FlooringView extends UserIOConsoleImpl{
    private final UserIO io;
    public FlooringView(UserIO io) { this.io = io; }


    public int printMenuAndGetSelection() {
        io.println("<<Flooring Program>>");
        io.println("1. Display Orders");
        io.println("2. Add an Order");
        io.println("3. Edit an Order");
        io.println("4. Remove an Order");
        io.println("5. Export All Data");
        io.println("6. Quit");

        return io.readInt("Enter choice: ", 1, 6);
    }


    public LocalDate readOrderDate() {

        while (true) {
            String s = io.readString("Enter date (MM-dd-yyyy): ");

            try { return LocalDate.parse(s, java.time.format.DateTimeFormatter.ofPattern("MM-dd-yyyy")); }
            catch (Exception e) { io.println("Invalid date format"); }
        }

    }

    public Order readNewOrder(LocalDate date) {
        Order o = new Order();
        o.setOrderDate(date);
        o.setCustomerName(io.readString("Enter customer name: "));
        o.setState(io.readString("Enter state abbreviation: "));
        o.setProductType(io.readString("Enter product type: "));

        while (true) {
            try {
                o.setArea(new BigDecimal(io.readString("Enter area (>=100): ")));
                break;
            } catch (Exception e) { io.println("Invalid decimal"); }
        }

        return o;
    }


    public void displayOrders(List<Order> orders) {

        if (orders.isEmpty()) { io.println("No orders for that date."); return; }

        for (Order o : orders) io.println(o.toString());

    }


    public int readOrderNumber() {
        return io.readInt("Enter order number: ", 1, Integer.MAX_VALUE);
    }


    public boolean confirm(String prompt) {
        String r = io.readString(prompt + " (Y/N): ");
        return r.trim().equalsIgnoreCase("Y");
    }

    public void showMessage(String s) { io.println(s); }
}
