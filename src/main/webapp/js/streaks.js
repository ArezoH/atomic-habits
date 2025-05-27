document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('streaks-list')) {
        loadStreaks();
    }
	if (document.getElementById('habit_id')) {
		loadHabits1()
	   }
	// Handle form submission
	   const logForm = document.getElementById('log-form');
	   if (logForm) {
	       logForm.addEventListener('submit', function(e) {
	           e.preventDefault();
	           submitHabitLog();
	       });
	   }
});

function loadStreaks() {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', 'http://localhost:8081/HabitManagement/habits?action=streaks', true);
    
    xhr.onload = function() {
        if (this.status === 200) {
            const streaks = JSON.parse(this.responseText);
            renderStreaks(streaks);
        } else {
            console.error('Error loading streaks');
        }
    };
    
    xhr.send();
}

function loadHabits1() {
       fetch('http://localhost:8081/HabitManagement/habits?action=list')
           .then(response => response.json())
           .then(habits => {
               const select = document.getElementById('habit_id');
               select.innerHTML = '<option value="">-- Select a habit --</option>';
               
               habits.forEach(habit => {
                   const option = document.createElement('option');
                   option.value = habit.id;
                   option.textContent = habit.name;
                   select.appendChild(option);
               });
           })
           .catch(error => console.error('Error loading habits:', error));
   }

function renderStreaks(streaks) {
    const container = document.getElementById('streaks-list');
    container.innerHTML = '';
    
    if (streaks.length === 0) {
        container.innerHTML = '<p>No streaks yet. Start logging habits!</p>';
        return;
    }
    
    const list = document.createElement('ul');
    streaks.forEach(streak => {
        const item = document.createElement('li');
        item.innerHTML = `
            <h3>${streak.name}</h3>
            <p><strong>Current Streak:</strong> ${streak.streak} days</p>
            <p><strong>Last Completed:</strong> ${streak.lastDate}</p>
			<button onclick="deleteHabit(${streak.id})">Delete</button>
        `;
        list.appendChild(item);
    });
    
    container.appendChild(list);
}

// Submit habit log
  function submitHabitLog() {
      const form = document.getElementById('log-form');
      const habitId = form.querySelector('[name="habit_id"]').value;
      const notes = form.querySelector('[name="notes"]').value;
      
      if (!habitId) {
          alert('Please select a habit');
          return;
      }

      const formData = new URLSearchParams();
      formData.append('habit_id', habitId);
      if (notes) formData.append('notes', notes);
	  const xhr = new XMLHttpRequest();
      xhr.open('POST','http://localhost:8081/HabitManagement/habits?action=log');
        
	  // Set proper header for URLSearchParams
	     xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
     
		 xhr.onload = function() {
		        if (this.status === 201) {
		            window.location.href = 'index.html?success=habit_created';
		        } else {
		            alert('success: ');
		        }
		    };
		    
		    xhr.send(formData.toString());
     
  }