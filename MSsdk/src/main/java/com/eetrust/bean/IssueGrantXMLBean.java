package com.eetrust.bean;

/**
 * Created by sunh on 16-9-23.
 */
public class IssueGrantXMLBean {
    private String name;
    private String identifier;
    private String oldPath;
    private long archiveId;
    private long versionId;
    private long docId;

    public IssueGrantXMLBean(long archiveId, long versionId) {
        this.archiveId = archiveId;
        this.versionId = versionId;
    }

    public IssueGrantXMLBean(String name, String oldPath, String identifier) {
        this.name = name;
        this.oldPath = oldPath;
        this.identifier = identifier;
    }

    public long getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(long archiveId) {
        this.archiveId = archiveId;
    }

    public long getDocId() {
        return docId;
    }

    public void setDocId(long docId) {
        this.docId = docId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOldPath() {
        return oldPath;
    }

    public void setOldPath(String oldPath) {
        this.oldPath = oldPath;
    }

    public long getVersionId() {
        return versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }
}
