package uw.codegen.jsonxml.example;

import java.io.File;

/**
 * @author liliang
 * @since 2017/9/13
 */
public interface GenerationConfig {

    /**
     * 生成源的类型 XML or Json
     * @return
     */
    public GenerationType getGenerationType();

    /**
     * 生成Java 注解类型
     * @return
     */
    public AnnotationStyle getAnnotationStyle();

    /**
     * 是否为骆峰式命名
     */
    public boolean isCamel();

    /**
     * 源文件目录
     * @return
     */
    public File getSourceDirectory();

    /**
     * 要生成的文件列表
     * @return
     */
    public String[] getIncludeFiles();

    /**
     * 生成目录
     * @return
     */
    public File getOutDirectory();

    /**
     * 生成包路径
     * @return
     */
    public String getPackageName();

    /**
     * 是否生成Java文档注释
     * @return
     */
    public boolean isGenerateJavaDoc();

    /**
     * 生成作者
     * @return
     */
    public String getAuthor();

    /**
     * 生成类注释
     * @return
     */
    public String getComment();
}
