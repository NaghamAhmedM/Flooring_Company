package com.wileyedge.flooring.service;

import com.wileyedge.flooring.dao.DaoException;
import com.wileyedge.flooring.model.Order;

import java.time.LocalDate;
import java.util.List;

public interface FlooringService {
    List<Order> getOrdersByDate(LocalDate date) throws DaoException;
    Order createOrder(Order order) throws DaoException, ValidationException;
    Order editOrder(LocalDate date, Order order) throws DaoException, ValidationException;
    Order removeOrder(LocalDate date, int orderNumber) throws DaoException;
    void exportAll(String exportFilePath) throws DaoException;
}
