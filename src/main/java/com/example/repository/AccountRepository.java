package com.example.repository;

public interface AccountRepository {
    boolean login(String username, String password);
    String createAccount(String firstName, String lastName, String ssn, String password);
    boolean updatePassword(long userId, String newPassword);
    boolean deleteAccount(long userId);
}
