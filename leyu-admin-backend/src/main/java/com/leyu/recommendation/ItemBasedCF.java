package com.leyu.recommendation;

import com.leyu.entity.UserBehavior;
import com.leyu.mapper.UserBehaviorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemBasedCF {

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    private Map<Long, Map<Long, Double>> similarityMatrix;
    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 30 * 60 * 1000;

    public List<Long> recommendForUser(Long userId, int numRecommendations) {
        if (shouldUpdateMatrix()) {
            calculateSimilarityMatrix();
        }
        List<Long> userSongIds = userBehaviorMapper.findSongIdsByUserId(userId);
        if (userSongIds.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Double> scores = new HashMap<>();
        for (Long songId : userSongIds) {
            Map<Long, Double> similarSongs = similarityMatrix.getOrDefault(songId, Collections.emptyMap());
            for (Map.Entry<Long, Double> entry : similarSongs.entrySet()) {
                Long similarSongId = entry.getKey();
                if (!userSongIds.contains(similarSongId)) {
                    scores.merge(similarSongId, entry.getValue(), Double::sum);
                }
            }
        }
        return scores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(numRecommendations)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private boolean shouldUpdateMatrix() {
        return similarityMatrix == null || System.currentTimeMillis() - lastUpdateTime > UPDATE_INTERVAL;
    }

    public synchronized void calculateSimilarityMatrix() {
        List<Long> allSongIds = userBehaviorMapper.findAllSongIds();
        similarityMatrix = new HashMap<>();
        for (Long songId : allSongIds) {
            Map<Long, Double> similarities = new HashMap<>();
            List<Long> usersOfSong = userBehaviorMapper.findUserIdsBySongId(songId);
            for (Long otherSongId : allSongIds) {
                if (!songId.equals(otherSongId)) {
                    double similarity = calculateCosineSimilarity(songId, otherSongId, usersOfSong);
                    if (similarity > 0.1) {
                        similarities.put(otherSongId, similarity);
                    }
                }
            }
            Map<Long, Double> topSimilar = similarities.entrySet().stream()
                    .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                    .limit(10)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
            similarityMatrix.put(songId, topSimilar);
        }
        lastUpdateTime = System.currentTimeMillis();
    }

    private double calculateCosineSimilarity(Long songId1, Long songId2, List<Long> usersOfSong1) {
        List<Long> usersOfSong2 = userBehaviorMapper.findUserIdsBySongId(songId2);
        if (usersOfSong2.isEmpty()) return 0.0;
        Set<Long> intersection = new HashSet<>(usersOfSong1);
        intersection.retainAll(usersOfSong2);
        if (intersection.isEmpty()) return 0.0;
        double dotProduct = intersection.size();
        double norm1 = Math.sqrt(usersOfSong1.size());
        double norm2 = Math.sqrt(usersOfSong2.size());
        return dotProduct / (norm1 * norm2);
    }
}
