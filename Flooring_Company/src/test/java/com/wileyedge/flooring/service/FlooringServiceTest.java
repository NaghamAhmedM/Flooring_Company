package com.wileyedge.flooring.service;

import com.wileyedge.flooring.dao.ProductFileDao;
import com.wileyedge.flooring.dao.TaxFileDao;
import com.wileyedge.flooring.dao.OrderFileDao;
import com.wileyedge.flooring.model.Order;
import org.junit.jupiter.api.*;


import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;


import static org.junit.jupiter.api.Assertions.*;

public class FlooringServiceTest {
    static OrderFileDao orderDao;
    static ProductFileDao productDao;
    static TaxFileDao taxDao;
    static FlooringServiceImpl service;


    @BeforeAll
    static void init() throws Exception {
        var tmp = Files.createTempDirectory("fm_test");
        orderDao = new OrderFileDao(tmp.toString());

        // Use sample small product/tax files (create minimal ones)
        var products = tmp.resolve("Products.txt");
        Files.writeString(products, "Tile,3.50,4.15 ");
        var taxes = tmp.resolve("Taxes.txt");
        Files.writeString(taxes, "TX,Texas,4.45 ");
        productDao = new ProductFileDao(products.toString());
        taxDao = new TaxFileDao(taxes.toString());
        service = new FlooringServiceImpl(orderDao, taxDao, productDao);
    }


    @Test
    void createOrderCalculations() throws Exception {
        Order o = new Order();
        o.setOrderDate(LocalDate.now().plusDays(2));
        o.setCustomerName("Alice");
        o.setState("TX");
        o.setProductType("Tile");
        o.setArea(new BigDecimal("200"));
        var created = service.createOrder(o);
        assertNotNull(created.getMaterialCost());
        assertNotNull(created.getLaborCost());
        assertNotNull(created.getTax());
        assertNotNull(created.getTotal());
    }
}
