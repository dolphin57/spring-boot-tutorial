package com.eric.achieve.bean;

import lombok.Data;

/**
 * @Description:
 * @Author: Eric Liang
 * @Since: 2020/7/29 11:19
 */
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
