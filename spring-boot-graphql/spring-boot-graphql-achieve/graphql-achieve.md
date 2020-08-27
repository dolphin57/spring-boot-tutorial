[toc]
# SpringBoot实现Graphql
## 环境说明
- Spring Boot : 2.3.1.RELEASE
- JDK：1.8
- graphql-spring-boot-starter ：5.0.2
- graphql-java-tools：5.2.4
- graphiql-spring-boot-starter：7.1.0

## 编码思路说明
1. 我们会新创建一个maven project；
2. 引入相关的依赖，比如springboot和graphql的依赖包；
3. 编写相应的实体类以及服务（这里的demo简化了DAO的部分）；
4. 定义GraphQLQueryResolver进行方法的声明；
5. 定义graphqls的接口定义和scheme定义

## Graphql Demo
### 新建一个Maven项目
使用IDE新建一个maven project，取名为：spring-boot-graphql-achieve
### 引入相关依赖
在pom.xml文件添加依赖：
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
        <exclusions>
            <exclusion>
                <groupId>org.junit.vintage</groupId>
                <artifactId>junit-vintage-engine</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <dependency>
        <groupId>com.graphql-java</groupId>
        <artifactId>graphql-spring-boot-starter</artifactId>
        <version>5.0.2</version>
    </dependency>
    <dependency>
        <groupId>com.graphql-java-kickstart</groupId>
        <artifactId>graphql-spring-boot-starter</artifactId>
        <version>7.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.graphql-java</groupId>
        <artifactId>graphql-java-tools</artifactId>
        <version>5.2.4</version>
    </dependency>
</dependencies>
```
说明：
1. graphql的依赖：graphql-spring-boot-starter、graphql-java-tools
2. graphiql（graphql GUI，图形化工具不是必需的）：graphiql-spring-boot-starter；GraphiQL 是一个可以直接和 GraphQL 服务交互的 UI 界面，可以执行查询和修改请求

### 编写实体类
这里我们编写一个作者的实体类Author：
```java
@Data
public class Author {
    /**
     * 作者的ID.
     */
    private int id;
    /**
     * 作者名称.
     */
    private String name;
    /**
     * 照片.
     */
    private String photo;
}
```
### 编写service
我们需要有一个服务进行处理Author，这里我们省去DAO层，直接从service中进行构造数据，实际项目中service在调用dao即可：
```java
@Service
public class AuthorService {
    public Author findById(int id) {
        Author author = new Author();
        author.setId(id);
        if (id == 1) {
            author.setName("Eric1");
            author.setPhoto("/img/1.png");
        } else if (id == 2) {
            author.setName("Eric2");
            author.setPhoto("/img/2.png");
        }
        return author;
    }
}
```
### 编写自定义GraphQLQueryResolver
GraphQL是通过实现GraphQLQueryResolver(空接口)，在里面定义自己的方法处理数据，类似controller中的代码
```java
@Component
public class AuthorQuery implements GraphQLQueryResolver {
    @Autowired
    private AuthorService authorService;


    public Author findAuthorById(int id) {
        return authorService.findById(id);
    }
}
```
我们发现这个类除了继承GraphQLQueryResolver之外，也没啥特殊的编码方式
### graphql服务定义和scheme定义
resources/graphql/root.graphqls : 一般会在root.graphqls文件中放Query或者Mutation的接口定义：
```graphql
#定义查询的方法
type Query {
    findAuthorById(id: Int!):Author
}
```
这里定义了Query操作findAuthorById，这里对应的是AuthorQuery.findAuthorById(int id)
> 另外如果在类型后面有！说明此参数/类型是非空的

resources/graphql/schema.graphqls：文件中定义type等数据对象：
```graphql
#定义实体类类型
type Author {
    id: Int!
    name: String
    photo: String
}
```
这里定义了和实体类对应的数据类型Author
### SpringBoot启动类
代码自动生成的，可以跳过此步骤：
```java
@SpringBootApplication
public class SpringBootGraphqlAchieveApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootGraphqlAchieveApplication.class, args);
    }
}
```
### 测试
&emsp;&emsp;运行启动类SpringBootGraphqlAchieveApplication，测试下访问如下地址：http://127.0.0.1:8080/graphiql
![springboot集成graphql UI.png](https://i.loli.net/2020/07/29/6GcvKw9H472Tyut.png)
根据GraphQL的语法进行调用下我们的API吧：

在左边的界面中输入如下的语法:
```graphql
query{
  findAuthorById(id:1){
    id,
    name,
    photo
  }
}
```
然后点击执行ExecuteQuery按钮，可以在右边看到返回的数据 ：
```graphql
{
  "data": {
    "findAuthorById": {
      "id": 1,
      "name": "Eric1",
      "photo": "/img/1.png"
    }
  }
}
```
## GraphQL特性理解
### 特性验证
我们在前面文章中说到GraphQL的一个特点是：想要什么, 就传入什么字段,也就会返回什么字段。我们可以修改GraphQL语句，在执行以下：
```graphql
query{
  findAuthorById(id:1){
    id,
    name
  }
}
```
那么执行的结果就是：
```graphql
{
  "data": {
    "findAuthorById": {
      "id": 1,
      "name": "Eric1"
    }
  }
}
```
后端代码无需做任何调整，是不是爽的一匹
### !非空验证
我们在前面说明了!字段字段非空的意思，那么如果我们在执行的时候，不传递参数的话，是否会报错呢？答案是会的：
```graphql
query{
  findAuthorById {
    id,
    name
  }
}
```

```graphql
{
  "data": null,
  "errors": [
    {
      "message": "Validation error of type MissingFieldArgument: Missing field argument id @ 'findAuthorById'",
      "locations": [
        {
          "line": 2,
          "column": 3,
          "sourceName": null
        }
      ],
      "description": "Missing field argument id",
      "validationErrorType": "MissingFieldArgument",
      "queryPath": [
        "findAuthorById"
      ],
      "errorType": "ValidationError",
      "path": null,
      "extensions": null
    }
  ]
}
```
## 总结
1. GraphQL的例子步骤：添加相关依赖、编写Query实现、定义.graphqls配置文件。
2. Graphiql：GraphQL GUI，通过图形化界面，可以发起graphql语句执行，返回结果。
3. 理解GraphQL的特性：想要什么, 就传入什么字段, 也就会返回什么字段