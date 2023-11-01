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

package me.akraml.serversync.broker;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.akraml.serversync.server.ServersManager;
import me.akraml.serversync.connection.ConnectionResult;
import me.akraml.serversync.connection.auth.AuthenticatedConnection;
import me.akraml.serversync.connection.auth.ConnectionCredentials;
import me.akraml.serversync.connection.auth.credentials.RedisCredentialsKeys;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * A concrete implementation of the {@link MessageBrokerService} that utilizes Redis as the message broker backend.
 * This class handles server-related messages by subscribing to a Redis channel and performing
 * appropriate actions based on the received message. The connection with the Redis server is
 * managed using a {@link JedisPool}.
 *
 * <p>This class also implements {@link AuthenticatedConnection} which mandates methods related to
 * connection handling and credential management.</p>
 *
 * @version 1.0-BETA
 */
public final class RedisMessageBrokerService extends MessageBrokerService implements AuthenticatedConnection<JedisPool> {

    private final Gson gson = new Gson();
    private final ConnectionCredentials credentials;
    private JedisPool pool;

    /**
     * Constructs a new RedisMessageBroker with the given {@link ServersManager} and {@link ConnectionCredentials}.
     *
     * @param serversManager The servers manager to use for actions on servers.
     * @param credentials The credentials used to establish a connection with Redis.
     */
    public RedisMessageBrokerService(final ServersManager serversManager,
                                     final ConnectionCredentials credentials) {
        super(serversManager);
        this.credentials = credentials;
    }

    @Override
    public ConnectionResult connect() {

        // Initializes a jedis pool configuration with required information.
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(credentials.getProperty(RedisCredentialsKeys.MAX_TOTAL, Integer.class));
        poolConfig.setMaxIdle(credentials.getProperty(RedisCredentialsKeys.MAX_IDLE, Integer.class));
        poolConfig.setMinIdle(credentials.getProperty(RedisCredentialsKeys.MIN_IDLE, Integer.class));
        poolConfig.setBlockWhenExhausted(credentials.getProperty(RedisCredentialsKeys.BLOCK_WHEN_EXHAUSTED, Boolean.class));
        poolConfig.setMinEvictableIdleTime(
                Duration.ofMillis(credentials.getProperty(RedisCredentialsKeys.MIN_EVICTABLE_IDLE_TIME, Long.class))
        );
        poolConfig.setTimeBetweenEvictionRuns(
                Duration.ofMillis(credentials.getProperty(RedisCredentialsKeys.TIME_BETWEEN_EVICTION_RUNS, Long.class))
        );

        // Initializes a new jedis pool instance to hold connections on.
        this.pool = new JedisPool(
                poolConfig,
                credentials.getProperty(RedisCredentialsKeys.HOST, String.class),
                credentials.getProperty(RedisCredentialsKeys.PORT, Integer.class),
                credentials.getProperty(RedisCredentialsKeys.TIMEOUT, Integer.class),
                credentials.getProperty(RedisCredentialsKeys.PASSWORD, String.class)
        );

        // Tests if the connection works properly and return the result.
        try (final Jedis ignore = pool.getResource()) {
            return ConnectionResult.SUCCESS;
        } catch (final Exception exception) {
            return ConnectionResult.FAILURE;
        }
    }

    @Override
    public JedisPool getConnection() {
        return pool;
    }

    @Override
    public ConnectionCredentials getCredentials() {
        return credentials;
    }

    @Override
    public void startHandler() {
        CompletableFuture.runAsync(() -> {
            try (final Jedis jedis = getConnection().getResource()) {
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        onMessageReceive(gson.fromJson(message, JsonObject.class));
                    }
                }, "serversync:servers");
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace(System.err);
            return null;
        });
    }

    @Override
    public void stop() {
        close();
    }

    @Override
    public void publish(String message) {
        try(final Jedis jedis = pool.getResource()) {
            jedis.publish("serversync:servers", message);
        }
    }
}
