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

import java.util.Iterator;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit Tests for the Class GetResourceTag.
 *
 * @see org.apache.sling.scripting.jsp.taglib.GetResourceTag
 */
public class TestGetParentsTag {

    private static final Logger log = LoggerFactory.getLogger(TestGetParentsTag.class);

    @Rule
    public final SlingContext ctx = new SlingContext();

    private GetParentsTag getParentsTag;
    private MockPageContext pageContext;
    private Resource child;
    private static final String VAR_KEY = "resource";
    private static final String TEST_ABSOLUTE_PATH = "/content";
    private static final String TEST_RELATIVE_PATH = "test";

    /**
     * Initializes the fields for this test.
     */
    @Before
    public void init() {
        log.info("init");

        ctx.build().resource("/").resource(TEST_ABSOLUTE_PATH).resource(TEST_ABSOLUTE_PATH + "/" + TEST_RELATIVE_PATH);

        child = ctx.resourceResolver().getResource(TEST_ABSOLUTE_PATH + "/" + TEST_RELATIVE_PATH);

        getParentsTag = new GetParentsTag();

        pageContext = new MockPageContext();
        getParentsTag.setPageContext(pageContext);

        log.info("init Complete");
    }

    /**
     * Tests basic functionality.
     */
    @Test
    public void testParents() {
        log.info("testParents");

        getParentsTag.setVar(VAR_KEY);
        getParentsTag.setResource(child);
        getParentsTag.setStartDepth("0");
        getParentsTag.doEndTag();
        Object result = pageContext.getAttribute(VAR_KEY);
        assertNotNull(result);
        assertTrue(result instanceof Iterator);
        @SuppressWarnings("unchecked")
        Iterator<Resource> parents = (Iterator<Resource>) result;
        assertTrue(parents.hasNext());

        assertEquals("/", parents.next().getPath());
        assertTrue(parents.hasNext());
        assertEquals(TEST_ABSOLUTE_PATH, parents.next().getPath());
        assertFalse(parents.hasNext());

        log.info("Test successful!");
    }

    /**
     * Tests basic functionality.
     */
    @Test
    public void testParentsWithStartDepth() {
        log.info("testParentsWithStartDepth");

        getParentsTag.setVar(VAR_KEY);
        getParentsTag.setResource(child);
        getParentsTag.setStartDepth("1");
        getParentsTag.doEndTag();
        Object result = pageContext.getAttribute(VAR_KEY);
        assertNotNull(result);
        assertTrue(result instanceof Iterator);
        @SuppressWarnings("unchecked")
        Iterator<Resource> parents = (Iterator<Resource>) result;
        assertTrue(parents.hasNext());

        assertEquals(TEST_ABSOLUTE_PATH, parents.next().getPath());
        assertFalse(parents.hasNext());

        log.info("Test successful!");
    }
}
