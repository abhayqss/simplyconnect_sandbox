package com.scnsoft.eldermark.web.commons.filter;

import com.scnsoft.eldermark.beans.security.UserPrincipal;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.web.commons.dto.basic.ApiRequestLoggingNotificationDto;
import com.scnsoft.eldermark.web.commons.service.ApiRequestLoggingNotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

public class ApiRequestLoggingFilter extends OncePerRequestFilter {

    private final ApiRequestLoggingNotificationService notificationService;
    private final LoggedUserService loggedUserService;

    public ApiRequestLoggingFilter(
            ApiRequestLoggingNotificationService notificationService,
            LoggedUserService loggedUserService
    ) {
        this.notificationService = notificationService;
        this.loggedUserService = loggedUserService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        var startTime = Instant.now().toEpochMilli();
        var logRequestMessage = "\nEnvironment: " + request.getServerName() +
                "\nRequest: " + request.getMethod() + " " + request.getRequestURI();

        filterChain.doFilter(request, response);

        try {
            var currentEmployeeId = loggedUserService.getCurrentUser()
                    .map(UserPrincipal::getEmployeeId)
                    .orElse(null);

            int status = response.getStatus();
            var executionTime = (Instant.now().toEpochMilli() - startTime) / 1000.F;
            logRequestMessage += "\nEmployee id: " + currentEmployeeId +
                    "\nRequest query params: " + request.getQueryString();

            var logResponseMessage = "\nResponse status: " + status + " " + HttpStatus.valueOf(status).getReasonPhrase() + "\n" +
                    "Execution time: " + String.format("%.3f", executionTime) + " s" + "\n";
            logger.info(logRequestMessage + logResponseMessage);

            prepareNotificationToSend(request, currentEmployeeId, startTime, executionTime);
        } catch (Exception e) {
            logger.error("Logging API request failed", e);
        }
    }

    private void prepareNotificationToSend(
            HttpServletRequest request,
            Long currentEmployeeId,
            long startTime,
            float executionTime
    ) {
        try {
            var dto = new ApiRequestLoggingNotificationDto();
            dto.setEnvironment(request.getServerName());
            dto.setMethod(request.getMethod());
            dto.setEndpoint(request.getRequestURI());
            dto.setStartTime(Instant.ofEpochMilli(startTime));
            dto.setExecutionTime(executionTime);
            dto.setEmployeeId(currentEmployeeId);
            dto.setQueryString(request.getQueryString());
            notificationService.sendEmailNotifications(dto);
        } catch (Exception e) {
            logger.error("Sending API request log notification failed", e);
        }
    }
}
