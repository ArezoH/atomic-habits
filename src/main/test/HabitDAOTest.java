import org.junit.jupiter.api.*;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

public class HabitDAOTest {
    private static HabitDAO habitDao;
    
    @BeforeAll
    static void setup() throws SQLException {
        habitDao = new HabitDAO();
        // Setup test database connection
        habitDao.jdbcURL = "jdbc:mysql://localhost:3308/atomic_habits_test";
    }
    
    @Test
    void testInsertAndDeleteHabit() throws SQLException {
        Habit testHabit = new Habit("Test Habit", "Test Desc", "good", 
                                   "test", "test", "test", "test");
        
        // Test insert
        boolean inserted = habitDao.insertHabit(testHabit);
        assertTrue(inserted);
        
        // Test delete (get the last inserted ID)
        List<Habit> habits = habitDao.listAllHabits();
        int testId = habits.get(0).getId();
        boolean deleted = habitDao.deleteHabit(testId);
        assertTrue(deleted);
    }
    
    @Test
    void testLogHabitCompletion() throws SQLException {
        // Similar structure for other tests
    }
}