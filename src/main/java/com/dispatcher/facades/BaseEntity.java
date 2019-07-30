/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.dispatcher.facades;

import com.dispatcher.models.DateRange;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import com.dispatcher.utilities.UtilStatus;

/**
 *
 * @author Aubain
 */
@MappedSuperclass
public abstract class BaseEntity extends Filter implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id", length = 255, nullable = false)
    private String id;
    @Basic(optional = false)
    @Size(min = 1, max = 12)
    @Column(name = "status", length = 12, nullable = false)
    @UtilStatus(name = "status")
    private String status;
    @Basic(optional = false)
    @Column(name = "creationTime", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyy-MM-dd HH:mm:ss", timezone = "GMT+0")
    private Date creationTime;

    public BaseEntity() {
        super();
    }

    public BaseEntity(String id, DateRange creationTimeRange, Integer pageNumber, Integer pageSize, Boolean isDesc) {
        super(creationTimeRange, pageNumber, pageSize, isDesc);
        this.id = id;
    }

    public BaseEntity(String id, String status, Date creationTime) {
        this.id = id;
        this.status = status;
        this.creationTime = creationTime;
    }

    public BaseEntity(DateRange creationTimeRange, Integer pageNumber, Integer pageSize, Boolean isDesc) {
        super(creationTimeRange, pageNumber, pageSize, isDesc);
    }

    public BaseEntity(String id, String status, Date creationTime, DateRange creationTimeRange, Integer pageNumber, Integer pageSize, Boolean isDesc) {
        super(creationTimeRange, pageNumber, pageSize, isDesc);
        this.id = id;
        this.status = status;
        this.creationTime = creationTime;
    }
    
    public BaseEntity(String status, Date creationTime) {
        this.status = status;
        this.creationTime = creationTime;
    }  

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public String toString() {
        return "\"id\":\"" + id + "\", \"status\":\"" + status + "\", \"creationTime\":\"" + creationTime +"\"";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.status);
        hash = 97 * hash + Objects.hashCode(this.creationTime);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BaseEntity other = (BaseEntity) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.status, other.status)) {
            return false;
        }
        if (!Objects.equals(this.creationTime, other.creationTime)) {
            return false;
        }
        return true;
    }    
}
