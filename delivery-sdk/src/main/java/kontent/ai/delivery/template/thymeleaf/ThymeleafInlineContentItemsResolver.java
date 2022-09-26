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

package kontent.ai.delivery.template.thymeleaf;

import kontent.ai.delivery.template.RenderingEngineMissingException;
import kontent.ai.delivery.template.TemplateEngine;
import kontent.ai.delivery.template.TemplateEngineInlineContentItemsResolver;
import kontent.ai.delivery.template.TemplateEngineModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Thymeleaf;

public class ThymeleafInlineContentItemsResolver implements TemplateEngineInlineContentItemsResolver {
    private static final Logger logger = LoggerFactory.getLogger(ThymeleafInlineContentItemsResolver.class);

    protected ThymeleafTemplateEngine thymeleafTemplateEngine;

    public ThymeleafInlineContentItemsResolver() throws RenderingEngineMissingException {
        super();
        try {
            Class.forName("org.thymeleaf.Thymeleaf");
            if (Thymeleaf.VERSION_MAJOR < 3) {
                throw new RenderingEngineMissingException("Support is only available for Thymeleaf version 3.0.0.RELEASE and above");
            }
            thymeleafTemplateEngine = new ThymeleafTemplateEngine();
        } catch (ClassNotFoundException e) {
            String msg = "Thymeleaf version 3.0.0.RELEASE or above is not on the classpath, Thymeleaf Inline Content resolution is disabled";
            logger.warn(msg);
            throw new RenderingEngineMissingException(msg, e);
        }
    }

    @Override
    public boolean supports(TemplateEngineModel data) {
        return thymeleafTemplateEngine.supports(data);
    }

    @Override
    public TemplateEngine getTemplateEngine() {
        return thymeleafTemplateEngine;
    }
}
