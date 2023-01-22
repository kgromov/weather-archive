package com.domestic.weather.weatherarchive.service;

import com.domestic.weather.weatherarchive.config.BrokerSettings;
import events.SyncEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SyncEventPublisher {
    private final BrokerSettings brokerSettings;
    private final RabbitTemplate rabbitTemplate;

    public void sendSynEvent(SyncEvent event) {
        log.info("About to send sync event");
        rabbitTemplate.convertAndSend(brokerSettings.getQueueName(), event);
        log.info("Sent sync event for range = [{}; {}]", event.getStartDate(), event.getEndDate());
    }
}
