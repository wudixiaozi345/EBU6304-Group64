import com.bupt.recruit.model.User;
import com.bupt.recruit.service.UserService;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class LoginTest {

    private static final String TEST_TA_ID = "TEST_TA_001";
    private static final String TEST_TA_PASSWORD = "Test1234";
    private static final String TEST_TA_NAME = "Test TA";
    private static final String TEST_TA_EMAIL = "test.ta@example.com";

    private static final String TEST_MO_ID = "TEST_MO_001";
    private static final String TEST_MO_PASSWORD = "TestMo123";
    private static final String TEST_MO_NAME = "Test MO";
    private static final String TEST_MO_EMAIL = "test.mo@example.com";

    private static final String TEST_ADMIN_ID = "TEST_ADMIN_001";
    private static final String TEST_ADMIN_PASSWORD = "TestAdmin123";
    private static final String TEST_ADMIN_NAME = "Test Admin";
    private static final String TEST_ADMIN_EMAIL = "test.admin@example.com";

    private List<User> backupTaUsers() {
        return new ArrayList<>(UserService.readTaUsers());
    }

    private List<User> backupMoUsers() {
        return new ArrayList<>(UserService.readMoUsers());
    }

    private List<User> backupAdminUsers() {
        return new ArrayList<>(UserService.readAdminUsers());
    }

    private void restoreTaUsers(List<User> original) {
        UserService.writeTaUsers(original);
    }

    private void restoreMoUsers(List<User> original) {
        UserService.writeMoUsers(original);
    }

    private void restoreAdminUsers(List<User> original) {
        UserService.writeAdminUsers(original);
    }

    private void ensureTaUserExists() {
        List<User> users = backupTaUsers();
        List<User> working = new ArrayList<>(users);
        working.removeIf(u -> TEST_TA_ID.equals(u.getId()));
        working.add(new User(TEST_TA_ID, TEST_TA_NAME, TEST_TA_EMAIL, TEST_TA_PASSWORD, "ta", "active"));
        UserService.writeTaUsers(working);
    }

    private void ensureMoUserExists() {
        List<User> users = backupMoUsers();
        List<User> working = new ArrayList<>(users);
        working.removeIf(u -> TEST_MO_ID.equals(u.getId()));
        working.add(new User(TEST_MO_ID, TEST_MO_NAME, TEST_MO_EMAIL, TEST_MO_PASSWORD, "mo", "active"));
        UserService.writeMoUsers(working);
    }

    private void ensureAdminUserExists() {
        List<User> users = backupAdminUsers();
        List<User> working = new ArrayList<>(users);
        working.removeIf(u -> TEST_ADMIN_ID.equals(u.getId()));
        working.add(new User(TEST_ADMIN_ID, TEST_ADMIN_NAME, TEST_ADMIN_EMAIL, TEST_ADMIN_PASSWORD, "admin", "active"));
        UserService.writeAdminUsers(working);
    }

    @Test
    public void testTaLoginSuccess() {
        System.out.println("Testing TA login with valid credentials...");
        List<User> original = backupTaUsers();
        ensureTaUserExists();
        try {
            User authUser = UserService.authenticate(TEST_TA_ID, TEST_TA_PASSWORD, "ta");
            assertNotNull("TA login should succeed", authUser);
            assertEquals("Role should be ta", "ta", authUser.getRole());
            assertEquals("ID should match", TEST_TA_ID, authUser.getId());
            System.out.println("PASS: TA login successful");
        } finally {
            restoreTaUsers(original);
        }
    }

    @Test
    public void testTaLoginFailure() {
        System.out.println("Testing TA login with invalid credentials...");
        List<User> original = backupTaUsers();
        ensureTaUserExists();
        try {
            User user = UserService.authenticate(TEST_TA_ID, "wrongpassword", "ta");
            assertNull("TA login should fail with wrong password", user);
            System.out.println("PASS: TA login correctly failed");
        } finally {
            restoreTaUsers(original);
        }
    }

    @Test
    public void testMoLoginSuccess() {
        System.out.println("Testing MO login with valid credentials...");
        List<User> original = backupMoUsers();
        ensureMoUserExists();
        try {
            User user = UserService.authenticate(TEST_MO_ID, TEST_MO_PASSWORD, "mo");
            assertNotNull("MO login should succeed", user);
            assertEquals("Role should be mo", "mo", user.getRole());
            assertEquals("ID should match", TEST_MO_ID, user.getId());
            System.out.println("PASS: MO login successful");
        } finally {
            restoreMoUsers(original);
        }
    }

    @Test
    public void testAdminLoginSuccess() {
        System.out.println("Testing Admin login with valid credentials...");
        List<User> original = backupAdminUsers();
        ensureAdminUserExists();
        try {
            User user = UserService.authenticate(TEST_ADMIN_ID, TEST_ADMIN_PASSWORD, "admin");
            assertNotNull("Admin login should succeed", user);
            assertEquals("Role should be admin", "admin", user.getRole());
            assertEquals("ID should match", TEST_ADMIN_ID, user.getId());
            System.out.println("PASS: Admin login successful");
        } finally {
            restoreAdminUsers(original);
        }
    }

    @Test
    public void testInvalidRole() {
        System.out.println("Testing login with invalid role...");
        List<User> original = backupTaUsers();
        ensureTaUserExists();
        try {
            User user = UserService.authenticate(TEST_TA_ID, TEST_TA_PASSWORD, "invalid");
            assertNull("Login should fail with invalid role", user);
            System.out.println("PASS: Invalid role correctly rejected");
        } finally {
            restoreTaUsers(original);
        }
    }
}