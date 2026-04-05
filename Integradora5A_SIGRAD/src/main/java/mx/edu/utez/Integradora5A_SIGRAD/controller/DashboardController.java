package mx.edu.utez.Integradora5A_SIGRAD.controller;

import mx.edu.utez.Integradora5A_SIGRAD.dto.DashboardDTO;
import mx.edu.utez.Integradora5A_SIGRAD.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardDTO> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.obtenerEstadisticas());
    }
}