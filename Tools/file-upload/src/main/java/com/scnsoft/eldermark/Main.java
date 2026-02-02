package com.scnsoft.eldermark;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

@SpringBootApplication
@RestController
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Value("${auth.user}")
    private String user;

    @Value("${auth.password}")
    private String password;

    private String basicAuth;

    @PostConstruct
    void init() {
        basicAuth = "Basic " + Base64.getEncoder().encodeToString((user + ":" + password).getBytes());
    }

    @PostMapping("/upload-files")
    public void upload(@RequestParam("files") MultipartFile[] files,
                       @RequestHeader("Authorization") String auth,
                       HttpServletResponse response) throws IOException {
        if (!basicAuth.equals(auth)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().println("Unauthorized");
        }

        for (var file : files) {
            FileOutputStream out = new FileOutputStream(file.getOriginalFilename());
            out.write(file.getBytes());
            out.close();
        }
        response.setStatus(HttpStatus.OK.value());
    }
}
