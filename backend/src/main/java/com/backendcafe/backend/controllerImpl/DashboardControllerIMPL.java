package com.backendcafe.backend.controllerImpl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping("/dashboard")
public interface DashboardControllerIMPL {

    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getCount();
}
