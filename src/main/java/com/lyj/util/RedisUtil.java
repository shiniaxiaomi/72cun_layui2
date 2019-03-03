package com.lyj.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/3/2.
 */
public class RedisUtil {

    //将object转成int类型
    public static int toInt(Object object){
        if(object==null){
            return 0;
        }else if(object.getClass()==String.class){
            return Integer.valueOf((String)object);
        }else if(object.getClass()==Integer.class){
            return (int)object;
        }else if(object.getClass()==Long.class){
            return ((Long)object).intValue();
        }else if(object.getClass()==Double.class){
            return (int)((double)object*10)/10;
        }else{
            return -1;
        }
    }

    public static byte[] toByte(Object object){
        if(object==null){
            return "".getBytes();
        }else if(object.getClass()==String.class){
            return String.valueOf(object).getBytes();
        }else if(object.getClass()==Integer.class){
            return String.valueOf(object).getBytes();
        }else if(object.getClass()==Long.class){
            return String.valueOf(object).getBytes();
        }else if(object.getClass()==Double.class){
            return String.valueOf(((int)((double)object*10)/10)).getBytes();
        }else{
            return "-1".getBytes();
        }
    }



    public static void main(String[] args) {
//        Object object=0.78;
//        int i = RedisUtil.toInt(object);
//        System.out.println(i);

//        System.out.println(RedisUtil.toByte(object));
    }


    public static byte[][] toByteArray(List<Integer> ids) {
        byte[][] list=new byte[ids.size()][];
        for(int i=0;i<ids.size();i++){
            list[i]=String.valueOf(ids.get(i)).getBytes();
        }
        return list;
    }

    public static byte[][] toByteArray(Object[] ids) {
        byte[][] list=new byte[ids.length][];
        for(int i=0;i<ids.length;i++){
            list[i]=String.valueOf(ids[i]).getBytes();
        }
        return list;
    }

    public static String toString(Object value) {
        if(value==null){
            return "";
        }else{
            return (String)value;
        }
    }
}
