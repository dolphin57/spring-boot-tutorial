package com.eric.sensitive.controller;

import com.eric.sensitive.entity.UserInfo;
import com.eric.sensitive.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:
 * @Author: Eric Liang
 * @Since: 2020-9-10 9:38
 */
@RestController
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/select")
    public List<UserInfo> select() {
        return userInfoService.getUserInfoList();
    }

}
