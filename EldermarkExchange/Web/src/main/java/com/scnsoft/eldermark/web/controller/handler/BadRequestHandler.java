package com.scnsoft.eldermark.web.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

//@ControllerAdvice
public class BadRequestHandler {

    public BadRequestHandler() {
        System.out.println("Handler working");
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleMyException(Exception  exception) {
        exception.printStackTrace();
        return null;
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(HttpMessageNotReadableException e) {
        e.printStackTrace();
        throw e;
    }
}
