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
package org.apache.sling.scripting.jsp.taglib.internal;

import org.apache.sling.xss.XSSAPI;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Support for basic XSS protection as provided by the OWASP ESAPI's escape
 * methods.
 * 
 * <p>Note that this class is an OSGi component only to make sure it can get a reference to the
 * {@link XSSAPI} correctly. Its usage is intended to be only within this bundle. For all other usages,
 * directly accessing the {@link XSSAPI} service is strongly recommended.</p>
 */
@Component
public class XSSSupport {
    
    @Reference
    private XSSAPI xssApi;
    
    private static volatile XSSAPI XSS_API;
    
    protected void activate() {
        XSS_API = xssApi;
    }
    
    protected void deactivate() {
        XSS_API = null;
    }

	/**
	 * The encoding modes supported by this tag.
	 */
	public enum EncodingMode {
		/**
		 * Encodes the content as HTML
		 */
		HTML, HTML_ATTR, XML, XML_ATTR, JS
	}
	
	/**
	 * Encodes the unencoded string using the specified mode. This will be
	 * deferred to the corresponding OWASP ESAPI encoding method.
	 *
	 * @param unencoded
	 *            the unencoded string
	 * @param mode
	 *            the mode with which to encode the string
	 * @return the encoded string
	 */
	public static String encode(String unencoded, EncodingMode mode) {

	    if ( XSS_API == null )
	        throw new IllegalStateException("No XSS_API field set. Is the XSSAPI service available?");
	    switch ( mode ) {
        case HTML:
            return XSS_API.encodeForHTML(unencoded);
        case HTML_ATTR:
            return XSS_API.encodeForHTMLAttr(unencoded);
        case XML:
            return XSS_API.encodeForXML(unencoded);
        case XML_ATTR:
            return XSS_API.encodeForHTMLAttr(unencoded);
        case JS:
            return XSS_API.encodeForJSString(unencoded);
            default:
                return unencoded;
        }
	}

    /**
     * Retrieves the encoding mode associated with the specified string. Will throw
     * an IllegalArgumentException if the mode string is not a valid mode and will
     * throw a NullPointerException if the mode string is null.
     *
     * @param modeStr the mode string
     * @return the encoding mode
     */
    public static EncodingMode getEncodingMode(String modeStr) {
        return EncodingMode.valueOf(modeStr.toUpperCase());
    }

    private XSSSupport() {
        // hide the public constructor
    }
}
