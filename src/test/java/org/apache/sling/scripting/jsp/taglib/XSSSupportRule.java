package org.apache.sling.scripting.jsp.taglib;

import java.lang.reflect.Field;

import org.apache.sling.scripting.jsp.taglib.helpers.XSSSupport;
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
        if (!accessible) 
            xssApiField.setAccessible(true);
        xssApiField.set(null, val);
        if (!accessible)
            xssApiField.setAccessible(false);
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
