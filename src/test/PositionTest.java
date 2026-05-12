import com.bupt.recruit.model.Position;
import com.bupt.recruit.service.PositionService;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class PositionTest {

    private static final String CREATE_ID = "P_TEST_CREATE_001";
    private static final String UPDATE_ID = "P_TEST_UPDATE_001";
    private static final String COURSE_ID = "C_TEST_COURSE_001";
    private static final String MO_ID = "MO_TEST_001";

    @Test
    public void testCreatePosition() {
        System.out.println("Testing position creation...");
        PositionService.deletePosition(CREATE_ID);
        Position pos = new Position(CREATE_ID, "Test TA Position", COURSE_ID, "Assist with grading", "open", "2024-12-31", "2", MO_ID, "Good academic record", "3.0", "6.0");
        PositionService.addPosition(pos);

        Position retrieved = PositionService.findPositionById(CREATE_ID);
        assertNotNull("Position should be created", retrieved);
        assertEquals("Title should match", "Test TA Position", retrieved.getTitle());

        PositionService.deletePosition(CREATE_ID);
        System.out.println("PASS: Position created successfully");
    }

    @Test
    public void testUpdatePosition() {
        System.out.println("Testing position update...");
        PositionService.deletePosition(UPDATE_ID);
        PositionService.addPosition(new Position(UPDATE_ID, "Update TA Position", COURSE_ID, "Assist with lab", "open", "2024-12-31", "3", MO_ID, "Strong academic record", "3.2", "6.5"));

        Position pos = PositionService.findPositionById(UPDATE_ID);
        assertNotNull("Position should exist", pos);

        pos.setStatus("closed");
        pos.setVacancies("1");
        PositionService.updatePosition(pos);

        Position updated = PositionService.findPositionById(UPDATE_ID);
        assertNotNull("Updated position should exist", updated);
        assertEquals("Status should be updated", "closed", updated.getStatus());
        assertEquals("Vacancies should be updated", "1", updated.getVacancies());

        PositionService.deletePosition(UPDATE_ID);
        System.out.println("PASS: Position updated successfully");
    }

    @Test
    public void testGetPositionsByCourse() {
        System.out.println("Testing get positions by course...");
        String courseKey = "C_TEST_COURSE_002";
        PositionService.deletePosition("P_TEST_COURSE_002A");
        PositionService.deletePosition("P_TEST_COURSE_002B");
        PositionService.addPosition(new Position("P_TEST_COURSE_002A", "Course Position A", courseKey, "Assist with grading", "open", "2024-12-31", "1", MO_ID, "Good record", "3.0", "6.0"));
        PositionService.addPosition(new Position("P_TEST_COURSE_002B", "Course Position B", courseKey, "Assist with lab", "open", "2024-12-31", "1", MO_ID, "Good record", "3.0", "6.0"));

        List<Position> positions = PositionService.getPositionsByCourseId(courseKey);
        assertNotNull("Positions list should not be null", positions);
        assertTrue("Should have at least two positions", positions.size() >= 2);

        PositionService.deletePosition("P_TEST_COURSE_002A");
        PositionService.deletePosition("P_TEST_COURSE_002B");
        System.out.println("PASS: Retrieved positions by course successfully");
    }

    @Test
    public void testGetPositionsByMO() {
        System.out.println("Testing get positions by MO...");
        String moKey = "MO_TEST_002";
        PositionService.deletePosition("P_TEST_MO_002A");
        PositionService.deletePosition("P_TEST_MO_002B");
        PositionService.addPosition(new Position("P_TEST_MO_002A", "MO Position A", COURSE_ID, "Assist with grading", "open", "2024-12-31", "1", moKey, "Good record", "3.0", "6.0"));
        PositionService.addPosition(new Position("P_TEST_MO_002B", "MO Position B", COURSE_ID, "Assist with lab", "open", "2024-12-31", "1", moKey, "Good record", "3.0", "6.0"));

        List<Position> positions = PositionService.getPositionsByMoId(moKey);
        assertNotNull("Positions list should not be null", positions);
        assertTrue("Should have at least two positions", positions.size() >= 2);

        PositionService.deletePosition("P_TEST_MO_002A");
        PositionService.deletePosition("P_TEST_MO_002B");
        System.out.println("PASS: Retrieved positions by MO successfully");
    }

    @Test
    public void testDeletePosition() {
        System.out.println("Testing position deletion...");
        PositionService.deletePosition("P_TEST_DELETE_001");
        PositionService.addPosition(new Position("P_TEST_DELETE_001", "Delete Position", COURSE_ID, "Assist with grading", "open", "2024-12-31", "1", MO_ID, "Good record", "3.0", "6.0"));

        PositionService.deletePosition("P_TEST_DELETE_001");
        Position deleted = PositionService.findPositionById("P_TEST_DELETE_001");
        assertNull("Position should be deleted", deleted);
        System.out.println("PASS: Position deleted successfully");
    }
}
