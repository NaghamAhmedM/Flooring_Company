package com.wileyedge.flooring;

import com.wileyedge.flooring.config.SpringConfig;
import com.wileyedge.flooring.controller.FlooringController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        var controller = ctx.getBean(FlooringController.class);
        controller.run();
        ctx.close();
    }
}
