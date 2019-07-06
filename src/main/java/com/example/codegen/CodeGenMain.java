package com.example.codegen;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodeGenMain {

    private static final String POJO_TEMPLATE_NAME="pojo.vm";
    private static final String RESOURCE_FILE_PATH="codegenResource.properties";


    /**
     * 根据数据库表结构生成java文件
     * @throws Exception
     */
    public static void codegen()throws Exception {
        List<Pojo>pojoList=PojoGen.getAll();
        for(Pojo pojo:pojoList){
            codegenPojo(pojo);
        }
    }

    /**
     * 根据属性文件中配置的表名生成java文件
     * @throws Exception
     */
    public static void codegenPartion()throws Exception{
        List<Pojo>pojoList=PojoGen.getPojoListByTable(
                PropertiesUtil.getProperty("table_name",RESOURCE_FILE_PATH).split(","));
        for(Pojo pojo:pojoList){
            codegenPojo(pojo);
        }
    }

    /**
     * 根据pojo生成java文件
     * @param pojo
     * @throws Exception
     */
    private static void codegenPojo(Pojo pojo) throws Exception{
        VelocityEngine ve=new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER,"classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        Template t=ve.getTemplate(POJO_TEMPLATE_NAME);
        VelocityContext vct=new VelocityContext();
        writeContextData(vct,pojo);
        String pojoPath=createPojoFileDictory(pojo.getPackageName());
        String fileName=pojoPath+File.separator+pojo.getClassName()+".java";
        writePojoFile(fileName,t,vct);
    }

    /**
     * 模板写变量数据
     * @param vct
     * @param pojo
     */
    private static void writeContextData(VelocityContext vct,Pojo pojo) {
        vct.put("pojo",pojo);
        List importList=createImportList(pojo);
        vct.put("importList",importList);
        vct.put("fieldList",pojo.getFieldList());
    }

    /**
     * 根据属性的数据类型（过滤掉基本数据类型）生成import列表数据
     * @param pojo
     * @return
     */
    private static List createImportList(Pojo pojo) {
        List<Field>fieldList=pojo.getFieldList();
        String[]baseDataType=new String[]{"byte","short","char","int","long","double","float","boolean","String"};
        List<String> baseTypeList= Arrays.asList(baseDataType);
        List<String> dataTypeList=new ArrayList();
        for(Field field:fieldList){
            String dataType=field.getDataType();
            //过滤掉基本数据类型和重复的数据类型
            if((!baseTypeList.contains(dataType))&&(!dataTypeList.contains(dataType))){
                dataTypeList.add(dataType);
            }
        }
        List importList=new ArrayList();
        for(String dataType:dataTypeList){
            String importData=PropertiesUtil.getProperty(dataType,"dataConvert.properties");
            if(importData!=null){
                importList.add(importData);
            }
        }
        return  importList;
    }


    /**
     * 生成pojo文件
     * @param fileName
     * @param t
     * @param vct
     * @throws Exception
     */
    private static void writePojoFile(String fileName, Template t, VelocityContext vct) throws Exception{
        File fileNew=new File(fileName);
        if(fileNew.exists()){
            fileNew.delete();
        }
        PrintWriter pw=new PrintWriter(fileName);
        t.merge(vct,pw);
        pw.flush();
    }

    /**
     * 根据package生成文件路径
     * @param packageName
     * @return
     */
    private static String createPojoFileDictory(String packageName) {
        StringBuilder sb=new StringBuilder(
                PropertiesUtil.getProperty("code.filepath","codegenResource.properties"));
        String[]packageArray=packageName.split("\\.");
        for(String str:packageArray){
            sb.append(File.separator+str);
        }
        File file=new File(sb.toString());
        if(!file.exists()){
            file.mkdirs();
        }
        return sb.toString();
    }

    public static void main(String[]args) throws Exception{
//        codegen();
        codegenPartion();
    }

}
