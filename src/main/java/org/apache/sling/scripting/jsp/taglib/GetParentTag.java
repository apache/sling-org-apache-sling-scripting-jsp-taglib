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

import javax.servlet.jsp.tagext.TagSupport;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tag for retrieving a parent resource based on the absolute parent level
 */
public class GetParentTag extends TagSupport {

    /** The Constant log. */
    private static final Logger log = LoggerFactory.getLogger(GetParentTag.class);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3419869755342010983L;

    /** The current resource. */
    private transient Resource resource;

    /** The level. */
    private String level;

    /** The var. */
    private String var;

    @Override
    public int doEndTag() {
        log.trace("doEndTag");

        Resource parent = null;

        if (resource != null) {
            if (level != null) {
                String parentPath = ResourceUtil.getParent(resource.getPath(), Integer.parseInt(level, 10));
                log.debug("Retrieving {} parent resource at path {}", level, parentPath);
                parent = resource.getResourceResolver().getResource(parentPath);
            } else {
                log.debug("Retrieving parent resource");
                parent = resource.getParent();
            }
        }

        log.debug("Saving {} to variable {}", parent, var);
        pageContext.setAttribute(var, parent);

        return EVAL_PAGE;
    }

    /**
     * Gets the resource.
     *
     * @return the resource
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Get the level of the parent resource to retrieve.
     *
     * @return the level
     */
    public String getLevel() {
        return level;
    }

    /**
     * Gets the variable name to which to save the list of children.
     *
     * @return the variable name
     */
    public String getVar() {
        return var;
    }

    /**
     * Sets the resource.
     *
     * @param resource the new resource
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * Set the level of the parent resource to retrieve.
     *
     * @param level the level
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * Sets the variable name to which to save the parent resource.
     *
     * @param var the variable name
     */
    public void setVar(String var) {
        this.var = var;
    }
}
