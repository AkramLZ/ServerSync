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

/**
 * This exception is thrown when an authentication error occurs during initializing a {@link AuthenticatedConnection}.
 */
public class ConnectionAuthenticationException extends RuntimeException {

    /**
     * Constructs a new ConnectionAuthenticationException with the specified error message.
     *
     * @param message The error message associated with the exception.
     */
    public ConnectionAuthenticationException(final String message) {
        super(message);
    }

    /**
     * Constructs a new ConnectionAuthenticationException with the specified error message and cause.
     *
     * @param message   The error message associated with the exception.
     * @param throwable The cause of the exception.
     */
    public ConnectionAuthenticationException(final String message,
                                             final Throwable throwable) {
        super(message, throwable);
    }

}