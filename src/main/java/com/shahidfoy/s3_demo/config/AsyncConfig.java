package com.shahidfoy.s3_demo.config;

import com.n1netails.n1netails.kuda.service.ExceptionReporter;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

//    private final ExceptionReporter reporter;
//
//    public AsyncConfig(ExceptionReporter reporter) {
//        this.reporter = reporter;
//    }

    @Override
    public Executor getAsyncExecutor() {
        return Executors.newCachedThreadPool();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            // Forward to your reporter
            ExceptionReporter reporter = new ExceptionReporter();
            reporter.reportException(throwable, method.getName());
            throwable.printStackTrace();
        };
    }
}
