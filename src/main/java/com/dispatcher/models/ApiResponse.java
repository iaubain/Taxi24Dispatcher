/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dispatcher.models;

import biz.galaxy.commons.utilities.UtilAbstractModel;


/**
 *
 * @author ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr /
 * aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662
 */
public class ApiResponse extends UtilAbstractModel<ApiResponse>{
    private int statusCode;
    private Object body;

    public ApiResponse() {
        super(ApiResponse.class);
    }

    public ApiResponse(int statusCode, Object body) {
        super(ApiResponse.class);
        this.statusCode = statusCode;
        this.body = body;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    
}
