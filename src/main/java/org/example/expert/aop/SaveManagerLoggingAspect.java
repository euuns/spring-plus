package org.example.expert.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.expert.domain.log.service.LogService;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SaveManagerLoggingAspect {

    private final LogService logService;
    private final HttpServletRequest request;

    private String logging;
    private String result;

    @Around("execution(* org.example.expert.domain.manager.controller.ManagerController.saveManager(..))")
    public void logSaveManager(ProceedingJoinPoint joinPoint) throws Throwable {

        String method = joinPoint.getSignature().getName();
        String email = String.valueOf(request.getAttribute("email"));

        String[] uris = request.getRequestURI().split("/");
        String todoId = uris[uris.length - 2];

        log.info("Method: {}", method);
        log.info("User Email: {}", email);
        log.info("Todo Id: {}", todoId);

        logging = "Method: " + method + ", TodoId: " + todoId + ", UserEmail: " + email;

        try{
            Object proceed = joinPoint.proceed();
            result = "Success: " + proceed;

        } catch (Throwable e) {
            result = "Error: " + e.getMessage();
            throw e;

        } finally {
            log.info("Result: {}", result);
            logService.save(logging, result);
        }
    }
}
