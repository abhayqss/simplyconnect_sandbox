package com.scnsoft.eldermark.web.interceptor.hashkey;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface InvalidHashKeyStrategy {
    void onInvalidHashKeyDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
}
