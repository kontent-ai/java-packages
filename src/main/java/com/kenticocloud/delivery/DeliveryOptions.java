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

/**
 * Keeps settings which are provided by customer or have default values,
 * used in {@link DeliveryClient}.
 */
public class DeliveryOptions {

    String productionEndpoint = "https://deliver.kenticocloud.com/%s";
    String previewEndpoint = "https://preview-deliver.kenticocloud.com/%s";
    String projectId;
    String previewApiKey;
    boolean usePreviewApi = false;
    boolean waitForLoadingNewContent = false;

    /**
     * Gets the Production endpoint address.
     * @return The Production endpoint address set in this options instance.
     */
    public String getProductionEndpoint() {
        return productionEndpoint;
    }

    /**
     * Sets the Production endpoint address.
     * @param productionEndpoint
     */
    public void setProductionEndpoint(String productionEndpoint) {
        this.productionEndpoint = productionEndpoint;
    }

    /**
     * Gets the Preview endpoint address.
     * @return The Preview endpoint address set in this options instance.
     */
    public String getPreviewEndpoint() {
        return previewEndpoint;
    }

    /**
     * Sets the Preview endpoint address.
     * @param previewEndpoint
     */
    public void setPreviewEndpoint(String previewEndpoint) {
        this.previewEndpoint = previewEndpoint;
    }

    /**
     * Gets the Project identifier.
     * @return The Project identifier set in this options instance.
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * Sets the Project identifier.
     * @param projectId
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * Gets the Preview API key.
     * @return The Preview API key set in this options instance.
     */
    public String getPreviewApiKey() {
        return previewApiKey;
    }

    /**
     * Sets the Preview API key.
     * @param previewApiKey
     */
    public void setPreviewApiKey(String previewApiKey) {
        this.previewApiKey = previewApiKey;
    }

    /**
     * Gets whether the Preview API will be used.
     * @return Whether the Preview API is used.
     */
    public boolean isUsePreviewApi() {
        return usePreviewApi;
    }

    /**
     * Sets whether the Preview API should be used.  If TRUE, the Preview API needs to be set as well.
     * @see #setPreviewApiKey(String)
     * @param usePreviewApi
     */
    public void setUsePreviewApi(boolean usePreviewApi) {
        this.usePreviewApi = usePreviewApi;
    }

    /**
     * Gets whether you want to wait for updated content.
     * @return Whether waiting for updated content is set.
     */
    public boolean isWaitForLoadingNewContent() {
        return waitForLoadingNewContent;
    }

    /**
     * Set to TRUE if you want to wait for updated content.  It should be used when you are acting upon a webhook call.
     * @param waitForLoadingNewContent
     */
    public void setWaitForLoadingNewContent(boolean waitForLoadingNewContent) {
        this.waitForLoadingNewContent = waitForLoadingNewContent;
    }
}
