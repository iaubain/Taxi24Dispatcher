/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.dispatcher.entities;

import biz.galaxy.commons.config.CommonErrorCodeConfig;
import biz.galaxy.commons.models.ErrorModel;
import biz.galaxy.commons.models.ErrorsListModel;
import biz.galaxy.commons.utilities.ErrorGeneralException;
import biz.galaxy.commons.utilities.IdGen;
import biz.galaxy.commons.utilities.UtilModel;
import biz.galaxy.commons.utilities.serializer.UtilSerializer;
import com.dispatcher.config.StatusConfig;
import com.dispatcher.config.SystemConfig;
import com.dispatcher.facades.BaseEntity;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Aubain
 */
@Entity
@Table(name = "SystemComponent",
        indexes = {@Index(name = "idx_1", columnList = "name"),
            @Index(name = "idx_2", columnList = "status"),
            @Index(name = "idx_3", columnList = "creationTime")})
public class SystemComponent extends BaseEntity implements Serializable, UtilModel {
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 90)
    @Column(name = "name", length = 90, unique = true)
    private String name;
    
    @Transient
    private List<Command> commands;

    public SystemComponent() {
        super();
    }

    public SystemComponent(String name, String status, Date creationTime) {
        super(status, creationTime);
        this.name = name;
    }
    
    @Override
    public void validateOb() throws ErrorGeneralException {
        ErrorsListModel errors = new ErrorsListModel();
        if(this == null){
            errors.addError(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.VALIDATION_ERROR[0], CommonErrorCodeConfig.VALIDATION_ERROR[1], "Null object"));
        }
        if(name == null){
            errors.addError(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.VALIDATION_ERROR[0], CommonErrorCodeConfig.VALIDATION_ERROR[1], "Field: name shouldn't be null"));
        }
        if(!errors.getErrors().isEmpty()){
            throw new ErrorGeneralException(errors);
        }
    }
    
    @Override
    public void prepare() throws Exception {
        if(super.getId() == null)
            super.setId(IdGen.SIMPLE());
        super.setCreationTime(new Date());
        super.setStatus(StatusConfig.ACTIVE);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public String toString() {
        try {
            return new UtilSerializer(SystemComponent.class).deSerialize(this);
        } catch (Exception e) {
            return "Component{" + "\"name\":\"" + name + "\", "+super.toString()+'}';
        }
    }
    
}
