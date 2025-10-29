package com.wileyedge.flooring.service;

import com.wileyedge.flooring.dao.DaoException;
import com.wileyedge.flooring.dao.OrderDao;
import com.wileyedge.flooring.dao.ProductDao;
import com.wileyedge.flooring.dao.TaxDao;
import com.wileyedge.flooring.model.Order;
import com.wileyedge.flooring.model.Product;
import com.wileyedge.flooring.model.Tax;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class FlooringServiceImpl implements FlooringService{

    private final OrderDao orderDao;
    private final TaxDao taxDao;
    private final ProductDao productDao;


    public FlooringServiceImpl(OrderDao orderDao, TaxDao taxDao, ProductDao productDao) {
        this.orderDao = orderDao;
        this.taxDao = taxDao;
        this.productDao = productDao;
    }

    @Override
    public List<Order> getOrdersByDate(LocalDate date) throws DaoException {
        return orderDao.getOrdersByDate(date);
    }

    @Override
    public Order createOrder(Order order) throws DaoException, ValidationException {
        validateForCreate(order);
        populateCosts(order);
        return orderDao.addOrder(order);
    }

    @Override
    public Order editOrder(LocalDate date, Order order) throws DaoException, ValidationException {
        validateForEdit(order);
        populateCosts(order);
        return orderDao.editOrder(date, order);
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber) throws DaoException {
        return orderDao.removeOrder(date, orderNumber);
    }

    @Override
    public void exportAll(String exportFilePath) throws DaoException {
        orderDao.exportAll(exportFilePath);
    }

    private void validateForCreate(Order o) throws DaoException, ValidationException {

        if (o.getOrderDate() == null || !o.getOrderDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Order date must be in the future.");
        }

        validateCommon(o);
    }

    private void validateForEdit(Order o) throws DaoException, ValidationException {
        validateCommon(o);
    }

    private void validateCommon(Order o) throws DaoException, ValidationException {
        if (o.getCustomerName() == null || o.getCustomerName().trim().isEmpty())
            throw new ValidationException("Customer name cannot be blank.");
        if (!o.getCustomerName().matches("[A-Za-z0-9., ]+"))
            throw new ValidationException("Customer name contains invalid characters.");
        Tax tax = taxDao.getTax(o.getState());
        if (tax == null) throw new ValidationException("We do not sell in state: " + o.getState());
        Product p = productDao.getProduct(o.getProductType());
        if (p == null) throw new ValidationException("Unknown product: " + o.getProductType());
        if (o.getArea() == null || o.getArea().compareTo(new BigDecimal("100")) < 0)
            throw new ValidationException("Area must be at least 100 sq ft.");
    }

    private void populateCosts(Order o) throws DaoException {
        try {
            Product p = productDao.getProduct(o.getProductType());
            Tax t = taxDao.getTax(o.getState());
            o.setCostPerSquareFoot(p.getCostPerSquareFoot());
            o.setLaborCostPerSquareFoot(p.getLaborCostPerSquareFoot());
            o.setTaxRate(t.getTaxRate());


            BigDecimal materialCost = o.getArea().multiply(o.getCostPerSquareFoot()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal laborCost = o.getArea().multiply(o.getLaborCostPerSquareFoot()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal taxRateDecimal = o.getTaxRate().divide(new BigDecimal("100"));
            BigDecimal tax = (materialCost.add(laborCost)).multiply(taxRateDecimal).setScale(2, RoundingMode.HALF_UP);
            BigDecimal total = materialCost.add(laborCost).add(tax).setScale(2, RoundingMode.HALF_UP);


            o.setMaterialCost(materialCost);
            o.setLaborCost(laborCost);
            o.setTax(tax);
            o.setTotal(total);
        } catch (Exception e) {
            throw new DaoException("Failed to populate costs", e);
        }
    }
}
