/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.dispatcher.logic;

import biz.galaxy.commons.config.CommonErrorCodeConfig;
import biz.galaxy.commons.models.ErrorModel;
import biz.galaxy.commons.models.ErrorsListModel;
import biz.galaxy.commons.utilities.ErrorGeneralException;
import biz.galaxy.commons.utilities.serializer.UtilSerializer;
import com.dispatcher.config.SystemConfig;
import com.dispatcher.entities.SystemComponent;
import com.dispatcher.entities.Command;
import com.dispatcher.facades.CommonQueries;
import com.dispatcher.facades.GenericDao;
import com.dispatcher.utilities.Log;
import com.dispatcher.utilities.QueryWrapper;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Aubain
 */
@Component
public class FilterProcessor {
    @Autowired
            GenericDao genericDao;
    @Autowired
            CommonQueries commonQueries;
    public List<SystemComponent> filterSystemComponent(String token, String body, String txId, String sourceSystemId)throws ErrorGeneralException, Exception{
        SystemComponent filter = null;
        //Serialize token and the body
        try {
            filter = new UtilSerializer(SystemComponent.class).serialize(body);
        } catch (ErrorGeneralException e) {
            throw e;
        }catch (Exception e) {
            e.printStackTrace(out);
            Log.d(getClass(), e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There was an internal issue while proccessing your request."))));
        }
        try {
            if(filter == null)
                return genericDao.findAll(SystemComponent.class);
            else{
                QueryWrapper queryWrapper = new QueryWrapper();
                StringBuilder query = new StringBuilder("select A from SystemComponent A where A.id is not null ");
                if(filter.getId() != null){
                    query.append("AND A.id = :id ");
                    queryWrapper.setParameter("id", filter.getId());
                }
                if(filter.getCreationTimeRange() != null && (filter.getCreationTimeRange().getStartDate() != null && filter.getCreationTimeRange().getEndDate() != null)){
                    query.append(" AND (A.creationTime >= :startDate AND A.creationTime <= :endDate) ");
                    queryWrapper.setParameter("startDate", filter.getCreationTimeRange().getStartDate()).setParameter("endDate", filter.getCreationTimeRange().getEndDate());
                }else if(filter.getCreationTime() != null){
                    query.append(" AND A.creationTime = :creationTime ");
                    queryWrapper.setParameter("creationTime", filter.getCreationTime());
                }
                if(filter.getName()!= null){
                    query.append("AND A.name = :name ");
                    queryWrapper.setParameter("name", filter.getName());
                }
                if(filter.getStatus()!= null){
                    query.append("AND A.status = :status ");
                    queryWrapper.setParameter("status", filter.getStatus());
                }
                if(filter.getIsDesc() != null && filter.getIsDesc()){
                    query.append("Order by A.creationTime DESC ");
                }
                if(filter.getPageNumber() != null){
                    queryWrapper.setPageNumber(filter.getPageNumber());
                }
                if(filter.getPageSize()!= null){
                    queryWrapper.setPageNumber(filter.getPageSize());
                }
                queryWrapper.setQuery(query.toString());
                List<SystemComponent> mSystemComponents = commonQueries.filter(queryWrapper);
                List<SystemComponent> Systemcomponents = new ArrayList<>();
                for(SystemComponent cpt : mSystemComponents){
                    List<Command> commands = commonQueries.listSystemCommand(cpt.getId());
                    if(commands == null || commands.isEmpty()){
                        commands = new ArrayList<>();
                    }
                    cpt.setCommands(commands);
                    Systemcomponents.add(cpt);
                }
                return Systemcomponents;
            }
        } catch(ErrorGeneralException e){
            throw e;
        }catch (Exception e) {
            Log.d(getClass(), e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There was an internal issue while proccessing your request."))));
        }
    }
    
    public List<Command> filterCommand(String token, String body, String txId, String sourceSystemId)throws ErrorGeneralException, Exception{
        Command filter;
        //IpfundoToken ipfundoToken;
        //ProfileModel staffProfile;
        
        //Serialize token and the body
        try {
            //ipfundoToken = TokenUtil.serialize(token);
            //staffProfile = ipfundoToken.getProfile();
            filter = new UtilSerializer(Command.class).serialize(body);
        } catch (ErrorGeneralException e) {
            throw e;
        }catch (Exception e) {
            e.printStackTrace(out);
            Log.d(getClass(), e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There was an internal issue while proccessing your request."))));
        }
        try {
            if(filter == null)
                return genericDao.findAll(Command.class);
            else{
                QueryWrapper queryWrapper = new QueryWrapper();
                StringBuilder query = new StringBuilder("select A from Command A where A.id is not null ");
                if(filter.getId() != null){
                    query.append("AND A.id = :id ");
                    queryWrapper.setParameter("id", filter.getId());
                }
                if(filter.getCreationTimeRange() != null && (filter.getCreationTimeRange().getStartDate() != null && filter.getCreationTimeRange().getEndDate() != null)){
                    query.append(" AND (A.creationTime >= :startDate AND A.creationTime <= :endDate) ");
                    queryWrapper.setParameter("startDate", filter.getCreationTimeRange().getStartDate()).setParameter("endDate", filter.getCreationTimeRange().getEndDate());
                }else if(filter.getCreationTime() != null){
                    query.append(" AND A.creationTime = :creationTime ");
                    queryWrapper.setParameter("creationTime", filter.getCreationTime());
                }
                if(filter.getCommand()!= null){
                    query.append("AND A.command = :command ");
                    queryWrapper.setParameter("command", filter.getCommand());
                }
                if(filter.getMethod()!= null){
                    query.append("AND A.method = :method ");
                    queryWrapper.setParameter("method", filter.getMethod());
                }
                if(filter.getUri()!= null){
                    query.append("AND A.uri = :uri ");
                    queryWrapper.setParameter("uri", filter.getUri());
                }
                if(filter.getSystem()!= null){
                    query.append("AND A.systemId = :systemId ");
                    queryWrapper.setParameter("systemId", filter.getSystem().getId());
                }
                if(filter.getStatus()!= null){
                    query.append("AND A.status = :status ");
                    queryWrapper.setParameter("status", filter.getStatus());
                }
                if(filter.getIsDesc() != null && filter.getIsDesc()){
                    query.append("Order by A.creationTime DESC ");
                }
                if(filter.getPageNumber() != null){
                    queryWrapper.setPageNumber(filter.getPageNumber());
                }
                if(filter.getPageSize()!= null){
                    queryWrapper.setPageNumber(filter.getPageSize());
                }
                queryWrapper.setQuery(query.toString());
                List<Command> mCommands = commonQueries.filter(queryWrapper);
                List<Command> commands = new ArrayList<>();
                for(Command cmd : mCommands){
                    SystemComponent systemcomponent = genericDao.find(SystemComponent.class, cmd.getSystemId());
                    if(systemcomponent == null){
                        systemcomponent = new SystemComponent();
                        systemcomponent.setId(cmd.getSystemId());
                    }
                    cmd.setSystem(systemcomponent);
                    commands.add(cmd);
                }
                return commands;
            }
        } catch(ErrorGeneralException e){
            throw e;
        }catch (Exception e) {
            Log.d(getClass(), e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There was an internal issue while proccessing your request."))));
        }
    }
}
