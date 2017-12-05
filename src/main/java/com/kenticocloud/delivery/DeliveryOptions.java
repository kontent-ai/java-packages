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
    String productionApiKey = null;
    String previewApiKey;
    boolean usePreviewApi = false;
    boolean waitForLoadingNewContent = false;

    /**
     * Constructs an empty settings instance of {@link DeliveryOptions}.
     */
    public DeliveryOptions() {
        //Default constructor
    }

    /**
     * Constructs a setting instance of {@link DeliveryOptions} using your Kentico Cloud Project identifier.
     * @param projectId The Project ID associated with your Kentico Cloud account.  Must be in the format of an
     * {@link java.util.UUID}.
     */
    public DeliveryOptions(String projectId) {
        this.setProjectId(projectId);
    }

    /**
     * Constructs a settings instance of {@link DeliveryOptions} using your Kentico Cloud Project identifier and using
     * the preview API.
     * @param projectId The Project ID associated with your Kentico Cloud account.  Must be in the format of an
     * {@link java.util.UUID}.
     * @param previewApiKey The Preview API key configured with your Kentico Cloud account.
     */
    public DeliveryOptions(String projectId, String previewApiKey) {
        this(projectId);
        this.setPreviewApiKey(previewApiKey);
        this.setUsePreviewApi(true);
    }

    /**
     * Gets the Production endpoint address.
     * @return The Production endpoint address set in this options instance.
     */
    public String getProductionEndpoint() {
        return productionEndpoint;
    }

    /**
     * Sets the Production endpoint address.  Defaults to "https://deliver.kenticocloud.com/%s"
     * @see java.util.Formatter
     * @param productionEndpoint A Java format String containing the base URL.
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
     * Sets the Preview endpoint address.  Defaults to "https://preview-deliver.kenticocloud.com/%s".
     * @see java.util.Formatter
     * @param previewEndpoint A Java format String containing the base URL.
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
     * @param projectId The Project ID associated with your Kentico Cloud account.  Must be in the format of an
     * {@link java.util.UUID}.
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * Gets the Production API key.
     * @return The Preview API key set in this options instance.
     */
    public String getProductionApiKey() {
        return productionApiKey;
    }

    /**
     * Sets the Production API key.
     * @param productionApiKey The Preview API key configured with your Kentico Cloud account.
     */
    public void setProductionApiKey(String productionApiKey) {
        this.productionApiKey = productionApiKey;
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
     * @param previewApiKey The Preview API key configured with your Kentico Cloud account.
     */
    public void setPreviewApiKey(String previewApiKey) {
        this.previewApiKey = previewApiKey;
        setUsePreviewApi(previewApiKey != null);
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
     * @param usePreviewApi Whether the API should be used.  Defaults to false.
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
     * @param waitForLoadingNewContent Whether to wait for new content.  Defaults to false.
     */
    public void setWaitForLoadingNewContent(boolean waitForLoadingNewContent) {
        this.waitForLoadingNewContent = waitForLoadingNewContent;
    }
}
