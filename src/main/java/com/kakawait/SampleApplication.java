package com.kakawait;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class SampleApplication extends AsyncConfigurerSupport {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(500);
        executor.initialize();
        return executor;
    }

    @Service
    static class DummyService {

        @Async
        CompletableFuture<String> async() {
            return CompletableFuture.completedFuture("async");
        }

        String sync() {
            return "sync";
        }
    }

    @RestController
    static class DummyController {

        private final DummyService dummyService;

        DummyController(DummyService dummyService) {
            this.dummyService = dummyService;
        }

        @GetMapping("/async")
        CompletableFuture<String> async() {
            return dummyService.async();
        }

        @GetMapping("/sync")
        String sync() {
            return dummyService.sync();
        }
    }

    @ControllerAdvice
    static class StringResponseBodyAdvice implements ResponseBodyAdvice<String> {

        @Override
        public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
            return ClassUtils.isAssignable(String.class, returnType.getParameterType());
        }

        @Override
        public String beforeBodyWrite(String body, MethodParameter returnType, MediaType selectedContentType,
                Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                ServerHttpResponse response) {
            if (request.getPrincipal() != null) {
                response.getHeaders().add("X-Principal", "1");
            }
            response.getHeaders().add("X-Auth", SecurityContextHolder.getContext().getAuthentication().getName());
            return body;
        }
    }
}
