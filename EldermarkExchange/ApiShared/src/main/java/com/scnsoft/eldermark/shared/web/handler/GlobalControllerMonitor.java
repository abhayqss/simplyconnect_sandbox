package com.scnsoft.eldermark.shared.web.handler;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * @author phomal
 * Created on 3/25/17.
 */
@Aspect
@Component
public class GlobalControllerMonitor {

    Logger logger = Logger.getLogger(GlobalControllerMonitor.class.getName());

    @Before("execution(* com.scnsoft.eldermark.web.controller..*Controller.*(..))")
    public void logControllerAccess(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        final Object[] args = joinPoint.getArgs();
        logger.info("Invocation of " + className + "#" + methodName + "()");
        logger.info("Arguments: " + toString(args));
    }

    // modified Arrays#toString()
    private static String toString(Object[] a) {
        if (a == null)
            return "null";

        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; ++i) {
            String str = String.valueOf(a[i]);
            if (str.length() == 9) {
                // possible ssn -> mask the value
                str = "****-**-" + StringUtils.right(str, 4);
            } else if (str.length() > 73) {
                // possible token or Base64 image -> mask the value
                str = StringUtils.abbreviate(str, 73);
            } else if (str.contains("@")) {
                // possible email -> mask the value
                str = str.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
            } else if (str.startsWith("+") && str.length() > 5) {
                // possible phone -> mask the value
                str = StringUtils.rightPad(StringUtils.left(str, 4), str.length() - 1, '*') + StringUtils.right(str, 1);
            }
            b.append(str);
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

}
