package com.ecommerce.notification.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    // Simple mock list for demo purposes
    public static final List<String> NOTIFICATION_LOGS = new ArrayList<>();

    @GetMapping("/logs")
    public List<String> getLogs() {
        return NOTIFICATION_LOGS;
    }
}
