package com.eric.sensitive.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.eric.sensitive.config.Sensitive;
import com.eric.sensitive.config.SensitiveStrategy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description:
 * @Author: Eric Liang
 * @Since: 2020-5-5 16:54
 */
@Data
@TableName("user_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class UserInfo extends Model<UserInfo> {
    /**
     * 主键Id
     */
    private Long userId;

    /**
     * 用户名称(待脱敏字段)
     */
    //@Sensitive(strategy = SensitiveStrategy.USERNAME)
    private String name;

    /**
     * 用户年龄
     */
    private Integer age;
}
