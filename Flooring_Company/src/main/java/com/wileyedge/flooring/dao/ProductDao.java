package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.model.Product;

import java.util.Map;

public interface ProductDao {
    Map<String, Product> getAllProducts() throws DaoException;
    Product getProduct(String productType) throws DaoException;
}
