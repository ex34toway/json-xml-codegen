package uw.codegen.jsonxml.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import uw.codegen.jsonxml.example.AnnotationStyle;
import uw.codegen.jsonxml.example.GenerationConfig;
import uw.codegen.jsonxml.example.GenerationTool;
import uw.codegen.jsonxml.example.GenerationType;

import java.io.File;
import java.io.IOException;

/**
 * Mojo配置类
 * @author liliang
 */
@Mojo(name = "generate")
public class MavenPlugin extends AbstractMojo implements GenerationConfig {

    /**
     * 生成源的类型 xml or json
     */
    @Parameter(defaultValue = "JSON",required = true)
    private String generationType;

    /**
     * 生成Java 注解类型，目前支持JACKSON2,FASTJSON
     */
    @Parameter(defaultValue = "JACKSON2",required = true)
    private String annotationStyle;

    /**
     * 是否为骆峰式命名
     */
    @Parameter(defaultValue = "true",required = false)
    private boolean camel;

    /**
     * 源文件目录
     */
    @Parameter(defaultValue = "src/main/resources",required = true)
    private File sourceDirectory;

    /**
     * 包含文件
     */
    @Parameter
    private String[] includeFiles;

    /**
     * 生成目录
     */
    @Parameter(defaultValue = "target/generated-sources/zwy",required = true)
    private File outDirectory;

    /**
     * 生成包路径
     */
    @Parameter(defaultValue = "com.zwy.example",required = true)
    private String packageName;

    /**
     * 是否生成Java文档注释
     */
    @Parameter(defaultValue = "true",required = true)
    private boolean generateJavaDoc;

    /**
     * 生成作者
     */
    @Parameter(defaultValue = "",required = false)
    private String author;

    /**
     * 生成注释
     */
    @Parameter(defaultValue = "",required = false)
    private String comment;

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    /**
     * 生成源的类型 XML or Json
     * @return
     */
    @Override
    public GenerationType getGenerationType() {
        return GenerationType.valueOf(generationType.toUpperCase());
    }

    /**
     * 生成Java 注解类型
     * @return
     */
    @Override
    public AnnotationStyle getAnnotationStyle() {
        return AnnotationStyle.valueOf(annotationStyle.toUpperCase());
    }

    /**
     * 是否是骆峰式命名
     */
    public boolean isCamel(){
        return camel;
    }

    /**
     * 源文件目录
     * @return
     */
    @Override
    public File getSourceDirectory() {
        return sourceDirectory == null ?
                getGenerationType() == GenerationType.JSON ?
                        new File("src/main/resources/json") :
                        new File("src/main/resources/xml") :
                sourceDirectory;
    }

    /**
     * 要生成的文件列表
     * @return
     */
    @Override
    public String[] getIncludeFiles() {
        return includeFiles;
    }

    /**
     * 生成目录
     * @return
     */
    @Override
    public File getOutDirectory() {
        return outDirectory;
    }

    /**
     * 生成包路径
     * @return
     */
    @Override
    public String getPackageName() {
        return packageName;
    }

    /**
     * 是否生成Java文档注释
     * @return
     */
    @Override
    public boolean isGenerateJavaDoc() {
        return generateJavaDoc;
    }

    /**
     * 生成作者
     * @return
     */
    @Override
    public String getAuthor() {
        return author;
    }

    /**
     * 生成类注释
     * @return
     */
    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            GenerationTool.generator(this);
            project.addCompileSourceRoot(this.getOutDirectory().getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
