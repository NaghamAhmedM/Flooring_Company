package com.wileyedge.flooring.config;

import com.wileyedge.flooring.controller.FlooringController;
import com.wileyedge.flooring.dao.*;
import com.wileyedge.flooring.service.FlooringService;
import com.wileyedge.flooring.service.FlooringServiceImpl;
import com.wileyedge.flooring.view.FlooringView;
import com.wileyedge.flooring.view.UserIO;
import com.wileyedge.flooring.view.UserIOConsoleImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    @Bean
    public UserIO userIO() { return new UserIOConsoleImpl(); }


    @Bean
    public FlooringView view(UserIO io) { return new FlooringView(io); }


    @Bean
    public ProductDao productDao() { return new ProductFileDao("Data/Products.txt"); }


    @Bean
    public TaxDao taxDao() { return new TaxFileDao("Data/Taxes.txt"); }


    @Bean
    public OrderDao orderDao() { return new OrderFileDao("Orders"); }


    @Bean
    public FlooringService service(OrderDao orderDao, TaxDao taxDao, ProductDao productDao) {
        return new FlooringServiceImpl(orderDao, taxDao, productDao);
    }


    @Bean
    public FlooringController controller(FlooringView view, FlooringService service) {
        return new FlooringController(view, service);
    }
}
