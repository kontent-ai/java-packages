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

package kentico.kontent.delivery.template.thymeleaf;

import kentico.kontent.delivery.ContentItem;
import kentico.kontent.delivery.ContentItemMapping;
import kentico.kontent.delivery.template.TemplateEngine;
import kentico.kontent.delivery.template.TemplateEngineModel;
import kentico.kontent.delivery.template.ViewResolverConfiguration;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.AbstractTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ThymeleafTemplateEngine implements TemplateEngine {
    private static final Logger logger = LoggerFactory.getLogger(ThymeleafTemplateEngine.class);

    boolean configured = false;
    protected ViewResolverConfiguration viewResolverConfiguration;

    org.thymeleaf.TemplateEngine templateEngine;

    @Override
    public void setViewResolverConfiguration(ViewResolverConfiguration viewResolverConfiguration) {
        this.viewResolverConfiguration = viewResolverConfiguration;
        configure();
    }

    @Override
    public String process(TemplateEngineModel data) {
        if (!configured) {
            throw new IllegalStateException("Engine not configured.  Did you call setViewResolverConfiguration()?");
        }
        Map<String, Object> variables = data.getVariables();
        Object inlineContentItem = data.getInlineContentItem();
        String contentType = getContentTypeFromModel(inlineContentItem);
        variables.put("model", inlineContentItem);
        IContext context = new Context(data.getLocale(), variables);
        return templateEngine.process(contentType, context);
    }

    protected void configure() {
        if (viewResolverConfiguration != null) {
            org.thymeleaf.TemplateEngine engine = new org.thymeleaf.TemplateEngine();
            Set<ITemplateResolver> resolvers = new HashSet<>();
            for (String prefix : viewResolverConfiguration.getPrefixes()) {
                ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
                templateResolver.setPrefix(prefix);
                templateResolver.setSuffix(viewResolverConfiguration.getSuffix());
                templateResolver.setTemplateMode(TemplateMode.HTML);
                templateResolver.setCheckExistence(true);
                resolvers.add(templateResolver);
            }
            engine.setTemplateResolvers(configureTemplateResolvers(resolvers));
            this.templateEngine = engine;
            configured = true;
        }
    }

    protected boolean supports(TemplateEngineModel data) {
        if (!configured) {
            return false;
        }
        String contentType = getContentTypeFromModel(data.getInlineContentItem());
        if (contentType == null) {
            return false;
        }
        Set<ITemplateResolver> templateResolvers = templateEngine.getTemplateResolvers();
        for (ITemplateResolver resolver : templateResolvers) {
            if (resolver instanceof AbstractTemplateResolver) {
                TemplateResolution templateResolution = resolver.resolveTemplate(templateEngine.getConfiguration(), null, contentType, null);
                if (templateResolution != null && templateResolution.getTemplateResource().exists()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Override this method to reconfigure template resolvers
     * @param resolvers A Set of {@link ClassLoaderTemplateResolver}
     * @return A set of {@link ITemplateResolver}
     */
    protected Set<ITemplateResolver> configureTemplateResolvers(Set<ITemplateResolver> resolvers) {
        return resolvers;
    }

    private String getContentTypeFromModel(Object model) {
        if (model instanceof ContentItem) {
            ContentItem contentItem = (ContentItem) model;
            return contentItem.getSystem().getType();
        }
        ContentItemMapping contentItemMapping = model.getClass().getAnnotation(ContentItemMapping.class);
        if (contentItemMapping != null) {
            return contentItemMapping.value();
        }
        try {
            Object system = PropertyUtils.getProperty(model, "system");
            if (system instanceof  kentico.kontent.delivery.System) {
                return ((kentico.kontent.delivery.System) system).getType();
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.debug("Unable to find System property on model", e);
        }
        return null;
    }
}
