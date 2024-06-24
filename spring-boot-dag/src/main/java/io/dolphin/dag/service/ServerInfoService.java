package io.dolphin.dag.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.base.Stopwatch;
import io.dolphin.dag.entity.ServerInfoDO;
import io.dolphin.dag.exception.DolphinException;
import io.dolphin.dag.lock.LockService;
import io.dolphin.dag.mapper.ServerInfoMapper;
import io.dolphin.dag.utils.CommonUtils;
import io.dolphin.dag.utils.NetUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dolphin
 * @date 2024年03月14日 16:51
 * @description management server info, like heartbeat, server id etc
 */
@Slf4j
@Service
public class ServerInfoService {
    private final String ip;
    private final long serverId;

    private final ServerInfoMapper serverInfoMapper;

    private static final long MAX_SERVER_CLUSTER_SIZE = 10000;

    private static final String SERVER_INIT_LOCK = "server_init_lock";
    private static final int SERVER_INIT_LOCK_MAX_TIME = 15000;

    public long getServerId() {
        return serverId;
    }

    @Autowired
    public ServerInfoService(LockService lockService, ServerInfoMapper serverInfoMapper) {

        this.ip = NetUtils.getLocalHost();
        this.serverInfoMapper = serverInfoMapper;

        Stopwatch sw = Stopwatch.createStarted();

        while (!lockService.tryLock(SERVER_INIT_LOCK, SERVER_INIT_LOCK_MAX_TIME)) {
            log.info("[ServerInfoService] waiting for lock: {}", SERVER_INIT_LOCK);
            CommonUtils.easySleep(100);
        }

        try {

            // register server then get server_id
            ServerInfoDO server = serverInfoMapper.selectOne(new LambdaQueryWrapper<ServerInfoDO>()
                    .eq(ServerInfoDO::getIp, ip));

            if (server == null) {
                ServerInfoDO newServerInfo = new ServerInfoDO(ip);
                serverInfoMapper.insert(newServerInfo);
                server = newServerInfo;
            } else {
                serverInfoMapper.update(null, new LambdaUpdateWrapper<ServerInfoDO>()
                        .set(ServerInfoDO::getGmtModified, new Date())
                        .eq(ServerInfoDO::getIp, ip));
            }

            if (server.getId() < MAX_SERVER_CLUSTER_SIZE) {
                this.serverId = server.getId();
            } else {
                this.serverId = retryServerId();
                serverInfoMapper.update(null, new LambdaUpdateWrapper<ServerInfoDO>()
                        .set(ServerInfoDO::getId, this.serverId)
                        .eq(ServerInfoDO::getIp, ip));
            }

        } catch (Exception e) {
            log.error("[ServerInfoService] init server failed", e);
            throw e;
        } finally {
            lockService.unlock(SERVER_INIT_LOCK);
        }

        log.info("[ServerInfoService] ip:{}, id:{}, cost:{}", ip, serverId, sw);
    }

    @Scheduled(fixedRate = 15000, initialDelay = 15000)
    public void heartbeat() {
        serverInfoMapper.update(new ServerInfoDO(ip).setGmtModified(new Date()), new LambdaQueryWrapper<ServerInfoDO>().eq(ServerInfoDO::getIp, ip));
    }


    private long retryServerId() {

        List<ServerInfoDO> serverInfoList = serverInfoMapper.selectList(null);

        log.info("[ServerInfoService] current server record num in database: {}", serverInfoList.size());

        // clean inactive server record first
        if (serverInfoList.size() > MAX_SERVER_CLUSTER_SIZE) {

            // use a large time interval to prevent valid records from being deleted when the local time is inaccurate
            Date oneDayAgo = DateUtils.addDays(new Date(), -1);
            int delNum =serverInfoMapper.delete(new LambdaQueryWrapper<ServerInfoDO>().lt(ServerInfoDO::getGmtCreate, oneDayAgo));
            log.warn("[ServerInfoService] delete invalid {} server info record before {}", delNum, oneDayAgo);

            serverInfoList = serverInfoMapper.selectList(null);
        }

        if (serverInfoList.size() > MAX_SERVER_CLUSTER_SIZE) {
            throw new DolphinException(String.format("The powerjob-server cluster cannot accommodate %d machines, please rebuild another cluster", serverInfoList.size()));
        }

        Set<Long> uedServerIds = serverInfoList.stream().map(ServerInfoDO::getId).collect(Collectors.toSet());
        for (long i = 1; i <= MAX_SERVER_CLUSTER_SIZE; i++) {
            if (uedServerIds.contains(i)) {
                continue;
            }

            log.info("[ServerInfoService] ID[{}] is not used yet, try as new server id", i);
            return i;
        }
        throw new DolphinException("impossible");
    }
}
