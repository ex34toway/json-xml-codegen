package uw.codegen.jsonxml.example;

import java.util.Map;

/**
 * 生成配置类
 * @author liliang
 * @since 2017/9/13
 */
public class Json2JavaVo {

    private String propName;

    private String className = null;

    private Map<String,JavaType> propMap;

    private long prvId;

    private long id = System.currentTimeMillis();

    public Json2JavaVo(String propName,String className,long prvId) {
        this.propName = propName;
        this.className = className;
        this.prvId = prvId;
    }

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Map<String, JavaType> getPropMap() {
        return propMap;
    }

    public void setPropMap(Map<String, JavaType> propMap) {
        this.propMap = propMap;
    }

    public long getPrvId() {
        return prvId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
