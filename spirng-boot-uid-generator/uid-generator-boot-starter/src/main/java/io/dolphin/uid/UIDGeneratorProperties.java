package io.dolphin.uid;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author dolphin
 * @date 2024年05月15日 15:42
 * @description
 */
@Component
@ConfigurationProperties(prefix = "uid.generator", ignoreUnknownFields = false)
//@ConditionalOnProperty(prefix = "uid.generator", name = "enabled", havingValue = "true", matchIfMissing = true)
public class UIDGeneratorProperties {
    private String name;

    private NoCache noCache;

    private Segment segment;

    private RedisCache redisCache;

    public static class NoCache {
    }

    public static class Segment {
        private boolean enable = false;

        private String url;

        private String username;

        private String password;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String toString() {
            return "Segment{" +
                    "enable=" + enable +
                    '}';
        }
    }

    public static class RedisCache {
        private boolean enable = false;
        private String url;
        private String username;
        private String pwd;
        private String keyPrefix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NoCache getNoCache() {
        return noCache;
    }

    public void setNoCache(NoCache noCache) {
        this.noCache = noCache;
    }

    public Segment getSegment() {
        return segment;
    }

    public void setSegment(Segment segment) {
        this.segment = segment;
    }

    public RedisCache getRedisCache() {
        return redisCache;
    }

    public void setRedisCache(RedisCache redisCache) {
        this.redisCache = redisCache;
    }
}
