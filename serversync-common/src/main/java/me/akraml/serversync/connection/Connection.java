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

package me.akraml.serversync.connection;

import java.io.Closeable;

/**
 * Represents a connection to a remote system.
 */
public interface Connection<C> {

    /**
     * Attempts to establish a connection.
     *
     * @return The result of the connection attempt.
     */
    ConnectionResult connect();

    /**
     * Attempts to close the current connection.
     */
    default void close() {
        // Check if the connection is closeable.
        if (getConnection() instanceof Closeable) {
            try {
                ((Closeable) getConnection()).close();
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
        // Check if the connection is auto-closable
        else if (getConnection() instanceof AutoCloseable) {
            try {
                ((AutoCloseable) getConnection()).close();
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
        // Otherwise, just throw an exception
        else {
            throw new RuntimeException(new UnsupportedOperationException("Non-closeable connection"));
        }
    }

    /**
     * Retrieves the connection instance.
     *
     * @return Connection instance.
     */
    C getConnection();

}