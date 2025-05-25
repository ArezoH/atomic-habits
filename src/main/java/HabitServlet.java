import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet("/habits")
public class HabitServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private HabitDAO habitDao;
    
    public void init() {
        habitDao = new HabitDAO();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String action = request.getParameter("action");
        
        try {
            if (action == null) {
                action = "list";
            }
            
            switch (action) {
                case "list":
                    listHabits(request, response);
                    break;
                case "streaks":
                    getStreaks(request, response);
                    break;
                case "stacks":
                    getHabitStacks(request, response);
                    break;
                default:
                    listHabits(request, response);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String action = request.getParameter("action");
        
        try {
            switch (action) {
                case "create":
                    createHabit(request, response);
                    break;
                case "log":
                    logHabitCompletion(request, response);
                    break;
                case "stack":
                    createHabitStack(request, response);
                    break;
                case "environment":
                    addEnvironmentDesign(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    private void listHabits(HttpServletRequest request, HttpServletResponse response)
    throws SQLException, IOException {
        List<Habit> habits = habitDao.listAllHabits();
        String json = convertHabitsToJson(habits);
        
        sendJsonResponse(response, json);
    }
    
    private void createHabit(HttpServletRequest request, HttpServletResponse response)
    	    throws SQLException, IOException {
    	    
    	    // Debugging - log all received parameters
    	    System.out.println("Received parameters:");
    	    request.getParameterMap().forEach((k, v) -> 
    	        System.out.println(k + " = " + Arrays.toString(v)));
    	    
    	    String name = request.getParameter("name");
    	    if (name == null || name.trim().isEmpty()) {
    	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
    	                         "Habit name is required. Received parameters: " + 
    	                         request.getParameterMap().keySet());
    	        return;
    	    }
    	    
    	    // Rest of your create logic
    	    Habit newHabit = new Habit(
    	        name,
    	        request.getParameter("description"),
    	        request.getParameter("type"),
    	        request.getParameter("cue"),
    	        request.getParameter("craving"),
    	        request.getParameter("response"),
    	        request.getParameter("reward")
    	    );
    	    
    	    boolean success = habitDao.insertHabit(newHabit);
    	    
    	    if (success) {
    	        response.setStatus(HttpServletResponse.SC_CREATED);
    	        response.getWriter().write("Habit created successfully");
    	    } else {
    	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
    	                         "Failed to create habit");
    	    }
    	}
    
    private void logHabitCompletion(HttpServletRequest request, HttpServletResponse response)
    throws SQLException, IOException {
        int habitId = Integer.parseInt(request.getParameter("habit_id"));
        String notes = request.getParameter("notes");
        
        boolean success = habitDao.logHabitCompletion(habitId, notes);
        sendSuccessResponse(response, success, "habit_logged");
    }
    
    private void createHabitStack(HttpServletRequest request, HttpServletResponse response)
    throws SQLException, IOException {
        int baseHabitId = Integer.parseInt(request.getParameter("base_habit_id"));
        int newHabitId = Integer.parseInt(request.getParameter("new_habit_id"));
        
        boolean success = habitDao.createHabitStack(baseHabitId, newHabitId);
        sendSuccessResponse(response, success, "habit_stack_created");
    }
    
    private void addEnvironmentDesign(HttpServletRequest request, HttpServletResponse response)
    throws SQLException, IOException {
        int habitId = Integer.parseInt(request.getParameter("habit_id"));
        String designType = request.getParameter("design_type");
        String description = request.getParameter("description");
        
        boolean success = habitDao.addEnvironmentDesign(habitId, designType, description);
        sendSuccessResponse(response, success, "environment_design_added");
    }
    
    private void getStreaks(HttpServletRequest request, HttpServletResponse response)
    throws SQLException, IOException {
        List<Map<String, Object>> streaks = habitDao.getHabitStreaks();
        String json = convertStreaksToJson(streaks);
        
        sendJsonResponse(response, json);
    }
    
    private void getHabitStacks(HttpServletRequest request, HttpServletResponse response)
    throws SQLException, IOException {
        List<Map<String, String>> stacks = habitDao.getHabitStacks();
        String json = convertStacksToJson(stacks);
        
        sendJsonResponse(response, json);
    }
    
    // Helper methods
    private void sendJsonResponse(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
    
    private void sendSuccessResponse(HttpServletResponse response, boolean success, String successParam) 
    throws IOException {
        if (success) {
            response.sendRedirect("index.html?success=" + successParam);
        } else {
            response.sendRedirect("index.html?error=operation_failed");
        }
    }
    
    // JSON conversion methods
    private String convertHabitsToJson(List<Habit> habits) {
        StringBuilder json = new StringBuilder("[");
        
        for (int i = 0; i < habits.size(); i++) {
            Habit habit = habits.get(i);
            json.append("{")
                .append("\"id\":").append(habit.getId()).append(",")
                .append("\"name\":\"").append(escapeJson(habit.getName())).append("\",")
                .append("\"description\":\"").append(escapeJson(habit.getDescription())).append("\",")
                .append("\"type\":\"").append(escapeJson(habit.getType())).append("\",")
                .append("\"cue\":\"").append(escapeJson(habit.getCue())).append("\",")
                .append("\"craving\":\"").append(escapeJson(habit.getCraving())).append("\",")
                .append("\"response\":\"").append(escapeJson(habit.getResponse())).append("\",")
                .append("\"reward\":\"").append(escapeJson(habit.getReward())).append("\",")
                .append("\"createdAt\":\"").append(habit.getCreatedAt()).append("\"")
                .append("}");
            
            if (i < habits.size() - 1) {
                json.append(",");
            }
        }
        
        return json.append("]").toString();
    }
    
    private String convertStreaksToJson(List<Map<String, Object>> streaks) {
        StringBuilder json = new StringBuilder("[");
        
        for (int i = 0; i < streaks.size(); i++) {
            Map<String, Object> streak = streaks.get(i);
            json.append("{")
                .append("\"id\":").append(streak.get("id")).append(",")
                .append("\"name\":\"").append(escapeJson((String)streak.get("name"))).append("\",")
                .append("\"streak\":").append(streak.get("streak")).append(",")
                .append("\"lastDate\":\"").append(streak.get("lastDate")).append("\"")
                .append("}");
            
            if (i < streaks.size() - 1) {
                json.append(",");
            }
        }
        
        return json.append("]").toString();
    }
    
    private String convertStacksToJson(List<Map<String, String>> stacks) {
        StringBuilder json = new StringBuilder("[");
        
        for (int i = 0; i < stacks.size(); i++) {
            Map<String, String> stack = stacks.get(i);
            json.append("{")
                .append("\"existing\":\"").append(escapeJson(stack.get("existing"))).append("\",")
                .append("\"new\":\"").append(escapeJson(stack.get("new"))).append("\"")
                .append("}");
            
            if (i < stacks.size() - 1) {
                json.append(",");
            }
        }
        
        return json.append("]").toString();
    }
    
    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}