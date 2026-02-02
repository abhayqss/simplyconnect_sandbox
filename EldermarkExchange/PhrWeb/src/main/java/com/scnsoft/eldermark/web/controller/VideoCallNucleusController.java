package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.entity.phr.AccountType;
import com.scnsoft.eldermark.service.VideoCallNucleusService;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.web.entity.CalleeInfoDto;
import com.scnsoft.eldermark.web.entity.NucleusInfoDto;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.HttpURLConnection;
import java.util.List;

/**
 * @author phomal
 * Created on 6/2/2017.
 */
@Api(value = "Audio & Video calls from / to Nucleus device", description = "Audio & Video calls from / to Nucleus device")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class)
})
@RestController
@RequestMapping("/videocall/nucleus")
public class VideoCallNucleusController {

    final VideoCallNucleusService videoCallNucleusService;

    @Autowired
    public VideoCallNucleusController(VideoCallNucleusService videoCallNucleusService) {
        this.videoCallNucleusService = videoCallNucleusService;
    }

    @ApiOperation(value = "Get Nucleus user IDs", notes = "Get Nucleus user IDs (if known) that can be used to make calls between PHR app and Nucleus device. Please note, that a single user may have multiple Nucleus user IDs, in general, one user ID per health provider. The returned response for the current user-consumer depends on the active health provider(s) selected and must be invalidated when this setting is changed.")
    @GetMapping(value = "/info")
    public Response<List<NucleusInfoDto>> getNucleusInfo(
            @ApiParam(value = "Current account type.", allowableValues = "PROVIDER, CONSUMER, PROVIDER, CONSUMER", defaultValue = "consumer")
            @RequestParam(value = "accountType", required = false, defaultValue="consumer") String type
    ) {
        final AccountType.Type accountType = AccountType.Type.fromValue(type);
        final List<NucleusInfoDto> nucleusInfoDtos = videoCallNucleusService.listNucleusInfo(accountType);
        return Response.successResponse(nucleusInfoDtos);
    }

    @ApiOperation(value = "Get mobile user ID by Nucleus User ID", notes = "Get mobile `userId` by Nucleus user ID so mobile app can show avatar of a callee. It returns `null` body if nothing found.")
    @GetMapping(value = "/incoming")
    public Response<CalleeInfoDto> getNucleusIncoming(
            @ApiParam(value = "Nucleus user ID of a callee (36-char UUID); for example, \"dfa1953f-e401-442f-bbcf-bde33b3ca018\"", required = true)
            @RequestParam(value = "from", required = true) String from,
            @ApiParam(value = "Current account type.", allowableValues = "PROVIDER, CONSUMER, PROVIDER, CONSUMER", defaultValue = "consumer")
            @RequestParam(value = "accountType", required = false, defaultValue="consumer") String type
    ) {
        final AccountType.Type accountType = AccountType.Type.fromValue(type);
        final CalleeInfoDto dto = videoCallNucleusService.getCalleeInfoByNucleusId(from, accountType);
        return Response.successResponse(dto);
    }

}
