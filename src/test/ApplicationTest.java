import com.bupt.recruit.model.Application;
import com.bupt.recruit.model.Position;
import com.bupt.recruit.model.Resume;
import com.bupt.recruit.service.ApplicationService;
import com.bupt.recruit.service.PositionService;
import com.bupt.recruit.service.ResumeService;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class ApplicationTest {

    @Test
    public void testCreateApplication() {
        System.out.println("Testing application creation...");
        Application app = new Application("APP_TEST_001", "20210001", "P001", "pending", "", "2024-01-01");
        ApplicationService.saveApplication(app);

        Application retrieved = ApplicationService.findApplicationById("APP_TEST_001");
        assertNotNull("Application should be created", retrieved);
        assertEquals("Student ID should match", "20210001", retrieved.getStudentId());
        System.out.println("PASS: Application created successfully");
    }

    @Test
    public void testUpdateApplicationStatus() {
        System.out.println("Testing application status update...");
        ApplicationService.updateApplicationStatus("APP_TEST_001", "approved", "Good fit for the position");

        Application updated = ApplicationService.findApplicationById("APP_TEST_001");
        assertNotNull("Application should exist", updated);
        assertEquals("Status should be updated", "approved", updated.getStatus());
        assertEquals("Reason should be updated", "Good fit for the position", updated.getReason());
        System.out.println("PASS: Application status updated successfully");
    }

    @Test
    public void testGetApplicationsByStudent() {
        System.out.println("Testing get applications by student...");
        List<Application> apps = ApplicationService.getApplicationsByStudentId("2023001");
        assertNotNull("Applications list should not be null", apps);
        // Note: May be empty if no applications exist for this student
        System.out.println("PASS: Retrieved applications by student successfully");
    }

    @Test
    public void testDeleteApplication() {
        System.out.println("Testing application deletion...");
        ApplicationService.deleteApplication("APP_TEST_001");

        Application deleted = ApplicationService.findApplicationById("APP_TEST_001");
        assertNull("Application should be deleted", deleted);
        System.out.println("PASS: Application deleted successfully");
    }

    @Test
    public void testResumeSnapshot() {
        System.out.println("Testing resume snapshot for application...");
        Resume resume = new Resume("20210001", "Test Student", "test@example.com", "Computer Science", "3", "3.8", "6.5", "Java, Python", "CS101, CS102", "Dean's List", "Web App Project", "Internship", "Leadership", "20");

        ResumeService.saveApplicationResume("APP_TEST_RESUME", resume);
        Resume retrieved = ResumeService.getApplicationResume("APP_TEST_RESUME");

        assertNotNull("Resume snapshot should exist", retrieved);
        assertEquals("Name should match", "Test Student", retrieved.getName());
        assertEquals("Email should match", "test@example.com", retrieved.getEmail());

        ResumeService.deleteApplicationResume("APP_TEST_RESUME");
        System.out.println("PASS: Resume snapshot functionality works");
    }
}