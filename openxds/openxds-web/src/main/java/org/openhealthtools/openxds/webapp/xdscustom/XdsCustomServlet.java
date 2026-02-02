package org.openhealthtools.openxds.webapp.xdscustom;

import org.openhealthtools.openxds.XdsFactory;
import org.openhealthtools.openxds.registry.DocumentBriefData;
import org.openhealthtools.openxds.registry.adapter.omar31.XdsRegistryCustomServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handle a couple of additional requests that are required by Exchange, but not implemented by OpenXDS.<br/>
 * These 2 requests are: {@code UpdateDocumentEntryTitle} and {@code GetDocumentData}.
 * <br/><br/>
 * The first request is needed by Exchange, when it gets request from Cloud Scanning application. When cloud scanning application changes
 * title of a document, that is connected to Exchange, Exchange sets same request to update this information in XdsRegistry.
 * <br/><br/>
 * The second one is used to get information about current state of document metadata in XdsRegistry, compare it to Exchange document state,
 * and then synchronize (for example, change state of the document in XdsRegistry from Approved to Deprecated, in case the document is deleted
 * from Exchange).
 *
 * @author averazub
 * Created on 8/29/2016.
 */
public class XdsCustomServlet extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(XdsCustomServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        String path = req.getRequestURI().substring(req.getContextPath().length());
        if (path.endsWith("/documentData")) {
            getDocumentData(req, resp);
        } else if (path.endsWith("/updateDocEntryTitle")) {
            updateDocEntryTitle(req, resp);
        } else {
            writeNotFound(resp);
        }
    }

    private void getDocumentData(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uuid = req.getParameter("uuid");
        try {
            if (uuid == null) throw new RuntimeException("uuid and docTitle parameters are required");
            XdsRegistryCustomServiceImpl service = (XdsRegistryCustomServiceImpl) XdsFactory.getInstance().getBean("xdsRegistryCustomService");
            DocumentBriefData data = service.getDocumentData(uuid);
            String json = "{" +
                    "\"uuid\":\"" + data.getUuid() + "\"," +
                    "\"exists\":" + data.getExists() + "," +
                    "\"approved\":" + data.getApproved() + "" +
                    "}";
            resp.setStatus(200);
            resp.setContentType("application/json");
            resp.getWriter().write(json);
            resp.getWriter().close();
        } catch (RuntimeException e) {
            resp.setStatus(500);
            resp.getWriter().write("Failure: " + e.getLocalizedMessage());
            resp.getWriter().close();
            logger.error("Error during 'documentData'.", e);

        }
    }

    private void updateDocEntryTitle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uuid = req.getParameter("uuid");
        String docTitle = req.getParameter("docTitle");
        try {
            if ((uuid == null) || (docTitle == null))
                throw new RuntimeException("uuid and docTitle parameters are required");
            XdsRegistryCustomServiceImpl service = (XdsRegistryCustomServiceImpl) XdsFactory.getInstance().getBean("xdsRegistryCustomService");
            service.updateDocumentEntryTitle(uuid, docTitle);
            resp.setStatus(200);
            resp.getWriter().write("Success");
            resp.getWriter().close();
        } catch (RuntimeException e) {
            resp.setStatus(500);
            resp.getWriter().write("Failure: " + e.getLocalizedMessage());
            resp.getWriter().close();
            logger.error("Error during 'updateDocEntryTitle'.", e);
        }
    }

    private void writeNotFound(HttpServletResponse resp) throws IOException {
        resp.setStatus(404);
        resp.getWriter().write("Not Found");
        resp.getWriter().close();
    }
}
