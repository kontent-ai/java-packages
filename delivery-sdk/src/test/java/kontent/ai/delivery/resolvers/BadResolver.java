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

package kontent.ai.delivery.resolvers;

import kontent.ai.delivery.template.RenderingEngineMissingException;
import kontent.ai.delivery.template.TemplateEngine;
import kontent.ai.delivery.template.TemplateEngineInlineContentItemsResolver;
import kontent.ai.delivery.template.TemplateEngineModel;

public class BadResolver implements TemplateEngineInlineContentItemsResolver {

    public BadResolver() throws RenderingEngineMissingException {
        throw new RenderingEngineMissingException("Bad resolver", new Exception());
    }

    @Override
    public boolean supports(TemplateEngineModel data) {
        return false;
    }

    @Override
    public TemplateEngine getTemplateEngine() {
        return null;
    }
}
