package com.github.swim_developer.validator.ffice.provider.domain.port.in;

import com.github.swim_developer.validator.ffice.provider.domain.model.HttpResult;

public interface ConformanceHttpPort {
    HttpResult get(String baseUrl, String path, String bearerToken);
    HttpResult post(String baseUrl, String path, String bearerToken, String body);
    HttpResult delete(String baseUrl, String path, String bearerToken);
}
