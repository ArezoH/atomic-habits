import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HabitDAO {
    private String jdbcURL;
    private String jdbcUsername;
    private String jdbcPassword;
    private Connection jdbcConnection;
    
    public HabitDAO() {
        this.jdbcURL = "jdbc:mysql://localhost:3308/atomic_habits_tracker";
        this.jdbcUsername = "root";
        this.jdbcPassword = "";
    }
    
    protected void connect() throws SQLException {
        if (jdbcConnection == null || jdbcConnection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException(e);
            }
            jdbcConnection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        }
    }
    
    protected void disconnect() throws SQLException {
        if (jdbcConnection != null && !jdbcConnection.isClosed()) {
            jdbcConnection.close();
        }
    }
    
    // Habit CRUD operations
    public boolean insertHabit(Habit habit) throws SQLException {
        String sql = "INSERT INTO habits (name, description, habit_type, cue, craving, response, reward) VALUES (?, ?, ?, ?, ?, ?, ?)";
        connect();
        
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setString(1, habit.getName());
        statement.setString(2, habit.getDescription());
        statement.setString(3, habit.getType());
        statement.setString(4, habit.getCue());
        statement.setString(5, habit.getCraving());
        statement.setString(6, habit.getResponse());
        statement.setString(7, habit.getReward());
        
        boolean rowInserted = statement.executeUpdate() > 0;
        statement.close();
        disconnect();
        return rowInserted;
    }
    
    public List<Habit> listAllHabits() throws SQLException {
        List<Habit> listHabit = new ArrayList<>();
        String sql = "SELECT * FROM habits ORDER BY created_at DESC";
        
        connect();
        
        Statement statement = jdbcConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String description = resultSet.getString("description");
            String type = resultSet.getString("habit_type");
            String cue = resultSet.getString("cue");
            String craving = resultSet.getString("craving");
            String response = resultSet.getString("response");
            String reward = resultSet.getString("reward");
            Date createdAt = resultSet.getDate("created_at");
            
            Habit habit = new Habit(name, description, type, cue, craving, response, reward);
            habit.setId(id);
            habit.setCreatedAt(createdAt);
            
            listHabit.add(habit);
        }
        
        resultSet.close();
        statement.close();
        disconnect();
        
        return listHabit;
    }
    
    // Improved habit logging with notes
    public boolean logHabitCompletion(int habitId, String notes) throws SQLException {
        String sql = "INSERT INTO habit_logs (habit_id, completion_date, notes) VALUES (?, CURDATE(), ?)";
        connect();
        
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setInt(1, habitId);
        statement.setString(2, notes);
        
        boolean rowInserted = statement.executeUpdate() > 0;
        statement.close();
        disconnect();
        return rowInserted;
    }
    
    // Improved habit stacking using IDs instead of names
    public boolean createHabitStack(int baseHabitId, int newHabitId) throws SQLException {
        String sql = "INSERT INTO habit_stacks (existing_habit, new_habit) VALUES (?, ?)";
        connect();
        
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setInt(1, baseHabitId);
        statement.setInt(2, newHabitId);
        
        boolean rowInserted = statement.executeUpdate() > 0;
        statement.close();
        disconnect();
        return rowInserted;
    }
    
    // Improved streak tracking with more detailed information
    public List<Map<String, Object>> getHabitStreaks() throws SQLException {
        List<Map<String, Object>> streaks = new ArrayList<>();
        String sql = "SELECT h.id, h.name, COUNT(*) as streak, MAX(l.completion_date) as last_date " +
                     "FROM habit_logs l " +
                     "JOIN habits h ON l.habit_id = h.id " +
                     "GROUP BY h.id, h.name " +
                     "ORDER BY streak DESC";
        
        connect();
        
        Statement statement = jdbcConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        
        while (resultSet.next()) {
            Map<String, Object> streakInfo = new HashMap<>();
            streakInfo.put("id", resultSet.getInt("id"));
            streakInfo.put("name", resultSet.getString("name"));
            streakInfo.put("streak", resultSet.getInt("streak"));
            streakInfo.put("lastDate", resultSet.getDate("last_date"));
            streaks.add(streakInfo);
        }
        
        resultSet.close();
        statement.close();
        disconnect();
        
        return streaks;
    }
    
    // New method for environment design
    public boolean addEnvironmentDesign(int habitId, String designType, String description) throws SQLException {
        String sql = "INSERT INTO environment_designs (habit_id, design_type, description) VALUES (?, ?, ?)";
        connect();
        
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setInt(1, habitId);
        statement.setString(2, designType);
        statement.setString(3, description);
        
        boolean rowInserted = statement.executeUpdate() > 0;
        statement.close();
        disconnect();
        return rowInserted;
    }
    
    // New method for getting habit stacks with names
    public List<Map<String, String>> getHabitStacks() throws SQLException {
        List<Map<String, String>> stacks = new ArrayList<>();
        String sql = "SELECT h1.name as existing_habit_name, h2.name as new_habit_name " +
                     "FROM habit_stacks hs " +
                     "JOIN habits h1 ON hs.existing_habit = h1.id " +
                     "JOIN habits h2 ON hs.new_habit = h2.id";
        
        connect();
        Statement statement = jdbcConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        
        while (resultSet.next()) {
            Map<String, String> stack = new HashMap<>();
            stack.put("existing", resultSet.getString("existing_habit_name"));
            stack.put("new", resultSet.getString("new_habit_name"));
            stacks.add(stack);
        }
        
        resultSet.close();
        statement.close();
        disconnect();
        return stacks;
    }
    
    public boolean deleteHabit(int id) throws SQLException {
        String sql = "DELETE FROM habits WHERE id = ?";
        
        connect();
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setInt(1, id);
        
        boolean rowDeleted = statement.executeUpdate() > 0;
        statement.close();
        disconnect();
        return rowDeleted;
    }
}