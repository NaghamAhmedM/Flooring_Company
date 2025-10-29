package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.model.Order;

import java.time.LocalDate;
import java.util.List;

public interface OrderDao {
    List<Order> getOrdersByDate(LocalDate date) throws DaoException;
    Order addOrder(Order order) throws DaoException;
    Order editOrder(LocalDate date, Order order) throws DaoException;
    Order removeOrder(LocalDate date, int orderNumber) throws DaoException;
    void exportAll(String exportFilePath) throws DaoException;
}
