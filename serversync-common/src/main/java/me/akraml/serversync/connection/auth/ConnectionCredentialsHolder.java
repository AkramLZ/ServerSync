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

import me.akraml.serversync.connection.ConnectionType;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds the credentials for the specific connection to make retrieving them easier.
 */
public final class ConnectionCredentialsHolder {

    private final Map<ConnectionType, ConnectionCredentials> credentialsMap = new HashMap<>();

    /**
     * Registers the provided credentials to be retrieved later by its connection method.
     *
     * @param method      The method of the specific connection.
     * @param credentials The credentials data of the connection.
     */
    public void register(ConnectionType method,
                         ConnectionCredentials credentials) {
        credentialsMap.put(method, credentials);
    }

    /**
     * Retrieves the credentials data by its connection method.
     *
     * @param method The type of the connection.
     * @return Credentials data, null if not present.
     */
    public ConnectionCredentials getCredentials(ConnectionType method) {
        return credentialsMap.get(method);
    }

}