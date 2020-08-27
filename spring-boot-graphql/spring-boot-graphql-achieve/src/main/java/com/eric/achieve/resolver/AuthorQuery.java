package com.eric.achieve.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.eric.achieve.bean.Author;
import com.eric.achieve.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description: GraphQLQueryResolver 定义的是数据查询的方法接口
 * @Author: Eric Liang
 * @Since: 2020/7/29 11:25
 */
@Component
public class AuthorQuery implements GraphQLQueryResolver {
    @Autowired
    private AuthorService authorService;


    public Author findAuthorById(int id) {
        return authorService.findById(id);
    }
}
