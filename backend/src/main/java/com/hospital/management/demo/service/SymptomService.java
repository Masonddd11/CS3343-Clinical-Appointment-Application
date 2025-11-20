package com.hospital.management.demo.service;

import com.hospital.management.demo.model.entity.Department;
import com.hospital.management.demo.model.entity.Symptom;
import com.hospital.management.demo.repository.DepartmentRepository;
import com.hospital.management.demo.repository.SymptomRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SymptomService {

    private final SymptomRepository symptomRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public Symptom createSymptom(String symptom, Long departmentId, Integer priority, List<String> keywords) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Symptom symptomEntity = Symptom.builder()
                .symptom(normalizeSymptom(symptom))
                .recommendedDepartment(department)
                .priority(priority != null ? priority : 1)
                .keywords(normalizeKeywords(keywords))
                .build();

        return symptomRepository.save(symptomEntity);
    }

    public Optional<SymptomMatchResult> analyzeSymptom(String symptomText) {
        String normalizedInput = normalizeSymptom(symptomText);
        List<String> inputTokens = tokenize(normalizedInput);

        List<Symptom> allSymptoms = symptomRepository.findAll();
        Symptom bestSymptom = null;
        double bestScore = 0;
        Set<String> bestMatches = new HashSet<>();

        for (Symptom symptom : allSymptoms) {
            SymptomScore score = computeScore(inputTokens, symptom);
            double adjustedScore = score.getScore() - (symptom.getPriority() * 0.05);
            if (adjustedScore > bestScore) {
                bestScore = adjustedScore;
                bestSymptom = symptom;
                bestMatches = score.getMatchedKeywords();
            }
        }

        if (bestSymptom == null || bestScore <= 0) {
            return Optional.empty();
        }

        return Optional.of(SymptomMatchResult.builder()
                .department(bestSymptom.getRecommendedDepartment())
                .confidenceScore(Math.min(1.0, bestScore))
                .matchedKeywords(new ArrayList<>(bestMatches))
                .build());
    }

    public List<Symptom> getAllSymptoms() {
        return symptomRepository.findAllByOrderByPriorityAsc();
    }

    public List<Symptom> getSymptomsByDepartment(Long departmentId) {
        return symptomRepository.findByRecommendedDepartmentId(departmentId);
    }

    private String normalizeSymptom(String symptom) {
        if (symptom == null) {
            return "";
        }
        String normalized = Normalizer.normalize(symptom.toLowerCase(Locale.ROOT).trim(), Normalizer.Form.NFD);
        return NON_ALPHANUMERIC.matcher(normalized).replaceAll(" ").replaceAll("\\s+", " ").trim();
    }

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9 ]");

    private List<String> normalizeKeywords(List<String> keywords) {
        List<String> result = new ArrayList<>();
        if (keywords != null) {
            keywords.stream()
                    .map(this::normalizeSymptom)
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .forEach(result::add);
        }
        return result;
    }

    private List<String> tokenize(String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        return List.of(text.split(" "));
    }

    private SymptomScore computeScore(List<String> inputTokens, Symptom symptom) {
        if (inputTokens.isEmpty()) {
            return new SymptomScore(0, Set.of());
        }

        Set<String> symptomTokens = new HashSet<>(tokenize(symptom.getSymptom()));
        symptomTokens.addAll(symptom.getKeywords());

        double matches = 0;
        Set<String> matched = new HashSet<>();

        for (String token : inputTokens) {
            for (String keyword : symptomTokens) {
                if (keyword.contains(token) || token.contains(keyword)) {
                    matches++;
                    matched.add(keyword);
                    break;
                }
            }
        }

        double score = matches / inputTokens.size();
        return new SymptomScore(score, matched);
    }

    @Data
    @Builder
    public static class SymptomMatchResult {
        private Department department;
        private double confidenceScore;
        private List<String> matchedKeywords;
    }

    @Data
    @Builder
    private static class SymptomScore {
        private final double score;
        private final Set<String> matchedKeywords;
    }
}

