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

import java.lang.reflect.Field;

import org.apache.sling.scripting.jsp.taglib.internal.XSSSupport;
import org.apache.sling.xss.impl.XSSAPIImpl;
import org.apache.sling.xss.impl.XSSFilterImpl;
import org.junit.rules.ExternalResource;

public class XSSSupportRule extends ExternalResource {

    @Override
    protected void before() throws Throwable {
        setXssApiField(createXSSAPI());
    }

    @Override
    protected void after() {
        try {
            setXssApiField(null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void setXssApiField(XSSAPIImpl val) throws ReflectiveOperationException {
        Field xssApiField = XSSSupport.class.getDeclaredField("XSS_API");
        boolean accessible = xssApiField.isAccessible();
        if (!accessible) xssApiField.setAccessible(true);
        xssApiField.set(null, val);
        if (!accessible) xssApiField.setAccessible(false);
    }

    private XSSAPIImpl createXSSAPI() throws ReflectiveOperationException {
        // not activating since it requires repository access and we don't need that part of the code anyway
        XSSFilterImpl filter = new XSSFilterImpl();
        XSSAPIImpl api = new XSSAPIImpl();
        Field filterField = XSSAPIImpl.class.getDeclaredField("xssFilter");
        filterField.setAccessible(true);
        filterField.set(api, filter);

        return api;
    }
}
