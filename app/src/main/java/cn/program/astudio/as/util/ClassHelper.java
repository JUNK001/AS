package cn.program.astudio.as.util;

/**
 * Created by JUNX on 2016/8/17.
 */
public class ClassHelper {

    public static Object getField(Object object,String fieldName){
        Object ret=null;
        try {
            ret= object.getClass().getSuperclass().getField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
