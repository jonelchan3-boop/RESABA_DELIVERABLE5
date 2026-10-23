package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final Scanner sc = new Scanner(System.in);
    private final UserDao userDao = new UserDao();

    private void displayUserTable(List<User> users) {
        System.out.println("-----------------------------------------------------------");
        System.out.printf("%-5s | %-15s | %-18s | %-10s%n", "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println("-----------------------------------------------------------");
        for (User u : users) {
            System.out.printf("%-5d | %-15s | %-18s | %-10s%n",
                    u.getUserId(), u.getUsername(), u.getRole(), u.getAccountStatus());
        }
        System.out.println("-----------------------------------------------------------");
    }

    public void show() {
        while (true) {
            System.out.println("\n=====================================");
            System.out.println("          ADMIN DASHBOARD            ");
            System.out.println("=====================================");
            System.out.println("1. View All Users");
            System.out.println("2. View Pending Approvals");
            System.out.println("3. Approve User");
            System.out.println("4. Add New User");
            System.out.println("5. Update User Role/Status");
            System.out.println("6. Delete User");
            System.out.println("7. Logout");
            System.out.print("Choice: ");

            switch (sc.nextLine().trim()) {
                case "1" -> viewAllUsers();
                case "2" -> viewPendingApprovals();
                case "3" -> approveRegistration();
                case "4" -> addNewUser();
                case "5" -> updateUserInfo();
                case "6" -> deleteUser();
                case "7" -> {
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("[!] Invalid choice.");
            }
        }
    }

    private void viewAllUsers() {
        System.out.println("\n--- ALL USERS ---");
        List<User> list = userDao.getAllUsers();
        if (list.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        displayUserTable(list);
    }

    private void viewPendingApprovals() {
        System.out.println("\n--- PENDING APPROVALS ---");
        List<User> pending = userDao.getPendingUsers();
        if (pending.isEmpty()) {
            System.out.println("No pending registrations.");
            return;
        }
        displayUserTable(pending);
    }

    private void approveRegistration() {
        viewPendingApprovals();
        List<User> pending = userDao.getPendingUsers();
        if (pending.isEmpty()) return;

        System.out.print("\nEnter User ID to approve (0 = Cancel): ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            if (id == 0) {
                System.out.println("Approval cancelled.");
                return;
            }
            boolean exists = pending.stream().anyMatch(u -> u.getUserId() == id);
            if (!exists) {
                System.out.println("[!] ID not in pending list.");
                return;
            }
            if (userDao.approveUser(id)) {
                System.out.println("[✓] User approved! Status → APPROVED");
            } else {
                System.out.println("[✗] Approval failed.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid number.");
        }
    }

    private void addNewUser() {
        System.out.println("\n--- ADD NEW USER ---");
        System.out.print("Username: ");
        String uname = sc.nextLine().trim();
        if (uname.isEmpty()) { System.out.println("Empty username."); return; }

        System.out.print("Password: ");
        String pwd = sc.nextLine().trim();
        if (pwd.isEmpty()) { System.out.println("Empty password."); return; }

        System.out.print("Role (ADMIN | STAFF/CASHIER | DELIVERY RIDER | CUSTOMER): ");
        String role = sc.nextLine().trim().toUpperCase();

        System.out.print("Status [PENDING/APPROVED]: ");
        String status = sc.nextLine().trim().toUpperCase();
        if (!status.equals("APPROVED")) status = "PENDING";

        if (userDao.addUser(new User(uname, pwd, role, status))) {
            System.out.println("[✓] User added.");
        } else {
            System.out.println("[✗] Failed. Username exists or invalid role.");
        }
    }

    private void updateUserInfo() {
        viewAllUsers();
        System.out.print("\nUser ID to update (0 = Cancel): ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            if (id == 0) return;

            System.out.print("Update Role? (Y/N): ");
            if (sc.nextLine().trim().equalsIgnoreCase("Y")) {
                System.out.print("New Role (ADMIN | STAFF/CASHIER | DELIVERY RIDER | CUSTOMER): ");
                String newRole = sc.nextLine().trim().toUpperCase();
                if (userDao.updateRole(id, newRole)) System.out.println("[✓] Role updated.");
                else System.out.println("[✗] Invalid role.");
            }

            System.out.print("Update Status? (Y/N): ");
            if (sc.nextLine().trim().equalsIgnoreCase("Y")) {
                System.out.print("New Status: ");
                String newStat = sc.nextLine().trim().toUpperCase();
                if (List.of("PENDING", "APPROVED", "REJECTED", "SUSPENDED").contains(newStat)) {
                    if (userDao.updateStatus(id, newStat)) System.out.println("[✓] Status updated.");
                    else System.out.println("[✗] Update failed.");
                } else System.out.println("[!] Invalid status.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid ID.");
        }
    }

    private void deleteUser() {
        viewAllUsers();
        System.out.print("\nUser ID to DELETE (0 = Cancel): ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            if (id == 0) return;
            System.out.print("Confirm DELETE? YES/NO: ");
            if (sc.nextLine().trim().equalsIgnoreCase("YES")) {
                if (userDao.deleteUser(id)) System.out.println("[✓] Deleted.");
                else System.out.println("[✗] Failed.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid ID.");
        }
    }
}