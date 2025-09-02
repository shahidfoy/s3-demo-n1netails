package com.shahidfoy.s3_demo.config;

import com.n1netails.n1netails.kuda.internal.TailConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class KudaConfig {

    @Value("${n1netails.kuda.config.api}")
    private String api;

    @Value("${n1netails.kuda.config.path}")
    private String path;

    @Value("${n1netails.kuda.config.token}")
    private String token;

    @Bean
    public String tailConfig() {
        log.info("=== SETTING UP KUDA TAIL CONFIG");
        TailConfig.setApiUrl(api);
        TailConfig.setApiPath(path);
        TailConfig.setN1neToken(token);
        TailConfig.enableExceptionHandler();
        return "Kuda Configured";
    }
}
