package com.example.repository;

import java.util.List;

public interface MoonMissionRepository {
    List<String> findAllSpacecrafts();
    String findMissionById(long id);
    int countMissionsForYear(int year);
}
