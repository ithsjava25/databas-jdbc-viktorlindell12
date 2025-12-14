package com.example.repository;

import com.example.datasource.SimpleDriverManagerDataSource;

import java.sql.*;

public class JdbcAccountRepository implements AccountRepository {

    private final SimpleDriverManagerDataSource dataSource;

    public JdbcAccountRepository(SimpleDriverManagerDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean login(String username, String password) {
        String sql = "SELECT 1 FROM account WHERE name = ? AND password = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String createAccount(String firstName, String lastName, String ssn, String password) {
        String sql = "INSERT INTO account (first_name, last_name, ssn, password, name) VALUES (?, ?, ?, ?, ?)";
        String generatedName = firstName.substring(0, 3) + lastName.substring(0, 3);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, ssn);
            ps.setString(4, password);
            ps.setString(5, generatedName);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long userId = keys.getLong(1);
                    System.out.println("Generated user_id = " + userId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return generatedName;
    }


    @Override
    public boolean updatePassword(long userId, String newPassword) {
        String sql = "UPDATE account SET password = ? WHERE user_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setLong(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteAccount(long userId) {
        String sql = "DELETE FROM account WHERE user_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
