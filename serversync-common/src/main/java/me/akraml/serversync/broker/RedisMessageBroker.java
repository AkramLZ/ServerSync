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

import me.akraml.serversync.ServersManager;
import me.akraml.serversync.connection.ConnectionResult;
import me.akraml.serversync.connection.auth.AuthenticatedConnection;
import me.akraml.serversync.connection.auth.ConnectionCredentials;
import me.akraml.serversync.connection.auth.credentials.RedisCredentialsKeys;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

public final class RedisMessageBroker extends MessageBroker implements AuthenticatedConnection<JedisPool> {

    private final ConnectionCredentials credentials;
    private JedisPool pool;

    public RedisMessageBroker(final ServersManager serversManager,
                              final ConnectionCredentials credentials) {
        super(serversManager);
        this.credentials = credentials;
    }

    @Override
    public ConnectionResult connect() {
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
        this.pool = new JedisPool(
                poolConfig,
                credentials.getProperty(RedisCredentialsKeys.HOST, String.class),
                credentials.getProperty(RedisCredentialsKeys.PORT, Integer.class),
                credentials.getProperty(RedisCredentialsKeys.TIMEOUT, Integer.class),
                credentials.getProperty(RedisCredentialsKeys.PASSWORD, String.class)
        );
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

}
