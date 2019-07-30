/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dispatcher.utilities;

import java.io.File;

import org.springframework.http.ResponseEntity;

/**
 *
 * @author Hp
 */
public class ReturnUtil {
    public static final ResponseEntity isSuccess( String outPut){
        return ResponseEntity.ok(outPut);//.ok(outPut, MediaType.APPLICATION_JSON).build();
    }
    public static final ResponseEntity isFile( File outPut){
        return ResponseEntity.ok(outPut);
    }
    public static final ResponseEntity isFailed(int statusCode, String cause){
        return ResponseEntity.status(statusCode).body(""+cause);
    }
    public static final ResponseEntity isRaw(int statusCode, String raw){
        return ResponseEntity.status(statusCode).body(""+raw);
    }
}
