import com.bupt.recruit.model.Course;
import com.bupt.recruit.service.CourseService;
import org.junit.Test;
import static org.junit.Assert.*;

public class CourseTest {

    private static final String CREATE_ID = "C_TEST_CREATE_001";
    private static final String EXISTS_ID = "C_TEST_EXISTS_001";
    private static final String UPDATE_ID = "C_TEST_UPDATE_001";
    private static final String DELETE_ID = "C_TEST_DELETE_001";

    @Test
    public void testCreateCourse() {
        System.out.println("Testing course creation...");
        CourseService.deleteCourse(CREATE_ID);

        Course course = new Course(CREATE_ID, "Test Course", "CS101", "MO001", "2024-Fall", "active");
        CourseService.addCourse(course);

        Course retrieved = CourseService.findCourseById(CREATE_ID);
        assertNotNull("Course should be created", retrieved);
        assertEquals("Name should match", "Test Course", retrieved.getName());

        CourseService.deleteCourse(CREATE_ID);
        System.out.println("PASS: Course created successfully");
    }

    @Test
    public void testCourseExists() {
        System.out.println("Testing course existence check...");
        CourseService.deleteCourse(EXISTS_ID);
        CourseService.addCourse(new Course(EXISTS_ID, "Exists Course", "CS102", "MO002", "2024-Spring", "active"));

        boolean exists = CourseService.courseExists(EXISTS_ID);
        assertTrue("Course should exist", exists);

        boolean notExists = CourseService.courseExists("C_NON_EXISTENT");
        assertFalse("Course should not exist", notExists);

        CourseService.deleteCourse(EXISTS_ID);
        System.out.println("PASS: Course existence check works");
    }

    @Test
    public void testUpdateCourse() {
        System.out.println("Testing course update...");
        CourseService.deleteCourse(UPDATE_ID);
        CourseService.addCourse(new Course(UPDATE_ID, "Update Course", "CS103", "MO003", "2024-Fall", "active"));

        Course course = CourseService.findCourseById(UPDATE_ID);
        assertNotNull("Course should exist", course);

        course.setStatus("inactive");
        course.setSemester("2024-Spring");
        CourseService.updateCourse(course);

        Course updated = CourseService.findCourseById(UPDATE_ID);
        assertEquals("Status should be updated", "inactive", updated.getStatus());
        assertEquals("Semester should be updated", "2024-Spring", updated.getSemester());

        CourseService.deleteCourse(UPDATE_ID);
        System.out.println("PASS: Course updated successfully");
    }

    @Test
    public void testDeleteCourse() {
        System.out.println("Testing course deletion...");
        CourseService.deleteCourse(DELETE_ID);
        CourseService.addCourse(new Course(DELETE_ID, "Delete Course", "CS104", "MO004", "2024-Fall", "active"));

        CourseService.deleteCourse(DELETE_ID);
        Course deleted = CourseService.findCourseById(DELETE_ID);
        assertNull("Course should be deleted", deleted);
        System.out.println("PASS: Course deleted successfully");
    }
}
