/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.scripting.jsp.taglib;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit Tests for the Class AdaptObject.
 *
 * @see org.apache.sling.scripting.jsp.taglib.AdaptObjectTag
 */
public class TestAdaptObjectTag {

    private static final Logger log = LoggerFactory.getLogger(TestAdaptObjectTag.class);

    @Rule
    public final SlingContext context = new SlingContext();

    private AdaptToTag adaptToTag;
    private Resource resource;
    private MockPageContext pageContext;
    private static final String VAR_KEY = "properties";

    /**
     * Initializes the fields for this test.
     */
    @SuppressWarnings("serial")
    @Before
    public void init() {
        log.info("init");
        adaptToTag = new AdaptToTag() {
            protected ClassLoader getClassLoader() {
                return TestAdaptObjectTag.class.getClassLoader();
            }
        };

        pageContext = new MockPageContext();
        adaptToTag.setPageContext(pageContext);
        context.build().resource("/", "sling:resourceType", "test");
        resource = context.resourceResolver().getResource("/");
        log.info("init Complete");
    }

    /**
     * Tests the adapt object Tag functionality.
     */
    @Test
    public void testAdaptObject() {
        log.info("testAdaptObject");

        log.info("Setting up tests");
        adaptToTag.setAdaptable(resource);
        adaptToTag.setAdaptTo(ValueMap.class.getCanonicalName());
        adaptToTag.setVar(VAR_KEY);
        adaptToTag.doEndTag();

        log.info("Checking result");
        Object result = pageContext.getAttribute(VAR_KEY);
        assertNotNull(result);
        assertTrue(result instanceof ValueMap);

        log.info("Test successful!");
    }

    /**
     * Tests to ensure that a null result is returned if the class to adapt does not
     * exist.
     */
    @Test
    public void testMissingClass() {
        log.info("testMissingClass");

        log.info("Setting up tests");
        adaptToTag.setAdaptable(resource);
        adaptToTag.setAdaptTo("com.bad.class");
        adaptToTag.setVar(VAR_KEY);
        adaptToTag.doEndTag();

        log.info("Checking result");
        Object result = pageContext.getAttribute(VAR_KEY);
        assertNull(result);

        log.info("Test successful!");
    }
}
