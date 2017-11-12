package com.eetrust.bean;

/**
 * Created by chenmeng on 2017/5/31.
 */

public class UserBean {
    private String identifier;//用户唯一标识
    private String rights;//权限

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public UserBean(String identifier, String rights) {
        this.identifier = identifier;
        this.rights = rights;
    }
}
