import com.bupt.recruit.model.Feedback;
import com.bupt.recruit.service.FeedbackService;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class FeedbackTest {

    @Test
    public void testCreateFeedback() {
        System.out.println("Testing feedback creation...");
        Feedback feedback = new Feedback("FB_TEST_001", "ta", "20210001", "System Feedback", "The application process is smooth", "pending", "", "2024-01-01");
        FeedbackService.saveFeedback(feedback);

        Feedback retrieved = FeedbackService.findFeedbackById("FB_TEST_001");
        assertNotNull("Feedback should be created", retrieved);
        assertEquals("Title should match", "System Feedback", retrieved.getTitle());
        assertEquals("Content should match", "The application process is smooth", retrieved.getContent());
        System.out.println("PASS: Feedback created successfully");
    }

    @Test
    public void testUpdateFeedback() {
        System.out.println("Testing feedback update...");
        FeedbackService.updateFeedback("FB_TEST_001", "resolved", "Thank you for your feedback!");

        Feedback updated = FeedbackService.findFeedbackById("FB_TEST_001");
        assertNotNull("Feedback should exist", updated);
        assertEquals("Status should be updated", "resolved", updated.getStatus());
        assertEquals("Reply should be updated", "Thank you for your feedback!", updated.getReply());
        System.out.println("PASS: Feedback updated successfully");
    }

    @Test
    public void testGetFeedbacksByUser() {
        System.out.println("Testing get feedbacks by user...");
        List<Feedback> feedbacks = FeedbackService.getFeedbacksByUserId("20210001");
        assertNotNull("Feedbacks list should not be null", feedbacks);
        assertTrue("Should have at least one feedback", feedbacks.size() > 0);
        System.out.println("PASS: Retrieved feedbacks by user successfully");
    }

    @Test
    public void testGetFeedbacksByStatus() {
        System.out.println("Testing get feedbacks by status...");
        List<Feedback> feedbacks = FeedbackService.getFeedbacksByStatus("resolved");
        assertNotNull("Feedbacks list should not be null", feedbacks);
        // Note: May be empty if no resolved feedbacks exist
        System.out.println("PASS: Retrieved feedbacks by status successfully");
    }
}
