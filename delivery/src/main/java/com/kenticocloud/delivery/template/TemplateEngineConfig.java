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

package com.kenticocloud.delivery.template;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import org.apache.commons.beanutils.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class TemplateEngineConfig {
    private static final Logger logger = LoggerFactory.getLogger(TemplateEngineConfig.class);

    private static final String DEFAULT_SCAN_PATH = "com.kenticocloud.delivery.template";

    private Map<String, Object> defaultModelVariables = new HashMap<>();
    private ViewResolverConfiguration viewResolverConfiguration = new ViewResolverConfiguration();
    private List<String> pathsToScan = new ArrayList<>();
    private boolean autoRegister = true;
    List<TemplateEngineInlineContentItemsResolver> resolvers = new ArrayList<>();

    public TemplateEngineConfig() {
        this.pathsToScan.add(DEFAULT_SCAN_PATH);
    }

    public void init() {
        if (isAutoRegister()) {
            FastClasspathScanner scanner =
                    new FastClasspathScanner(getPathsToScan().toArray(new String[0]));
            scanner.matchClassesImplementing(TemplateEngineInlineContentItemsResolver.class, implementingClass -> {
                try {
                    TemplateEngineInlineContentItemsResolver resolver =
                            ConstructorUtils.invokeConstructor(implementingClass, null);
                    resolver.getTemplateEngine().setViewResolverConfiguration(getViewResolverConfiguration());
                    addResolvers(resolver);
                    logger.info("Registered inline content template resolver: {}", resolver.getClass().getName());
                } catch (NoSuchMethodException |
                        IllegalAccessException |
                        InstantiationException e) {
                    logger.error("Exception instantiating template resolver {}", e);
                } catch (InvocationTargetException e) {
                    if (e.getTargetException() instanceof RenderingEngineMissingException) {
                        logger.info("Renderer Missing: {}", e.getTargetException().getMessage());
                    } else {
                        logger.error("Exception instantiating template resolver {}", e);
                    }
                }
            }).scan();

        }
    }

    public Map<String, Object> getDefaultModelVariables() {
        return defaultModelVariables;
    }

    public void setDefaultModelVariables(Map<String, Object> defaultModelVariables) {
        this.defaultModelVariables = defaultModelVariables;
    }

    public ViewResolverConfiguration getViewResolverConfiguration() {
        return viewResolverConfiguration;
    }

    public void setViewResolverConfiguration(ViewResolverConfiguration viewResolverConfiguration) {
        this.viewResolverConfiguration = viewResolverConfiguration;
    }

    public List<String> getPathsToScan() {
        return pathsToScan;
    }

    public void setPathsToScan(List<String> pathsToScan) {
        this.pathsToScan = pathsToScan;
    }

    public void addPathsToScan(String... paths) {
        this.pathsToScan.addAll(Arrays.asList(paths));
    }

    public boolean isAutoRegister() {
        return autoRegister;
    }

    public void setAutoRegister(boolean autoRegister) {
        this.autoRegister = autoRegister;
    }

    public List<TemplateEngineInlineContentItemsResolver> getResolvers() {
        return resolvers;
    }

    public void setResolvers(List<TemplateEngineInlineContentItemsResolver> resolvers) {
        this.resolvers = resolvers;
    }

    public void addResolvers(TemplateEngineInlineContentItemsResolver... resolvers) {
        this.resolvers.addAll(Arrays.asList(resolvers));
    }
}
