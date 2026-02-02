package com.scnsoft.eldermark.web.controller.palatiumcare;

import com.scnsoft.eldermark.shared.palatiumcare.AlertDto;
import com.scnsoft.eldermark.entity.palatiumcare.Alert;
import com.scnsoft.eldermark.entity.palatiumcare.AlertStatus;
import com.scnsoft.eldermark.service.palatiumcare.AlertService;
import com.scnsoft.eldermark.service.palatiumcare.IncorrectAlertFlowException;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@RestController
@RequestMapping("/phr/{userId:\\d+}/alert")
public class AlertController {

    private AlertService alertService;

    @Autowired
    public void setAlertService(AlertService alertService) {
        this.alertService = alertService;
    }


    @ApiOperation(value = "List of alerts", notes = "Returns all the alerts")
    @RequestMapping(method = RequestMethod.GET, value = "/list")
    public Response<List<AlertDto>> getAlertList(
            @PathVariable("userId") Long userId,
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all care receivers), ≥ 1", defaultValue = "10")
            @Min(1)
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {
        Page<AlertDto> alertDtoList = alertService.getAlertList(page, pageSize);
        System.out.println(alertDtoList.getContent());
        return Response.pagedResponse(alertDtoList);
    }


    @ApiOperation(value = "Save alert", notes = "This method is for debugging push notifications for alerts")
    @RequestMapping(method = RequestMethod.POST)
    public Response saveAlert(@RequestBody  Alert alert, @PathVariable("userId") Long userId) throws IOException {
        alertService.saveAlert(alert);
        return Response.successResponse();
    }

    @ApiOperation(value = "Get alert by alert id", notes = "Returns alert object")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public Response getAlertById(
            @ApiParam(value = "alert id", required = true)
            @PathVariable("id") Long id,
            @PathVariable("userId") Long userId) {
        return Response.successResponse(alertService.getAlertById(id));
    }

    @ApiOperation(value = "Changes status of alert",
            notes = "Possible statuses are NOT_TAKEN_YET, TAKEN, COMPLETED, CLOSED")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}")
    public Response changeAlertStatus(
            @ApiParam(value = "alert id", required = true)
            @PathVariable("id") Long id,
            @ApiParam(value = "status", required = true)
            @RequestBody Map<String, String> requestBody,
            @PathVariable("userId") Long userId) throws IncorrectAlertFlowException {
        System.out.println("alertId: " + id);
        AlertStatus status = AlertStatus.valueOf(requestBody.get("status"));
        System.out.println("status: " + status);
        alertService.changeAlertStatus(id, status);
        return Response.successResponse();
    }
}
