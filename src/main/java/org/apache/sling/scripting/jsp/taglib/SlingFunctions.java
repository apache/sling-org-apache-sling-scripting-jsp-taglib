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

import javax.servlet.jsp.PageContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.caconfig.resource.ConfigurationResourceResolver;
import org.apache.sling.scripting.jsp.taglib.internal.XSSSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class containing the TagLib Functions for Sling.
 */
public class SlingFunctions {

    /**
     * The SLF4J Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(SlingFunctions.class);

    /**
     * Adapt the adaptable to the adapter class.
     *
     * @param adaptable the adaptable instance
     * @param adapter   the class to which to adapt the adaptable
     * @return the adapted class instance
     * @throws ClassNotFoundException the adapter class was not found by the
     *                                Classloader
     */
    public static Object adaptTo(Adaptable adaptable, String adapter) throws ClassNotFoundException {
        log.trace("adaptTo");
        Object adapted = null;

        if (adaptable != null) {
            log.debug("Adapting {} to class {}", adaptable, adapter);
            try {
                Class<?> adapterClass = loadClass(adapter);
                adapted = adaptable.adaptTo(adapterClass);
            } catch (ClassNotFoundException e) {
                log.error("Could not load class " + adapter, e);
            }
        } else {
            log.debug("Null adaptable specified");
        }
        return adapted;
    }

    /**
     * XSS encodes the specified text using the specified mode.
     *
     * @param value The text to encode
     * @param mode  The XSS mode to use, see XSSSupport for the list of available
     *              modes
     * @return the encoded text
     */
    public static String encode(String value, String mode) {
        return XSSSupport.encode(value, XSSSupport.getEncodingMode(mode));
    }

    /**
     * Searches for resources using the given query formulated in the given
     * language.
     *
     * @param resourceResolver the resource resolver to use to find resources with
     *                         the specified query
     * @param query            The query string to use to find the resources.
     * @param language         The language in which the query is formulated.
     * @return An Iterator of Resource objects matching the query.
     */
    public static Iterator<Resource> findResources(ResourceResolver resourceResolver, String query, String language) {
        log.trace("findResources");

        Iterator<Resource> resources = null;
        if (resourceResolver != null) {
            log.debug("Finding resources with query {} of type {}", query, language);
            resources = resourceResolver.findResources(query, language);
        } else {
            log.warn("Null resolver specified");
        }
        return resources;
    }

    /**
     * Method for retrieving an absolute parent resource.
     *
     * @param current the current resource
     * @param level   the absolute level for the parent resource to retrieve
     * @return the parent resource at the level
     */
    public static final Resource getAbsoluteParent(Resource current, String level) {
        log.trace("getAbsoluteParent");
        Resource parent = null;
        if (level != null) {
            String[] segments = current.getPath().split("\\/");
            int end = Integer.parseInt(level, 10);
            String parentPath = "/" + StringUtils.join(Arrays.copyOfRange(segments, 1, end + 1), "/");
            log.debug("Retrieving {} parent resource at path {}", level, parentPath);
            parent = current.getResourceResolver().getResource(parentPath);
        } else {
            log.debug("Retrieving parent resource");
            parent = current.getParent();
        }
        return parent;
    }

    /**
     * Method for retrieving the CA Config resource for a specified resource
     *
     * @param resource the resource for which to retrieve the CA Config resource
     * @param bucket   the bucket name of the configuration to retrieve
     * @param name     the configuration name to retrieve
     * @return the config resource
     */
    public static final Resource getCAConfigResource(Resource resource, String bucket, String name) {
        log.trace("getCAConfigResource");
        ConfigurationResourceResolver caResourceResolver =
                resource.getResourceResolver().adaptTo(ConfigurationResourceResolver.class);
        return caResourceResolver.getResource(resource, bucket, name);
    }

    /**
     * Method for retrieving the CA Config resources for a specified resource
     *
     * @param resource the resource for which to retrieve the CA Config resources
     * @param bucket   the bucket name of the configuration to retrieve
     * @param name     the configuration name to retrieve
     * @return the config resources
     */
    public static final Iterator<Resource> getCAConfigResources(Resource resource, String bucket, String name) {
        log.trace("getCAConfigResource");
        ConfigurationResourceResolver caResourceResolver =
                resource.getResourceResolver().adaptTo(ConfigurationResourceResolver.class);
        return caResourceResolver.getResourceCollection(resource, bucket, name).iterator();
    }

    /**
     * Function for retrieving all of the parent resources of a specified resource,
     * returning them in hierarchy order.
     *
     * @param current    the current resource for which to retrieve the parents
     * @param startDepth The depth at which to start, for example given a path of:
     *                   /content/page1/page2/page3 and a start depth of 3, the
     *                   parents page2/page3 would be returned
     * @return an iterator of the parent resources in order
     */
    public static final Iterator<Resource> getParents(Resource current, String startDepth) {
        List<Resource> parents = new ArrayList<>();
        while (true) {
            Resource parent = current.getParent();
            if (parent != null) {
                parents.add(parent);
                current = parent;
            } else {
                break;
            }
        }
        Collections.reverse(parents);

        int depth = Integer.parseInt(startDepth, 10);
        if (depth <= parents.size()) {
            parents = parents.subList(depth, parents.size());
        } else {
            parents.clear();
        }
        return parents.iterator();
    }

    /**
     * Gets the resource at the relative path to the provided resource.
     *
     * @param base the resource relative to which to find the path
     * @param path the relative path at which to find the resource
     * @return the resource
     */
    public static Resource getRelativeResource(Resource base, String path) {
        log.trace("getRelativeResource");

        Resource relative = null;
        if (base != null) {
            log.debug("Getting relative resource of {} at path {}", base.getPath(), path);
            relative = base.getResourceResolver().getResource(base, path);
        } else {
            log.warn("Null base resource specified");
        }

        return relative;
    }

    /**
     * Method allow for the retrieval of resources.
     *
     * @param resolver the current resource resolver
     * @param path     the path of the resource to retrieve
     * @return the resource at the path or null
     */
    public static final Resource getResource(ResourceResolver resolver, String path) {
        log.trace("getResource");

        log.debug("Getting resource at path {}", path);
        if (resolver == null) {
            throw new IllegalArgumentException("Null resource resolver");
        }
        return resolver.getResource(path);
    }

    /**
     * Method for retrieving the ResourceResolver from the page context.
     *
     * @return the resource resolver
     */
    protected static final ResourceResolver getResourceResolver(PageContext pageContext) {
        final SlingBindings bindings =
                (SlingBindings) pageContext.getRequest().getAttribute(SlingBindings.class.getName());
        final SlingScriptHelper scriptHelper = bindings.getSling();
        return scriptHelper.getRequest().getResourceResolver();
    }

    /**
     * Gets the value of the specified key from the ValueMap and either coerses the
     * value into the specified type or uses the specified type as a default
     * depending on the parameter passed in.
     *
     * @param properties    the ValueMap from which to retrieve the value
     * @param key           the key for the value to retrieve
     * @param defaultOrType either the default value or the class to which to coerce
     *                      the value
     * @return the value for the specified key or the default
     */
    @SuppressWarnings("unchecked")
    public static final <E> E getValue(ValueMap properties, String key, Object defaultOrType) {
        if (defaultOrType instanceof Class<?>) {
            return properties.get(key, (Class<E>) defaultOrType);
        } else {
            return properties.get(key, (E) defaultOrType);
        }
    }

    /**
     * Method for checking whether or not a resource has child resources.
     *
     * @param resource the resource to check for child resources
     * @return true if the resource has child resources, false otherwise
     * @since 2.2.2
     */
    public static final boolean hasChildren(Resource resource) {
        return resource != null ? resource.listChildren().hasNext() : false;
    }

    /**
     * Method for allowing the invocation of the Sling Resource listChildren method.
     *
     * @param resource the resource of which to list the children
     * @return the children of the resource
     * @see org.apache.sling.api.resource.Resource#listChildren()
     */
    public static final Iterator<Resource> listChildren(Resource resource) {
        log.trace("listChildren");

        Iterator<Resource> children = null;
        if (resource != null) {
            log.debug("Listing children at path {}", resource.getPath());
            children = resource.listChildren();
        } else {
            log.warn("Null resource specified");
        }
        return children;
    }

    /**
     * Loads the Class for the name from the current thread's classload.
     *
     * @param className The name of the class to load
     * @return the class
     * @throws ClassNotFoundException a class with the specified name could not be
     *                                found
     */
    private static Class<?> loadClass(String className) throws ClassNotFoundException {
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }
}
