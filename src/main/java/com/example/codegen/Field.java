package com.example.codegen;

import java.io.Serializable;

public class Field implements Serializable {
    public Field() {
    }

    public Field(String name, String dataType, String upName, String columnName, String columnType) {
        this.name = name;
        this.dataType = dataType;
        this.upName = upName;
        this.columnName = columnName;
        this.columnType = columnType;
    }

    private String name;//首字母小写的属性名称
    private String dataType;//属性的java数据类型
    private String upName;//首字母大写的属性名称
    private String columnName;//列明
    private String columnType;//列数据类型

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getUpName() {
        return upName;
    }

    public void setUpName(String upName) {
        this.upName = upName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }
}
