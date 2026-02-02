package com.scnsoft.eldermark.web.resolvers;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.services.direct.MailAccountDetailsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.annotation.Annotation;


public final class MessagingAccountArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private MailAccountDetailsFactory accountDetailsFactory;

    public boolean supportsParameter(MethodParameter parameter) {
        return findMethodAnnotation(MessagingAccount.class, parameter) != null;
    }

    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();

        if(principal != null && !ExchangeUserDetails.class.isAssignableFrom(principal.getClass())) {
            return null;
        }

        ExchangeUserDetails userDetails = (ExchangeUserDetails) principal;
        return accountDetailsFactory.createMailAccountDetails(userDetails);
    }

    private <T extends Annotation> T findMethodAnnotation(Class<T> annotationClass, MethodParameter parameter) {
        T annotation = parameter.getParameterAnnotation(annotationClass);
        if(annotation != null) {
            return annotation;
        }
        Annotation[] annotationsToSearch = parameter.getParameterAnnotations();
        for(Annotation toSearch : annotationsToSearch) {
            annotation = AnnotationUtils.findAnnotation(toSearch.annotationType(), annotationClass);
            if(annotation != null) {
                return annotation;
            }
        }
        return null;
    }
}