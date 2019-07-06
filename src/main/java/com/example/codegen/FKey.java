package com.example.codegen;

import java.io.Serializable;

public class FKey implements Serializable {

    public FKey(){}

    private String fkColumnName;//外键名称
    private String pkTableName;//外键依赖表名称
    private String pkColumnName;//外键依赖表字段名称

    public String getFkColumnName() {
        return fkColumnName;
    }

    public void setFkColumnName(String fkColumnName) {
        this.fkColumnName = fkColumnName;
    }

    public String getPkTableName() {
        return pkTableName;
    }

    public void setPkTableName(String pkTableName) {
        this.pkTableName = pkTableName;
    }

    public String getPkColumnName() {
        return pkColumnName;
    }

    public void setPkColumnName(String pkColumnName) {
        this.pkColumnName = pkColumnName;
    }
}
