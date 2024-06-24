package io.dolphin.uid.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.base.Preconditions;
import io.dolphin.uid.IDGen;
import io.dolphin.uid.common.Result;
import io.dolphin.uid.exception.InitException;
import io.dolphin.uid.segment.SegmentIDGenImpl;
import io.dolphin.uid.segment.dao.IDAllocDao;
import io.dolphin.uid.segment.dao.impl.IDAllocDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * @author dolphin
 * @date 2024年05月14日 14:52
 * @description
 */
public class SegmentService {
    private Logger logger = LoggerFactory.getLogger(SegmentService.class);

    private IDGen idGen;
    private DruidDataSource dataSource;

    public SegmentService(String url, String username, String pwd) throws InitException, SQLException {
        Preconditions.checkNotNull(url,"database url can not be null");
        Preconditions.checkNotNull(username,"username can not be null");
        Preconditions.checkNotNull(pwd,"password can not be null");
        // Config dataSource
        dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(pwd);
        dataSource.init();
        // Config Dao
        IDAllocDao dao = new IDAllocDaoImpl(dataSource);
        // Config ID Gen
        idGen = new SegmentIDGenImpl();
        ((SegmentIDGenImpl) idGen).setDao(dao);
        if (idGen.init()) {
            logger.info("Segment Service Init Successfully");
        } else {
            throw new InitException("Segment Service Init Fail");
        }
    }

    public Result getId(String key) {
        return idGen.get(key);
    }

    public SegmentIDGenImpl getIdGen() {
        if (idGen instanceof SegmentIDGenImpl) {
            return (SegmentIDGenImpl) idGen;
        }
        return null;
    }
}
