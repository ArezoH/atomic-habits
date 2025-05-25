document.addEventListener('DOMContentLoaded', function() {
    // Load habits on index page
    if (document.getElementById('habits-list')) {
        loadHabits();
    }

    // Handle habit form submission
    const habitForm = document.getElementById('habit-form');
    if (habitForm) {
        habitForm.addEventListener('submit', function(e) {
            e.preventDefault();
            createHabit();
        });
    }
});

function loadHabits() {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', 'http://localhost:8081/HabitManagement/habits?action=list', true);
    
    xhr.onload = function() {
        if (this.status === 200) {
            const habits = JSON.parse(this.responseText);
            renderHabits(habits);
        } else {
            console.error('Error loading habits');
        }
    };
    
    xhr.send();
}

function renderHabits(habits) {
    const container = document.getElementById('habits-list');
    container.innerHTML = '';
    
    if (habits.length === 0) {
        container.innerHTML = '<p>No habits found. Create your first habit!</p>';
        return;
    }
    
    const list = document.createElement('ul');
    habits.forEach(habit => {
        const item = document.createElement('li');
        item.innerHTML = `
            <h3>${habit.name}</h3>
            <p>${habit.description}</p>
            <p><strong>Type:</strong> ${habit.type}</p>
            <p><strong>Cue:</strong> ${habit.cue}</p>
            <p><strong>Response:</strong> ${habit.response}</p>
            <p><strong>Reward:</strong> ${habit.reward}</p>
            <small>Created: ${habit.createdAt}</small>
        `;
        list.appendChild(item);
    });
    
    container.appendChild(list);
}

function createHabit() {
    const form = document.getElementById('habit-form');
    const formData = new URLSearchParams(new FormData(form));
    formData.append('action', 'create');
    
    const xhr = new XMLHttpRequest();
    xhr.open('POST', 'http://localhost:8081/HabitManagement/habits', true);
    
    // Set proper header for URLSearchParams
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    
    xhr.onload = function() {
        if (this.status === 201) {
            window.location.href = 'index.html?success=habit_created';
        } else {
            alert('sucess: ' + this.responseText);
        }
    };
    
    xhr.send(formData.toString());
}