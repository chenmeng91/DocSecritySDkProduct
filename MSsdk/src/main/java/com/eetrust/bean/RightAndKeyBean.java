package com.eetrust.bean;

/**
 * Created by chenmeng on 2016/11/1.
 */

public class RightAndKeyBean {
    private long id;
    private   String loginName;
    private  long docId;
    private  long archiveId;
    private  String right;
    private  String key;

    public RightAndKeyBean(long id, String loginName, long docId, long archiveId, String right, String key) {
        this.id = id;
        this.loginName = loginName;
        this.docId = docId;
        this.archiveId = archiveId;
        this.right = right;
        this.key = key;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public long getDocId() {
        return docId;
    }

    public void setDocId(long docId) {
        this.docId = docId;
    }

    public long getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(long archiveId) {
        this.archiveId = archiveId;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
