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

package me.akraml.serversync.server;

import me.akraml.serversync.player.SyncPlayer;

import java.util.Collection;

/**
 * Represents a server that can be monitored and synchronized with proxy or proxies.
 * This interface provides methods to retrieve required information about the server.
 *
 * @version 1.0-SNAPSHOT
 */
public interface Server {

    /**
     * Retrieves the name of the current synchronized server.
     *
     * @return {@link Server}'s current name.
     */
    String getName();

    /**
     * Retrieves the IP address of the current synchronized server.
     *
     * @return The IP address of the server.
     */
    String getIp();

    /**
     * Retrieves the port number on which the server is running.
     *
     * @return The port number of the server.
     */
    int getPort();

    /**
     * Retrieves a collection of the players currently online on this server.
     *
     * @return A collection of {@link SyncPlayer} representing the players online.
     */
    Collection<SyncPlayer> getOnlinePlayers();

    /**
     * Retrieves the maximum number of players allowed on this server.
     *
     * @return An integer representing the maximum number of players.
     */
    int getMaxPlayers();

    /**
     * Gets the timestamp of the last heartbeat received from this server.
     * This can be used to check the status or health of the server.
     *
     * @return A long representing the timestamp of the last heartbeat in milliseconds.
     */
    long getLastHeartbeat();

    /**
     * Computes the time elapsed since the last heartbeat received from this server.
     *
     * @return A long representing the time elapsed since the last heartbeat in milliseconds.
     */
    default long getSinceLastHeartbeat() {
        return System.currentTimeMillis() - getLastHeartbeat();
    }

    /**
     * Initializes a new instance of {@link ServerImpl} from the provided name, ip and port.
     *
     * @param name Name of the registered server.
     * @param ip IP Address of the server.
     * @param port Port of the server which will be accessed from.
     * @return New instance of {@link ServerImpl}
     */
    static Server of(final String name,
                      final String ip,
                      final int port) {
        return new ServerImpl(name, ip, port);
    }

}
