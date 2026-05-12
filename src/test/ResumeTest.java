import com.bupt.recruit.model.ApplicationDraft;
import com.bupt.recruit.model.Resume;
import com.bupt.recruit.service.ResumeService;
import org.junit.Test;
import static org.junit.Assert.*;

public class ResumeTest {

    @Test
    public void testSaveAndGetResume() {
        System.out.println("Testing resume save and retrieve...");
        Resume resume = new Resume("99999999", "Test Student", "test@example.com", "Computer Science", "3", "3.8", "6.5", "Java, Python", "CS101, CS102", "Dean's List", "Web App Project", "Internship", "Leadership", "20");

        ResumeService.saveResume(resume);
        Resume retrieved = ResumeService.getResume("99999999");

        assertNotNull("Resume should be retrieved", retrieved);
        assertEquals("Name should match", "Test Student", retrieved.getName());
        assertEquals("Email should match", "test@example.com", retrieved.getEmail());
        assertEquals("Major should match", "Computer Science", retrieved.getMajor());
        assertEquals("GPA should match", "3.8", retrieved.getGpa());
        System.out.println("PASS: Resume save and retrieve works");
    }

    @Test
    public void testResumeCompleteness() {
        System.out.println("Testing resume completeness check...");
        Resume completeResume = new Resume("99999998", "Complete Student", "complete@example.com", "Computer Science", "3", "3.8", "6.5", "Java, Python", "CS101, CS102", "Dean's List", "Web Project", "Internship", "Leadership", "20");
        ResumeService.saveResume(completeResume);
        assertTrue("Complete resume should be reported complete", ResumeService.isResumeComplete("99999998"));

        Resume incompleteResume = new Resume("99999997", "", "incomplete@example.com", "Computer Science", "3", "3.8", "6.5", "", "CS101, CS102", "", "", "", "", "");
        ResumeService.saveResume(incompleteResume);
        assertFalse("Incomplete resume should be reported incomplete", ResumeService.isResumeComplete("99999997"));
        System.out.println("PASS: Resume completeness check works");
    }

    @Test
    public void testApplicationDraft() {
        System.out.println("Testing application draft functionality...");
        Resume resume = new Resume("99999996", "Test Student", "test@example.com", "Computer Science", "3", "3.8", "6.5", "Java, Python", "CS101, CS102", "Dean's List", "Web App Project", "Internship", "Leadership", "20");
        ApplicationDraft draft = new ApplicationDraft("DRAFT_TEST_001", "99999996", "P_TEST_DRAFT_001", "apply", "/path/to/resume.pdf", "2099-01-01T10:00:00", "2099-01-08T10:00:00", resume);

        ResumeService.saveApplicationDraft(draft);
        ApplicationDraft retrieved = ResumeService.findValidDraft("99999996", "P_TEST_DRAFT_001");

        assertNotNull("Draft should be found", retrieved);
        assertEquals("Student ID should match", "99999996", retrieved.getStudentId());
        assertEquals("Job ID should match", "P_TEST_DRAFT_001", retrieved.getJobId());

        ResumeService.deleteApplicationDraft("99999996", "P_TEST_DRAFT_001");
        System.out.println("PASS: Application draft functionality works");
    }
}
