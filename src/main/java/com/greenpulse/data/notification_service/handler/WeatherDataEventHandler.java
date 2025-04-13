package com.greenpulse.data.notification_service.handler;

import com.greenpulse.data.notification_service.event.UserLoginedEvent;
import com.greenpulse.data.notification_service.event.WeatherDataEvent;
import com.greenpulse.data.notification_service.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WeatherDataEventHandler {

    @Autowired
    private MailService mailService;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    // In-memory cache: email -> UserLoginedEvent
    private final Map<String, UserLoginedEvent> userCache = new ConcurrentHashMap<>();

    @KafkaListener(topics = "user-logged-in-event-topic", groupId = "user-logined-events", containerFactory = "loginedUserKafkaListenerContainerFactory")
    public void handleLoginEvent(UserLoginedEvent event) {
        LOGGER.info("Received UserLoginedEvent: {}", event.getEmail());

        if (event.getEmail() != null) {
            userCache.put(event.getEmail(), event); // store user for later use
        }
    }

    @KafkaListener(topics = "weather-data-event-topic", groupId = "weather-data-events", containerFactory = "kafkaListenerContainerFactory")
    public void handleWeatherDataEvent(WeatherDataEvent weatherDataEvent) {
        LOGGER.info("Received WeatherDataEvent: {} - {}", weatherDataEvent.getCity(), weatherDataEvent.getTemperature());

        try {
            for (Map.Entry<String, UserLoginedEvent> entry : userCache.entrySet()) {
                UserLoginedEvent user = entry.getValue();
                if (user.getRoles() != null && user.getRoles().contains("ADMIN")) {
                    continue; // skip admins
                }

                String email = entry.getKey();

                if (weatherDataEvent.getTemperature() > 0) {
                    String alert = "Temperature is too high in " + weatherDataEvent.getCity() + ": " + weatherDataEvent.getTemperature();
                    LOGGER.warn(alert + " -> Email: " + email);
                    mailService.sendAlert(email, "Temperature warning", alert);
                }

                if (weatherDataEvent.getHumidity() < 20.0) {
                    String alert = "Humidity is too low in " + weatherDataEvent.getCity() + ": " + weatherDataEvent.getHumidity();
                    LOGGER.warn(alert + " -> Email: " + email);
                    mailService.sendAlert(email, "Humidity warning", alert);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to handle WeatherDataEvent", e);
        }
    }
}