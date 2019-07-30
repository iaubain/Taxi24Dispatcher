/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dispatcher.facades;

import com.dispatcher.models.DateRange;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.io.Serializable;
import javax.persistence.Transient;

/**
 *
 * @author Aubain
 */
public abstract class Filter implements Serializable{
    private static final long serialVersionUID = 1L;
    @Transient
    @JsonProperty(access = Access.WRITE_ONLY)
    private DateRange creationTimeRange;
    @Transient
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer pageNumber;
    @Transient
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer pageSize;
    @Transient
    @JsonProperty(access = Access.WRITE_ONLY)
    private Boolean isDesc;

    public Filter() {
    }

    public Filter(DateRange creationTimeRange, Integer pageNumber, Integer pageSize, Boolean isDesc) {
        this.creationTimeRange = creationTimeRange;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.isDesc = isDesc;
    }
    
    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public DateRange getCreationTimeRange() {
        return creationTimeRange;
    }

    public void setCreationTimeRange(DateRange creationTimeRange) {
        this.creationTimeRange = creationTimeRange;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Boolean getIsDesc() {
        return isDesc;
    }

    public void setIsDesc(Boolean isDesc) {
        this.isDesc = isDesc;
    }
}
