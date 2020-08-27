[toc]
# SpringBoot集成Graphql进阶
## 多个.graphqls文件探索
### 说明
&emsp;&emsp;我们项目如果有Book和Author的查询的话，那么我们应该不会把配置都放到一个root.graphqls和schema.graphqls文件中吧，这样不利于团队配合开发，那么是否可以book放到book.graphqls中，author放到author.graphqls中呐？
### 建立多个.grphqls文件
建立/resources/graphql/author.graphqls，内容如下：
```graphql
type Author {
    id: Int!
    name: String
    photo: String
}

extend type Query{
    findAuthorById(id: Int!):Author
}
```
> 这里使用extend继承Root Query，如果不使用extend的话，多个文件的话就不能都生效了。另外对于类型的声明也移到了一起统一管理

建立/resources/graphql/book.graphqls，内容如下：
```graphql
type Book {
    id: Int!
    name: String
    isbn: String
}
extend type Query{
    findBookById(id: Int!):Book
}
```
对于root.graphqls和schema.graphqls文件内容为空或者删除掉文件
### 运行测试
我们这时候启动运行测试，会报一个异常信息：
```
SchemaClassScannerError:Type definition for root query type 'Query' not found!
```
意思我们没有定义Root Query，解决的方式很简单在root.graphqls文件中添加一个空的Query即可：
```graphql
#定义查询的方法
type Query {}
```
这时候在运行应该是可以正常运行的，在右侧Query进入可以看到两个Query：
```
findAuthorById(id: Int!): Author
findBookById(id: Int!): Book
```
## 如何加注释
&emsp;&emsp;我们在方法也没有注释说明，字段也没有注释说明，肯定不利于前端使用，那么怎么加注释呐，很简单，只要“#注释内容即可”，举例说明：
```graphql
# 作者
type Author {
    # 作者的id
    id: Int!
    # 作者名称
    name: String
    # 作者照片
    photo: String
}

# 查询方法
extend type Query{
    # 通过作者id进行查询作者的信息
    findAuthorById(id: Int!):Author
}
```
重启之后，在图形化界面可以看到我们刚添加的注释了：

## 使用curl如何进行查询
### 说明
我们已经知道使用图形化界面如何进行查询了，那么如果使用curl如何查询呐？
### 分析
我们观察下浏览器是如何发起请求的：
![graphql请求分析.png](https://i.loli.net/2020/07/30/yDaiVCcwIZs3uT9.png)  
从这里可以看到好像是发送了一个字符串到后端，然后就可以返回一个json的结果了
### 使用curl进行查询
发起请求的指令：
```
curl -i -X POST -d  '{"query": "query {findAuthorById(id:1) {id,name}}"}' http://127.0.0.1:8080/graphql
```
说明：
1. curl：cURL是一个利用URL语法在命令行下工作的文件传输工具。
2. -i：显示 http response 的头信息，连同网页代码一起，-I 参数则只显示 http response 的头信息。
3. -X：指定 HTTP 请求的方法（要大写，举例：POST/GET，错误示例：post/get）。
4. -d：用于发送 POST 请求的数据体

执行我们看下帅帅的结果， 结果是返回了，但是是不是觉得挨着某些不该挨着的信息，看起来很不舒服呀，偷偷告诉你个小技巧，只需要在上面的命令后面加上&& echo，就会换行了，具体命令如下：
```
curl -i -X POST -d  '{"query": "query {findAuthorById(id:1) {id,name}}"}' http://127.0.0.1:8080/graphql && echo
```
## 合并查询
### 说明
我们经常会查询多个接口，然后进行展示的需求。一种方法就是后端写一个接口，进行查询返回，另外一种方法就是前端调用多个接口进行查询渲染

### GraphQL实现合并查询
当碰上GraphQL就显得很简单了，我们看看如何实现合并查询，只需要需改下GraphQL语句即可：
```graphql
query{
   findAuthorById(id:1){
    id,
    name
  },
  findBookById(id:1){
    id,
    name,
    isbn
  }
}
```
是不是很酷。这里想问下大家，对于前端发起的请求是一次，还是两次呐？  
另外findAuthorById和findBookById的顺序会影响后端的执行顺序吗（这个是会有影响的，谁在前面，谁先执行）  
### 同名不同字段的时候，结果是什么？
我们有这么一个指令：
```graphql
query{
  findAuthorById(id:1){
    id,
    name
  },
  findBookById(id:1){
    id,
    name,
    isbn
  },
  findAuthorById(id:1){
    id,
    photo
  }
}
```
结果是：后端只会执行两次，结果返回是并集，如下截图：
```graphql
{
  "data": {
    "findAuthorById": {
      "id": 1,
      "name": "Eric1",
      "photo": "/img/1.png"
    },
    "findBookById": {
      "id": 1,
      "name": "从零开始学SpringBoot",
      "isbn": "9881200"
    }
  }
}
```
## 复杂查询
### 说明
&emsp;&emsp;一般我们的关系是这样子的，一本书会有一个或者n个作者，这里我们假设1个，那么对于我们想要返回author和book信息，怎么操作呢？  
### 第一步：修改java代码建立关系
对于Book.java中需要增加一个属性Author：
```java
private Author author;
```
在BookService中，设置author的值：
```java
public class BookService {
  @Autowired
  private AuthorService authorService;
  
  public Book findById(int id) {
    Book book = new Book();
    book.setId(id);
    if(id==1) {
      book.setName("从零开始学SpringBoot");
      book.setIsbn("9881200");
      book.setAuthor(authorService.findById(1));
    }else if(id==2) {
      book.setName("JVM性能调优");
      book.setIsbn("9881201");
      book.setAuthor(authorService.findById(2));
    }
    return book;
  }
}
```
这里的demo没有使用数据源，都是直接本地构建的对象，实际中，换成数据库库的查询即可
### 第二步：修改graphqls文件
修改book.graphqls文件typeBook添加author属性：
```graphql
#书本信息
type Book {
  #书本id
    id: Int!
    #书本名称
    name: String
    #书本isbn编码
    isbn: String
    #作者
    author:Author
}
```
如果是多个作者的话，java中使用List<Author>，配置文件使用[Author]定义即可
### 第三步：GraphQL语句
到这里就可以使用GraphQL语句查询下了：
```graphql
query{
  findBookById(id:1){
    id,
    name,
    isbn,
    author {
        id,
        name
    }
  }
}

{
  "data": {
    "findBookById": {
      "id": 1,
      "name": "从零开始学SpringBoot",
      "isbn": "9881200",
      "author": {
        "id": 1,
        "name": "Eric1"
      }
    }
  }
}
```
要返回啥字段还是你说的算，如果不想要返回author，只要不配置就可以了