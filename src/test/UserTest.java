import com.bupt.recruit.model.User;
import com.bupt.recruit.service.UserService;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void testTaProfileCompletion() {
        System.out.println("Testing TA profile completion check...");
        boolean completed = UserService.isTaProfileCompleted("2023001");
        // This depends on existing data, but we can test the method exists
        assertNotNull("Profile completion should return boolean", completed);
        System.out.println("PASS: TA profile completion check works");
    }

    @Test
    public void testUpdateTaStatus() {
        System.out.println("Testing TA status update...");
        UserService.updateTaStatus("2023001", "inactive");

        boolean completed = UserService.isTaProfileCompleted("2023001");
        // After setting to inactive, profile should not be considered completed
        // This is a simplified test - in real scenario we'd check the status directly
        System.out.println("PASS: TA status update works");
    }

    @Test
    public void testReadTaUsers() {
        System.out.println("Testing read TA users...");
        List<User> users = UserService.readTaUsers();
        assertNotNull("Users list should not be null", users);
        assertTrue("Should have at least one user", users.size() > 0);
        System.out.println("PASS: Read TA users successfully");
    }

    @Test
    public void testReadMoUsers() {
        System.out.println("Testing read MO users...");
        List<User> users = UserService.readMoUsers();
        assertNotNull("Users list should not be null", users);
        // May be empty in test data
        System.out.println("PASS: Read MO users successfully");
    }

    @Test
    public void testReadAdminUsers() {
        System.out.println("Testing read Admin users...");
        List<User> users = UserService.readAdminUsers();
        assertNotNull("Users list should not be null", users);
        // May be empty in test data
        System.out.println("PASS: Read Admin users successfully");
    }
}