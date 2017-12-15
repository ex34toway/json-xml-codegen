package uw.codegen.jsonxml.example;

import com.google.common.base.CaseFormat;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;
import org.json.JSONException;
import org.json.XML;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import java.util.Map;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

/**
 * 生成静态内部类
 * @author liliang
 * @since 2017/9/13
 */
public class GenerationTool {

    private JSONTokener jsonTokener;

    /**
     * 是否引入了List
     */
    private boolean importList = false;

    /**
     * 引了那些Java对象,如java.util.Date,方便之后支持对象类型
     */
    private Set<String> importTypeSet = Sets.newHashSet();

    private String filePath;

    private String fileName;

    private String mainProName;

    private Map<String,JavaType> mainProp;

    private List<Json2JavaVo> json2JavaVoList = Lists.newArrayList();

    private Set<String> typeSet = Sets.newHashSet();

    /**
     * 换行符
     */
    private final static String lineSeparator = System.getProperty("line.separator");

    /**
     * 默认四个空格对齐
     */
    private final static String propW = Strings.padEnd("",4,' ');

    /**
     * 类型分隔器
     */
    private final static Splitter splitter = Splitter.on("$");

    private static Date now = new Date();

    private GenerationConfig generationConfig;

    private AnnotationStyle annotationStyle = AnnotationStyle.JACKSON2;

    private Predicate<Json2JavaVo> findPredicate = new Predicate<Json2JavaVo>() {
        @Override
        public boolean apply(Json2JavaVo input) {
            return input.getPropName().equals(mainProName);
        }
    };

    public GenerationTool(GenerationConfig config,String fileName,String json) {
        this.generationConfig = config;
        this.annotationStyle = config.getAnnotationStyle();
        // 文件名,可内部修饰
        this.fileName = firstUpperCaseCamel(StringUtils.substringBeforeLast(fileName,"."));
        this.filePath = config.getOutDirectory() + File.separator + config.getPackageName().replace('.', File.separatorChar);
        this.jsonTokener = new JSONTokener(json);
    }

    /**
     * 是否需要驼峰式命名
     * @return
     */
    private boolean needCamel(){
        return !this.generationConfig.isCamel();
    }

    public boolean run(){
        Object curPoint = jsonTokener.nextValue();
        JSONObject next = null;
        while(!(curPoint instanceof JSONObject)){
            try {
                curPoint = jsonTokener.nextValue();
            }catch (JSONException e){
                return false;
            }
        }
        next = (JSONObject)curPoint;
        mainProp = Maps.newTreeMap();
        long rootId = 0;
        while (next != JSONObject.NULL) {
            JSONArray jsonArray = next.names();
            String[] nameList = jsonArray.toList().toArray(new String[0]);
            // XML响应头元素
            if(generationConfig.getGenerationType() == GenerationType.XML) {
                if (StringUtils.isBlank(mainProName) && nameList.length > 0) {
                    mainProName = nameList[0];
                    fileName = mainProName;// XML 默认就是Camel
                }
            }
            for (int i = 0; i < nameList.length; i++) {
                String name = nameList[i];
                Object prop = next.get(name);
                JavaType javaType = null;
                if(prop instanceof JSONObject){// 对象
                    // 继续迭代
                    javaType = new JavaType(needCamel() ? firstUpperCaseCamel(name) : firstToUpper(name),"");
                    mainProp.put(name,javaType);
                    getJavaObject(name,javaType.getType(),rootId,(JSONObject)prop);
                }else if(prop instanceof JSONArray){//数组
                    JSONArray array = (JSONArray)prop;
                    if(array.length() ==0)
                        javaType = new JavaType("Object","");
                    else {
                        Object object = array.get(0);
                        if (object instanceof JSONObject) {
                            javaType = new JavaType(needCamel() ? firstUpperCaseCamel(name) : firstToUpper(name),"");

                            // 登记一个类型
                            getJavaObject(name,javaType.getType(), rootId, (JSONObject) object);
                        } else {
                            javaType = getJavaType(object);
                        }
                    }
                    importList = true;
                    mainProp.put(name,new JavaType(javaType.getType(),true,""));
                }else{
                    mainProp.put(name,getJavaType(prop));
                }
            }

            // 没到尾,接着跑
            if (jsonTokener.more()) {
                next = (JSONObject) jsonTokener.nextValue();
            }else{
                break;
            }
        }
        // 如果是生成XML,则直接以根节点做为Java的最外层类
        if(generationConfig.getGenerationType() == GenerationType.XML){
            if(annotationStyle == AnnotationStyle.JAXB ||
                    annotationStyle == AnnotationStyle.XSTREAM ||
                    annotationStyle == AnnotationStyle.JACKSON2) {
                Json2JavaVo it = Iterators.find(json2JavaVoList.iterator(), findPredicate);
                mainProp = it.getPropMap();
                Iterators.removeIf(json2JavaVoList.iterator(),findPredicate);
            }
        }
        write();
        return true;
    }

    /**
     * 生成式入口
     * @return
     */
    public static void generator(GenerationConfig config) throws IOException {
        File sourceDirectory = config.getSourceDirectory();
        File targetPackageDir = new File(config.getOutDirectory() + File.separator + config.getPackageName().replace('.', File.separatorChar));
        if(!targetPackageDir.exists()){
            targetPackageDir.mkdirs();
        }
        empty(targetPackageDir,".java");
        GenerationType sourceType = config.getGenerationType();
        Collection<File> sourceFileCollection =
                FileUtils.listFiles(sourceDirectory,sourceType == GenerationType.JSON ?
                                new String[]{"json"}: new String[]{"xml"},true);
        String[] includeFiles = config.getIncludeFiles();
        Set<String> filterSet = null;
        boolean filter = false;
        if(includeFiles != null && includeFiles.length> 0){
            filterSet = Sets.newHashSet(includeFiles);
            filter = true;
        }
        for(File sourceFile : sourceFileCollection){
            String fileName = sourceFile.getName();
            if(filter && !filterSet.contains(fileName))
                continue;
            System.out.println(String.format("generate for %s... start",fileName));
            String sourceString = FileUtils.readFileToString(sourceFile);
            GenerationTool generator = null;
            if(sourceType == GenerationType.JSON){
                generator = new GenerationTool(config,sourceFile.getName(),sourceString);
            }else{
                JSONObject xmlJSONObj = XML.toJSONObject(sourceString);
                String json = xmlJSONObj.toString();
                generator = new GenerationTool(config,sourceFile.getName(),json);
            }
            generator.run();
            System.out.println(String.format("generate for %s... end",fileName));
        }
        System.out.println("all generate java file is ok.");
    }

    /**
     * 生成属性getter,setter
     * @param out
     * @param propMap
     */
    public void writeGetterSetter(StringWriter out, Map<String,JavaType> propMap,int indent){
        for (Map.Entry<String,JavaType> typeEntry : propMap.entrySet()){
            JavaType javaType = typeEntry.getValue();
            String typeName = javaType.getType();
            String name = typeEntry.getKey();
            String javaName = toCamel(name);
            String getterSetterName = needCamel() ? firstUpperCaseCamel(name) : firstToUpper(name);
            out.write(String.format("%spublic void set%s(%s %s) {%s",Strings.repeat(propW,indent),getterSetterName,typeName,javaName,lineSeparator));
            out.write(String.format("%sthis.%s = %s;%s%s}",Strings.repeat(propW,indent+1),javaName,javaName,lineSeparator,Strings.repeat(propW,indent)));
            // 统一写GET上
            writeMethodAnnotation(AnnotationType.METHOD_GET,out,name,javaType,indent);
            out.write(String.format("%s%spublic %s get%s (){%s",lineSeparator,Strings.repeat(propW,indent),typeName,getterSetterName,lineSeparator));
            out.write(String.format("%sreturn this.%s;%s%s}%s%s",Strings.repeat(propW,indent+1),javaName,lineSeparator,Strings.repeat(propW,indent),lineSeparator,lineSeparator));
        }
    }

    /**
     * java doc 注释
     * @param out
     * @param comment
     * @param author
     */
    public void writeJavaDoc(StringWriter out,String comment,String author)
    {
        out.write(String.format("%s/**%s",lineSeparator,lineSeparator));
        out.write(String.format(" * This class is auto generated by tools.%s",lineSeparator));

        if (StringUtils.isNotBlank(comment)) {// 不适合 paragraph
            out.write(String.format(" * %s %s",comment,lineSeparator));
        }
        if(StringUtils.isNotBlank(author)){
            out.write(String.format(" * @author %s %s", author,lineSeparator));
        }

        out.write(String.format(" * @since %s %s", DateFormatUtils.format(now,"yyyy-MM-dd"),lineSeparator));

        out.write(" */");
    }

    /**
     * JavaDoc 字段描述
     * @param out
     * @param javaType
     */
    public void writePropertyJavaDoc(StringWriter out,JavaType javaType,int indent){
        if(generationConfig.isGenerateJavaDoc()){
            String description = javaType.getDescription();
            out.append(Strings.repeat(propW,indent)).append(String.format("/**%s",lineSeparator))
               .append(Strings.repeat(propW,indent)).append(" * ").append(StringUtils.trimToEmpty(description)).append(lineSeparator)
               .append(Strings.repeat(propW,indent)).append(" */").append(lineSeparator);
        }
    }

    /**
     * 写注解
     * @param type - 注解类型
     * @param out - 输出流
     * @param propName - 属性名称
     * @param typeName - 对应Java类型名
     * @param indent - 当前对齐位置
     */
    public void writeMethodAnnotation(AnnotationType type,StringWriter out,String propName,JavaType typeName,int indent){
        StringBuilder builder = new StringBuilder();
        String javaType  = "";
        if(type != AnnotationType.CLASS)
            javaType = typeName.getType();
        switch (annotationStyle){
            case JACKSON2:
                if(generationConfig.getGenerationType() == GenerationType.JSON) {
                    switch (type) {
                        case CLASS:
                            builder.append(lineSeparator)
                                    .append(Strings.repeat(propW, indent - 1))
                                    .append("@JsonInclude(JsonInclude.Include.NON_NULL)");
                            break;
                        case PROPERTY:
                            if (generationConfig.isGenerateJavaDoc()) {
                                writePropertyJavaDoc(out, typeName, indent);
                            }
                            break;
                        case METHOD_GET:
                            builder.append(lineSeparator).append(lineSeparator)
                                    .append(Strings.repeat(propW, indent))
                                    .append("@JsonProperty(\"").append(propName).append("\")");
                            break;
                    }
                }else { // XML
                    switch (type) {
                        case CLASS:
                            builder.append(lineSeparator)
                                    .append(Strings.repeat(propW, indent - 1))
                                    .append("@JacksonXmlRootElement(localName = \"").append(propName).append("\")");
                            break;
                        case PROPERTY:
                            if (generationConfig.isGenerateJavaDoc()) {
                                writePropertyJavaDoc(out, typeName, indent);
                            }
                            break;
                        case METHOD_GET:
                            // @JacksonXmlElementWrapper(useWrapping=false)

                            if (typeName.isArray()) {
                                builder.append(lineSeparator).append(lineSeparator)
                                        .append(Strings.repeat(propW, indent))
                                        .append("@JacksonXmlElementWrapper(useWrapping=false)")
                                        .append(lineSeparator)
                                        .append(Strings.repeat(propW, indent))
                                        .append("@JacksonXmlProperty(localName=\"").append(propName).append("\")");
                            }else{
                                builder.append(lineSeparator).append(lineSeparator)
                                        .append(Strings.repeat(propW, indent))
                                        .append("@JacksonXmlProperty(localName=\"").append(propName).append("\")");
                            }
                            break;
                    }
                }
                break;
            case XSTREAM:
                switch (type){
                    case CLASS:
                        builder.append(lineSeparator)
                                .append(Strings.repeat(propW,indent-1))
                                .append("@XStreamAlias(\"").append(propName).append("\")");
                        break;
                    case PROPERTY:
                        if (generationConfig.isGenerateJavaDoc()) {
                            writePropertyJavaDoc(out, typeName, indent);
                        }
                        if(typeName.isArray()){
                            builder.append(Strings.repeat(propW,indent)).append("@XStreamImplicit")
                                    .append(lineSeparator);
                        }else{
                            builder.append(Strings.repeat(propW,indent)).append("@XStreamAlias(\"").append(propName).append("\")")
                                    .append(lineSeparator);
                        }
                        break;
                }
                break;
            case JAXB:
                switch (type){
                    case CLASS:
                        builder.append(lineSeparator)
                                .append(Strings.repeat(propW,indent-1))
                                .append("@XmlRootElement(name = \"").append(propName).append("\")");
                        break;
                    case PROPERTY:
                        if (generationConfig.isGenerateJavaDoc()) {
                            writePropertyJavaDoc(out, typeName, indent);
                        }
                        break;
                    case METHOD_GET://写GET上就好了
                        builder.append(lineSeparator).append(Strings.repeat(propW,indent))
                                .append("@XmlElement(name = \"").append(propName).append("\")")
                                .append(lineSeparator);
                        break;
                }
        }
        out.write(builder.toString());
    }

    /**
     * 写到文件
     */
    public void write(){
        StringWriter out = new StringWriter();
        out.write(String.format("package %s;%s",generationConfig.getPackageName(),lineSeparator));
        out.write(lineSeparator);

        switch (annotationStyle){
            case JACKSON2:
                switch (generationConfig.getGenerationType()){
                    case JSON:
                        out.write(String.format("import com.fasterxml.jackson.annotation.JsonInclude;%s",lineSeparator));
                        out.write(String.format("import com.fasterxml.jackson.annotation.JsonProperty;%s",lineSeparator));
                        out.write(String.format("import com.fasterxml.jackson.annotation.JsonPropertyOrder;%s",lineSeparator));
                        break;
                    case XML:
                        out.write(String.format("import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;%s",lineSeparator));
                        out.write(String.format("import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;%s",lineSeparator));
                        out.write(String.format("import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;%s",lineSeparator));
                        break;
                }
                break;
            case JAXB:
                out.write(String.format("import javax.xml.bind.annotation.XmlElement;%s",lineSeparator));
                out.write(String.format("import javax.xml.bind.annotation.XmlRootElement;%s",lineSeparator));
                break;
            case XSTREAM:
                out.write(String.format("import com.thoughtworks.xstream.annotations.XStreamAlias;%s",lineSeparator));
                out.write(String.format("import com.thoughtworks.xstream.annotations.XStreamImplicit;%s",lineSeparator));
                out.write(String.format("import com.thoughtworks.xstream.annotations.XStreamAsAttribute;%s",lineSeparator));
                break;
        }

        if(importList|| !importTypeSet.isEmpty())
            out.write(lineSeparator);

        // Java类型引入
        if(importList){
            out.write(String.format("import java.util.List;%s",lineSeparator));
            out.write(String.format("import java.util.ArrayList;%s",lineSeparator));
        }
        if(!importTypeSet.isEmpty()){
            for(String importPath : importTypeSet){
                out.write(String.format("import %s;%s",importPath,lineSeparator));
            }
        }
        boolean isFirstLine = true;

        if(generationConfig.isGenerateJavaDoc())
            writeJavaDoc(out,generationConfig.getComment(),generationConfig.getAuthor());

        // main class
        writeMethodAnnotation(AnnotationType.CLASS,out,mainProName,null,1);
        out.write(String.format("%spublic class %s {",lineSeparator,fileName));
        out.write(lineSeparator);
        for (Map.Entry<String,JavaType> typeEntry : mainProp.entrySet()) {
            JavaType javaType = typeEntry.getValue();
            String typeName = javaType.getType();
            String name = typeEntry.getKey();
            String javaName = toCamel(name);
            if (isFirstLine) {
                out.write(lineSeparator);
            }
            writeMethodAnnotation(AnnotationType.PROPERTY, out, name, javaType, 1);
            if (javaType.isPrimitive()) {
                out.write(String.format("%sprivate %s %s;%s", propW, typeName, javaName, lineSeparator));
            } else {
                out.write(String.format("%sprivate %s %s = %s;%s", propW, typeName, javaName, javaType.defaultValue(), lineSeparator));
            }
            out.write(lineSeparator);
            isFirstLine = false;
        }
        writeGetterSetter(out,mainProp,1);

        // inner static class
        if(!json2JavaVoList.isEmpty()){
            int indent = 2;// FIXME static class no need to care about inner order
            isFirstLine = true;
            for(Json2JavaVo json2JavaVo : json2JavaVoList){
                String className = json2JavaVo.getClassName();
                String propName = json2JavaVo.getPropName();
                Map<String,JavaType> propMap = json2JavaVo.getPropMap();

                // other class
                if(!isFirstLine)
                    out.write(lineSeparator);
                writeMethodAnnotation(AnnotationType.CLASS,out,propName,new JavaType(className,""),indent);
                out.write(String.format("%s%spublic static class %s {%s",lineSeparator,Strings.repeat(propW,indent-1),className,lineSeparator));
                for (Map.Entry<String,JavaType> typeEntry : propMap.entrySet()) {
                    JavaType javaType = typeEntry.getValue();
                    String typeName = javaType.getType();
                    String name = typeEntry.getKey();
                    String javaName = toCamel(name);
                    writeMethodAnnotation(AnnotationType.PROPERTY, out, name, javaType, indent);
                    if(javaType.isPrimitive()){
                        out.write(String.format("%sprivate %s %s;%s",Strings.repeat(propW,indent),typeName,javaName,lineSeparator));
                    }else{
                        out.write(String.format("%sprivate %s %s = %s;%s", Strings.repeat(propW, indent),
                                    typeName, javaName,javaType.defaultValue(), lineSeparator));
                    }
                    out.write(lineSeparator);
                }
                writeGetterSetter(out,propMap,indent);
                out.write(String.format("%s}",Strings.repeat(propW,indent-1)));
                isFirstLine = false;
            }
        }

        out.write(String.format("%s}",lineSeparator));

        FileWriter fw = null;
        try {
            fw = new FileWriter(filePath+File.separator+fileName+".java");
            fw.write(out.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 首字母大写
     * @param str
     * @return
     */
    private static String firstToUpper(String str){
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuffer(strLen)
                .append(Character.toUpperCase(str.charAt(0)))
                .append(str.substring(1))
                .toString();
    }

    /**
     * 首字母小写
     * @param str
     * @return
     */
    private static String firstToLower(String str){
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuffer(strLen)
                .append(Character.toLowerCase(str.charAt(0)))
                .append(str.substring(1))
                .toString();
    }

    /**
     * 下划线转首字母大写Camel
     * @param str
     * @return
     */
    private static String firstUpperCaseCamel(String str){
        if(StringUtils.isNotBlank(str))
            return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL,str);
        return "";
    }

    /**
     * 下划线转首字母小写Camel
     * @param str
     * @return
     */
    private static String firstLowerCaseCamel(String str){
        if(StringUtils.isNotBlank(str))
            return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,str);
        return "";
    }

    /**
     * toCamel
     * @param str
     * @return
     */
    private String toCamel(String str){
        if(generationConfig.isCamel())
            return str;
        return firstLowerCaseCamel(str);
    }

    /**
     * If file is a directory, recursively empty its children.
     * If file is a file, delete it
     */
    private static void empty(File file, String suffix) {
        if (file != null) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();

                if (children != null) {
                    for (File child : children) {
                        empty(child, suffix);
                    }
                }
            } else {
                if (file.getName().endsWith(suffix)) {
                    file.delete();
                }
            }
        }
    }

    /**
     * 转换属性类型
     * @param prop
     * @return
     */
    private JavaType getJavaType(Object prop){
        // 前面是类型，中间用$分割，后面是属性描述
        if(prop instanceof String && ((String) prop).contains("$")){
            List<String> dataList = splitter.splitToList((String)prop);
            String type = dataList.get(0);
            String description = dataList.size() > 1 ? dataList.get(1) : "";
            JavaType javaType = new JavaType(type,description);
            if(javaType.needImport()){
                importTypeSet.add(javaType.getImportPath());
            }
            return javaType;
        }
        // 如果解析时发现没有$，则默认为String类型，里面的东西都是属性描述
        return new JavaType("String",String.valueOf(prop));
    }

    /**
     * 对象登记
     * @param className
     * @param jsonObject
     * @return
     */
    private String getJavaObject(String propName,String className,long pId,JSONObject jsonObject){
        String typeLabel = pId+className;
        if(typeSet.contains(typeLabel))
            return null;
        typeSet.add(typeLabel);
        Json2JavaVo json2JavaVo = new Json2JavaVo(propName,firstToUpper(className),pId);
        long psId = json2JavaVo.getId();
        Map<String,JavaType> propMap = Maps.newHashMap();
        Iterator<String> key = jsonObject.keys();
        while (key.hasNext()){
            String name = String.valueOf(key.next());
            Object prop = jsonObject.get(name);
            JavaType javaType = null;
            if(prop instanceof JSONObject){// 对象
                // 继续迭代
                javaType = new JavaType(needCamel() ? firstUpperCaseCamel(name) : firstToUpper(name),"");
                propMap.put(name,javaType);
                getJavaObject(name,javaType.getType(),psId,(JSONObject)prop);
            }else if(prop instanceof JSONArray){//数组
                JSONArray array = (JSONArray)prop;
                if(array.length() ==0)
                    javaType = new JavaType("Object","");
                else {
                    Object object = array.get(0);
                    if (object instanceof JSONObject) {
                        javaType = new JavaType(needCamel() ? firstUpperCaseCamel(name) : firstToUpper(name),"");
                        // 登记一个类型
                        getJavaObject(name,javaType.getType(), psId, (JSONObject) object);
                    } else {
                        javaType = getJavaType(object);
                    }
                }
                importList = true;
                propMap.put(name,new JavaType(javaType.getType(),true,""));
            }else{
                propMap.put(name,getJavaType(prop));
            }
        }
        json2JavaVo.setPropMap(propMap);
        json2JavaVoList.add(json2JavaVo);
        return json2JavaVo.getClassName();
    }
}