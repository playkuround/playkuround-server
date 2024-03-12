package com.playkuround.playkuroundserver.global.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import redis.embedded.RedisServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

@Profile("test")
@Configuration
public class EmbeddedRedisConfig {

    @Value("${spring.data.redis.port}")
    private Integer redisPort;
    private RedisServer redisServer;


    @PostConstruct
    private void startRedis() throws IOException {
        int port = isRedisRunning() ? findAvailablePort() : redisPort;

        if (isArmArchitecture()) {
            redisServer = new RedisServer(Objects.requireNonNull(getRedisFileForArc()), port);
        }
        else {
            redisServer = new RedisServer(port);
        }

        redisServer.start();
        System.out.println("Embedded Redis Started");
    }

    private boolean isArmArchitecture() {
        return Objects.equals(System.getProperty("os.arch"), "aarch64");
    }

    private File getRedisFileForArc() {
        try {
            return new ClassPathResource("binary/redis/redis-server-7.2.4-mac-arm64").getFile();
        } catch (Exception e) {
            throw new RuntimeException("Embedded Redis Executable File Not Found");
        }
    }

    @PreDestroy
    private void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    private boolean isRedisRunning() throws IOException {
        return isRunning(executeGrepProcessCommand(redisPort));
    }

    private int findAvailablePort() throws IOException {
        for (int port = 10000; port <= 65535; port++) {
            Process process = executeGrepProcessCommand(port);
            if (!isRunning(process)) {
                return port;
            }
        }
        throw new IllegalArgumentException("Not Found Available port: 10000 ~ 65535");
    }

    private Process executeGrepProcessCommand(int port) throws IOException {
        String command = String.format("netstat -nat | grep LISTEN|grep %d", port);
        String[] shell = {"/bin/sh", "-c", command};
        return Runtime.getRuntime().exec(shell);
    }

    private boolean isRunning(Process process) {
        String line;
        StringBuilder pidInfo = new StringBuilder();

        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while ((line = input.readLine()) != null) {
                pidInfo.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return StringUtils.hasText(pidInfo.toString());
    }


}
