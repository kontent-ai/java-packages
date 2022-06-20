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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Content item system attributes
 * <p>
 * Every {@link ContentItem} and {@link ContentType} in a JSON response from the Delivery API contains a system
 * attribute. This attribute represents the System object with information about the retrieved content item
 */
@lombok.Getter
@lombok.Setter
@lombok.ToString
@lombok.EqualsAndHashCode
@lombok.NoArgsConstructor
public class System {

    /**
     * Unique identifier of the content item
     *
     * @param id    Sets the id of this.
     * @return      The identifier of this.
     */
    @JsonProperty("id")
    String id;

    /**
     * Display name of the content item.
     *
     * @param name  Sets the name of this.
     * @return      The display name of this.
     */
    @JsonProperty("name")
    String name;

    /**
     * Codename of the content item
     * <p>
     * Generated from the content item's display name.
     *
     * @param codename  Sets the codename of this.
     * @return          The codename of this.
     */
    @JsonProperty("codename")
    String codename;

    /**
     * Codename of the language variant
     *
     * @param language  Sets the language of this.
     * @return          The language variant of this.
     */
    @JsonProperty("language")
    String language;

    /**
     * Codename of the content type
     *
     * @param type  Sets the type of this.
     * @return      The content type codename.
     */
    @JsonProperty("type")
    String type;

    /**
     * Codename of the collection
     *
     * @param collection    Sets the collection of this.
     * @return              The collection codename.
     */
    @JsonProperty("collection")
    String collection;

    /**
     * A list of sitemap locations the content item is in
     *
     * @param sitemapLocations  Sets the sitemapLocations of this.
     * @return                  The sitemap location strings of this.
     */
    @JsonProperty("sitemap_locations")
    List<String> sitemapLocations;

    /**
     * When was the content item last modified
     *
     * @param lastModified  Sets the lastModified time of this.
     * @return              Zoned DateTime generated from ISO 8601 formatted string
     */
    @JsonProperty("last_modified")
    ZonedDateTime lastModified;

    /**
     * The codename of the item's current workflow step.
     *
     * @param workflowStep  Sets the workflowStep of this.
     * @return              The workflow step of this.
     */
    @JsonProperty("workflow_step")
    String workflowStep;
}
