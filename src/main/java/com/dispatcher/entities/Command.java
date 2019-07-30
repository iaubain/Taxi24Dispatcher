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
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
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
@Table(name = "Command",
        indexes = {@Index(name = "idx_1", columnList = "systemId"),
            @Index(name = "idx_2", columnList = "command"),
            @Index(name = "idx_3", columnList = "creationTime"),
            @Index(name = "idx_5", columnList = "status"),
            @Index(name = "idx_6", columnList = "uri"),
            @Index(name = "idx_7", columnList = "method")})
public class Command extends BaseEntity implements Serializable, UtilModel {
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 90)
    @Column(name = "command", length = 90, unique = true)
    private String command;
    @Basic(optional = false)
    @Column(name = "commandDescr", length = 255)
    private String commandDescr;
    @Basic(optional = false)
    @Column(name = "request", length = 523)
    private String request;
    @Basic(optional = false)
    @Column(name = "response", length = 523)
    private String response;
    @Basic(optional = false)
    @Column(name = "requestHeader", length = 255)
    private String requestHeader;
    @Basic(optional = false)
    @Column(name = "method", length = 120)
    private String method;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "uri", length = 255)
    private String uri;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "systemId", length = 255)
    @JsonIgnore
    private String systemId;
    @Basic(optional = false)
    @Column(name = "exported")
    private boolean exported;
    
    @Transient
    private SystemComponent system;
    
    public Command() {
        super();
    }
    
    public Command(String command, String commandDescr, String request, String response, String requestHeader, String method, String uri, String systemId, boolean exported, String status, Date creationTime) {
        super(status, creationTime);
        this.command = command;
        this.commandDescr = commandDescr;
        this.request = request;
        this.response = response;
        this.requestHeader = requestHeader;
        this.method = method;
        this.uri = uri;
        this.systemId = systemId;
        this.exported = exported;
    }
    
    @Override
    public void validateOb() throws ErrorGeneralException {
        ErrorsListModel errors = new ErrorsListModel();
        if(this == null){
            errors.addError(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.VALIDATION_ERROR[0], CommonErrorCodeConfig.VALIDATION_ERROR[1], "Null object"));
        }
        
        if(command == null){
            errors.addError(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.VALIDATION_ERROR[0], CommonErrorCodeConfig.VALIDATION_ERROR[1], "Field: command shouldn't be null"));
        }
        if(uri == null){
            errors.addError(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.VALIDATION_ERROR[0], CommonErrorCodeConfig.VALIDATION_ERROR[1], "Field: uri shouldn't be null"));
        }
        if(system == null){
            errors.addError(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.VALIDATION_ERROR[0], CommonErrorCodeConfig.VALIDATION_ERROR[1], "Field: system shouldn't be null"));
        }
        
        if(!errors.getErrors().isEmpty()){
            throw new ErrorGeneralException(errors);
        }
    }
    
    @Override
    public void prepare() throws Exception {
        if(super.getId() == null)
            super.setId(IdGen.SIMPLE());
        systemId = system.getId();
        super.setCreationTime(new Date());
        super.setStatus(StatusConfig.ACTIVE);
    }
    
    public SystemComponent getSystem() {
        return system;
    }
    
    public void setSystem(SystemComponent system) {
        this.system = system;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    public String getCommandDescr() {
        return commandDescr;
    }
    
    public void setCommandDescr(String commandDescr) {
        this.commandDescr = commandDescr;
    }
    
    public String getRequest() {
        return request;
    }
    
    public void setRequest(String request) {
        this.request = request;
    }
    
    public String getResponse() {
        return response;
    }
    
    public void setResponse(String response) {
        this.response = response;
    }
    
    public String getRequestHeader() {
        return requestHeader;
    }
    
    public void setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public String getUri() {
        return uri;
    }
    
    public void setUri(String uri) {
        this.uri = uri;
    }
    
    public String getSystemId() {
        return systemId;
    }
    
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
    
    public boolean isExported() {
        return exported;
    }
    
    public void setExported(boolean exported) {
        this.exported = exported;
    }
    
    @Override
    public String toString() {
        try {
            return new UtilSerializer(Command.class).deSerialize(this);
        } catch (Exception e) {
            return "Command{" + "\"command\":\"" + command + "\", \"commandDescr\":\"" + commandDescr + "\", \"request\":\"" + request + "\", \"response\":\"" + response + "\", \"requestHeader\":\"" + requestHeader + "\", \"method\":\"" + method + "\", \"uri\":\"" + uri + "\", \"systemId\":\"" + systemId + "\", \"exported\":\"" + exported + "\", \"system\":" + system.toString() +super.toString()+'}';
        }
    }
    
}
