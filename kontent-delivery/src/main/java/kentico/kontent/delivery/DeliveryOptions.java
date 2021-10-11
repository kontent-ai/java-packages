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

package kentico.kontent.delivery;

import lombok.Builder;

import java.net.Proxy;
import java.util.List;

/**
 * Keeps settings which are provided by customer or have default values, used in {@link DeliveryClient}.
 *
 * @see DeliveryClient
 * @see <a href="https://docs.kontent.ai/reference/delivery-api#section/Authentication">
 *      Kontent API reference - Authentication</a>
 * @see <a href="https://docs.kontent.ai/reference/delivery-api">
 *      Kontent API reference - Delivery API</a>
 * @see <a href="https://docs.kontent.ai/reference/delivery-api#tag/Secure-access">
 *      Kontent API reference - Secure access</a>
 * @see <a href="https://docs.kontent.ai/reference/delivery-api#tag/Secure-access">
 *      Kontent API reference - Securing public access</a>
 */
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
public class DeliveryOptions {

    /**
     * The Production endpoint address.  Mainly useful to change for mocks in unit tests, or if you are establishing a
     * proxy.
     * <p>
     * This defaults to "https://deliver.kontent.ai"
     *
     * @param productionEndpoint    New value for the productionEndpoint in this DeliveryOptions instance.
     * @return                      The value of the pstring used as the production endpoint to
     *                              Kontent.
     * @see                     <a href="https://docs.kontent.ai/reference/delivery-api#section/Production-vs.-Preview">
     *                          Kontent API reference - Production vs. preview</a>
     * @see                         java.util.Formatter
     */
    @Builder.Default
    String productionEndpoint = "https://deliver.kontent.ai";

    /**
     * The Preview endpoint address.  Mainly useful to change for mocks in unit tests, or if you are establishing a
     * proxy.
     * <p>
     * This defaults to "https://assets-us-01.kc-usercontent.com/"
     *
     * @param previewEndpoint   New value for the productionEndpoint in this DeliveryOptions instance.
     * @return                  The value of the string used as the preview endpoint to Kontent.
     * @see                     <a href="https://docs.kontent.ai/reference/delivery-api#section/Production-vs.-Preview">
     *                          Kontent API reference - Production vs. preview</a>
     * @see                     java.util.Formatter
     */
    @Builder.Default
    String previewEndpoint = "https://preview-deliver.kontent.ai";

    /**
     * The Project ID associated with your Kontent account.  Must be in the format of an {@link java.util.UUID}.
     *
     * @param projectId The Project ID associated with your Kontent account.  Must be in the format of an
     *                  {@link java.util.UUID}.
     * @return          The Project identifier set in this DeliveryOptions instance.
     * @see             <a href="https://docs.kontent.ai/reference/delivery-api">
     *                  Kontent API reference - Delivery API</a>
     */
    String projectId;

    /**
     * The Production API key (Secured access API key) configured with your Kontent account.
     * 
     *
     * @param productionApiKey  Sets the value of the production API key (Secured access API key) in this DeliveryOptions instance.
     * @return                  The value of the production API key in this DeliveryOptions instance.
     * @see                     <a href="https://docs.kontent.ai/reference/delivery-api#section/Production-vs.-Preview">
     *                          Kontent API reference - Production vs. preview</a>
     * @see                     <a href="https://docs.kontent.ai/reference/delivery-api#tag/Secure-access">
     *                          Secure access</a>
     */
    @Builder.Default
    String productionApiKey = null;

    /**
     * The Preview API key configured with your Kontent account.
     *
     * @return  The value of the preview API key in this DeliveryOptions instance.
     * @see     <a href="https://docs.kontent.ai/reference/delivery-api#section/Production-vs.-Preview">
     *          Kontent API reference - Production vs. preview</a>
     * @see     <a href="https://docs.kontent.ai/reference/delivery-api#section/Authentication">
     *          Kontent API reference - Authentication</a>
     */
    String previewApiKey;

    /**
     * This boolean flag determines if this client will use the preview API instead of the production API.  Defaults to
     * 'false'.
     *
     * @param usePreviewApi Whether the preview API should be used instead of the production API.  Defaults to 'false'.
     * @return              Whether this DeliveryOptions instance is set to use the preview API instead of the
     *                      production API.
     * @see                 <a href="https://docs.kontent.ai/reference/delivery-api#section/Production-vs.-Preview">
     *                      Kontent API reference - Production vs. preview</a>
     */
    @Builder.Default
    boolean usePreviewApi = false;

    /**
     * If the requested content has changed since the last request, the header determines whether to wait while fetching
     * content. This can be useful when retrieving changed content in reaction to a webhook call. By default, when set
     * to false, the API serves old content (if cached by the CDN) while it's fetching the new content to minimize wait
     * time. To always fetch new content, set the header to true.
     *
     * @param waitForLoadingNewContent  New value for this DeliveryOptions instance.
     * @return                          Whether this DeliveryOptions instance is set to always fetch new content.
     */
    @Builder.Default
    boolean waitForLoadingNewContent = false;

    /**
     * Sets the number of retry attempts the client should make when a request to the API fails.
     * Defaults to 3, meaning 1 normal request + 3 retrials.
     *
     * @param retryAttempts New value for this DeliveryOptions instance.
     * @return              The number of retry attempts configured per request in this DeliveryOptions instance.
     */
    @Builder.Default
    int retryAttempts = 3;

    /**
     * Sets the proxy server used by the http client.  See {@link Proxy}.
     *
     * @param proxyServer The {@link Proxy} instance to use with the client each request.
     * @return            The ProxyServer configured for this instance, or null.
     */
    @Builder.Default
    Proxy proxyServer = null;

    /**
     * Include custom request headers. Headers with name {@link DeliveryClient#HEADER_ACCEPT}, {@link DeliveryClient#HEADER_AUTHORIZATION}, {@link DeliveryClient#HEADER_X_KC_SDK_ID}, {@link DeliveryClient#HEADER_X_KC_WAIT_FOR_LOADING_NEW_CONTENT} will be ignored.
     */
    @Builder.Default
    List<Header> customHeaders = null;

    /**
     * Constructs a setting instance of {@link DeliveryOptions} using your Kontent Project identifier.
     *
     * @param projectId The Project ID associated with your Kontent account.  Must be in the format of an
     *                  {@link java.util.UUID}.
     */
    public DeliveryOptions(String projectId) {
        this();
        this.setProjectId(projectId);
    }

    /**
     * Constructs a settings instance of {@link DeliveryOptions} using your Kontent Project identifier and using
     * the preview API.
     *
     * @param projectId     The Project ID associated with your Kontent account.  Must be in the format of an
     *                      {@link java.util.UUID}.
     * @param previewApiKey The Preview API key configured with your Kontent account.
     */
    public DeliveryOptions(String projectId, String previewApiKey) {
        this(projectId);
        this.setPreviewApiKey(previewApiKey);
        this.setUsePreviewApi(true);
    }

    /**
     * The Preview API key configured with your Kontent account.
     *
     * @param previewApiKey Sets the value of the preview API key in this DeliveryOptions instance.  If not null,
     *                      automatically sets {@link #setUsePreviewApi(boolean)} to 'true'.
     * @see                 <a href="https://docs.kontent.ai/reference/delivery-api#section/Production-vs.-Preview">
     *                      Kontent API reference - Production vs. preview</a>
     * @see                 <a href="https://docs.kontent.ai/reference/delivery-api#section/Authentication">
     *                      Kontent API reference - Authentication</a>
     */
    public void setPreviewApiKey(String previewApiKey) {
        this.previewApiKey = previewApiKey;
        setUsePreviewApi(previewApiKey != null);
    }
}
