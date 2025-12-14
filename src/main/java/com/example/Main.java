package com.example;

import com.example.datasource.SimpleDriverManagerDataSource;
import com.example.repository.AccountRepository;
import com.example.repository.JdbcAccountRepository;
import com.example.repository.JdbcMoonMissionRepository;
import com.example.repository.MoonMissionRepository;

import java.util.Arrays;
import java.util.Scanner;

public class Main {

    private final MoonMissionRepository missionRepo;
    private final AccountRepository accountRepo;

    public Main(MoonMissionRepository missionRepo, AccountRepository accountRepo) {
        this.missionRepo = missionRepo;
        this.accountRepo = accountRepo;
    }

    public static void main(String[] args) {

        if (isDevMode(args)) {
            DevDatabaseInitializer.start();
        }

        String jdbcUrl = resolveConfig("APP_JDBC_URL", "APP_JDBC_URL");
        String dbUser = resolveConfig("APP_DB_USER", "APP_DB_USER");
        String dbPass = resolveConfig("APP_DB_PASS", "APP_DB_PASS");

        if (jdbcUrl == null || dbUser == null || dbPass == null) {
            throw new IllegalStateException("Missing DB configuration");
        }

        SimpleDriverManagerDataSource dataSource =
                new SimpleDriverManagerDataSource(jdbcUrl, dbUser, dbPass);

        MoonMissionRepository missionRepo = new JdbcMoonMissionRepository(dataSource);
        AccountRepository accountRepo = new JdbcAccountRepository(dataSource);

        new Main(missionRepo, accountRepo).run();
    }


    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Moon Mission CLI!");

        boolean running = true;

        while (running) {

            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            boolean validLogin = accountRepo.login(username, password);

            if (!validLogin) {
                System.out.println("Invalid username or password");
                continue;
            }

            boolean loggedIn = true;
            while (loggedIn) {
                System.out.println("\nMenu:");
                System.out.println("1) List moon missions");
                System.out.println("2) Get mission by id");
                System.out.println("3) Count missions for year");
                System.out.println("4) Create an account");
                System.out.println("5) Update an account password");
                System.out.println("6) Delete an account");
                System.out.println("7) Logout / Switch user");
                System.out.println("0) Exit");
                System.out.print("Choose an option: ");

                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        missionRepo.findAllSpacecrafts().forEach(System.out::println);
                        break;
                    case "2":
                        System.out.print("Mission ID: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        String mission = missionRepo.findMissionById(id);
                        System.out.println(mission != null ? mission : "Mission not found");
                        break;
                    case "3":
                        System.out.print("Year: ");
                        int year = Integer.parseInt(scanner.nextLine());
                        System.out.println(year + ": " + missionRepo.countMissionsForYear(year));
                        break;
                    case "4":
                        System.out.print("First name: ");
                        String first = scanner.nextLine();
                        System.out.print("Last name: ");
                        String last = scanner.nextLine();
                        System.out.print("SSN: ");
                        String ssn = scanner.nextLine();
                        System.out.print("Password: ");
                        String pass = scanner.nextLine();

                        String generatedUsername = accountRepo.createAccount(first, last, ssn, pass);
                        System.out.println("Account created. Your username is: " + generatedUsername);
                        break;
                    case "5":
                        System.out.print("User ID: ");
                        long uid = Long.parseLong(scanner.nextLine());
                        System.out.print("New password: ");
                        String newPass = scanner.nextLine();
                        accountRepo.updatePassword(uid, newPass);
                        System.out.println("updated");
                        break;
                    case "6":
                        System.out.print("User ID to delete: ");
                        long delId = Long.parseLong(scanner.nextLine());
                        accountRepo.deleteAccount(delId);
                        System.out.println("deleted");
                        break;
                    case "7":
                        System.out.println("You have been logged out.");
                        loggedIn = false;
                        break;
                    case "0":
                        running = false;
                        loggedIn = false;
                        System.out.println("Exiting program...");
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        }
    }

    private static boolean isDevMode(String[] args) {
        if (Boolean.getBoolean("devMode")) return true;
        if ("true".equalsIgnoreCase(System.getenv("DEV_MODE"))) return true;
        return Arrays.asList(args).contains("--dev");
    }

    private static String resolveConfig(String propertyKey, String envKey) {
        String v = System.getProperty(propertyKey);
        if (v == null || v.trim().isEmpty()) {
            v = System.getenv(envKey);
        }
        return (v == null || v.trim().isEmpty()) ? null : v.trim();
    }
}



