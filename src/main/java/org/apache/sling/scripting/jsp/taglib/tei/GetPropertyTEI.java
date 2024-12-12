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
package org.apache.sling.scripting.jsp.taglib.tei;

import javax.servlet.jsp.tagext.TagData;

public class GetPropertyTEI extends AbstractVarTEI {

    public static final String ATTR_DEFAULT_VALUE = "defaultValue";
    public static final String ATTR_RETURN_CLASS = "returnClass";

    @Override
    protected String getClassName(TagData data) {
        final Object defaultValue = data.getAttribute(ATTR_DEFAULT_VALUE);
        final String className = safeGetStringAttribute(data, ATTR_RETURN_CLASS, OBJECT_CLASS_NAME);
        if (defaultValue != null) {
            return defaultValue.getClass().getName();
        } else if (className != null) {
            return className;
        } else {
            return OBJECT_CLASS_NAME;
        }
    }
}
