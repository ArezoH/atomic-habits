document.addEventListener('DOMContentLoaded', function() {
    // Load stacks on index page
    if (document.getElementById('stacks-list')) {
        loadStacks();
    }

    // Handle stack form submission if on create-stack.html
    const stackForm = document.getElementById('stack-form');
    if (stackForm) {
        loadHabitsForStackForm();
        stackForm.addEventListener('submit', function(e) {
            e.preventDefault();
            createStack();
        });
    }
});

function loadStacks() {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', 'http://localhost:8081/HabitManagement/habits?action=stacks', true);
    
    xhr.onload = function() {
        if (this.status === 200) {
            const stacks = JSON.parse(this.responseText);
            renderStacks(stacks);
        } else {
            console.error('Error loading habit stacks');
        }
    };
    
    xhr.send();
}

function renderStacks(stacks) {
    const container = document.getElementById('stacks-list');
    container.innerHTML = '';
    
    if (stacks.length === 0) {
        container.innerHTML = '<p>No habit stacks created yet.</p>';
        return;
    }
    
    const list = document.createElement('ul');
    stacks.forEach(stack => {
        const item = document.createElement('li');
        item.innerHTML = `
            <p><strong>After I:</strong> ${stack.existing}</p>
            <p><strong>I will:</strong> ${stack.new}</p>
        `;
        list.appendChild(item);
    });
    
    container.appendChild(list);
}

function loadHabitsForStackForm() {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', 'http://localhost:8081/HabitManagement/habits?action=list', true);
    
    xhr.onload = function() {
        if (this.status === 200) {
            const habits = JSON.parse(this.responseText);
            populateHabitDropdowns(habits);
        }
    };
    
    xhr.send();
}

function populateHabitDropdowns(habits) {
    const baseSelect = document.getElementById('base_habit_id');
    const newSelect = document.getElementById('new_habit_id');
    
    // Clear existing options
    baseSelect.innerHTML = '';
    newSelect.innerHTML = '';
    
    // Add default option
    const defaultOption = document.createElement('option');
    defaultOption.value = '';
    defaultOption.textContent = '-- Select a habit --';
    baseSelect.appendChild(defaultOption.cloneNode(true));
    newSelect.appendChild(defaultOption.cloneNode(true));
    
    // Add habits to both dropdowns
    habits.forEach(habit => {
        const option = document.createElement('option');
        option.value = habit.id;
        option.textContent = habit.name;
        
        baseSelect.appendChild(option.cloneNode(true));
        newSelect.appendChild(option.cloneNode(true));
    });
}


function createStack() {
    const form = document.getElementById('stack-form');
    const formData = new URLSearchParams();
    formData.append('base_habit_id', form.querySelector('[name="base_habit_id"]').value);
    formData.append('new_habit_id', form.querySelector('[name="new_habit_id"]').value);
    
    console.log('Sending data:', formData.toString());

    const xhr = new XMLHttpRequest();
    xhr.open('POST', 'http://localhost:8081/HabitManagement/habits?action=stack', true);
    
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    
    xhr.onload = function() {
        if (this.status === 200) {
            window.location.href = 'index.html?success=habit_stack_created';
        } else {
            alert('error: ',this.responseText);
        }
    };
    
    xhr.send(formData.toString());
}