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

import java.util.*;

/**
 * Represents an implementation of the {@link Server} interface.
 * This class holds server-related details such as the server name,
 * online players, the maximum allowed players, and the last heartbeat timestamp.
 *
 * @version 1.0-SNAPSHOT
 */
public final class ServerImpl implements Server {

    /** The name of the server. */
    private final String name;

    /** The IP address of the server. */
    private final String ip;

    /** The port which the server is running on */
    private final int port;

    /** A map storing online players using their UUIDs as the key. */
    private final Map<UUID, SyncPlayer> onlinePlayers = new HashMap<>();

    /** The maximum number of players allowed on the server. */
    private int maxPlayers = 0;

    /** The timestamp of the last heartbeat received from the server. */
    private long lastHeartbeat = System.currentTimeMillis();

    /**
     * Constructs a new ServerImpl with the given server name.
     *
     * @param name The name of the server.
     * @param ip   The address of the server.
     * @param port The port which the server is running on.
     */
    ServerImpl(final String name,
               final String ip,
               final int port) {
        this.name = name;
        this.ip   = ip;
        this.port = port;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public Collection<SyncPlayer> getOnlinePlayers() {
        return onlinePlayers.values();
    }

    @Override
    public boolean containsPlayer(UUID uuid) {
        return onlinePlayers.containsKey(uuid);
    }

    @Override
    public SyncPlayer getPlayer(UUID uuid) {
        return onlinePlayers.get(uuid);
    }

    /**
     * Adds a player to the server's online players map or updates it if already present.
     *
     * @param syncPlayer The player to be added or updated.
     */
    public void addPlayer(final SyncPlayer syncPlayer) {
        removePlayer(syncPlayer);
        onlinePlayers.put(syncPlayer.getUuid(), syncPlayer);
    }

    /**
     * Removes a player from the server's online players map.
     *
     * @param syncPlayer The player to be removed.
     */
    public void removePlayer(final SyncPlayer syncPlayer) {
        onlinePlayers.remove(syncPlayer.getUuid());
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(final int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    @Override
    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    /**
     * Updates the last heartbeat timestamp to the current time.
     */
    public void heartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }
}
