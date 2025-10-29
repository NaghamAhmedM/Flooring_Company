package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.model.Order;
import org.junit.jupiter.api.*;


import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;


import static org.junit.jupiter.api.Assertions.*;

public class OrderFileDaoTest {
    static Path tempDir;
    static OrderFileDao dao;


    @BeforeAll
    static void setup() throws Exception {
        tempDir = Files.createTempDirectory("orders_test");
        dao = new OrderFileDao(tempDir.toString());
    }


    @Test
    void addAndGetOrder() throws Exception {
        LocalDate d = LocalDate.now().plusDays(1);
        Order o = new Order();
        o.setOrderDate(d);
        o.setCustomerName("Test");
        o.setState("TX");
        o.setTaxRate(new BigDecimal("4.45"));
        o.setProductType("Tile");
        o.setArea(new BigDecimal("100"));
        o.setCostPerSquareFoot(new BigDecimal("3.50"));
        o.setLaborCostPerSquareFoot(new BigDecimal("4.15"));
        o.setMaterialCost(new BigDecimal("350.00"));
        o.setLaborCost(new BigDecimal("415.00"));
        o.setTax(new BigDecimal("33.71"));
        o.setTotal(new BigDecimal("798.71"));


        Order added = dao.addOrder(o);
        assertTrue(added.getOrderNumber() > 0);
        var list = dao.getOrdersByDate(d);
        assertEquals(1, list.size());
    }
}
