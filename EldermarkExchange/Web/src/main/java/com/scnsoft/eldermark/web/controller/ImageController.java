package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.shared.exceptions.RestResourceNotFoundException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by averazub on 3/30/2016.
 */
//@Controller
public class ImageController {

    @Value("${image.upload.basedir}")
    String pictureUploadBasedir;

    @RequestMapping(value="resources/images/internal/**")
    public void getImage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String filePart = request.getRequestURL().substring(request.getRequestURL().indexOf("resources/images/internal")+26);
        File f = new File(pictureUploadBasedir+"/"+filePart);
        if (!f.exists()) throw new RestResourceNotFoundException("File not found");
        response.setContentType("image/"+ FilenameUtils.getExtension(f.getName()));
        FileInputStream fis = new FileInputStream(f);
        ServletOutputStream os = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int len = fis.read(buffer);
        while (len != -1) {
            os.write(buffer, 0, len);
            len = fis.read(buffer);
        }
        fis.close();
        os.close();
    }

}
