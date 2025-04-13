package com.greenpulse.data.notification_service.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserLoginedEvent {
    private String username;
    private String email;
    private Set<String> roles;
}
