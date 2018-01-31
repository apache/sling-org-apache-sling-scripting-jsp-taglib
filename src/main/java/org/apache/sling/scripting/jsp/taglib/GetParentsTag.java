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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.jsp.tagext.TagSupport;

import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tag for retrieving all of the parent resources of a specified resource,
 * returning them in hierarchy order.
 */
public class GetParentsTag extends TagSupport {

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(GetParentsTag.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7519905660523764503L;

	/** The current resource. */
	private Resource resource;

	/** The startDepth. */
	private String startDepth = "0";

	/** The var. */
	private String var;

	@Override
	public int doEndTag() {
		log.trace("doEndTag");

		List<Resource> parents = new ArrayList<Resource>();
		Resource current = resource;
		while (true) {
			Resource parent = current.getParent();
			if(parent != null){
				parents.add(parent);
				current = parent;
			} else {
				break;
			}
		}
		Collections.reverse(parents);
		
		int depth = Integer.parseInt(startDepth,10);
		if(depth <= parents.size()){
			parents = parents.subList(depth, parents.size());
		} else {
			parents.clear();
		}

		log.debug("Saving {} to variable {}", parents, var);
		pageContext.setAttribute(var, parents.iterator());

		return EVAL_PAGE;
	}

	/**
	 * Gets the resource.
	 * 
	 * @return the base resource
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * Retrieves the start depth
	 * 
	 * @return the start depth to retrieve
	 */
	public String getStartDepth() {
		return startDepth;
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
	 * @param base
	 *            the new resource
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * The depth at which to start, for example given a path of:
	 * /content/page1/page2/page3 and a start depth of 3, the parents
	 * page2/page3 would be returned
	 * 
	 * @param startDepth
	 */
	public void setStartDepth(String startDepth) {
		this.startDepth = startDepth;
	}

	/**
	 * Sets the variable name to which to save the parent resource.
	 * 
	 * @param var
	 *            the variable name
	 */
	public void setVar(String var) {
		this.var = var;
	}
}
