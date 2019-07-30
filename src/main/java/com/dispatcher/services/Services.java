/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.dispatcher.services;

import com.dispatcher.logic.Command;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Aubain
 */
@RestController
public class Services {
    @Autowired
            Command cmd;
    
    @RequestMapping( method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE, value = "/Taxi24Dispatcher/dispatcher/process/fact")
    @ResponseBody
    public ResponseEntity index(@RequestHeader Map<String, String> headers, @RequestParam Map<String, String[]> params, @RequestBody String body) {
        return cmd.exec(headers, body, params);
    }
}
