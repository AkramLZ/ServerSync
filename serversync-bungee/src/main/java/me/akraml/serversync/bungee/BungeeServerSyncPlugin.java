package me.akraml.serversync.bungee;

import me.akraml.serversync.ServerSync;
import me.akraml.serversync.VersionInfo;
import me.akraml.serversync.broker.RedisMessageBrokerService;
import me.akraml.serversync.connection.ConnectionResult;
import me.akraml.serversync.connection.ConnectionType;
import me.akraml.serversync.connection.auth.ConnectionCredentials;
import me.akraml.serversync.connection.auth.credentials.RedisCredentialsKeys;
import me.akraml.serversync.server.ServersManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * An implementation for ServerSync in BungeeCord platform.
 */
public final class BungeeServerSyncPlugin extends Plugin {

    private Configuration configuration;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onLoad() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        try {
            loadConfig();
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }

    }

    @Override
    public void onEnable() {
        final long start = System.currentTimeMillis();
        getLogger().info("\n" +
                " __                          __                  \n" +
                "/ _\\ ___ _ ____   _____ _ __/ _\\_   _ _ __   ___ \n" +
                "\\ \\ / _ \\ '__\\ \\ / / _ \\ '__\\ \\| | | | '_ \\ / __|\n" +
                "_\\ \\  __/ |   \\ V /  __/ |  _\\ \\ |_| | | | | (__ \n" +
                "\\__/\\___|_|    \\_/ \\___|_|  \\__/\\__, |_| |_|\\___|\n" +
                "                                |___/            \n");
        getLogger().info("This server is running ServerSync " + VersionInfo.VERSION + " by AkramL.");
        final ServersManager serversManager = new BungeeServersManager(getProxy());
        // Initialize message broker service.
        final ConnectionType connectionType = ConnectionType.valueOf(configuration.getString("message-broker-service"));
        switch (connectionType) {
            case REDIS: {
                getLogger().info("ServerSync will run under Redis message broker...");
                long redisStartTime = System.currentTimeMillis();
                final Configuration redisSection = configuration.getSection("redis");
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
                        serversManager,
                        credentials
                );
                final ConnectionResult connectionResult = messageBrokerService.connect();
                if (connectionResult == ConnectionResult.FAILURE) {
                    getLogger().severe("Failed to connect into redis, please check credentials!");
                    return;
                }
                getLogger().info("Successfully connected to redis, process took " + (System.currentTimeMillis() - redisStartTime) + "ms!");
                messageBrokerService.startHandler();
                ServerSync.initializeInstance(serversManager, messageBrokerService);
                break;
            }
            case RABBITMQ: {
                getLogger().severe("RabbitMQ is not ready yet, the plugin won't continue starting up!");
                return;
            }
        }
        getLogger().info("ServerSync has fully started in " + (System.currentTimeMillis() - start) + "ms.");
    }

    @Override
    public void onDisable() {
        ServerSync.getInstance().getMessageBrokerService().stop();
    }

    private void loadConfig() throws IOException {
        final File configFile = new File("config.toml");
        if (!configFile.exists()) {
            try (final InputStream inputStream = getClass().getResourceAsStream("/config.yml")) {
                assert inputStream != null;
                Files.copy(inputStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
    }

}
