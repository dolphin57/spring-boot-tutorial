# SpringBoot之自定义starter
&emsp;&emsp;Spring Boot由很多的Stater构成，需要使用什么，只需要引入spring-boot-start-xxx即可。当然我们自己也可以编写一个starter供其它的项目进行使用
## 工作原理
1. SpringBoot在启动时扫描项目所依赖的jar包,寻找包含spring.factories文件的jar包
2. 根据spring.factories配置价值AutoConfigure类
3. 根据@Conditional注解的条件,进行自动配置并将Bean注入SpringContext

## 具体例子
在这里编写一个计算器的starter提供加减乘除服务
## 编写思路
1. 首先我们需要有一个service,里面有加减乘除方法
2. 我们需要有一个properties配置类,支持一些配置,比如:保留精度
3. 需要有一个AutoConfigure注册计算器service
4. 需要配置spring.factories,供spring扫描

## starter命名说明
&emsp;&emsp;Spring官方Starter通常命名为spring-boot-starter-{name},如spring-boot-starter-web,Spring官方建议非官方Starter命名应遵循{name}-spring-boot-starter的形式
## 新建项目
&emsp;&emsp;我们这里新建一个starter项目,取名为calculate-spring-boot-starter(符号非官方的starter命名要求)
## 引入依赖
在pom.xml文件引入依赖:
```xml
<dependencies>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-autoconfigure</artifactId>
	</dependency>
</dependencies>

<dependencyManagement>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-dependencies</artifactId>
			<version>1.5.7.RELEASE</version>
			<type>pom</type>
			<scope>import</scope>
		</dependency>
	</dependencies>
</dependencyManagement>
```
## 编写Service
&emsp;&emsp;编写CalculateService提供加减乘除方法,这里的scale一方面可以通过方法设置,另外一方面可以使用配置文件进行默认配置
```java
public class CalculateService {
    /**
     * 这个从配置文件获取,就是默认的scale
     */
    private int scale;

    public CalculateService(int scale) {
        this.scale = scale;
    }

    /**
     * 加法
     * @param v1
     * @param v2
     * @return
     */
    public double add(double v1,double v2) {
        return v1+v2;
    }

    /**
     * 减法
     * @param v1
     * @param v2
     * @return
     */
    public double sub(double v1,double v2) {
        return v1-v2;
    }

    /**
     * 乘法
     * @param v1
     * @param v2
     * @return
     */
    public double mul(double v1,double v2) {
        return v1*v2;
    }

    /**
     * 精确到小数点后scale位,以后的数字四舍五入
     * @param v 数
     * @param scale 保留精度
     * @return 两个参数的商
     */
    public double setScale(double v, int scale) {
        return new BigDecimal(v).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 精确到小数点后scale位,以后的数字四舍五入
     * @param v 数
     * @return 两个参数的商
     */
    public double setScale(double v) {
        return new BigDecimal(v).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
```
## 编写配置类
&emsp;&emsp;编写CalculateProperties 这里只有一个字段，为了讲解需要，实际上可以根据需要任意添加需要的配置
```java
@ConfigurationProperties("calculate")
public class CalculateProperties {
    private int scale;

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

}
```
## 编写autoconfigure
&emsp;&emsp;编写CalculateAutoConfigure自动配置类
```java
@Configuration
@ConditionalOnClass(CalculateService.class)
@EnableConfigurationProperties(CalculateProperties.class)
public class CalculateAutoConfigure {
    @Autowired
    private CalculateProperties calculateProperties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "calculate", value = "enabled", havingValue = "true")
    public CalculateService calculateService() {
        return new CalculateService(calculateProperties.getScale());
    }
}
```
解释下用到的几个和Starter相关的注解：
- @ ConditionalOnClass:当classpath下发现该类的情况下进行自动配置
- @ConditionalOnMissingBean:当SpringContext中不存在该Bean时
- @ConditionalOnProperty(prefix = "calculate", value = "enabled", havingValue = "true"):当配置文件中的calculate. enabled = true

## 编写spring.factories
最后在src/main/resources/META-INF下创建spring.factories文件,内容如下:
```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.eric.starter.config.CalculateAutoConfigure
```
&emsp;&emsp;到这里一个Spring Boot Starter便开发完成了.如果你要进行打包的时候,那么需要先安装下,执行命令:mvn install打包安装,如果只是在另外一个项目引入测试的话,不执行打包也是可以运行的
## 引入测试
创建一个spring-boot项目,引入我们做的starter
### 引入依赖
```xml
<dependency>  
    <groupId>com.eric</groupId>  
    <artifactId>calculate-spring-boot-stater</artifactId>  
    <version>0.0.1-SNAPSHOT</version>  
</dependency>
```
### properties配置
创建application.properties,进行配置:
```java
calculate.enabled = true
calculate.scale = 2
```
### 编写controller进行访问测试
```java
@RestController
public class Demo2Controller {
    @Autowired
    private CalculateService calculateService;

    @RequestMapping("/add")
    public double add(double v1, double v2) {
        return calculateService.add(v1, v2);
    }

    @RequestMapping("/sub")
    public double sub(double v1,double v2){
        return calculateService.sub(v1, v2);
    }

    @RequestMapping("/mul")
    public double mul(double v1,double v2){
        return calculateService.mul(v1, v2);
    }

    @RequestMapping("/setScale")
    public double setScale(double v,int scale){
        return calculateService.setScale(v,scale);
    }
    @RequestMapping("/setScale2")
    public double setScale(double v){
        return calculateService.setScale(v);
    }
}
```
到这里就可以访问测试了:
```
http://127.0.0.1:8080/add?v1=3&v2=5
http://127.0.0.1:8080/sub?v1=3&v2=5
http://127.0.0.1:8080/mul?v1=3&v2=5
http://127.0.0.1:8080/setScale?v=3.454656&scale=3
http://127.0.0.1:8080/setScale2?v=3.454656
```