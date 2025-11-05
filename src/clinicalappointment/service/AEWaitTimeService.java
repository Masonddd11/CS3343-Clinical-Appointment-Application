package clinicalappointment.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.Duration;
import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AEWaitTimeService {
    private static final Logger logger = LoggerFactory.getLogger(AEWaitTimeService.class);
    private static final String REMOTE_URL = "https://www.ha.org.hk/opendata/aed/aedwtdata2-en.json";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private JsonNode cached = null;
    private Instant cachedAt = Instant.EPOCH;

    public synchronized JsonNode getRaw() {
        if (cached != null && Instant.now().isBefore(cachedAt.plus(CACHE_TTL))) {
            return cached;
        }
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(REMOTE_URL)).GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200) {
                JsonNode node = mapper.readTree(res.body());
                cached = node;
                cachedAt = Instant.now();
                return cached;
            } else {
                logger.warn("AE wait time fetch returned status {}. Falling back to local resource if available.", res.statusCode());
            }
        } catch (Exception ex) {
            logger.warn("Failed to fetch AE wait times from remote, will try local resource", ex);
        }

        // Try to load bundled resource data/a&e_waiting_time.json as a fallback
        try (var is = getClass().getClassLoader().getResourceAsStream("data/a&e_waiting_time.json")) {
            if (is != null) {
                java.nio.charset.Charset utf8 = java.nio.charset.StandardCharsets.UTF_8;
                String json = new String(is.readAllBytes(), utf8);
                JsonNode node = mapper.readTree(json);
                cached = node;
                cachedAt = Instant.now();
                logger.info("Loaded AE wait times from bundled resource data/a&e_waiting_time.json");
                return cached;
            } else {
                logger.warn("Bundled AE wait time resource not found: data/a&e_waiting_time.json");
            }
        } catch (Exception ex) {
            logger.warn("Failed to load bundled AE wait times resource", ex);
        }

        return cached;
    }

    public synchronized Map<String, JsonNode> getByHospitalName() {
        JsonNode root = getRaw();
        Map<String, JsonNode> map = new HashMap<>();
        if (root == null) return map;
        JsonNode wait = root.path("waitTime");
        if (wait.isArray()) {
            for (JsonNode item : wait) {
                String name = item.path("hospName").asText();
                if (name != null && !name.isEmpty()) map.put(name.trim(), item);
            }
        }
        return map;
    }
}
