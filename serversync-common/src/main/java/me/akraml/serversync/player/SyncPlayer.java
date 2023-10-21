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

package me.akraml.serversync.player;

import java.util.UUID;

/**
 * Represents a player in a synchronized server system.
 * This class contains information about the player such as UUID and username.
 * It provides a means to uniquely identify a player and get their associated display name.
 *
 * @version 1.0-SNAPSHOT
 */
public final class SyncPlayer {

    /** Unique identifier for the player. */
    private final UUID uuid;

    /** Display name or username of the player. */
    private final String username;

    /**
     * Constructs a new SyncPlayer with the given UUID and username.
     *
     * @param uuid The unique identifier for the player.
     * @param username The display name or username of the player.
     */
    public SyncPlayer(final UUID uuid,
                      final String username) {
        this.uuid = uuid;
        this.username = username;
    }

    /**
     * Retrieves the UUID of the player.
     *
     * @return The unique identifier of the player.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Retrieves the username or display name of the player.
     *
     * @return The username of the player.
     */
    public String getUsername() {
        return username;
    }

}
