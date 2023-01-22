package com.domestic.weather.weatherarchive.sync;

import com.domestic.weather.weatherarchive.config.BrokerSettings;
import events.SyncEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class SyncEventPublisher {
    private final BrokerSettings brokerSettings;
    private final RabbitTemplate rabbitTemplate;
    private final SourceTypeMapper sourceTypeMapper;

    public void sendSynEvent(LocalDate startDate, LocalDate endDate) {
        log.info("About to send sync event");
        SyncEvent event = sourceTypeMapper.buildSyncEvent(startDate, endDate);
        rabbitTemplate.convertAndSend(brokerSettings.getQueueName(), event);
        log.info("Sent sync event for range = [{}; {}]", event.getStartDate(), event.getEndDate());
    }
}
