package com.github.swim_developer.validator.ffice.provider.domain.port.in;

import com.github.swim_developer.validator.ffice.provider.domain.model.ReceivedMessage;
import java.util.List;

public interface MessagePort {
    List<ReceivedMessage> findBySubscriptionId(String subscriptionId);
    List<ReceivedMessage> findRecent(int limit);
}
