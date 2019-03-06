package com.example.lib_net.db;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjiao on 2019/3/6.
 * 表的属性
 */

public class TableEntity {
    public String mTableName; //表名
    private List<ColumnEntity> mList ;//所有的表字段

    public TableEntity(String tableName){
        this.mTableName = tableName;
        mList = new ArrayList<>();
    }
    public TableEntity addColumn(ColumnEntity columnEntity){
        mList.add(columnEntity);
        return this;
    }

    /**--------------建表语句----------------*/
    public String buildTableString(){
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXITS ");
        sb.append(mTableName).append("(");
        for (ColumnEntity columnEntity:mList){
            if(columnEntity.compositePrimaryKey!=null){
                sb.append("PRIMARY KEY (");
                for (String primaryKey:columnEntity.compositePrimaryKey){
                    sb.append(primaryKey).append(",");
                }
                sb.deleteCharAt(sb.length()-1);
                sb.append(")");
            }else{
                sb.append(columnEntity.columnName).append(" ").append(columnEntity.columnType);
                if(columnEntity.isPrimary){
                    sb.append(" PRIMARY KEY");
                }
                if(columnEntity.isNotNull){
                    sb.append(" NOT NULL");
                }
                if(columnEntity.isAutoincrement){
                    sb.append(" AUTOINCREMENT");
                }
                sb.append(",");
            }
        }
        if(sb.toString().endsWith(",")){
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append(")");
        return sb.toString();
    }

    /**--------------获得数据库表中字段名----------------*/
    public String getColumnName(int columnIndex){
        return mList.get(columnIndex).columnName;
    }
    /**--------------获取数据库表中字段数----------------*/
    public int getColumnCount(){
        return mList.size();
    }

    public int getColumnIndex(String columnName){
        for(int i=0;i<getColumnCount();i++){
            if(mList.get(i).columnName.equals(columnName)){
                return i;
            }
        }
        return -1;
    }


}
