package com.example.codegen;

import java.io.Serializable;
import java.util.List;

public class Pojo implements Serializable {

    public Pojo() {
    }

    private String className;
    private String packageName;
    private String tableName;
    private List<Field>fieldList;
    private List<FKey>fKeyList;
    private List<String>pKeyList;

    public List<String> getpKeyList() {
        return pKeyList;
    }

    public void setpKeyList(List<String> pKeyList) {
        this.pKeyList = pKeyList;
    }

    public List<FKey> getfKeyList() {
        return fKeyList;
    }

    public void setfKeyList(List<FKey> fKeyList) {
        this.fKeyList = fKeyList;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<Field> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<Field> fieldList) {
        this.fieldList = fieldList;
    }

}
