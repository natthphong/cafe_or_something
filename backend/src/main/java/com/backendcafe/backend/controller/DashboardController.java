package com.backendcafe.backend.controller;

import com.backendcafe.backend.controllerImpl.DashboardControllerIMPL;
import com.backendcafe.backend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DashboardController implements DashboardControllerIMPL {
    private  final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        return dashboardService.getCount();
    }
}
