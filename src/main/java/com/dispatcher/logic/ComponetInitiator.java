/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.dispatcher.logic;

import com.dispatcher.config.SystemConfig;
import com.dispatcher.utilities.Log;
import com.dispatcher.utilities.UtilInitiator;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 *
 * @author Aubain
 */
@Component
public class ComponetInitiator {
    @PostConstruct
    public void init() {
        //Initiate component
        try {
            Thread t = new Thread(new UtilInitiator());
            t.start();
        } catch (Exception e) {
            Log.e(getClass(), SystemConfig.SYSTEM_ID[1]+"_"+e.getMessage());
        }
    }
}
