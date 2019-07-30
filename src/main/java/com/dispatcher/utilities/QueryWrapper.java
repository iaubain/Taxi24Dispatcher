/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dispatcher.utilities;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Aubain
 */
public class QueryWrapper {
    private String query;
    private final Map<String, Object> parameters;
    private Integer pageNumber;
    private Integer pageSize;

    public QueryWrapper() {
        this.parameters = new HashMap();
    }

    public QueryWrapper(String query) {
        parameters = new HashMap();
        this.query = query;
    }
    
    public String getQuery() {
        return query;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public QueryWrapper setParameter(String key, Object value) {
        parameters.put(key, value);
        return this;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    
}
