package com.polarbookshop.orderservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;
import java.net.URI;

@Getter
@Setter
@ConfigurationProperties(prefix = "polar")
public class ClientProperties {

    @NotNull
    private URI catalogServiceUri;
}
