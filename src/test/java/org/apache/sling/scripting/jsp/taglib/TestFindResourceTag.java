/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.scripting.jsp.taglib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;

import javax.jcr.Session;
import javax.servlet.ServletRequest;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.jcr.MockJcr;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit Tests for the Class FindResourceTag.
 * 
 * @see org.apache.sling.scripting.jsp.taglib.FindResourceTag
 */
public class TestFindResourceTag {

    private static final Logger log = LoggerFactory.getLogger(TestFindResourceTag.class);

    @Rule
    public final SlingContext ctx = new SlingContext(ResourceResolverType.JCR_MOCK);
    private FindResourcesTag findResourcesTag;
    private Resource resource;
    private MockPageContext pageContext;
    private static final String VAR_KEY = "resource";
    private static final String TEST_PATH = "/content";

    /**
     * Initializes the fields for this test.
     */
    @Before
    public void init() {
        log.info("init");

        ctx.build()
            .resource("/")
            .resource(TEST_PATH);
        
        resource = ctx.resourceResolver().getResource(TEST_PATH);
        MockJcr.setQueryResult(ctx.resourceResolver().adaptTo(Session.class), Arrays.asList(resource.adaptTo(javax.jcr.Node.class)));

        pageContext = new MockPageContext() {
            public ServletRequest getRequest() {
                return ctx.request();
            }
        };
        findResourcesTag = new FindResourcesTag();
        findResourcesTag.setPageContext(pageContext);

        log.info("init Complete");
    }

    /**
     * Tests the find resources functionalty.
     */
    @Test
    public void testFindResources() {
        log.info("testGoodPath");

        findResourcesTag.setVar(VAR_KEY);
        findResourcesTag.setQuery("query");
        findResourcesTag.setLanguage("sql"); // must be something supported by the JCR provider
        findResourcesTag.doEndTag();
        Object result = pageContext.getAttribute(VAR_KEY);
        assertNotNull(result);
        assertTrue(result instanceof Iterator);
        @SuppressWarnings("unchecked")
        Iterator<Resource> resources = (Iterator<Resource>) result;
        assertTrue(resources.hasNext());
        assertEquals(TEST_PATH, resources.next().getPath());

        log.info("Test successful!");
    }
}
