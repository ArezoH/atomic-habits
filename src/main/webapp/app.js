// Global variables
let habits = [];

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    loadHabits();
    setupEventListeners();
});

// Set up event listeners
function setupEventListeners() {
    document.getElementById('habitForm').addEventListener('submit', function(e) {
        e.preventDefault();
        createHabit(new FormData(this));
    });
}

// Load all habits from the server
function loadHabits() {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (this.readyState === 4 && this.status === 200) {
            habits = JSON.parse(this.responseText);
            renderHabits();
            renderTodayHabits();
            updateStats();
        }
    };
    xhr.open('GET', 'http://localhost:8081/HabitManagement/habits?action=list', true);
    xhr.send();
}

// Create a new habit
function createHabit(formData) {
    var xhr = new XMLHttpRequest();
    var params = new URLSearchParams();
    
    // Add all form data to params
    for (var pair of formData.entries()) {
        params.append(pair[0], pair[1]);
    }
    
    xhr.onreadystatechange = function() {
        if (this.readyState === 4 && this.status === 200) {
            // Reset form
            document.getElementById('habitForm').reset();
            // Reload habits
            loadHabits();
        }
    };
    
    xhr.open('POST', 'http://localhost:8081/HabitManagement/habits', true);
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send(params.toString());
}

// Log habit completion
function logHabitCompletion(habitId) {
    var xhr = new XMLHttpRequest();
    var params = 'action=log&habit_id=' + habitId;
    
    xhr.onreadystatechange = function() {
        if (this.readyState === 4 && this.status === 200) {
            loadHabits();
        }
    };
    
    xhr.open('POST', 'http://localhost:8081/HabitManagement/habits', true);
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send(params);
}

// Render all habits
function renderHabits() {
    var container = document.getElementById('habitList');
    container.innerHTML = '';
    
    if (habits.length === 0) {
        container.innerHTML = '<div class="empty-state">No habits yet. Create your first habit above!</div>';
        return;
    }
    
    habits.forEach(function(habit) {
        var habitCard = document.createElement('div');
        habitCard.className = 'habit-card ' + (habit.type === 'bad' ? 'bad-habit' : '');
        habitCard.innerHTML = `
            <h3>${habit.name}</h3>
            <p><strong>Cue:</strong> ${habit.cue || 'Not specified'}</p>
            <p><strong>Response:</strong> ${habit.response || 'Not specified'}</p>
            <div class="habit-actions">
                <button class="btn btn-primary" onclick="logHabitCompletion(${habit.id})">
                    Mark as Complete
                </button>
            </div>
        `;
        container.appendChild(habitCard);
    });
}

// Render today's habits
function renderTodayHabits() {
    var container = document.getElementById('todayHabits');
    container.innerHTML = '';
    
    if (habits.length === 0) {
        container.innerHTML = '<div class="empty-state">No habits to complete today</div>';
        return;
    }
    
    // In a real app, you would filter for today's habits
    habits.slice(0, 3).forEach(function(habit) { // Show first 3 for demo
        var habitItem = document.createElement('div');
        habitItem.className = 'habit-card ' + (habit.type === 'bad' ? 'bad-habit' : '');
        habitItem.innerHTML = `
            <h3>${habit.name}</h3>
            <p>${habit.response || 'Complete this habit today'}</p>
            <div class="habit-actions">
                <button class="btn btn-primary" onclick="logHabitCompletion(${habit.id})">
                    ✓ Done
                </button>
            </div>
        `;
        container.appendChild(habitItem);
    });
}

// Update statistics
function updateStats() {
    document.getElementById('habitsTracked').textContent = habits.length;
    
    // Demo data - in a real app you would calculate these from actual completion data
    document.getElementById('currentStreak').textContent = habits.length > 0 ? '7 days' : '0 days';
    document.getElementById('completionRate').textContent = habits.length > 0 ? '75%' : '0%';
}

// scroll
document.addEventListener('DOMContentLoaded', function() {
  const scrollToFormBtn = document.getElementById('scrollToFormBtn');
  
  scrollToFormBtn.addEventListener('click', function() {
    const formSection = document.getElementById('habitFormSection');
    formSection.scrollIntoView({ behavior: 'smooth' });
    
    // Optional: Focus on the first input field for better UX
    document.getElementById('habitName').focus();
  });
});