/*
 * MIT License
 *
 * Copyright (c) 2023 Akram Louze
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.akraml.serversync.velocity;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import me.akraml.serversync.ServerSync;
import me.akraml.serversync.VersionInfo;
import me.akraml.serversync.broker.RedisMessageBrokerService;
import me.akraml.serversync.connection.ConnectionResult;
import me.akraml.serversync.connection.ConnectionType;
import me.akraml.serversync.connection.auth.ConnectionCredentials;
import me.akraml.serversync.connection.auth.credentials.RedisCredentialsKeys;
import me.akraml.serversync.server.ServersManager;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Velocity implementation for ServerSync proxy system.
 */
@Plugin(
        id = "serversync",
        name = "ServerSync",
        description = "Synchronize your spigot servers with proxies and manage them dynamically without extra configuration.",
        authors = "AkramL",
        version = VersionInfo.VERSION
)
public final class VelocityServerSyncPlugin {

    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Toml config;

    @Inject
    public VelocityServerSyncPlugin(final ProxyServer proxyServer,
                                    final Logger logger) throws Exception {
        this.proxyServer = proxyServer;
        this.logger = logger;

        // Load configuration.
        loadConfig();
        this.config = new Toml().read(new File("plugins/serversync/config.toml"));
    }

    @Subscribe
    public void onInitialize(final ProxyInitializeEvent event) {
        final long start = System.currentTimeMillis();
        logger.info("""
                 
                 __                          __                 \s
                / _\\ ___ _ ____   _____ _ __/ _\\_   _ _ __   ___\s
                \\ \\ / _ \\ '__\\ \\ / / _ \\ '__\\ \\| | | | '_ \\ / __|
                _\\ \\  __/ |   \\ V /  __/ |  _\\ \\ |_| | | | | (__\s
                \\__/\\___|_|    \\_/ \\___|_|  \\__/\\__, |_| |_|\\___|
                                                |___/           \s
                                                
                """);
        logger.info("This server is running ServerSync " + VersionInfo.VERSION + " by AkramL.");
        final ServersManager serversManager = new VelocityServersManager(proxyServer);
        // Initialize message broker service.
        final ConnectionType connectionType = ConnectionType.valueOf(config.getString("message-broker-service"));
        switch (connectionType) {
            case REDIS -> {
                logger.info("ServerSync will run under Redis message broker...");
                long redisStartTime = System.currentTimeMillis();
                final Toml redisTable = config.getTable("redis");
                final ConnectionCredentials credentials = ConnectionCredentials.newBuilder()
                        .addKey(RedisCredentialsKeys.HOST, redisTable.getString("host"))
                        .addKey(RedisCredentialsKeys.PORT, redisTable.getLong("port").intValue())
                        .addKey(RedisCredentialsKeys.PASSWORD, redisTable.getString("password"))
                        .addKey(RedisCredentialsKeys.TIMEOUT, redisTable.getLong("timeout").intValue())
                        .addKey(RedisCredentialsKeys.MAX_TOTAL, redisTable.getLong("max-total").intValue())
                        .addKey(RedisCredentialsKeys.MAX_IDLE, redisTable.getLong("max-idle").intValue())
                        .addKey(RedisCredentialsKeys.MIN_IDLE, redisTable.getLong("min-idle").intValue())
                        .addKey(RedisCredentialsKeys.MIN_EVICTABLE_IDLE_TIME, redisTable.getLong("min-evictable-idle-time").intValue())
                        .addKey(RedisCredentialsKeys.TIME_BETWEEN_EVICTION_RUNS, redisTable.getLong("time-between-eviction-runs").intValue())
                        .addKey(RedisCredentialsKeys.BLOCK_WHEN_EXHAUSTED, redisTable.getBoolean("block-when-exhausted"))
                        .build();
                final RedisMessageBrokerService messageBrokerService = new RedisMessageBrokerService(
                        serversManager,
                        credentials
                );
                final ConnectionResult connectionResult = messageBrokerService.connect();
                if (connectionResult == ConnectionResult.FAILURE) {
                    logger.error("Failed to connect into redis, please check credentials!");
                    return;
                }
                logger.info("Successfully connected to redis, process took " + (System.currentTimeMillis() - redisStartTime) + "ms!");
                messageBrokerService.startHandler();
                ServerSync.initializeInstance(serversManager, messageBrokerService);
            }
            case RABBITMQ -> {
                logger.error("RabbitMQ is not ready yet, the plugin won't continue starting up!");
                return;
            }
        }
        logger.info("ServerSync has fully started in " + (System.currentTimeMillis() - start) + "ms.");
    }

    private void loadConfig() throws IOException {
        final File dataFolder = new File("plugins/serversync");
        if (!dataFolder.exists()) dataFolder.createNewFile();
        final File configFile = new File("config.toml");
        if (!configFile.exists()) {
            try (final InputStream inputStream = VelocityServerSyncPlugin.class.getResourceAsStream("/config.toml")) {
                assert inputStream != null;
                Files.copy(inputStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    @Subscribe
    public void onShutdown(final ProxyShutdownEvent event) {
        ServerSync.getInstance().getMessageBrokerService().stop();
    }

}
