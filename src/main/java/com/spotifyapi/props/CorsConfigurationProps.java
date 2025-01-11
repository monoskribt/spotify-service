package com.spotifyapi.props;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class CorsConfigurationProps {

    @Value(value = "${cors.allowed-origins}")
    private String allowedOrigins;

    @Value(value = "${cors.allowed-methods}")
    private String allowedMethods;

    @Value(value = "${cors.allowed-headers}")
    private String allowedHeaders;

    @Value(value = "${cors.allow-credentials}")
    private boolean allowedCredentials;

    @Value(value = "${cors.max-age}")
    private long magAge;

}
