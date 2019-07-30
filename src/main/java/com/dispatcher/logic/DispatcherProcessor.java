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
import com.dispatcher.config.StatusConfig;
import com.dispatcher.config.SystemConfig;
import com.dispatcher.entities.SystemComponent;
import com.dispatcher.entities.Command;
import com.dispatcher.facades.CommonQueries;
import com.dispatcher.facades.GenericDao;
import com.dispatcher.remote.HttpCall;
import com.dispatcher.utilities.Log;
import com.dispatcher.utilities.ReturnUtil;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 *
 * @author Aubain
 */
@Component
public class DispatcherProcessor {
    @Autowired
            CommonQueries commonQueries;
    @Autowired
            GenericDao genericDao;
    public List<SystemComponent> initSystemComponent(String token, String body, String txId, String sourceSystemId) throws ErrorGeneralException, Exception{
        List<SystemComponent> SystemComponents;
        List<SystemComponent> mySystemComponents = new ArrayList<>();
        
        //Serialize token and the body
        try {
            SystemComponents = new UtilSerializer(SystemComponent.class).serializeList(body);
        } catch (ErrorGeneralException e) {
            throw e;
        }catch (Exception e) {
            Log.d(getClass(), e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There was an internal issue while proccessing your request."))));
        }
        
        //prepare the body payload
        try {
            if(SystemComponents == null || SystemComponents.isEmpty()){
                throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "Serialization produced null results."))));
            }
            for(SystemComponent cpt : SystemComponents){
                cpt.prepare();
                cpt.validateOb();
                
                SystemComponent systemComponent = commonQueries.findSystem(cpt.getName());
                if(systemComponent != null){
                    List<Command> commands = commonQueries.listSystemCommand(systemComponent.getId());
                    if(commands != null && !commands.isEmpty()){
                        //remove commands
                        genericDao.delete(commands);
                    }
                    //remove SystemComponent
                    genericDao.delete(systemComponent);
                }
                
                mySystemComponents.add(cpt);
            }
        } catch (ErrorGeneralException e) {
            throw e;
        }catch (Exception e) {
            e.printStackTrace(out);
            Log.d(getClass(), e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There was an internal issue while proccessing your request."))));
        }
        
        try {
            //save SystemComponent
            mySystemComponents = genericDao.save(mySystemComponents);
            List<SystemComponent> response = new ArrayList<>();
            for(SystemComponent cpt : SystemComponents){
                SystemComponent mCpt = new SystemComponent();
                mCpt.setId(cpt.getId());
                mCpt.setName(cpt.getName());
                
                if(cpt.getCommands()!= null && !cpt.getCommands().isEmpty()){
                    //save parking in this area
                    List<Command> mCommands = new ArrayList<>();
                    for(Command cmd : cpt.getCommands()){
                        cmd.setSystem(mCpt);
                        mCommands.add(cmd);
                    }
                    cpt.setCommands(addCommands(token, new UtilSerializer(Command.class).deSerialize(mCommands), txId, sourceSystemId));
                }
                response.add(cpt);
            }
            return response;
        } catch(ErrorGeneralException e){
            throw e;
        }catch (Exception e) {
            e.printStackTrace(out);
            Log.d(getClass(), e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_DATABASE_ERROR[0], CommonErrorCodeConfig.GENERAL_DATABASE_ERROR[1], "There was an internal issue while proccessing your request."))));
        }
    }
    
    public List<Command> addCommands(String token, String body, String txId, String sourceSystemId) throws ErrorGeneralException, Exception{
        List<Command> commands;
        List<Command> myCommands = new ArrayList<>();
        try {
            commands = new UtilSerializer(Command.class).serializeList(body);
        } catch (ErrorGeneralException e) {
            throw e;
        }catch (Exception e) {
            Log.d(getClass(), e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There was an internal issue while proccessing your request."))));
        }
        
        //prepare the body payload
        try {
            if(commands == null || commands.isEmpty()){
                throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "Serialization produced null results."))));
            }
            for(Command cmd : commands){
                if(cmd.getSystem()== null){
                    throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.CONSTRAINT_ERROR[0], CommonErrorCodeConfig.CONSTRAINT_ERROR[1], "You are trying to create an olphan command without SystemComponent, "+cmd.toString()))));
                }
                //if(adr.getOrganization() != null && adr.getBranch() != null){
                //throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.CONSTRAINT_ERROR[0], CommonErrorCodeConfig.CONSTRAINT_ERROR[1], "You are trying to create a dual parented address with organization and branch, "+adr.toString()))));
                //}
                if(commonQueries.isCommandCreated(cmd.getCommand())){
                    throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.CONSTRAINT_ERROR[0], CommonErrorCodeConfig.CONSTRAINT_ERROR[1], "The command already exist, "+cmd.toString()))));
                }
                SystemComponent SystemComponent = genericDao.find(SystemComponent.class, cmd.getSystem().getId());
                if(SystemComponent == null)
                    throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.CONSTRAINT_ERROR[0], CommonErrorCodeConfig.CONSTRAINT_ERROR[1], "Constraint violation, We could not find SystemComponent associated with this command. "+cmd.toString()))));
                
                cmd.setSystemId(cmd.getSystem().getId());
                cmd.prepare();
                cmd.validateOb();                
                myCommands.add(cmd);
            }
        } catch (ErrorGeneralException e) {
            throw e;
        }catch (Exception e) {
            Log.d(getClass(), e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There was an internal issue while proccessing your request."))));
        }
        try {
            //save
            return genericDao.save(myCommands);
        } catch(ErrorGeneralException e){
            throw e;
        }catch (Exception e) {
            Log.d(getClass(), e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_DATABASE_ERROR[0], CommonErrorCodeConfig.GENERAL_DATABASE_ERROR[1], "There was an internal issue while proccessing your request."))));
        }
    }
    
    public ResponseEntity forwardRequest(Map<String, String> headers, String cmd, String token, String body, String txId, String sourceSystemId, Map<String, String[]> params) throws ErrorGeneralException, Exception{
        Command command;
        //Serialize token and the body
        try {
            if(cmd == null || cmd.isEmpty()){
                Log.d(getClass(), "Command is NULL or empty, ");
                throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "No routing is possible, the command is empty"))));
            }
        }  catch (ErrorGeneralException e) {
            throw e;
        }catch (Exception e) {
            e.printStackTrace(out);
            Log.d(getClass(), e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There was an internal issue while proccessing your request."))));
        }
        try {
            //find command
            command = commonQueries.findCommand(cmd);
            if(command == null){
                Log.d(getClass(), "Command not found, "+cmd);
                throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.INTERNAL_ERROR[0], CommonErrorCodeConfig.INTERNAL_ERROR[1], "Command not found, or you are not allowed to perform this action, "+cmd))));
            }else if(!command.getStatus().equals(StatusConfig.ACTIVE)){
                Log.d(getClass(), "Command deactivated, "+cmd);
                throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.INTERNAL_ERROR[0], CommonErrorCodeConfig.INTERNAL_ERROR[1], "Command deactivated, or you are not allowed to perform this action, "+cmd))));
            }
        } catch(ErrorGeneralException e){
            throw e;
        }catch (Exception e) {
            Log.d(getClass(), e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_DATABASE_ERROR[0], CommonErrorCodeConfig.GENERAL_DATABASE_ERROR[1], "There was an internal issue while proccessing your request."))));
        }
        
        try {
            if(command.getUri() == null || command.getUri().isEmpty()){
                throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "Route not found. Please contact system administrator."))));
            }
            if(command.getMethod() == null){
                throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "Dispatcher could not found HTTP method. Please contact system administrator."))));
            }
            String url = command.getUri();
            if(params != null && !params.isEmpty()){
                url = url + "?";
                for(Map.Entry<String, String[]> entry : params.entrySet()){
                    url += entry.getKey()+"="+entry.getValue()[0]+"&";
                }
                out.println("URL: "+url);
                url = url.substring(0, url.length()-1);
            }
            
            if(command.getMethod().equalsIgnoreCase("POST")){
                return new HttpCall().forwardPost(url, headers, body);
            }else if(command.getMethod().equalsIgnoreCase("GET")){ 
                return ReturnUtil.isFailed(HttpStatus.FORBIDDEN.value(), "Not allowed");
            }else{
                throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "Dispatcher supported HTTP methods require an update. Please contact system administrator."))));
            }
        } catch(ErrorGeneralException e){
            throw e;
        }catch (Exception e) {
            Log.d(getClass(), e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There was an internal issue while proccessing your request."))));
        }
    }
}
