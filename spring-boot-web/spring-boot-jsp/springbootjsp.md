# SpringBoot之jsp支持
## 1. 创建Maven Web Project
新建一个Maven Web Project，项目取名为spring-boot-jsp，会生成对应的启动类
```java
@SpringBootApplication
public class SpringBootJsp2Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootJsp2Application.class, args);
    }

}
```
## 2.在pom.xml添加依赖 
```xml
<!--添加tomcat依赖模块-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
</dependency>
<!-- 添加servlet依赖模块 -->
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>3.0.1</version>
</dependency>
<!-- 添加jstl标签库依赖模块 -->
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>jstl</artifactId>
</dependency>
<!-- 使用jsp引擎，springboot内置tomcat没有此依赖 -->
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
</dependency>
```
## 3. 配置application.yml支持jsp
```yaml
spring:
  mvc:
    view:
      prefix: /WEB-INF/view/
      suffix: .jsp
```
## 4. 根据支持项配置静态文件路径
在main下创建webapp/WEB-INF/view文件夹，然后放置index.jsp
## 5. 创建index.jsp
```js
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Insert title here</title>
</head>
<body>
index2
<hr>
index
</body>
</html>
```
## 6. 创建访问jsp的controller
```java
@Controller
public class IndexController {

    @RequestMapping("index")
    public String getIndex() {
        System.out.printf("访问index2");
        return "index";
    }
}
```
启动项目访问http://localhost:8080/index即可成功访问
# 集成与jsp参数传递
## 1. 更改controller方法
```java
@Controller
public class IndexController {
    @Value("${application.index:Hello Eric}")
    private String index;

    @RequestMapping("index")
    public String getIndex() {
        System.out.printf("访问index2");
        return "index";
    }

    @RequestMapping("indexMap")
    public String getIndex(Map<String, Object> map) {
        System.out.printf("访问indexmap2");
        map.put("index", index);
        return "index";
    }
}
```
## 2.增加自定义参数配置
```yaml
spring:
  mvc:
    view:
      prefix: /WEB-INF/view/
      suffix: .jsp
application:
  index: Hello Dolphin
```
## 3. jsp增加参数接收
```js
<body>
index2
<hr>
${index}
</body>
</html>
```