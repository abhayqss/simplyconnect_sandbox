package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.pointclickcare.PointClickCareWebhookDto;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccPostWebhookSubscriptionResponse;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccPublicGetWebhookSubscriptionList;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccWebhookSubscriptionStatus;
import com.scnsoft.eldermark.facade.PointClickCareWebhookFacade;
import com.scnsoft.eldermark.service.pointclickcare.PointClickCareSyncService;
import com.scnsoft.eldermark.service.pointclickcare.PointClickCareWebhookService;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/pcc")
@ConditionalOnProperty(value = "pcc.integration.enabled", havingValue = "true")
public class PointClickCareController {

    @Value("${pcc.webhook.auth.user}")
    private String authUser;

    @Value("${pcc.webhook.auth.password}")
    private String authPassword;

    @Autowired
    private PointClickCareWebhookFacade pointClickCareWebhookFacade;

    @Autowired
    private PointClickCareSyncService pointClickCareSyncService;

    @Autowired
    private PointClickCareWebhookService pointClickCareWebhookService;

    @PostMapping(value = "/webhooks", produces = MediaType.APPLICATION_JSON_VALUE)
    public void acceptWebhook(@RequestBody PointClickCareWebhookDto dto, HttpServletRequest request) {
        validateWebhookAuth(request);
        pointClickCareWebhookFacade.acceptWebhook(dto);
    }

    private void validateWebhookAuth(HttpServletRequest request) {
        var authValue = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authValue)) {
            throw new AccessDeniedException("No access");
        }

        if (!authValue.startsWith("Basic ")) {
            throw new AccessDeniedException("No access");
        }

        var basicToken = authValue.substring("Basic ".length());
        if (basicToken.isEmpty()) {
            throw new AccessDeniedException("No access");
        }

        var decodedToken = new String(Base64Utils.decodeFromString(basicToken));
        var split = decodedToken.split(":");

        if (split.length != 2) {
            throw new AccessDeniedException("No access");
        }

        var user = split[0];
        var password = split[1];

        if (!authUser.equals(user) || !authPassword.equals(password)) {
            throw new AccessDeniedException("No access");
        }
    }

    @PostMapping("/sync/communities/{communityId}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMINISTRATOR')")
    public Response<Boolean> communityInitialSync(@PathVariable("communityId") Long communityId) {
        pointClickCareSyncService.syncCommunity(communityId);
        return Response.successResponse();
    }

    @GetMapping("/webhook-subscriptions")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMINISTRATOR')")
    public PccPublicGetWebhookSubscriptionList getWebhookSubscriptions(
            @RequestParam("status") PccWebhookSubscriptionStatus status,
            @RequestParam("page") int page,
            @RequestParam("pageSize") int pageSize) {
        return pointClickCareWebhookService.getSubscriptions(status, page, pageSize);
    }

    @PutMapping("/webhook-subscriptions")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMINISTRATOR')")
    public PccPostWebhookSubscriptionResponse subscribe(@RequestParam("groups") List<String> groups) {
        return pointClickCareWebhookService.subscribe(groups);
    }
}
