package com.example.repository;

import com.example.datasource.SimpleDriverManagerDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcMoonMissionRepository implements MoonMissionRepository {

    private final SimpleDriverManagerDataSource dataSource;

    public JdbcMoonMissionRepository(SimpleDriverManagerDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<String> findAllSpacecrafts() {
        List<String> result = new ArrayList<>();
        String sql = "SELECT spacecraft FROM moon_mission";

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(rs.getString("spacecraft"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public String findMissionById(long id) {
        String sql = "SELECT spacecraft FROM moon_mission WHERE mission_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("spacecraft");
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countMissionsForYear(int year) {
        String sql = "SELECT COUNT(*) FROM moon_mission WHERE YEAR(launch_date) = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

