package edu.sjsu.cmpe272.simpleblog.server.aspect;


import edu.sjsu.cmpe272.simpleblog.server.annotation.signature.MessageSignatureHolder;
import edu.sjsu.cmpe272.simpleblog.server.dto.MessageDTO;
import edu.sjsu.cmpe272.simpleblog.server.exception.NoUserFoundException;
import edu.sjsu.cmpe272.simpleblog.server.exception.SignatureMismatchException;
import edu.sjsu.cmpe272.simpleblog.server.model.UserDetails;
import edu.sjsu.cmpe272.simpleblog.server.service.SignatureValidationService;
import edu.sjsu.cmpe272.simpleblog.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class SignatureValidationAspect {
    private final SignatureValidationService validationService;
    private final UserService userService;

    @Before("@annotation(edu.sjsu.cmpe272.simpleblog.server.annotation.signature.ValidateMessageSignature)")
    @SneakyThrows
    public void before(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String[] parameterNames = methodSignature.getParameterNames();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < parameterTypes.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            if (hasMessageSignatureHolderAnnotation(annotations)) {
                Object parameterValue = joinPoint.getArgs()[i];
                log.debug("Found parameter annotated with @MessageSignatureHolder: {} - {}", parameterNames[i], parameterValue);
                if (parameterValue == null) {
                    throw new SignatureMismatchException();
                }
                MessageDTO.CreateRequest request = (MessageDTO.CreateRequest) parameterValue;
                UserDetails userDetails = userService.getUserByUsername(request.getAuthor()).orElseThrow(() -> new NoUserFoundException(request.getAuthor()));
                validationService.validateSignature(request, userDetails);
            }
        }
    }

    private boolean hasMessageSignatureHolderAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == MessageSignatureHolder.class) {
                return true;
            }
        }
        return false;
    }
}
