package com.github.swim_developer.validator.ffice.provider.application.usecase;

import com.github.swim_developer.validator.ffice.provider.domain.model.HttpResult;
import com.github.swim_developer.validator.ffice.provider.domain.port.in.ConformanceHttpPort;
import com.github.swim_developer.validator.ffice.provider.domain.port.in.ConformanceTestPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Map;

@ApplicationScoped
public class ConformanceTestService implements ConformanceTestPort {

    private static final String BASE_PATH = "/swim/v1";
    private static final String SUBSCRIBE_BODY =
        "{\"message_type\":[\"FILED_FLIGHT_PLAN\",\"FLIGHT_DEPARTURE\",\"FLIGHT_ARRIVAL\"]}";

    @Inject
    ConformanceHttpPort httpClient;

    @Override
    public Map<String, Object> executeTest(String scenarioId, String providerUrl, String bearerToken) {
        return switch (scenarioId) {
            case "API-01" -> testSubscribeHappyPath(providerUrl, bearerToken);
            case "API-02" -> testListSubscriptions(providerUrl, bearerToken);
            case "API-03" -> testGetTopics(providerUrl, bearerToken);
            case "API-04" -> testUnsubscribe(providerUrl, bearerToken);
            case "DM-01" -> testResponseRequiredFields(providerUrl, bearerToken);
            case "DM-02" -> testInitialPausedStatus(providerUrl, bearerToken);
            case "DM-03" -> testTopicReturnsFficeService(providerUrl, bearerToken);
            case "DM-04" -> testMessageTypeFilterPersisted(providerUrl, bearerToken);
            case "WFS-01" -> testWfsGetFeature(providerUrl, bearerToken);
            default -> Map.of("error", "Unknown scenario: " + scenarioId);
        };
    }

    private Map<String, Object> testSubscribeHappyPath(String url, String token) {
        ConformanceAssertions a = new ConformanceAssertions();
        HttpResult r = httpClient.post(url, BASE_PATH + "/subscriptions", token, SUBSCRIBE_BODY);
        a.assertStatusCode("POST /swim/v1/subscriptions returns 201", r, 201);
        a.assertFieldPresent("Response contains subscriptionId", r.body(), "subscriptionId");
        a.assertFieldPresent("Response contains queue_name", r.body(), "queue_name");
        return buildResult("API-01", "Subscribe — Happy Path (Yellow Profile REQ-0100)", a);
    }

    private Map<String, Object> testListSubscriptions(String url, String token) {
        ConformanceAssertions a = new ConformanceAssertions();
        HttpResult r = httpClient.get(url, BASE_PATH + "/subscriptions", token);
        a.assertStatusCode("GET /swim/v1/subscriptions returns 200", r, 200);
        return buildResult("API-02", "List Subscriptions (Yellow Profile REQ-0120)", a);
    }

    private Map<String, Object> testGetTopics(String url, String token) {
        ConformanceAssertions a = new ConformanceAssertions();
        HttpResult r = httpClient.get(url, BASE_PATH + "/topics", token);
        a.assertStatusCode("GET /swim/v1/topics returns 200", r, 200);
        a.assertFieldPresent("Response contains topics", r.body(), "topics");
        return buildResult("API-03", "Get Topics (Yellow Profile REQ-0110)", a);
    }

    private Map<String, Object> testUnsubscribe(String url, String token) {
        ConformanceAssertions a = new ConformanceAssertions();
        HttpResult create = httpClient.post(url, BASE_PATH + "/subscriptions", token, SUBSCRIBE_BODY);
        if (create.statusCode() == 201 && create.body() != null) {
            String subId = extractField(create.body(), "subscriptionId");
            if (subId != null) {
                HttpResult del = httpClient.delete(url, BASE_PATH + "/subscriptions/" + subId, token);
                a.assertStatusCode("DELETE /swim/v1/subscriptions/{id} returns 200", del, 200);
            }
        }
        return buildResult("API-04", "Unsubscribe (Yellow Profile REQ-0150)", a);
    }

    private Map<String, Object> testResponseRequiredFields(String url, String token) {
        ConformanceAssertions a = new ConformanceAssertions();
        HttpResult r = httpClient.post(url, BASE_PATH + "/subscriptions", token, SUBSCRIBE_BODY);
        a.assertStatusCode("POST /swim/v1/subscriptions returns 201", r, 201);
        a.assertFieldPresent("subscriptionId present", r.body(), "subscriptionId");
        a.assertFieldPresent("queue_name present", r.body(), "queue_name");
        a.assertFieldPresent("subscription_status present", r.body(), "subscription_status");
        a.assertFieldPresent("heartbeat_queue present", r.body(), "heartbeat_queue");
        return buildResult("DM-01", "Required Fields in Subscription Response", a);
    }

    private Map<String, Object> testInitialPausedStatus(String url, String token) {
        ConformanceAssertions a = new ConformanceAssertions();
        HttpResult r = httpClient.post(url, BASE_PATH + "/subscriptions", token, SUBSCRIBE_BODY);
        a.assertStatusCode("POST /swim/v1/subscriptions returns 201", r, 201);
        a.assertFieldEquals("Initial status is PAUSED", r.body(), "subscription_status", "PAUSED");
        return buildResult("DM-02", "Initial Subscription Status is PAUSED", a);
    }

    private Map<String, Object> testTopicReturnsFficeService(String url, String token) {
        ConformanceAssertions a = new ConformanceAssertions();
        HttpResult r = httpClient.get(url, BASE_PATH + "/topics", token);
        a.assertStatusCode("GET /swim/v1/topics returns 200", r, 200);
        a.assertFieldPresent("Topics contain FficeService", r.body(), "FficeService");
        return buildResult("DM-03", "Topics Returns FficeService", a);
    }

    private Map<String, Object> testMessageTypeFilterPersisted(String url, String token) {
        ConformanceAssertions a = new ConformanceAssertions();
        String body = "{\"message_type\":[\"FILED_FLIGHT_PLAN\"]}";
        HttpResult create = httpClient.post(url, BASE_PATH + "/subscriptions", token, body);
        a.assertStatusCode("POST /swim/v1/subscriptions returns 201", create, 201);
        a.assertFieldPresent("message_type present in response", create.body(), "message_type");
        String subId = extractField(create.body(), "subscriptionId");
        if (subId != null) {
            HttpResult get = httpClient.get(url, BASE_PATH + "/subscriptions/" + subId, token);
            a.assertStatusCode("GET /swim/v1/subscriptions/{id} returns 200", get, 200);
            a.assertFieldPresent("message_type persisted", get.body(), "message_type");
        }
        return buildResult("DM-04", "message_type Filter Persisted in Subscription", a);
    }

    private Map<String, Object> testWfsGetFeature(String url, String token) {
        ConformanceAssertions a = new ConformanceAssertions();
        HttpResult r = httpClient.get(url, BASE_PATH + "/features?typeName=ffice:FlightPlan&count=1", token);
        a.assertStatusCode("GET /swim/v1/features returns 200", r, 200);
        return buildResult("WFS-01", "WFS GetFeature Query", a);
    }

    private Map<String, Object> buildResult(String id, String name, ConformanceAssertions a) {
        return Map.of("scenarioId", id, "scenarioName", name,
            "passed", a.isAllPassed(), "checks", a.getResults());
    }

    private String extractField(String json, String field) {
        int idx = json.indexOf("\"" + field + "\":\"");
        if (idx < 0) return null;
        int start = idx + field.length() + 4;
        int end = json.indexOf("\"", start);
        return end > start ? json.substring(start, end) : null;
    }
}
