package sajipay.services;

import sajipay.models.User;
import sajipay.models.Employee;
import sajipay.models.Management;

public class AuthService {
    private static AuthService instance;
    private final Management management;
    private User currentUser;
    private String role;

    private AuthService() {
        this.management = Management.getInstance();
        this.currentUser = null;
        this.role = null;
    }

    public static AuthService getInstance() {
        if (instance == null) {
            synchronized (AuthService.class) {
                if (instance == null) {
                    instance = new AuthService();
                }
            }
        }
        return instance;
    }

    // Authenticate user and start session if successful
    public boolean authenticateAndLogin(String username, String password, String role) {
        User user = management.getUserByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            return false;
        }
        String actualRole = user instanceof Employee ? "Employee" : "Customer";
        if (!actualRole.equals(role)) {
            return false;
        }
        // Authentication successful, start session
        login(user, role);
        return true;
    }

    // Login method to set session
    public void login(User user, String role) {
        this.currentUser = user;
        this.role = role;
    }

    // Logout method to clear session
    public void logout() {
        this.currentUser = null;
        this.role = null;
    }

    // Get current logged-in user
    public User getCurrentUser() {
        return currentUser;
    }

    // Get current role
    public String getRole() {
        return role;
    }

    // Check if a user is logged in
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}