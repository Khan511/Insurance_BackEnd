package com.example.insurance.domain.emailService;

import java.util.Map;
import com.example.insurance.domain.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserEvent {
    private User user;
    private EventType eventType;
    private Map<?, ?> data;

}
