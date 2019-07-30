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
import com.dispatcher.config.HeaderConfig;
import com.dispatcher.config.SystemConfig;
import com.dispatcher.models.ApiResponse;
import com.dispatcher.utilities.Log;
import com.dispatcher.utilities.ReturnUtil;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr /
 * aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662
 */
public class HttpCall {
    
    public ApiResponse rawPost(String url, Map<String, String> headers, String body, String mediaType) throws ErrorGeneralException, Exception{
        URL uri = null;
        try {
            uri = new URL(url);
        } catch (MalformedURLException | NullPointerException e) {
            Log.e(getClass(), e);
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There is an issue with the format of external API URL."))));
        }
        //setting up new connection
        HttpURLConnection con = (HttpURLConnection) uri.openConnection();
        try {
            //setting headers
            con.setRequestMethod("POST");
            for(Map.Entry<String, String> entry : headers.entrySet()){
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
            con.setRequestProperty("Content-Type", mediaType);
            
            //setting connection timeout
            //con.setConnectTimeout(6000);
            //con.setReadTimeout(6000);
            
            Log.d(getClass(), "External call request URL : "+url+" | body: "+body);
            Log.d(getClass(), "External call request header: "+headers.toString());
            
            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(body);
            wr.flush();
            wr.close();
        } catch (Exception e) {
            Log.e(getClass(), e);
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There is an issue while calling external API URL."))));
        }
        
        Integer statusCode = 400;
        String received = null;
        try {
            statusCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            received = response.toString() == null || response.toString().isEmpty() ? "" : response.toString();
            Log.d(getClass(), "External call response Status code:"+statusCode+" body: "+received);
        } catch (Exception ex) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                received = response.toString() == null || response.toString().isEmpty() ? "" : response.toString();
                Log.d(getClass(), "External call response Status code:"+statusCode+" body: "+received);
            } catch (Exception e) {
                Log.e(HttpCall.class, e);
                throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "They was an issue while proccessing the result from external API. "+e.getMessage()))));
            }
        }
        if(statusCode != 200){
            ErrorsListModel errors = null;
            try {
                errors = new ErrorsListModel().serialize(received);
            } catch (ErrorGeneralException e) {
                Log.e(getClass(), e);
                throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "External API responded with error code:"+statusCode))));
            }
            if(errors == null){
                throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "External API responded with error code:"+statusCode))));
            }
            //errors.addError(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.EXTERNAL_API_ERROR[0], CommonErrorCodeConfig.EXTERNAL_API_ERROR[1], "External API responded with error code:"+statusCode));
            throw new ErrorGeneralException(errors);
        }
        
        return new ApiResponse(statusCode, received);
    }
    public ResponseEntity forwardPost(String url, Map<String, String> headers, String body) throws ErrorGeneralException, Exception{
        URL uri = null;
        try {
            uri = new URL(url);
        } catch (MalformedURLException | NullPointerException e) {
            Log.e(getClass(), e);
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There is an issue with the format of external API URL."))));
        }
        //setting up new connection
        HttpURLConnection con = (HttpURLConnection) uri.openConnection();
        try {
            //setting headers
            for(Map.Entry<String, String> entry : headers.entrySet()){
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
            Log.d(getClass(), "External call POST request URL : "+url+" | body: "+body);
            Log.d(getClass(), "External call POST request header: "+headers.toString());
            
            String contentType = headers.get(HeaderConfig.CONTENT.toLowerCase());
            if(contentType == null || contentType.length() <= 0)
                contentType = "*/*";
            con.setRequestProperty("Content-Type", contentType);
            con.setRequestMethod("POST");
            
            //send post payload
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(body);
            wr.flush();
            wr.close();
        } catch (Exception e) {
            Log.e(getClass(), e);
            throw new ErrorGeneralException(new biz.galaxy.commons.models.ErrorsListModel(Arrays.asList(new biz.galaxy.commons.models.ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There is an issue while calling external API URL."))));
        }
        Integer statusCode = 400;
        String received = null;
        try {
            statusCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            received = response.toString() == null || response.toString().isEmpty() ? "" : response.toString();
            Log.d(getClass(), "External call response Status code:"+statusCode+" body: "+received);
        } catch (Exception ex) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                received = response.toString() == null || response.toString().isEmpty() ? "" : response.toString();
                Log.d(getClass(), "External call response Status code:"+statusCode+" body: "+received);
            } catch (Exception e) {
                Log.e(HttpCall.class, e);
                throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "They was an issue while proccessing the result from external API. "+e.getMessage()))));
            }
        }
        if(statusCode != 200){
            ErrorsListModel errors = null;
            try {
                errors = new ErrorsListModel().serialize(received);
            } catch (ErrorGeneralException e) {
                Log.e(getClass(), e);
                errors = new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "External API responded with error code:"+statusCode)));
            }
            if(errors == null){
                errors = new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "External API responded with error code:"+statusCode)));
            }
            //errors.addError(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.EXTERNAL_API_ERROR[0], CommonErrorCodeConfig.EXTERNAL_API_ERROR[1], "External API responded with error code:"+statusCode));
            return ReturnUtil.isFailed(statusCode, new UtilSerializer(ErrorsListModel.class).deSerialize(errors));
        }
        
        return ReturnUtil.isSuccess(received);
    }
}
