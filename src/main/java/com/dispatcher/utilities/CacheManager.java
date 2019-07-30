/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.dispatcher.utilities;

import com.dispatcher.config.CasheConfig;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Aubain
 */
public class CacheManager {
    private static final LoadingCache<String, Object> cache = CacheBuilder.newBuilder()
            .maximumSize(CasheConfig.TOKEN_STACK_VALUE)
            .expireAfterWrite(CasheConfig.TOKEN_TTL_VALUE, TimeUnit.HOURS)
            .build(new CacheLoader<String, Object>() {
                @Override
                public Object load(String key) throws Exception {
                    return loadObject(key);
                }
                
            });
    
    
    private static <T> T loadObject(String key) {
        return (T) new Object();
    }
    
    public static void ADD(String key, Object object){
        cache.put(key, object);
    }
    public static Object GET(String key){
        return cache.getIfPresent(key);
    }
    public static boolean IS_AVAILABLE(String key){
        try {
            return cache.getIfPresent(key) != null;
        } catch (Exception e) {
            Log.e(CacheManager.class, e);
            return false;
        }        
    }
    public static void CLEAR (){
        cache.cleanUp();
    }
}
