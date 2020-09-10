package com.eric.sensitive.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eric.sensitive.entity.UserInfo;
import com.eric.sensitive.mapper.UserInfoMapper;
import com.eric.sensitive.service.UserInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: Eric Liang
 * @Since: 2020-9-10 9:41
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Override
    public List<UserInfo> getUserInfoList() {
        return baseMapper.selectList(Wrappers.<UserInfo>lambdaQuery());
    }
}
