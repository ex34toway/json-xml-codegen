package uw.codegen.jsonxml.example;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

/**
 * Java类型信息
 * @author liliang
 * @since 2017/10/10
 */
public class JavaType {

    /**
     * 原生类型信息
     */
    private final static Map<String,String> PRIMITIVE_TYPE_MAP =
            new ImmutableMap.Builder<String,String>()
                    .put("byte","")
                    .put("int","")
                    .put("short","")
                    .put("long","")
                    .put("float","")
                    .put("double","")
                    .put("boolean","")
                    .put("char","")
                    .put("String","") // 把String归为原始类型方便处理
                    .build();

    private final static Map<String,String> NEED_IMPORT_TYPE =
            new ImmutableMap.Builder<String,String>()
                    .put("Date","java.util.Date").build();

    /**
     * 装箱类型
     */
    private final static ImmutableBiMap<String,String> BOXED_TYPE_MAP =
            new ImmutableBiMap.Builder<String,String>()
                     .put("byte","Byte")
                     .put("int","Integer")
                     .put("short","Short")
                     .put("long","Long")
                     .put("float","Float")
                     .put("double","Double")
                     .put("boolean","Boolean")
                     .put("char","Character")
                     .build();

    private final String type;

    private final String description;

    private final boolean isList;

    public JavaType(String type, String description){
        this.type = type;
        this.description = description;
        this.isList = false;
    }

    public JavaType(String type,boolean isList,String description){
        this.type = type;
        this.description = description;
        this.isList = isList;
    }

    public String getType() {
        return isArray() ? "List<"+this.type+">" : this.type;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 是否需要导包
     * @return
     */
    public boolean needImport(){
        return NEED_IMPORT_TYPE.containsKey(this.type);
    }

    /**
     * 默认初始值
     * @return
     */
    public String defaultValue(){
        if(!isPrimitive()){
            if(!isList)
                return String.format("new %s()",this.type);
            else
                return String.format("new ArrayList<%s>()",this.type);
        }
        return null;
    }

    /**
     * 获取导入路径
     * @return
     */
    public String getImportPath(){
        return NEED_IMPORT_TYPE.get(this.type);
    }

    /**
     * 是否原生类型
     * @return
     */
    public boolean isPrimitive() {
        return PRIMITIVE_TYPE_MAP.containsKey(this.type);
    }

    /**
     * 是否是数组
     * @return
     */
    public boolean isArray(){
        return isList;
    }

    /**
     * 装箱
     * @return
     */
    public String box() {
        if(BOXED_TYPE_MAP.containsKey(type))
            return BOXED_TYPE_MAP.get(type);
        return type;
    }

    /**
     * 拆箱
     * @return
     */
    public String unbox() {
        if(BOXED_TYPE_MAP.containsValue(type))
            return BOXED_TYPE_MAP.inverse().get(type);
        return type;
    }
}
