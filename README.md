# 数据容器2.0

## 1.项目介绍

以UdxSchema为导向，结合数据映射方法、数据重构方法、以及在线生成工具，生成UdxData，并将其发布为数据服务。
根据服该服务，用户可动态的、按需的获取UdxData中的数据。
同时容器提供了可视化服务，来对UdxData进行可视化。

## 2.架构

前后端分离开发，分工明确，职责清晰。
前端关注界面展示，提供UI交互功能，利用AJAX调用后台接口。后端专注于业务逻辑，处理AJAX请求，返回JSON数据。

前端：Vue框架构建Spa。

后端：SpringBoot+Mongodb 提供相关支持。

![相关流程](https://raw.githubusercontent.com/sunlingzhiliber/imgstore/master/W9IGGB3HXEQRKJSIUR%5DK%7ELG.png)

## 3.模块划分

### Schema仓库

针对结构化表达数据模型，进行资源库的建立。

### 数据映射

完成原始数据和UDXData之间的转换。

### 数据重构

完成UDXData之间的相互转化。

### 数据可视化

以结构化表达数据模型为基础，构建可视化服务。

### 数据服务

数据生成：以结构化表达数据模型为基础，通过UI界面构建UDXData。
数据发布：将构建好的结构化数据表达模型(UDXData)发布为服务。
数据抽取：以发布的数据服务为基础，从中抽取用户感兴趣的信息。

## 4.使用说明

[API文档参考](http://localhost:8080/swagger-ui.html)

### Schema仓库

#### 创建

```java
public class AddSchemaDocDTO {
    String name;
    String detailMarkDown;
    String description;
    UdxSchema udxSchema;
}
```
我们在创建SchemaDoc的时候，需要注意UdxSchema字段，
该字段目前的获取方式是根据Rest请求`/schemaDoc/getSchemaFromFile`,加载JSON文件或者XML文件来生成的。

#### 详情

```java
public class SchemaDoc {
    @Id
    String id;
    String name;
    String description;
    String detailMarkDown;
    @JsonFormat (pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date createDate;
    UdxSchema udxSchema;
}
```
针对于SchemaDoc,我们在页面中应该可以对其所有字段均可进行查看。
同时可以经由SchemaDoc跳转到相关的映射方法，重构方法。
`/schemaDoc/related?id=schemaDocId`


### 数据映射

#### 创建

```java
public class AddMappingMethodDTO {
    String name;
    String description;
    String detailMarkdown;
    String supportedUdxSchema;
    String storePath;
}
```

我们在创建map的时候，需要注意`supportedUdxSchema`字段和`storePath`字段。

1. 数据映射实体都会绑定一个supportedUdxSchema，因此在增加数据映射实体时，需要首先创建一个Schema实体（或者选择一个已有的Schema实体）。
2. 每个数据映射实体还有绑定一个Zip包，因此需要首先上传Zip包，得到上传路径，然后再创建实体
`/file/upload/map` 上传Zip文件


#### 详情

```java
public class MappingMethod {
    @Id
    String id;
    String name;
    String description;
    String detailMarkDown;
    Date createDate;
    String supportedUdxSchema;
    String storePath;
}

```
针对于MappingMethod,我们在页面中应该可以对其所有字段均可进行查看。
并且对于`storePath`可以提供下载到本地的使用。`/file/download?path=storePath`
同时可以经由MappingMethod跳转到相关的SchemaDoc页面
Attention：数据映射实体目前只是简单上传，后期需要提供在线调用的接口。

### 数据重构

#### 创建

```java
public class AddRefactorMethodDTO {
    String name;
    String description;
    String detailMarkdown;
    List<String> supportedUdxSchemas;
    String storePath;
}
```

我们在创建refactor的时候，需要注意`supportedUdxSchema`字段和`storePath`字段。

1. 数据重构实体都会绑定多个UdxSchema，因此在增加数据重构实体时，需要首先创建Schema实体（或者选择已有的Schema实体）。同时我们这里并没有限定输入和输出以及是否对应，只是简单的将其全部合为了一个List。
2. 数据重构实体绑定一个Zip包，因此需要首先上传Zip包，得到上传路径，然后再创建实体
`/file/upload/refactor` 上传Zip文件

#### 详情

```java
public class RefactorMethod {
    @Id
    String id;
    String name;
    String description;
    String detailMarkDown;
    Date createDate;
    List<String> supportedUdxSchemas;
    String storePath;
}

```
针对于RefactorMethod,我们在页面中应该可以对其所有字段均可进行查看。
并且对于`storePath`可以提供下载到本地的使用。`/file/download?path=storePath`
同时可以经由RefactorMethod跳转到相关的SchemaDoc
Attention：数据重构实体目前只是简单上传，后期需要提供在线调用的接口。

### 数据可视化

TODO

### 数据抽取

TODO

### 数据服务

TODO


