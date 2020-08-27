package com.eric.achieve.service;

import com.eric.achieve.bean.Author;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: Eric Liang
 * @Since: 2020/7/29 11:20
 */
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
