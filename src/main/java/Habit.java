import java.sql.Date;

public class Habit {
    private int id;
    private String name;
    private String description;
    private String type;
    private String cue;
    private String craving;
    private String response;
    private String reward;
    private Date createdAt;
    
    // Constructors
    public Habit() {}
    
    public Habit(String name, String description, String type, String cue, 
                String craving, String response, String reward) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.cue = cue;
        this.craving = craving;
        this.response = response;
        this.reward = reward;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getCue() {
        return cue;
    }
    
    public void setCue(String cue) {
        this.cue = cue;
    }
    
    public String getCraving() {
        return craving;
    }
    
    public void setCraving(String craving) {
        this.craving = craving;
    }
    
    public String getResponse() {
        return response;
    }
    
    public void setResponse(String response) {
        this.response = response;
    }
    
    public String getReward() {
        return reward;
    }
    
    public void setReward(String reward) {
        this.reward = reward;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    @Override
    public String toString() {
        return "Habit{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", type='" + type + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }
}