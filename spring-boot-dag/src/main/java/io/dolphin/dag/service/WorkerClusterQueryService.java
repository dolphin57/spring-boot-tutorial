package io.dolphin.dag.service;

import com.google.common.collect.Lists;
import io.dolphin.dag.entity.JobInfoDO;
import io.dolphin.dag.workcluster.service.ClusterStatusHolder;
import io.dolphin.dag.workcluster.model.WorkerInfo;
import io.dolphin.dag.workcluster.service.WorkerClusterManagerService;
import io.dolphin.dag.workcluster.service.WorkerFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author dolphin
 * @date 2024年03月15日 13:57
 * @description 获取 worker 集群信息
 */
@Slf4j
@Service
public class WorkerClusterQueryService {
    private List<WorkerFilter> workerFilters;

    @Autowired
    public WorkerClusterQueryService(List<WorkerFilter> workerFilters) {
        this.workerFilters = workerFilters;
    }

    /**
     * get worker for job
     * @param jobInfo job
     * @return worker cluster info, sorted by metrics desc
     */
    public List<WorkerInfo> getSuitableWorkers(JobInfoDO jobInfo) {
        List<WorkerInfo> workers = Lists.newLinkedList(getWorkerInfosByAppId(jobInfo.getAppId()).values());

        workers.removeIf(workerInfo -> filterWorker(workerInfo, jobInfo));

        // 按健康度排序
        workers.sort((o1, o2) -> o2 .getSystemMetrics().calculateScore() - o1.getSystemMetrics().calculateScore());

        // 限定集群大小（0代表不限制）
        if (!workers.isEmpty() && jobInfo.getMaxWorkerCount() > 0 && workers.size() > jobInfo.getMaxWorkerCount()) {
            workers = workers.subList(0, jobInfo.getMaxWorkerCount());
        }
        return workers;
    }

    private Map<String, WorkerInfo> getWorkerInfosByAppId(Long appId) {
        ClusterStatusHolder clusterStatusHolder = getAppId2ClusterStatus().get(appId);
        if (clusterStatusHolder == null) {
            log.warn("[WorkerManagerService] can't find any worker for app(appId={}) yet.", appId);
            return Collections.emptyMap();
        }
        return clusterStatusHolder.getAllWorkers();
    }

    public Map<Long, ClusterStatusHolder> getAppId2ClusterStatus() {
        return WorkerClusterManagerService.getAppId2ClusterStatus();
    }

    /**
     * filter invalid worker for job
     * @param workerInfo worker info
     * @param jobInfo job info
     * @return filter this worker when return true
     */
    private boolean filterWorker(WorkerInfo workerInfo, JobInfoDO jobInfo) {
        for (WorkerFilter filter : workerFilters) {
            if (filter.filter(workerInfo, jobInfo)) {
                return true;
            }
        }
        return false;
    }
}
