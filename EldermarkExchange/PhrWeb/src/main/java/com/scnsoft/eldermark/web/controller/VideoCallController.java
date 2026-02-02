package com.scnsoft.eldermark.web.controller;

import java.net.HttpURLConnection;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opentok.exception.OpenTokException;
import com.scnsoft.eldermark.phr.chat.dto.VideoCallLogResponseDto;
import com.scnsoft.eldermark.service.VideoCallService;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.web.entity.TokBoxTokenDto;
import com.scnsoft.eldermark.web.entity.UsersNotificationIdDto;
import com.scnsoft.eldermark.web.entity.VideoCallEventDto;
import com.scnsoft.eldermark.web.entity.VideoCallResponseDto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author phomal Created on 6/2/2017.
 */
@Api(value = "PHR - Video conferences", description = "Video conferences")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class) })
@RestController
@RequestMapping("/phr/{userId:\\d+}/videocall")
public class VideoCallController {
    private final VideoCallService videoCallService;

    @Autowired
    public VideoCallController(VideoCallService videoCallService) {
        this.videoCallService = videoCallService;
    }

    @ApiOperation(value = "Conduct a video conference between two users")
    @PostMapping(value = "/{userId2}")
    public Response<TokBoxTokenDto> createVideoConference(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "another user id", required = true) @PathVariable("userId2") Long userId2) {
        final TokBoxTokenDto dto = videoCallService.createVideoConference(userId, userId2);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Conduct a multi video conference between two or more users", notes = "(Implementation Notes) This request creates a new session in TokBox, one generated token is sent in response to this request and another one is sent via push notification. The other side should be listening for the push notification with token.")
    @PostMapping(value = "/create-connection")
    public Response<VideoCallResponseDto> createMultiVideoConference(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @RequestBody UsersNotificationIdDto calleeId)
            throws OpenTokException, JsonProcessingException, InterruptedException, ExecutionException {
        VideoCallResponseDto videoCallResponseDto;
        videoCallResponseDto = videoCallService.createMultiVideoConference(userId, calleeId);
        return Response.successResponse(videoCallResponseDto);
    }

    @ApiOperation(value = "Call action listener performed by end user", notes = "(Implementation Notes) Listen end user action and send information through push notification to all device connecting with same opentok session")
    @PostMapping(value = "/events")
    public Response<Boolean> videoCallEvent(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @RequestBody VideoCallEventDto videoCallEventDto) {
        return Response.successResponse(videoCallService.videoCallEvent(userId, videoCallEventDto));

    }

    @ApiOperation(value = "Call logs for request user", notes = "(Implementation Notes) get call logs for a specific time")
    @GetMapping(value = "/logs/{receiverId:\\d+}")
    public Response<VideoCallLogResponseDto> videoCallLogs(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "receiver id", required = true) @PathVariable("receiverId") Long receiverId, int page,
            int size) {
        VideoCallLogResponseDto phrVideoCallParticipants = videoCallService.callLog(userId, receiverId, page, size);
        return Response.successResponse(phrVideoCallParticipants);
    }

}
