package com.github.swim_developer.validator.ffice.provider.domain.port.in;

import java.util.Map;

public interface ConformanceTestPort {
    Map<String, Object> executeTest(String scenarioId, String providerUrl, String bearerToken);
}
