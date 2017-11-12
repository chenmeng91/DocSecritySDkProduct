package com.eetrust.bean;

/**
 * Created by chenmeng on 2017/5/31.
 */

public class DeptBean {
    private String outerid;//部门唯一标识
    private String rights;//权限

    public DeptBean(String outerid, String rights) {
        this.outerid = outerid;
        this.rights = rights;
    }

    public String getOuterid() {
        return outerid;
    }

    public void setOuterid(String outerid) {
        this.outerid = outerid;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }
}
