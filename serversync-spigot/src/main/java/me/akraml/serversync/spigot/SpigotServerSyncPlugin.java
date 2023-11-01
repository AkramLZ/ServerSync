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

package me.akraml.serversync.spigot;

import lombok.Getter;
import me.akraml.serversync.ServerSync;
import me.akraml.serversync.VersionInfo;
import me.akraml.serversync.broker.RedisMessageBrokerService;
import me.akraml.serversync.connection.ConnectionResult;
import me.akraml.serversync.connection.ConnectionType;
import me.akraml.serversync.connection.auth.ConnectionCredentials;
import me.akraml.serversync.connection.auth.credentials.RedisCredentialsKeys;
import me.akraml.serversync.player.SyncPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Collectors;

/**
 * An implementation for ServerSync in Spigot platform.
 */
@Getter
public final class SpigotServerSyncPlugin extends JavaPlugin {

    private String serverName;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        final long start = System.currentTimeMillis();
        getLogger().info("\n" +
                " __                          __                  \n" +
                "/ _\\ ___ _ ____   _____ _ __/ _\\_   _ _ __   ___ \n" +
                "\\ \\ / _ \\ '__\\ \\ / / _ \\ '__\\ \\| | | | '_ \\ / __|\n" +
                "_\\ \\  __/ |   \\ V /  __/ |  _\\ \\ |_| | | | | (__ \n" +
                "\\__/\\___|_|    \\_/ \\___|_|  \\__/\\__, |_| |_|\\___|\n" +
                "                                |___/            \n");
        getLogger().info("This server is running ServerSync " + VersionInfo.VERSION + " by AkramL.");
        this.serverName = getConfig().getString("server-name");
        // Initialize message broker service.
        final ConnectionType connectionType = ConnectionType.valueOf(getConfig().getString("message-broker-service"));
        switch (connectionType) {
            case REDIS: {
                getLogger().info("ServerSync will run under Redis message broker...");
                long redisStartTime = System.currentTimeMillis();
                final ConfigurationSection redisSection = getConfig().getConfigurationSection("redis");
                assert redisSection != null;
                final ConnectionCredentials credentials = ConnectionCredentials.newBuilder()
                        .addKey(RedisCredentialsKeys.HOST, redisSection.getString("host"))
                        .addKey(RedisCredentialsKeys.PORT, redisSection.getInt("port"))
                        .addKey(RedisCredentialsKeys.PASSWORD, redisSection.getString("password"))
                        .addKey(RedisCredentialsKeys.TIMEOUT, redisSection.getInt("timeout"))
                        .addKey(RedisCredentialsKeys.MAX_TOTAL, redisSection.getInt("max-total"))
                        .addKey(RedisCredentialsKeys.MAX_IDLE, redisSection.getInt("max-idle"))
                        .addKey(RedisCredentialsKeys.MIN_IDLE, redisSection.getInt("min-idle"))
                        .addKey(RedisCredentialsKeys.MIN_EVICTABLE_IDLE_TIME, redisSection.getInt("min-evictable-idle-time"))
                        .addKey(RedisCredentialsKeys.TIME_BETWEEN_EVICTION_RUNS, redisSection.getInt("time-between-eviction-runs"))
                        .addKey(RedisCredentialsKeys.BLOCK_WHEN_EXHAUSTED, redisSection.getBoolean("block-when-exhausted"))
                        .build();
                final RedisMessageBrokerService messageBrokerService = new RedisMessageBrokerService(
                        null,
                        credentials
                );
                final ConnectionResult connectionResult = messageBrokerService.connect();
                if (connectionResult == ConnectionResult.FAILURE) {
                    getLogger().severe("Failed to connect into redis, please check credentials!");
                    setEnabled(false);
                    return;
                }
                getLogger().info("Successfully connected to redis, process took " + (System.currentTimeMillis() - redisStartTime) + "ms!");
                ServerSync.initializeInstance(null, messageBrokerService);
                break;
            }
            case RABBITMQ: {
                getLogger().severe("RabbitMQ is not ready yet, the plugin won't continue starting up!");
                return;
            }
        }
        getServer().getPluginManager().registerEvents(new SpigotServerSyncListener(this), this);
        ServerSync.getInstance().getMessageBrokerService().publishCreate(
                serverName,
                getServer().getIp(),
                getServer().getPort(),
                getServer().getMaxPlayers()
        );
        final int interval = getConfig().getInt("heartbeat-interval");
        getServer().getScheduler().runTaskTimerAsynchronously(this, () ->
                ServerSync.getInstance().getMessageBrokerService().publishHeartbeat(
                        serverName,
                        getServer().getIp(),
                        getServer().getPort(),
                        getServer().getMaxPlayers(),
                        getServer().getOnlinePlayers().stream()
                                .map(player -> new SyncPlayer(player.getUniqueId(), player.getName()))
                                .collect(Collectors.toList())
        ), interval, interval);
        getLogger().info("ServerSync has fully started in " + (System.currentTimeMillis() - start) + "ms.");
    }

    @Override
    public void onDisable() {
        if (ServerSync.getInstance() != null) {
            ServerSync.getInstance().getMessageBrokerService().publishRemove(serverName);
            ServerSync.getInstance().getMessageBrokerService().stop();
        }
    }

}
