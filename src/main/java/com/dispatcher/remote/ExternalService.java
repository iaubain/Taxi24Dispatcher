/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.dispatcher.remote;

import biz.galaxy.commons.config.CommonErrorCodeConfig;
import biz.galaxy.commons.models.ErrorModel;
import biz.galaxy.commons.models.ErrorsListModel;
import biz.galaxy.commons.utilities.ErrorGeneralException;
import biz.galaxy.commons.utilities.serializer.UtilSerializer;
import com.dispatcher.config.SystemConfig;
import com.dispatcher.config.UrlConfig;
import com.dispatcher.entities.SystemComponent;
import com.dispatcher.models.ApiResponse;
import com.dispatcher.utilities.Log;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 *
 * @author ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr /
 * ishimwe.aubain.consolateur@galaxygroup.biz Tel: +250 785 534 672 / +250 736 864 662
 */
public class ExternalService {
    public static final List<SystemComponent> INIT(List<SystemComponent> component) throws ErrorGeneralException, Exception{
        HttpCall htppCall = new HttpCall();
        Map<String, String> headers = new HashMap<>();
        headers.put("cmd", UrlConfig.INIT_COMPONENT);
        ApiResponse response;
        try {
            response = htppCall.rawPost(UrlConfig.SYSTEM_DISPATCHER, headers, new UtilSerializer(Component.class).deSerialize(component), "application/json");
            if(response == null){
                throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.PARSING_ERROR[0], CommonErrorCodeConfig.PARSING_ERROR[1], "Unparsable NULL result from Internal component."))));
            }
            if(response.getStatusCode() != 200){
                throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.EXTERNAL_API_ERROR[0], CommonErrorCodeConfig.EXTERNAL_API_ERROR[1], "Requested failed and remote status: "+response.getStatusCode()))));
            }
            return new UtilSerializer(SystemComponent.class).serializeList(response.getBody().toString());
        } catch(ErrorGeneralException e){
            throw e;
        }catch (Exception e) {
            Log.d(ExternalService.class, e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There was an issue while processing external API response."))));
        }
    }
    public static final ResponseEntity FORWARD_REQUEST(Map<String, String> headers, String url, String body)throws ErrorGeneralException, Exception{
        HttpCall httpCall = new HttpCall();
        try {
            return httpCall.forwardPost(url, headers, body);           
        } catch(ErrorGeneralException e){
            throw e;
        }catch (Exception e) {
            Log.d(ExternalService.class, e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There was an issue while processing external API response."))));
        }
    }
}
