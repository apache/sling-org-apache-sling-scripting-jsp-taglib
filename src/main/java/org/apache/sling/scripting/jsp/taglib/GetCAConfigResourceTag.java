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
import org.apache.sling.caconfig.resource.ConfigurationResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tag for retrieving Context-Aware Configuration resources for a specified
 * resource, bucket and name.
 */
public class GetCAConfigResourceTag extends AbstractCATag {

    /** Serialization UID */
    private static final long serialVersionUID = -8191004532757820167L;

    /** The Constant log. */
    private static final Logger log = LoggerFactory.getLogger(GetCAConfigResourceTag.class);

    @Override
    public int doEndTag() {
        log.trace("doEndTag");

        ConfigurationResourceResolver caResourceResolver = this.getConfigurationResourceResolver();

        log.debug("Finding configuration with {}/{} for {}", getBucket(), getName(), getResource());
        Resource config = caResourceResolver.getResource(getResource(), getBucket(), getName());

        log.debug("Saving {} to variable {}", config, getVar());
        pageContext.setAttribute(getVar(), config);

        return EVAL_PAGE;
    }
}
