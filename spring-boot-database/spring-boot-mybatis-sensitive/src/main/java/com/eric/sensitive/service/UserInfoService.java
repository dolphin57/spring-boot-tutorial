package com.eric.sensitive.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eric.sensitive.entity.UserInfo;

import java.util.List;

/**
 * @Description:
 * @Author: qianliang
 * @Since: 2020-9-10 9:39
 */
public interface UserInfoService extends IService<UserInfo> {
    /**
     * 查询全部用户信息
     * @return
     */
    List<UserInfo> getUserInfoList();
}
