package com.vgarcia.marketplace.application.services;

import com.vgarcia.marketplace.domain.models.ProductDomain;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductRecommendationService {

    public List<ProductDomain> findSimilarProducts(ProductDomain target,
                                                   List<ProductDomain> candidates,
                                                   int limit) {
        double[] targetVector = parseVector(target.similarityVector());

        return candidates.stream()
                .filter(p -> !p.id().equals(target.id()))
                .filter(p -> p.similarityVector() != null)
                .sorted(Comparator.comparingDouble(
                        p -> -calculateCosineSimilarity(targetVector,
                                parseVector(p.similarityVector()))
                ))
                .limit(limit)
                .collect(Collectors.toList());
    }


    private double calculateCosineSimilarity(double[] vectorA, double[] vectorB) {
        if (vectorA.length != vectorB.length || vectorA.length == 0) return 0.0;

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        if (normA == 0 || normB == 0) return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private double[] parseVector(String jsonVector) {
        if (jsonVector == null || jsonVector.isBlank()) return new double[0];
        try {
            String clean = jsonVector.replace("[", "").replace("]", "");
            return Arrays.stream(clean.split(","))
                    .map(String::trim)
                    .mapToDouble(Double::parseDouble)
                    .toArray();
        } catch (Exception e) {
            return new double[0];
        }
    }
}
