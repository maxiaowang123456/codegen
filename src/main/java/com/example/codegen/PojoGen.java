package com.example.codegen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * POJO生成器
 */
public class PojoGen {

    private static final String RESOURCE_FILE_PATH="codegenResource.properties";
    private static final String CONVERT_FILE_PATH="dataConvert.properties";

    /**
     * 获取所有的Pojo
     * @return
     * @throws Exception
     */
    public static List<Pojo> getAll() throws Exception {
        List<Pojo>pojoList=new ArrayList<>();
        Connection conn=getConnection();
        DatabaseMetaData dbMeataData=conn.getMetaData();
        ResultSet resultSet=dbMeataData.getTables(conn.getCatalog(),null,null,new String[]{"TABLE"});
        Pojo pojo;
        while(resultSet.next()){
            pojo=new Pojo();
            pojo.setTableName(resultSet.getString("TABLE_NAME"));
            pojo.setClassName(convertName(pojo.getTableName()));
            pojo.setFieldList(getFieldList(pojo.getTableName(),conn));
            pojo.setPackageName(PropertiesUtil.getProperty("pojo.package",RESOURCE_FILE_PATH));
            pojo.setfKeyList(getFKeyList(pojo.getTableName(),conn));
            pojo.setpKeyList(getPKeyList(pojo.getTableName(),conn));
            changeFieldList(pojo);
            pojoList.add(pojo);
        }
        adjuestPojoRelationsByTableFKey(pojoList);
        close(conn,resultSet);
        return pojoList;
    }

    /**
     * 根据一个或多个表名生成Pojo信息
     * @param tables
     * @return
     * @throws Exception
     */
    public static List<Pojo> getPojoListByTable(String ...tables)throws  Exception{
        List<Pojo>pojoList=new ArrayList<>();
        Connection conn=getConnection();
        Pojo pojo;
        for(String table:tables){
            pojo=new Pojo();
            pojo.setTableName(table);
            pojo.setClassName(convertName(pojo.getTableName()));
            pojo.setFieldList(getFieldList(pojo.getTableName(),conn));
            pojo.setPackageName(PropertiesUtil.getProperty("pojo.package",RESOURCE_FILE_PATH));
            pojo.setfKeyList(getFKeyList(pojo.getTableName(),conn));
            pojo.setpKeyList(getPKeyList(pojo.getTableName(),conn));
            changeFieldList(pojo);
            pojoList.add(pojo);
        }
        adjuestPojoRelationsByTableFKey(pojoList);
        close(conn);
        return pojoList;
    }

    /**
     * 根据表外键调整Pojo与Pojo之间的关系（例如一对一，一对多，多对多）
     * @param pojoList
     */
    private static void adjuestPojoRelationsByTableFKey(List<Pojo> pojoList) {
        for(Pojo pojo:pojoList){
            if(pojo.getfKeyList().size()>0){
                for(FKey fKey:pojo.getfKeyList()){
                    Pojo toPojo=findPojoByFKey(fKey,pojoList);
                    if(toPojo!=null){
                        Field field=new Field();
                        field.setName(pojo.getClassName().toLowerCase().charAt(0)+pojo.getClassName().substring(1)+"List");
                        field.setUpName(pojo.getClassName()+"List");
                        field.setDataType("List");
                        toPojo.getFieldList().add(field);
                    }
                }
            }
        }
    }

    /**
     * 根据外键列名查找依赖的表Pojo
     * @param fKey
     * @param pojoList
     * @return
     */
    private static Pojo findPojoByFKey(FKey fKey, List<Pojo> pojoList) {
        for(Pojo pojo:pojoList){
            if(pojo.getTableName().equalsIgnoreCase(fKey.getPkTableName())){
                return pojo;
            }
        }
        return null;
    }

    /**
     * 获取jdbc连接
     * @return
     * @throws Exception
     */
    private static Connection getConnection() throws Exception {
        Class.forName(PropertiesUtil.getProperty("jdbc_driverClassName",RESOURCE_FILE_PATH));
        return DriverManager.getConnection(
                PropertiesUtil.getProperty("jdbc_url",RESOURCE_FILE_PATH),
                PropertiesUtil.getProperty("jdbc_username",RESOURCE_FILE_PATH),
                PropertiesUtil.getProperty("jdbc_password",RESOURCE_FILE_PATH));
    }

    /**
     * 资源关闭操作
     * @param closeables
     * @throws Exception
     */
    private static void close(AutoCloseable...closeables) throws Exception{
        for(AutoCloseable closeable:closeables){
            if(closeable!=null){
                closeable.close();
            }
        }
    }

    /**
     * 根据外键调整pojo的属性数据类型
     * @param pojo
     */
    private static void changeFieldList(Pojo pojo) {
        List<FKey>fKeyList=pojo.getfKeyList();
        List<Field>fieldList=pojo.getFieldList();
        for(FKey fKey:fKeyList){
            for(Field field:fieldList){
                if(fKey.getFkColumnName().equalsIgnoreCase(field.getColumnName())){
                    String name=convertName(fKey.getPkTableName());
                    field.setName(name.toLowerCase().charAt(0)+name.substring(1));
                    field.setDataType(name);
                    field.setUpName(name);
                }
            }
        }
        pojo.setFieldList(fieldList);
    }

    /**
     * 获取主键
     * @param tableName
     * @param conn
     * @return
     * @throws Exception
     */
    private static List<String> getPKeyList(String tableName, Connection conn) throws Exception{
        List<String>pkeyList=new ArrayList<>();
        DatabaseMetaData metaData=conn.getMetaData();
        ResultSet pkeyResult=metaData.getPrimaryKeys(conn.getCatalog(),null,tableName);
        while(pkeyResult.next()){
          pkeyList.add(pkeyResult.getString("COLUMN_NAME"));
        }
        close(pkeyResult);
        return pkeyList;
    }

    /**
     * 获取表的外键数据
     * @param tableName
     * @param conn
     * @return
     * @throws Exception
     */
    private static List<FKey> getFKeyList(String tableName, Connection conn) throws  Exception{
        List<FKey>fkeyList=new ArrayList<>();
        DatabaseMetaData metaData=conn.getMetaData();
        ResultSet fkeyResult=metaData.getImportedKeys(conn.getCatalog(),null,tableName);
        FKey fKey;
        while(fkeyResult.next()){
            fKey=new FKey();
            fKey.setFkColumnName(fkeyResult.getString("FKCOLUMN_NAME"));
            fKey.setPkTableName(fkeyResult.getString("PKTABLE_NAME"));
            fKey.setPkColumnName(fkeyResult.getString("PKCOLUMN_NAME"));
            fkeyList.add(fKey);
        }
        close(fkeyResult);
        return fkeyList;
    }

    /**
     * 获取表对应的列名
     * @param tableName
     * @param conn
     * @return
     * @throws Exception
     */
    private static List<Field> getFieldList(String tableName, Connection conn) throws Exception {
        List<Field>fieldList=new ArrayList<>();
        DatabaseMetaData metaData=conn.getMetaData();
        ResultSet columnResult=metaData.getColumns(conn.getCatalog(),
                null,tableName,"%");
        Field field;
        while(columnResult.next()){
            field= new Field();
            field.setColumnName(columnResult.getString("COLUMN_NAME"));
            field.setColumnType(columnResult.getString("TYPE_NAME"));
            String fieldUpName=convertName(field.getColumnName());
            field.setName(fieldUpName.toLowerCase().charAt(0)+fieldUpName.substring(1));
            field.setUpName(fieldUpName);
            field.setDataType(PropertiesUtil.getProperty(field.getColumnType(),CONVERT_FILE_PATH));
            fieldList.add(field);
        }
        close(columnResult);
        return fieldList;
    }



    /**
     * 表名转化成类名/列名转化成属性名
     * @param name
     * @return
     */
    private static String convertName(String name) {
        String[]listStr=name.split("_");
        StringBuilder sb=new StringBuilder();
        for(String str:listStr){
            sb.append(str.toUpperCase().charAt(0));
            String fNext=str.length()>1?str.toLowerCase().substring(1):"";
            sb.append(fNext);
        }
        return sb.toString();
    }

    public static  void main(String[] args) throws Exception {
        List<Pojo> pojos=getAll();
        for(Pojo pojo:pojos){
            System.out.println(pojo.getTableName()+"=="+pojo.getClassName()+"=="+pojo.getPackageName());
            List<Field>fieldList=pojo.getFieldList();
            for(Field field:fieldList){
                System.out.println(field.getColumnName()+"==="+field.getColumnType()
                        +"||"+field.getName()+","+field.getDataType()+","+field.getUpName());
            }
        }
    }
}
