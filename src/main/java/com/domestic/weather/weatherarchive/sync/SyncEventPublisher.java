package com.domestic.weather.weatherarchive.sync;

import events.SyncEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class SyncEventPublisher {
    private final KafkaTemplate<String, SyncEvent> kafkaTemplate;
    private final SourceTypeMapper sourceTypeMapper;

    public void sendSynEvent(LocalDate startDate, LocalDate endDate) {
        log.info("About to send sync event");
        SyncEvent event = sourceTypeMapper.buildSyncEvent(startDate, endDate);
        kafkaTemplate.send(kafkaTemplate.getDefaultTopic(), event);
        log.info("Sent sync event for range = [{}; {}]", event.getStartDate(), event.getEndDate());
    }
}
