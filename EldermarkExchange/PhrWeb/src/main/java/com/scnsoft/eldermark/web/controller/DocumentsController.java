package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.service.DocumentService;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.shared.web.entity.ResponseValidationErrorDto;
import com.scnsoft.eldermark.web.entity.DocumentInfoDto;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.concurrent.Callable;

import static com.scnsoft.eldermark.shared.utils.PaginationUtils.buildPageable;
import static com.scnsoft.eldermark.shared.utils.PaginationUtils.lazyTotalCount;

/**
 * @author phomal
 * Created on 6/23/2017.
 */
@Api(value = "PHR - Documents", description = "CCD, Facesheet report, custom documents")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@Validated
@Controller
@RequestMapping("/phr")
public class DocumentsController {

    @Autowired
    DocumentService documentService;

    // ================ [deprecated] since next after 2.34.23 release version of frontend ===============================

    @ApiOperation(value = "Get Document Content", notes = "Download custom Document by its ID. <br/>You should pass appropriate content type in headers.")
    @GetMapping(value = "/{userId:\\d+}/documents/{documentId:\\d+}")
    @Deprecated
    public void getDocumentContent(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "document id", required = true) @PathVariable("documentId") Long documentId, HttpServletResponse response
    ) {
        getDocumentContentForUser(userId, documentId, response);
    }

    @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK",
            responseHeaders = {@ResponseHeader(name = "Content-Disposition", description = "Filename", response = String.class)})
    @ApiOperation(value = "Get CCD Content", notes = "Download Continuity of Care Document (XML)<br/>You should pass \"Accept: " + MediaType.TEXT_XML_VALUE + "\" in headers.")
    @GetMapping(value = "/{userId:\\d+}/documents/ccd")
    @Deprecated
    public void getCcdContent(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            HttpServletResponse response
    ) {
        getCcdContentForUser(userId, response);
    }

    @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK",
            responseHeaders = {@ResponseHeader(name = "Content-Disposition", description = "Filename", response = String.class)})
    @ApiOperation(value = "Get Facesheet Report Content", notes = "Download Facesheet report (PDF)<br/>You should pass \"Accept: " + MediaType.APPLICATION_PDF_VALUE + "\" in headers.")
    @GetMapping(value = "/{userId:\\d+}/documents/facesheet")
    @Deprecated
    public void getFacesheetContent(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            HttpServletResponse response
    ) {
        getFacesheetContentForUser(userId, response);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK",
                    responseHeaders = {@ResponseHeader(name = "Content-Disposition", description = "Filename", response = String.class)})
    })
    @ApiOperation(value = "Get Documents", notes = "Get a list of Documents for the specified User.<br/>Patients (Receivers) can see all their documents, Care Team Members / Physicians (Providers) can see only documents shared with their organization.<br/>Usually there're at least two documents available for each patient - CCD.xml and FACESHEET.pdf. NOTE! Attributes `createdOn`, `sizeKb`, and `id` are always null for CCD.xml and FACESHEET.pdf.")
    @GetMapping(value = "/{userId:\\d+}/documents")
    @Deprecated
    public @ResponseBody
    Response<List<DocumentInfoDto>> getDocuments(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") final Long userId,
            @Min(2)
            @ApiParam(value = "Maximum results to appear in event notifications list (if not specified, system will return all documents), ≥ 2")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page. The first page is 0, the second page is 1, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        return getDocumentsForUser(userId, pageSize, page);
    }

    // =============================================== [deprecated] ========================================================

    // ====================================== new endpoints =============================================================

    @ApiOperation(value = "Get Document Content", notes = "Download custom Care Receiver's Document by its ID.<br/>You should pass appropriate content type in headers.<br/>When fetched successfully, this endpoint returns the document content with its filename in headers. Error responses are returned in JSON.")
    @GetMapping(value = "/care-receivers/{receiverId}/documents/{documentId}")
    public void getDocumentContentForReceiver(
            @ApiParam(value = "Care-receiver id", required = true) @PathVariable("receiverId") Long receiverId,
            @ApiParam(value = "document id", required = true) @PathVariable("documentId") Long documentId,
            HttpServletResponse response
    ) {
        documentService.downloadCustomDocumentForReceiver(receiverId, documentId, response);
    }

    @ApiOperation(value = "Get Cda document in view mode.", nickname = "getCdaDocumentViewForUserUsingGET", notes = "Get Cda document by its ID transformed into html via xsl transformation.", authorizations = {
            @Authorization(value = "X-Auth-Token")
    }, tags = {"documents-controller",})
    @GetMapping(value = "/users/{userId}/documents/{documentId}/cda-view")
    @ResponseBody
    public Response<String> getCdaDocumentViewForUserUsingGET(@ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId, @ApiParam(value = "document id", required = true) @PathVariable("documentId") Long documentId) {
        return Response.successResponse(documentService.getCdaHtmlViewForDocumentForUser(userId, documentId));
    }

    @ApiOperation(value = "Get Cda document in view mode.", notes = "Get Cda document by its ID transformed into html via xsl transformation.", tags = {"documents-controller"})
    @GetMapping(value = "/care-receivers/{receiverId}/documents/{documentId}/cda-view")
    @ResponseBody
    public Response<String> getCdaDocumentViewForReceiverUsingGET(
            @ApiParam(value = "Care receiver id", required = true) @PathVariable("receiverId") Long receiverId,
            @ApiParam(value = "document id", required = true) @PathVariable("documentId") Long documentId
    ) {
        return Response.successResponse(documentService.getCdaHtmlViewForDocumentForReceiver(receiverId, documentId));
    }

    @ApiOperation(value = "Get Document Content", notes = "Download custom User's Document by its ID.<br/>You should pass appropriate content type in headers.<br/>When fetched successfully, this endpoint returns the document content with its filename in headers. Error responses are returned in JSON.")
    @RequestMapping(value = "/users/{userId}/documents/{documentId}", method = RequestMethod.GET)
    public void getDocumentContentForUser(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "document id", required = true) @PathVariable("documentId") Long documentId,
            HttpServletResponse response
    ) {
        documentService.downloadCustomDocument(userId, documentId, response);
    }

    @ApiOperation(value = "Get CCD Content", notes = "Download Continuity of Care Document (XML) of Care receiver.<br/>You should pass `Accept `&#58;` text/xml` in headers.<br/>When fetched successfully, this endpoint returns the document content with its filename in headers. Error responses are returned in JSON.", tags = {"not-implemented"})
    @RequestMapping(value = "/care-receivers/{receiverId}/documents/ccd", method = RequestMethod.GET)
    public void getCcdContentForReceiver(
            @ApiParam(value = "Care receiver id", required = true) @PathVariable("receiverId") Long receiverId,
            HttpServletResponse response
    ) {
        documentService.downloadContinuityOfCareDocumentForReceiver(receiverId, response);
    }

    @ApiOperation(value = "Get CCD Content", notes = "Download Continuity of Care Document (XML) of User.<br/>You should pass `Accept `&#58;` text/xml` in headers.<br/>When fetched successfully, this endpoint returns the document content with its filename in headers. Error responses are returned in JSON.", tags = {"not-implemented"})
    @RequestMapping(value = "/users/{userId}/documents/ccd", method = RequestMethod.GET)
    public void getCcdContentForUser(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId, HttpServletResponse response
    ) {
        documentService.downloadContinuityOfCareDocument(userId, response);
    }

    @ApiOperation(value = "Get Facesheet Report Content", notes = "Download Facesheet report (PDF) of Care receiver<br/>You should pass `Accept `&#58;` application/pdf` in headers.<br/>When fetched successfully, this endpoint returns the document content with its filename in headers. Error responses are returned in JSON.", tags = {"not-implemented"})
    @RequestMapping(value = "/care-receivers/{receiverId}/documents/facesheet", method = RequestMethod.GET)
    public void getFacesheetContentForReceiver(
            @ApiParam(value = "Care receiverId id", required = true) @PathVariable("receiverId") Long receiverId,
            HttpServletResponse response
    ) {
        documentService.downloadFacesheetReportForReceiver(receiverId, response);
    }

    @ApiOperation(value = "Get Facesheet Report Content", notes = "Download Facesheet report (PDF) of User<br/>You should pass `Accept `&#58;` application/pdf` in headers.<br/>When fetched successfully, this endpoint returns the document content with its filename in headers. Error responses are returned in JSON.", tags = {"not-implemented"})
    @RequestMapping(value = "/users/{userId}/documents/facesheet", method = RequestMethod.GET)
    public void getFacesheetContentForUser(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId, HttpServletResponse response
    ) {
        documentService.downloadFacesheetReport(userId, response);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK",
                    responseHeaders = {@ResponseHeader(name = "Content-Disposition", description = "Filename", response = String.class)})
    })
    @ApiOperation(value = "Get Documents", notes = "Get a list of Documents for the specified Care receiver.<br/>Patients (Receivers) can see all their documents.<br/>Usually there're at least two documents available for each patient - CCD.xml and FACESHEET.pdf. NOTE! Attributes `createdOn`, `sizeKb`, and `id` are null for CCD.xml and FACESHEET.pdf.", tags = {"not-implemented"})
    @RequestMapping(value = "/care-receivers/{receiverId}/documents", method = RequestMethod.GET)
    public @ResponseBody
    Response<List<DocumentInfoDto>> getDocumentsForReceiver(
            @ApiParam(value = "Care receiver id", required = true) @PathVariable("receiverId") final Long receiverId,
            @Min(2) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all documents), ≥ 2") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        final List<DocumentInfoDto> dto = documentService.getDocumentsForReceiver(receiverId, pageable);
        final Long totalCount = lazyTotalCount(dto.size(), page, pageSize, new Callable<Long>() {
            @Override
            public Long call() {
                return documentService.countDocumentsForReceiver(receiverId);
            }
        });
        return Response.pagedResponse(dto, totalCount);
    }

    @ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK",
                responseHeaders = {@ResponseHeader(name = "Content-Disposition", description = "Count", response = Long.class)})
	})
	@ApiOperation(value = "Get Document Count", notes = "Get count of Documents for the specified Care receiver.<br/>Patients (Receivers) can see all their documents.<br/>Usually there're at least two documents available for each patient - CCD.xml and FACESHEET.pdf. NOTE! Attributes `createdOn`, `sizeKb`, and `id` are null for CCD.xml and FACESHEET.pdf.")
	@RequestMapping(value = "/care-receivers/{receiverId}/documents/count", method = RequestMethod.GET)
	public @ResponseBody
	Response<Long> getReceiverDocumentCount(
	        @ApiParam(value = "Care receiver id", required = true) @PathVariable("receiverId") final Long receiverId) {
	    final Long totalCount =  documentService.countDocumentsForReceiver(receiverId);
	    return Response.successResponse(totalCount);
	}
    
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK",
                    responseHeaders = {@ResponseHeader(name = "Content-Disposition", description = "Filename", response = String.class)})
    })
    @ApiOperation(value = "Get Documents", notes = "Get a list of Documents for the specified User.<br/>Patients (Receivers) can see all their documents, Care Team Members / Physicians (Providers) can see only documents shared with their organization.<br/>Usually there're at least two documents available for each patient - CCD.xml and FACESHEET.pdf. NOTE! Attributes `createdOn`, `sizeKb`, and `id` are null for CCD.xml and FACESHEET.pdf.", tags = {"not-implemented"})
    @RequestMapping(value = "/users/{userId}/documents", method = RequestMethod.GET)
    public @ResponseBody
    Response<List<DocumentInfoDto>> getDocumentsForUser(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") final Long userId,
            @Min(2) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all documents), ≥ 2") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        final List<DocumentInfoDto> dto = documentService.getDocumentsForUser(userId, pageable);
        final Long totalCount = lazyTotalCount(dto.size(), page, pageSize, new Callable<Long>() {
            @Override
            public Long call() {
                return documentService.countDocumentsForUser(userId);
            }
        });
        return Response.pagedResponse(dto, totalCount);
    }
    
	@ApiResponses({
			@ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
			@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK", responseHeaders = {
					@ResponseHeader(name = "Content-Disposition", description = "Count", response = Long.class) }) })
	@ApiOperation(value = "Get Document Count", notes = "Get a count of Documents for the specified User.<br/>Patients (Receivers) can see all their documents, Care Team Members / Physicians (Providers) can see only documents shared with their organization.<br/>Usually there're at least two documents available for each patient - CCD.xml and FACESHEET.pdf. NOTE! Attributes `createdOn`, `sizeKb`, and `id` are null for CCD.xml and FACESHEET.pdf.")
	@RequestMapping(value = "/users/{userId}/documents/count", method = RequestMethod.GET)
	public @ResponseBody Response<Long> getUserDocumentCount(
			@ApiParam(value = "user id", required = true) @PathVariable("userId") final Long userId) {
		Long totalCount = documentService.countDocumentsForUser(userId);
		return Response.successResponse(totalCount);
	}
}
