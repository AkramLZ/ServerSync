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

/**
 * Represents the different types of messages that can be sent to or received from a server.
 * These messages help in monitoring and managing server synchronization operations.
 *
 * <ul>
 *     <li>{@link #CREATE} - Indicates a request or action to create a new server.</li>
 *     <li>{@link #UPDATE} - Indicates an update action on an existing server.</li>
 *     <li>{@link #HEARTBEAT} - Represents a periodic signal sent by the server indicating its active status.</li>
 *     <li>{@link #REMOVE} - Indicates a request or action to remove an existing server.</li>
 * </ul>
 *
 * @version 1.0-BETA
 */
public enum ServerMessageType {

    /** Represents a creation message or action. */
    CREATE,

    /** Represents an update message or action. */
    UPDATE,

    /** Represents a heartbeat or periodic status check message. */
    HEARTBEAT,

    /** Represents a removal message or action. */
    REMOVE
}
