#  Notification Service

The **Notification Service** is responsible for delivering alerts and updates to users via various channels such as email, SMS or internal systems (Kafka).

---

##  Technologies

- Java 21  
- Spring Boot 3.4.4  
- Kafka (consuming event messages)  
- Spring Mail (optional for email)  
- SLF4J + Logback (logging)  
- Prometheus + Grafana (observability)

---


##  Purpose

Service listens to **Kafka topics** like:

- `user-logged-in-event-topic`
- `weather-data-event-topic`

And performs actions such as:

- Send email notifications  
- Log or store alerts  
- Update internal dashboards  

---

##  Kafka Listener Example

```java
@KafkaListener(topics = "user-logged-in-event-topic", groupId = "user-logined-events")
public void handleUserRegistration(UserRegisteredEvent event) {
    notificationService.sendWelcomeEmail(event.getEmail(), event.getUsername());
}
```

---

##  Sending Emails 

config:

```yaml
spring:
  mail:
    host: smtp.example.com
    port: 587
    username: email
    password: password
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true
```

```java
@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
```

---

##  Monitoring

Expose metrics at:

```
/actuator/prometheus
```

And track

---

##  Future Improvements

- Add WebSocket notifications  
- Retry failed deliveries  
- Store alerts in database  
- Multi-language support  
- Notification templates (Thymeleaf / Markdown)
