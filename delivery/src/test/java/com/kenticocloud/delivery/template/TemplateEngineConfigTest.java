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

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class TemplateEngineConfigTest {

    @Test
    public void testDefaultInit() {
        TemplateEngineConfig config = new TemplateEngineConfig();
        config.init();
        Assert.assertEquals(1, config.getResolvers().size());
    }

    @Test
    public void testInitWithAddedPath() {
        TemplateEngineConfig config = new TemplateEngineConfig();
        config.addPathsToScan("com.kenticocloud.delivery.resolvers");
        config.init();
        Assert.assertEquals(2, config.getResolvers().size());
    }

    @Test
    public void testInitWithReplacedPath() {
        TemplateEngineConfig config = new TemplateEngineConfig();
        config.setPathsToScan(Collections.singletonList("com.kenticocloud.delivery.resolvers"));
        config.init();
        Assert.assertEquals(1, config.getResolvers().size());
    }

    @Test
    public void testInitWhenAutoRegisterIsFalse() {
        TemplateEngineConfig config = new TemplateEngineConfig();
        config.setAutoRegister(false);
        config.init();
        Assert.assertEquals(0, config.getResolvers().size());
    }
}
