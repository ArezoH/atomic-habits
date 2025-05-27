document.addEventListener('DOMContentLoaded', function() {
    const envForm = document.getElementById('environment-form');
    if (envForm) {
        loadHabitsForEnvironmentForm();
        
        envForm.addEventListener('submit', function(e) {
            e.preventDefault();
            addEnvironmentDesign();
        });
    }
});

function loadHabitsForEnvironmentForm() {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', 'http://localhost:8081/HabitManagement/habits?action=list', true);
    
    xhr.onload = function() {
        if (this.status === 200) {
            try {
                const habits = JSON.parse(this.responseText);
                populateHabitDropdown(habits);
            } catch (e) {
                console.error('Error parsing habits data:', e);
                alert('Error loading habits data');
            }
        } else {
            console.error('Error loading habits:', this.statusText);
            alert('Failed to load habits. Please try again.');
        }
    };
    
    xhr.onerror = function() {
        console.error('Request failed');
        alert('Network error. Please check your connection.');
    };
    
    xhr.send();
}

function populateHabitDropdown(habits) {
    const select = document.getElementById('habit_id');
    if (!select) return;
    
    // Clear existing options
    select.innerHTML = '';
    
    // Add default option
    const defaultOption = document.createElement('option');
    defaultOption.value = '';
    defaultOption.textContent = '-- Select a habit --';
    defaultOption.disabled = true;
    defaultOption.selected = true;
    select.appendChild(defaultOption);
    
    // Add habits to dropdown
    habits.forEach(habit => {
        const option = document.createElement('option');
        option.value = habit.id;
        option.textContent = habit.name;
        select.appendChild(option);
    });
}

function addEnvironmentDesign() {
    const form = document.getElementById('environment-form');
    if (!form) return;
    
    const habitId = form.querySelector('[name="habit_id"]').value;
    if (!habitId || habitId === '') {
        alert('Please select a habit');
        return;
    }
    
    // Using URLSearchParams for more reliable form submission
    const params = new URLSearchParams();
    params.append('action', 'environment');
    params.append('habit_id', habitId);
    
    const designType = form.querySelector('[name="design_type"]').value;
    if (designType) {
        params.append('design_type', designType);
    }
    
    const description = form.querySelector('[name="description"]').value;
    if (description) {
        params.append('description', description);
    }
    
    console.log('Submitting environment design:', params.toString());

    const xhr = new XMLHttpRequest();
    xhr.open('POST', 'http://localhost:8081/HabitManagement/habits', true);
    
    // Set proper content type
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    
    xhr.onload = function() {
        if (this.status === 200) {
            window.location.href = 'index.html?success=environment_design_added';
        } else {
            alert('Error adding environment design: ' + (this.responseText || 'Unknown error'));
        }
    };
    
    
    xhr.send(params.toString());
}