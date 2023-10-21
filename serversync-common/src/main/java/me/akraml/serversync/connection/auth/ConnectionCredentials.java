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

package me.akraml.serversync.connection.auth;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the credentials information which will be used to connect.
 *
 * @see me.akraml.serversync.connection.auth.AuthenticatedConnection
 */
public final class ConnectionCredentials {

    private final Map<CredentialsKey, Object> keyMap = new HashMap<>();

    /**
     * Constructor must be private to disallow external initialization.
     */
    private ConnectionCredentials() {
    }

    /**
     * Retrieves the value associated with the specified key and casts it to the specified type.
     *
     * @param key       The key of the property.
     * @param typeClass The class representing the type of the property.
     * @param <T>       The type of the property.
     * @return The value associated with the key, cast to the specified type.
     * @throws ConnectionAuthenticationException if the key is missing in the credentials.
     * @throws ClassCastException                if the type is not the same as the key.
     */
    public <T> T getProperty(final CredentialsKey key,
                             final Class<T> typeClass) {
        if (!keyMap.containsKey(key)) {
            throw new ConnectionAuthenticationException("Missing key=" + key);
        }
        Object keyObject = keyMap.get(key);
        return typeClass.cast(keyObject);
    }

    /**
     * Creates a new instance of the ConnectionCredentials.Builder.
     *
     * @return A new instance of the ConnectionCredentials.Builder.
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * A builder class for constructing ConnectionCredentials objects.
     */
    public static final class Builder {

        private final ConnectionCredentials credentials;

        private Builder() {
            this.credentials = new ConnectionCredentials();
        }

        /**
         * Adds a key-value pair to the connection credentials.
         *
         * @param key   The key of the credential.
         * @param value The value of the credential.
         * @return The Builder instance.
         */
        public Builder addKey(final CredentialsKey key,
                              final Object value) {
            credentials.keyMap.put(key, value);
            return this;
        }

        /**
         * Builds and returns the ConnectionCredentials object.
         *
         * @return The constructed ConnectionCredentials object.
         */
        public ConnectionCredentials build() {
            return credentials;
        }

    }

}