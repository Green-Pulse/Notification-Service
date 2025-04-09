package com.greenpulse.data.notification_service.handler;

import com.greenpulse.data.notification_service.event.WeatherDataEvent;
import com.greenpulse.data.notification_service.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "weather-data-event-topic")
public class WeatherDataEventHandler {

    @Autowired
    private MailService mailService;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @KafkaHandler
    public void handleWeatherDataEvent(WeatherDataEvent weatherDataEvent) {
        LOGGER.info("Received WeatherDataEvent: " + weatherDataEvent.getCity() + ": " + weatherDataEvent.getTemperature());

        try {

            if (weatherDataEvent.getTemperature() > 15.0) {
                System.out.println("CAUTION: Temperature is too high in " + weatherDataEvent.getCity());
                String alert = "Temperature is too high in " + weatherDataEvent.getCity() + ": " + weatherDataEvent.getTemperature();
                mailService.sendAlert("eljan.najafov02@gmail.com", "Temperature warning", alert); // hardcoded, лучше брать мейс с JWT, скорее всего сделаю этот сервис secure
            }

            if (weatherDataEvent.getHumidity() < 20.0) {
                System.out.println("CAUTION: Humidity is too low in " + weatherDataEvent.getCity());
                String alert = "Humidity is too low in " + weatherDataEvent.getCity() + ": " + weatherDataEvent.getHumidity();
                mailService.sendAlert("eljan.najafov02@gmail.com", "Humidity warning", alert);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to handle WeatherDataEvent", e);
            // тут можно логировать или сохранять в отдельную очередь
        }
    }
}