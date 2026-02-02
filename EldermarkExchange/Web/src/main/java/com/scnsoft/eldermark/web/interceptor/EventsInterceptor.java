package com.scnsoft.eldermark.web.interceptor;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;

/**
 * Created by pzhurba on 21-Sep-15.
 */

public class EventsInterceptor extends HandlerInterceptorAdapter {
    Resource xsdFile;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpServletRequestWrapper requestWrapper = new BufferedHttpServletRequestWrapper(request);

        try {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(xsdFile.getInputStream()));
            Validator validator = schema.newValidator();

            validator.validate(new StreamSource(requestWrapper.getInputStream()));


            return super.preHandle(requestWrapper, response, handler);
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());

            return false;
        }

    }

    public void setXsdFile(Resource xsdFile) {
        this.xsdFile = xsdFile;
    }


    public class BufferedHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private byte[] body;

        public BufferedHttpServletRequestWrapper(HttpServletRequest httpServletRequest) throws IOException{
            super(httpServletRequest);
            body = IOUtils.toByteArray(httpServletRequest.getInputStream());
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new ServletInputStreamImpl(new ByteArrayInputStream(body));
        }

        @Override
        public BufferedReader getReader() throws IOException {
            String enc = getCharacterEncoding();
            if (enc == null) enc = "UTF-8";
            return new BufferedReader(new InputStreamReader(getInputStream(), enc));
        }

        private class ServletInputStreamImpl extends ServletInputStream {

            private InputStream is;

            public ServletInputStreamImpl(InputStream is) {
                this.is = is;
            }

            public int read() throws IOException {
                return is.read();
            }

            public boolean markSupported() {
                return false;
            }

            public synchronized void mark(int i) {
                throw new RuntimeException(new IOException("mark/reset not supported"));
            }

            public synchronized void reset() throws IOException {
                throw new IOException("mark/reset not supported");
            }
        }
    }
}
