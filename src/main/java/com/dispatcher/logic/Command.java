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
import com.dispatcher.config.CmdConfig;
import com.dispatcher.config.HeaderConfig;
import com.dispatcher.config.SystemConfig;
import com.dispatcher.config.UrlConfig;
import com.dispatcher.entities.SystemComponent;
import com.dispatcher.remote.ExternalService;
import com.dispatcher.utilities.CacheManager;
import com.dispatcher.utilities.Log;
import com.dispatcher.utilities.ReturnUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 *
 * @author aubain
 */
@Component
public class Command {
    @Autowired
            DispatcherProcessor dispatcherProcessor;
    @Autowired
            FilterProcessor filterProcessor;
    public ResponseEntity exec(Map<String,String> headers, String body, Map<String, String[]> params){
        try {
            String command = headers.get(HeaderConfig.CMD).toUpperCase();
            String token = headers.get(HeaderConfig.AUTH_KEY);
            String txId = headers.get(HeaderConfig.TXID);
            String sourceSysId = headers.get(HeaderConfig.SOURCE_SYS_ID);
            for(CmdConfig cmd : CmdConfig.values()){
                if(cmd.toString().equals(command) && cmd == CmdConfig.INIT_COMPONENT){
                    return ReturnUtil.isSuccess(new UtilSerializer(SystemComponent.class).deSerialize(dispatcherProcessor.initSystemComponent(token, body, txId, sourceSysId)));
                }else if(cmd.toString().equals(command) && cmd == CmdConfig.ADD_COMMAND){
                    return ReturnUtil.isSuccess(new UtilSerializer(Command.class).deSerialize(dispatcherProcessor.addCommands(token, body, txId, sourceSysId)));
                }else if(cmd.toString().equals(command) && cmd == CmdConfig.FILTER_COMMAND){
                    return ReturnUtil.isSuccess(new UtilSerializer(Command.class).deSerialize(filterProcessor.filterCommand(token, body, txId, sourceSysId)));
                }else if(cmd.toString().equals(command) && cmd == CmdConfig.FILTER_COMPONENT){
                    return ReturnUtil.isSuccess(new UtilSerializer(SystemComponent.class).deSerialize(filterProcessor.filterSystemComponent(token, body, txId, sourceSysId)));
                }else if(cmd.toString().equals(command) && cmd == CmdConfig.SAVE_TOKEN){
                    CacheManager.ADD(token, body);
                    return ReturnUtil.isSuccess(body);
                }else if(cmd.toString().equals(command) && cmd == CmdConfig.OUT_BOUND){
                    //forward request to the gateway
                    try {
                        Map<String, String> hds = new HashMap<>();
                        for(Map.Entry entry : headers.entrySet()){
                            String value = entry.getValue().toString();
                            if(value.length() > 2)
                                value = value.substring(1, value.length()-1);
                            String key = entry.getKey().toString();
                            hds.put(key, value);
                        }
                        return ExternalService.FORWARD_REQUEST(hds, UrlConfig.SYSTEM_GATEWAY, body);
                    } catch(ErrorGeneralException e){
                        return ReturnUtil.isFailed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
                    }catch (Exception e) {
                        Log.d(getClass(), e.getMessage());
                        ErrorGeneralException error = new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There is an issue with request. Contact your system administrator."))));
                        return ReturnUtil.isFailed(HttpStatus.BAD_REQUEST.value(), error.getMessage());
                    }
                }
            }
            Map<String, String> mHeaders = new HashMap<>();
            for(Map.Entry<String, String> entry : headers.entrySet()){
                String value = entry.getValue();
                if(value.length() > 2 && value.contains("[") && value.contains("]"))
                    value = value.substring(1, value.length()-1);
                String key = entry.getKey();
                mHeaders.put(key, value);
            }
            
            return dispatcherProcessor.forwardRequest(mHeaders, command, token, body, txId, sourceSysId, params);
        } catch(ErrorGeneralException e){
            return ReturnUtil.isFailed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }catch (Exception e) {
            Log.d(getClass(), e.getMessage());
            ErrorGeneralException error = new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There is an issue with request. Contact your system administrator."))));
            return ReturnUtil.isFailed(HttpStatus.BAD_REQUEST.value(), error.getMessage());
        }
    }
}
