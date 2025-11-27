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
import java.util.Collections;
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

    private static final double MIN_CONFIDENCE = 0.25;

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
        if (inputTokens.isEmpty()) {
            return Optional.empty();
        }

        List<Symptom> allSymptoms = symptomRepository.findAll();
        Symptom bestSymptom = null;
        double bestScore = 0;
        Set<String> bestMatches = new HashSet<>();

        for (Symptom symptom : allSymptoms) {
            SymptomScore score = computeScore(inputTokens, symptom);
            if (score.getScore() <= 0) {
                continue;
            }
            double priorityBoost = 1.0 / ((symptom.getPriority() != null ? symptom.getPriority() : 1) + 1.0);
            double adjustedScore = (score.getScore() * 0.7) + (score.getKeywordCoverage() * 0.2) + (priorityBoost * 0.1);
            if (adjustedScore > bestScore) {
                bestScore = adjustedScore;
                bestSymptom = symptom;
                bestMatches = score.getMatchedKeywords();
            }
        }

        if (bestSymptom == null || bestScore < MIN_CONFIDENCE) {
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
            return SymptomScore.builder()
                    .score(0)
                    .keywordCoverage(0)
                    .matchedKeywords(Collections.emptySet())
                    .build();
        }

        Set<String> symptomTokens = buildTokenSet(symptom);
        if (symptomTokens.isEmpty()) {
            return SymptomScore.builder()
                    .score(0)
                    .keywordCoverage(0)
                    .matchedKeywords(Collections.emptySet())
                    .build();
        }

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
        double coverage = (double) matched.size() / symptomTokens.size();
        return SymptomScore.builder()
                .score(score)
                .keywordCoverage(coverage)
                .matchedKeywords(matched)
                .build();
    }

    private Set<String> buildTokenSet(Symptom symptom) {
        Set<String> symptomTokens = new HashSet<>(tokenize(symptom.getSymptom()));
        if (symptom.getKeywords() != null) {
            symptom.getKeywords().stream()
                    .map(this::normalizeSymptom)
                    .map(this::tokenize)
                    .forEach(tokenList -> symptomTokens.addAll(tokenList));
        }
        return symptomTokens;
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
        private final double keywordCoverage;
        private final Set<String> matchedKeywords;
    }
}
