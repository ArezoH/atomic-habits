document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('streaks-list')) {
        loadStreaks();
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
        `;
        list.appendChild(item);
    });
    
    container.appendChild(list);
}