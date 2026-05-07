package com.github.swim_developer.validator.ffice.provider.domain.port.in;

import com.github.swim_developer.validator.ffice.provider.domain.model.ReceivedMessage;

public interface MessagePersistencePort {
    void save(ReceivedMessage message);
}
