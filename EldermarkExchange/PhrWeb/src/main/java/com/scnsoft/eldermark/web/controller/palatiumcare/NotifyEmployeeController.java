package com.scnsoft.eldermark.web.controller.palatiumcare;

import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.palatiumcare.employee.NotifyEmployeeService;
import com.scnsoft.eldermark.shared.palatiumcare.resident.NotifyResidentDto;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.HttpURLConnection;
import java.util.List;

@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@RestController
@RequestMapping("/phr/{userId}/notify-employee")
public class NotifyEmployeeController {

    private NotifyEmployeeService notifyEmployeeService;

    private ResidentDao residentDao;

    @Autowired
    public void setNotifyEmployeeService(NotifyEmployeeService notifyEmployeeService) {
        this.notifyEmployeeService = notifyEmployeeService;
    }

    @Autowired
    public void setResidentDao(ResidentDao residentDao) {
        this.residentDao = residentDao;
    }

    @RequestMapping(value = "/community-resident-list/{employeeId}", method = RequestMethod.GET)
    public Response<List<NotifyResidentDto>> getCommunityResidentList(@PathVariable("userId") Long userId, @PathVariable("employeeId") Long employeeId) {
        return null;
    }

    @RequestMapping(value = "/resident-care-team-member-list/{employeeId}", method = RequestMethod.GET)
    @Transactional
    public Response<List<NotifyResidentDto>> getResidentCareTeamMemberList(@PathVariable("userId") Long userId, @PathVariable("employeeId") Long employeeId) {
        return null;
    }

    @RequestMapping(value = "/all-attached-residents/{employeeId}", method = RequestMethod.GET)
    public Response<List<NotifyResidentDto>> getAllAttachedResidents(@PathVariable("userId") Long userId, @PathVariable("employeeId") Long employeeId) {
        return null;
    }

}

