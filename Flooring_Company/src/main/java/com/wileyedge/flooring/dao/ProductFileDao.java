package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.model.Product;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProductFileDao implements ProductDao{

    private final String productFilePath;
    private Map<String, Product> products;


    public ProductFileDao(String productFilePath) {
        this.productFilePath = productFilePath;
    }


    private void loadIfNeeded() throws DaoException {
        if (products != null) return;
        products = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(productFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (line.startsWith("ProductType")) continue; // header
                String[] parts = line.split(",");
                String type = parts[0];
                var cost = new BigDecimal(parts[1].trim());
                var labor = new BigDecimal(parts[2].trim());
                products.put(type, new Product(type, cost, labor));
            }
        } catch (Exception e) {
            throw new DaoException("Could not load products", e);
        }
    }

    @Override
    public Map<String, Product> getAllProducts() throws DaoException {
        loadIfNeeded();
        return Collections.unmodifiableMap(products);
    }

    @Override
    public Product getProduct(String productType) throws DaoException {
        loadIfNeeded();
        return products.get(productType);
    }
}
