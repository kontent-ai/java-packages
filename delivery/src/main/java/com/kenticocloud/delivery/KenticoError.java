/*
 * MIT License
 *
 * Copyright (c) 2017
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

package com.kenticocloud.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Kentico error response
 * <p>
 * Kentico Cloud returns standard HTTP status codes to indicate success or failure of a request. In general, codes in
 * the 2xx range indicate a successful request, codes in the 4xx range indicate errors caused by an incorrect input
 * (for example, providing incorrect API key), and codes in the 5xx range indicate an error on our side.
 * <p>
 * For troubleshooting failed requests, the Kentico Cloud APIs provide error messages defined in a consumable format to
 * help you identify and fix the issue. For example, when you request a content item that does not exist (e.g., you
 * mistype its codename), the API returns a 404 HTTP error along with a JSON message.
 * <p>
 * If you cannot identify and resolve an issue with your API call, you can contact us with the response status and the
 * unique error ID. Hint: use the chat button in the bottom right corner of
 * <a href="https://developer.kenticocloud.com/v1/reference">this page</a>.
 */
public class KenticoError implements Serializable {

    static final long serialVersionUID = 42L;

    @JsonProperty("message")
    String message;

    @JsonProperty("request_id")
    String requestId;

    @JsonProperty("error_code")
    int errorCode;

    @JsonProperty("specific_code")
    int specificCode;

    KenticoError() {
        //Default constructor
    }

    /**
     * Returns the error message provided by Kentico detailing the error.
     * @return the error message from Kentico.
     */
    public String getMessage() {
        return message;
    }

    void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns a unique ID that can be provided to Kentico for support in relation to the error.
     * @return the unique ID for this error
     */
    public String getRequestId() {
        return requestId;
    }

    void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Returns the HTTP error code returned by Kentico.
     * <table summary="HTTP error codes">
     * <tr><td>400 - Bad Request</td><td>The request was not understood. Check for a missing required parameter, or an
     * invalid parameter value.</td></tr>
     * <tr><td>401 - Unauthorized</td><td>The provided API key is invalid or missing. See
     * {@link DeliveryClient#DeliveryClient(String, String)}.</td></tr>
     * <tr><td>403 - Forbidden</td><td>The provided API key is invalid for the requested project.</td></tr>
     * <tr><td>404 - Not Found</td><td>The requested resource doesn't exist. Try checking the resource name for typos.
     * </td></tr>
     * </table>
     * @return the http error code
     */
    public int getErrorCode() {
        return errorCode;
    }

    void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Returns the specific code returned by the Kentico error response.
     * @return the specific code returned by the Kentico error response
     */
    public int getSpecificCode() {
        return specificCode;
    }

    void setSpecificCode(int specificCode) {
        this.specificCode = specificCode;
    }
}
