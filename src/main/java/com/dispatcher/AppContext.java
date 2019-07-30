/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.dispatcher;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author Aubain
 */
@Component
public class AppContext implements CommandLineRunner {
    
    @Autowired
            ApplicationContext applicationContext;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("Taxi24 Dispatcher Beans:");
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }        
    }
}