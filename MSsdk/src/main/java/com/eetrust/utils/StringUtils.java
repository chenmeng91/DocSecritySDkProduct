package com.eetrust.utils;

import com.eetrust.bean.DeptBean;
import com.eetrust.bean.UserBean;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenmeng on 2017/8/16.
 */

public class StringUtils {

    public static boolean isChineseLetter(String str){
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static boolean isCanAccerdit(String rights,List<UserBean> userBeens, List<DeptBean> deptBeens){
       boolean isCan = true;
        for(UserBean userBean : userBeens){
            String userRights = userBean.getRights();
            if(userRights!=null&&!"".equals(userRights)){
                String str[] = userRights.split(",");
                for(String st:str){
                    if(!rights.contains(st+",")){
                        isCan = false;
                        break;
                    }
                }
                if(!isCan){
                    break;
                }
            }
        }
        for (DeptBean deptBean:deptBeens){
            String deptRights = deptBean.getRights();
            if(deptRights!=null&&!"".equals(deptRights)){
                String str[] = deptRights.split(",");
                for(String st:str){
                    if(!rights.contains(st+",")){
                        isCan = false;
                        break;
                    }
                }
                if(!isCan){
                    break;
                }
            }
        }
        return isCan;

    }


    public static String getErrorMessage(String errorMessage,String defalutErrorMessage){
        String errorString = null;
        String errors[] = errorMessage.split(":");
        if (errors.length == 2) {
            errorString = errors[1];
        } else {
            errorString = errorMessage;
        }
        if(isChineseLetter(errorMessage)){
            return errorString;
        }else {
            if(errorString.contains("Interface query_docRights find user Confidential lt archives Confidential!")
                    ||errorMessage.contains("Interface query_docRights  user is not write list user!")){

                return "没有权限";
            }else {
                return defalutErrorMessage;
            }
        }

    }
}
