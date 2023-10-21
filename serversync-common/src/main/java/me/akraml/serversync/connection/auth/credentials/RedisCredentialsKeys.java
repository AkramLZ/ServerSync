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

package me.akraml.serversync.connection.auth.credentials;

import me.akraml.serversync.connection.auth.CredentialsKey;

/**
 * Standard credentials keys for the Redis connection.
 */
public enum RedisCredentialsKeys implements CredentialsKey {

    HOST("host"),
    PORT("port"),
    PASSWORD("password"),
    TIMEOUT("timeout"),
    MAX_TOTAL("maxTotal"),
    MAX_IDLE("maxIdle"),
    MIN_IDLE("minIdle"),
    MIN_EVICTABLE_IDLE_TIME("minEvictableIdleTime"),
    TIME_BETWEEN_EVICTION_RUNS("timeBetweenEvictionRuns"),
    BLOCK_WHEN_EXHAUSTED("blockWhenExhausted");

    private final String keyName;

    RedisCredentialsKeys(String keyName) {
        this.keyName = keyName;
    }

    @Override
    public String getKeyName() {
        return keyName;
    }
}