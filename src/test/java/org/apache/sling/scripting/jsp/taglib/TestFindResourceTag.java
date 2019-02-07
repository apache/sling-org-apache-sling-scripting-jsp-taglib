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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletRequest;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScript;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.commons.testing.sling.MockResource;
import org.apache.sling.commons.testing.sling.MockResourceResolver;
import org.apache.sling.commons.testing.sling.MockSlingHttpServletRequest;
import org.junit.Before;
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
    private FindResourcesTag findResourcesTag;
    private MockResource resource;
    private MockPageContext pageContext;
    private MockSlingHttpServletRequest request;
    private static final String VAR_KEY = "resource";
    private static final String TEST_PATH = "/content";

    /**
     * Initializes the fields for this test.
     */
    @Before
    public void init() {
        log.info("init");

        final MockResourceResolver resolver = new MockResourceResolver() {
            @Override
            public Iterator<Resource> findResources(String query, String language) {
                if (query.equals("query") && language.equals("language")) {
                    List<Resource> resources = new ArrayList<Resource>();
                    resources.add(resource);
                    return resources.iterator();
                } else {
                    return null;
                }
            }
        };
        resource = new MockResource(resolver, TEST_PATH, "test");
        resolver.addResource(resource);

        final SlingBindings bindings = new SlingBindings();
        bindings.setSling(new SlingScriptHelper() {

            @Override
            public SlingHttpServletRequest getRequest() {
                return request;
            }

            @Override
            public SlingHttpServletResponse getResponse() {
                throw new UnsupportedOperationException();
            }

            @Override
            public SlingScript getScript() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void include(String path) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void include(String path, String requestDispatcherOptions) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void include(String path, RequestDispatcherOptions options) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void include(Resource resource) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void include(Resource resource, String requestDispatcherOptions) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void include(Resource resource, RequestDispatcherOptions options) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void forward(String path) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void forward(String path, String requestDispatcherOptions) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void forward(String path, RequestDispatcherOptions options) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void forward(Resource resource) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void forward(Resource resource, String requestDispatcherOptions) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void forward(Resource resource, RequestDispatcherOptions options) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <ServiceType> ServiceType getService(Class<ServiceType> serviceType) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <ServiceType> ServiceType[] getServices(Class<ServiceType> serviceType, String filter) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void dispose() {
                throw new UnsupportedOperationException();
            }
        });
        request = new MockSlingHttpServletRequest("/content", "", "html", "", "") {
            public Object getAttribute(String name) {
                return bindings;
            }
            public ResourceResolver getResourceResolver() {
                return resolver;
            }
        };

        pageContext = new MockPageContext() {
            public ServletRequest getRequest() {
                return request;
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
        findResourcesTag.setLanguage("language");
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
