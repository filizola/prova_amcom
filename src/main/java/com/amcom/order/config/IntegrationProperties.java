package com.amcom.order.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class IntegrationProperties {

    @Value("${order.integration.system-a.api-key}")
    private String systemAApiKey;

    @Value("${order.integration.system-b.api-key}")
    private String systemBApiKey;
}
