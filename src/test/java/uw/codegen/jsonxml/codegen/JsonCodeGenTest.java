package uw.codegen.jsonxml.codegen;

import org.junit.Test;
import uw.codegen.jsonxml.example.AnnotationStyle;
import uw.codegen.jsonxml.example.GenerationConfig;
import uw.codegen.jsonxml.example.GenerationTool;
import uw.codegen.jsonxml.example.GenerationType;

import java.io.File;
import java.io.IOException;

public class JsonCodeGenTest {

    @Test
    public void testJsonCodeGen() throws IOException {
        GenerationConfig generationConfig = new GenerationConfig() {
            @Override
            public GenerationType getGenerationType() {
                return GenerationType.XML;
            }

            @Override
            public AnnotationStyle getAnnotationStyle() {
                return AnnotationStyle.JACKSON2;
            }

            @Override
            public boolean isCamel() {
                return true;
            }

            @Override
            public File getSourceDirectory() {
                return new File("d://tmp");
            }

            @Override
            public String[] getIncludeFiles() {
                return new String[]{"lvmama_price.xml"};
            }

            @Override
            public File getOutDirectory() {
                return new File("d://tmp");
            }

            @Override
            public String getPackageName() {
                return "justt.tet";
            }

            @Override
            public boolean isGenerateJavaDoc() {
                return true;
            }

            @Override
            public String getAuthor() {
                return "liliang";
            }

            @Override
            public String getComment() {
                return "";
            }
        };

        GenerationTool.generator(generationConfig);
    }
}