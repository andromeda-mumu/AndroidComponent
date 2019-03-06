package com.example.lib_net.db;

/**
 * Created by wangjiao on 2019/3/6.
 * 表字段的属性
 */

public class ColumnEntity {
    public String columnName; //列名
    public String columnType; //列类型
    public String[] compositePrimaryKey;//复合主键  yyy
    public boolean isPrimary;
    public boolean isNotNull;
    public boolean isAutoincrement; //是否自增

    /**
     *
     * @param compositePrimaryKey 复合主键
     */

    public ColumnEntity(String... compositePrimaryKey){
        this.compositePrimaryKey = compositePrimaryKey;
    }
    public ColumnEntity(String columnName, String columnType){
       this(columnName,columnType,false,false,false);
    }
    public ColumnEntity(String columnName, String columnType,boolean isPrimary,boolean isNotNull){
       this(columnName,columnType,isPrimary,isNotNull,false);
    }

    public ColumnEntity(String columnName, String columnType,boolean isPrimary,boolean isNotNull,boolean isAutoincrement){
        this.columnName = columnName;
        this.columnType = columnType;
        this.isPrimary = isPrimary;
        this.isNotNull = isNotNull;
        this.isAutoincrement = isAutoincrement;
    }



}
