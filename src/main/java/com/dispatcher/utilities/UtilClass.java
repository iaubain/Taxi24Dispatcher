/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dispatcher.utilities;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Aubain
 */
public class UtilClass {
    public static List<Field> getFields(Class mClass) {
        List<Field> fields = new ArrayList<>();
        
        while (mClass != Object.class) {
            fields.addAll(Arrays.asList(mClass.getDeclaredFields()));
            if(mClass.getSuperclass() != null)
                mClass = mClass.getSuperclass();
        }
        return fields;
    }
}
