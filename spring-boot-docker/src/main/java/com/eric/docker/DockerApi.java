/**
 * Copyright 2017 - 2025 Evergrande Group
 */
package com.eric.docker;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description:
 * @Author: qianliang
 * @Since: 2019/1/18 10:08
 */
@Controller
public class DockerApi {
    @GetMapping("/hello")
    public void getHello(HttpServletResponse response) throws IOException {
        response.getWriter().write("Hello docker");
    }
}
