package io.dolphin.dag.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author dolphin
 * @date 2024年03月14日 16:52
 * @description 服务器信息表（用于分配服务器唯一ID）
 */
@Data
@TableName("server_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ServerInfoDO extends Model<ServerInfoDO> {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 服务器IP地址
     */
    private String ip;

    private Date gmtCreate;

    private Date gmtModified;

    public ServerInfoDO(String ip) {
        this.ip = ip;
        this.gmtCreate = new Date();
        this.gmtModified = this.gmtCreate;
    }
}
