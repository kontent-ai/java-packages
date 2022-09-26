/*
 * MIT License
 *
 * Copyright (c) 2019
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

package kontent.ai.delivery;

/**
 * Thrown to indicate failure of a Kentico request.
 *
 * @see KenticoError
 */
public class KenticoErrorException extends RuntimeException implements KenticoException {

    private final KenticoError kenticoError;
    private boolean shouldRetry;

    /**
     * Thrown to indicate failure of a Kentico request
     *
     * @param kenticoError The original KenticoError
     */
    public KenticoErrorException(KenticoError kenticoError, boolean shouldRetry) {
        super(kenticoError.getMessage());
        this.kenticoError = kenticoError;
        this.shouldRetry = shouldRetry;
    }

    /**
     * Returns the original error provided by Kentico.  Useful for debugging.
     *
     * @return The original KenticoError
     * @see KenticoError
     */
    public KenticoError getKenticoError() {
        return kenticoError;
    }

    @Override
    public boolean shouldRetry() {
        return this.shouldRetry;
    }
}
