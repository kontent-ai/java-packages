/*
 * MIT License
 *
 * Copyright (c) 2020
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

package kentico.kontent.delivery;

/**
 * Thrown when retry attempts are is thrown when executing against the KenticoKontent API.  Generally means
 * connectivity problems with Kentico.
 */
public class KenticoRetryException extends RuntimeException implements KenticoException {

    private final int maxRetryAttempts;

    KenticoRetryException(int maxRetryAttempts) {
        super(String.format("Retry attempty reached max retry attempts (%d) ", maxRetryAttempts));
        this.maxRetryAttempts = maxRetryAttempts;
    }

    public int getMaxRetryAttempts() {
        return maxRetryAttempts;
    }

    @Override
    public boolean shouldRetry() {
        return false;
    }
}
