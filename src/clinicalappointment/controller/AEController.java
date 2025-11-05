package clinicalappointment.controller;

import clinicalappointment.service.AEWaitTimeService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AEController {
    private final AEWaitTimeService service;

    public AEController(AEWaitTimeService service) {
        this.service = service;
    }

    @GetMapping("/ae-wait-times")
    public ResponseEntity<JsonNode> getAeWaitTimes() {
        JsonNode node = service.getRaw();
        if (node == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(node);
    }

    @GetMapping("/ae-wait-times/map")
    public ResponseEntity<Map<String, JsonNode>> getAeWaitTimesMap() {
        Map<String, JsonNode> map = service.getByHospitalName();
        return ResponseEntity.ok(map);
    }
}

