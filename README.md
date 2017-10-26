# json-xml-codegen

#### 项目简介
这个项目是对之前一直在使用的jsonschema2pojo生成json工具的一个替代，因为jsonschema2pojo无法生成单个静态类，有时因为json数据结构复杂，可能生成
很多鸡肋的Java类，十分不便管理，加之也没有找到合适的xml生成工具，我便尝试自己写了个简单版的，可能存在一些问题，但是这只是个代码生成工具，生成后用户照
样能对代码进行调整，比较方便，基本上，只要给出的json或者xml数据格式良好，都能直接生成的。

#### 项目构思
一开始，我是直接从json出发的，毕竟json在http接口中更受欢迎些，加之org.json有着良好的解析Tokenizer可以直接使用，而且支持xml到json的转换，方便
之后的代码唯护。写单个静态类比写分散个每一个文件的类的方式要容易，你不需要考虑Java文法以及相互引用，而且内部静态之间的引用关系也不需要先后顺序，所以
只需要直接迭代json数据结构树就行了。

#### 目前方案的不足
1. json转Java类型时可能会丢失其应有的类型，比如，json属性{"CostPrice" : 172.00}，很显然这个CostPrice应该是double类型的，可是org.json把
其parse为Integer型了，导致生成代码变成了int型，最终导致用户代码解析失败，所以需要人为的去改成一个不能转换为int的double值,比如写成172.01就好了。
2. 对Camel的识别不灵活，这需要一些Spell检查工具，目前只是让用户自行决定生成类是否需要进行Camel处理。

#### 更新日志
2.1.1 更新:
   1. 支持对象默认以new对象形式初始化(原始类型和String除外)
   
2.1.0 更新:
   1. 注解默认生成在Vo的getter器上,避免非驼峰式属性Vo序列化时生成两个不同的属性;
   2. XML类型生成Vo时默认使用jackson-dataformat-xm,支持XML属性(需要手动修改注解添加 isAttribute = true),XML CDATA标签(需要手动添加注解);
   3. 默认数组生成Vo带有JacksonXmlElementWrapper注解,并设定了useWrapping=false,详情见[wiki](https://github.com/FasterXML/jackson-dataformat-xml/wiki/Jackson-XML-annotations);
   
2.0.0 更新feature:
   1. 支持指定生成字段的类型,指定方式于json或者xml指定值为{propName: "Date$这是一个日期"},$符号前为字段类型,目前支持
   Java原生类型,String,Date;
   2. 默认生成JavaDoc注释

### 使用简介

```xml
<plugin>
    <groupId>com.zwy.plugin</groupId>
    <artifactId>json-xml-codegen</artifactId>
    <version>${project.version}</version>
    <goals>
        <goal>generate</goal>
    </goals>
    <executions>
        <execution>
            <id>xml-test</id>
            <configuration>
                <!-- 生成类型 XML或者 JSON -->
                <generationType>XML</generationType>
                <!-- 注解类型 JACKSON2,FASTJSON,XSTREAM,JAXB Annotation --> 
                <annotationStyle>JACKSON2</annotationStyle>
                <!-- xml or json 数据文件所在位置,默认取src/main/resources/xml or src/main/resources/xml的*.xml和*.json文件 -->
                <sourceDirectory>${project.basedir}/src/main/resources/xml</sourceDirectory>
                <!-- 生成Vo输出目录,默认在 target/generated-sources/zwy 目录下面 -->
                <outDirectory>${project.build.directory}/generated-sources/zwy</outDirectory>
                <!-- 生成文件的 packageName,会按照sourceDirectory/packageName 展开生成 -->
                <packageName>just.xml</packageName>
                <!-- 包含文件,默认包含所有,如果指定，则只包含指定的file -->
                <includeFiles>
                    <file>data2.xml</file>
                </includeFiles>
            </configuration>
        </execution>
        <execution>
            <id>json-test</id>
            <configuration>
                <generationType>JSON</generationType>
                <annotationStyle>JACKSON2</annotationStyle>
                <sourceDirectory>${project.basedir}/src/main/resources/json</sourceDirectory>
                <outDirectory>${project.build.directory}/generated-sources/zwy</outDirectory>
                <packageName>just.json</packageName>
                <includeFiles>
                    <file>order2.json</file>
                </includeFiles>
            </configuration>
        </execution>
    </executions>
</plugin>
```

maven plugin 调用命令行: mvn json-xml-codegen:generate@json-test