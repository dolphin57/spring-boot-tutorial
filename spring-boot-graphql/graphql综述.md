[toc]
# SpringBoot简介GraphQL
## GraphQL官方介绍
我们进入GraphQL的官方文档：https://graphql.cn ，可以看到一段话：
```
GraphQL既是一种用于API的查询语言也是一个满足你数据查询的运行时。
GraphQL对你的API中的数据提供了一套易于理解的完整描述，使得客户端能够准确得获得它需要的数据，而且没有任何冗余，也让API更容易地随着时间推移而演进，还能用于构建强大的开发者工具
```
## GraphQL的探索之路
### GraphQL定义
&emsp;&emsp;GraphQL 是一种 API查询语言, 用于服务器端执行按已定义类型系统的查询. GraphQL 不与任何特定的数据库或存储引擎进行绑定, 而是由您的代码和数据支持.(官方描述)  
&emsp;&emsp;GraphQL 是一种针对 Graph（图状数据）进行查询特别有优势的 Query Language（查询语言），所以叫做 GraphQL  
&emsp;&emsp;说白了就是想要什么, 就<font color='red'>传入什么字段, 也就会返回什么字段</font>,具体字段处理是服务器所提供, 而 graphql 并不会关心服务器怎么处理
#### 举例说明
##### 传统的rest api
/user/{id}  return { id, name, age } 是一成不变的
##### graphql
findUser(id: xx)  return { id, name }  (注: 传输参数id, 指定返回字段 id, name, 当然也可以写{ name, age }，<font color='red'>完全取决于前端需求</font> )
### GraphQL工作机制
&emsp;&emsp;一个GraphQL查询可以包含一个或者多个操作（operation），类似于一个RESTful API。操作（operation）可以使两种类型：查询（Query）或者修改（mutation）。我们看一个例子：
```graphsql
query {
  findById(id: 1) {
    id 
    name
  }
}
```
&emsp;&emsp;你的第一印象：“这个不是JSON？”。还真不是！就如我们之前说的，GraphQL设计的中心是为客户端服务。GraphQL的设计者希望可以写一个和期待的返回数据schema差不多的查询  

注意上面的例子有三个不同的部分组成：
1. findById是查询的operation（这是一个Query）
2. (id: 1) 包含了传入给Query的参数（多个参数，使用逗号隔开，比如：(id:1,name:”张三”) ）
3. 查询包含id和name字段，这些字段也是我们希望查询可以返回的。（这里就是GraphQL的特性，<font color='red'>能够指定要返回的字段</font>，如果只指定了id，那么返回结果就只返回id。）

&emsp;&emsp;到这里是不是有点理解了GraphQL了，以前如果要返回不同的数据的话，需要编写多个接口，或者编写一个接口干脆什么字段都给返回给你，前端你自己就看着办吧，要显示哪些你就显示哪些，不要显示的，你就不要管了，4G时代，还什么流量问题，不用考虑

我们看看server会给这个查询返回什么：
```graphsql
{
  "data": {
    "findById": {
      "id": "1",
      "name": "悟纤"
    }
  }
}
```
就如我们期望的，server会返回一个JSON串。这个JSON的schema和查询的基本一致  

&emsp;&emsp;从以上的例子里你可以看出来GraphQL允许客户端明确指定它要的是什么，避免了数据后去的冗余或者不足。和RESTful API对比一下，每一个客户端都会对应很多个RESTful API或者一个API要服务很多个客户端。所以说GraphQL是很好的查询语言。所有的operation、参数和所有可以查询的字段都需要在GraphQL server上定义、实现  

## 总结
1. GraphQL（Grapha Query Language）：主要是作用于数据接口，比如前端后端交互，给客户端筛选自由获取服务端事先定义好的数据，提高了交互接口的灵活性
2. GraphQL官方说：GraphQL一种为你的API而生的查询语言
3. API：应用编程接口，全称是Application Programming Interface
4. GraphQL简单理解：在请求API的时候，允许我们指定要返回的字段
5. GraphQL主要有两种操作（Operation）：查询（Query）或者修改（mutation）

GraphQL解决了接口对查询字段差异性的要求