package com.scnsoft.eldermark.web.filter;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author stsiushkevich
 */

public class CompressHtmlResponseFilter implements Filter {

    private HtmlCompressor compressor;

    private static final String CONTENT_COMPRESSED_HEADER = "X-Content-Compressing";

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
                         FilterChain chain) throws IOException, ServletException {


        String header = ((HttpServletRequest) req).getHeader(CONTENT_COMPRESSED_HEADER);

        if (header != null && header.equals("enabled")) {
            CharResponseWrapper responseWrapper = new CharResponseWrapper(
                    (HttpServletResponse) resp);

            chain.doFilter(req, responseWrapper);

            String servletResponse = responseWrapper.toString();
            resp.getWriter().write(compressor.compress(servletResponse));
        } else  {
            chain.doFilter(req, resp);
        }
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        compressor = new HtmlCompressor();
    }

    @Override
    public void destroy() {
    }

    class CharResponseWrapper extends HttpServletResponseWrapper {

        private final CharArrayWriter output;

        @Override
        public String toString() {
            return output.toString();
        }

        CharResponseWrapper(HttpServletResponse response) {
            super(response);
            output = new CharArrayWriter();
        }

        @Override
        public PrintWriter getWriter() {
            return new PrintWriter(output);
        }
    }
}
