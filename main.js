function updateCountdown() {
    // Target date: January 17, 2026
    const targetDate = new Date('January 17, 2026 00:00:00').getTime();
    
    function refresh() {
        const now = new Date().getTime();
        const distance = targetDate - now;

        // Calculate days (rounding up to include the current day)
        const days = Math.ceil(distance / (1000 * 60 * 60 * 24));
        
        const daysElement = document.getElementById('days-number');
        
        if (distance < 0) {
            daysElement.innerText = "0";
            document.querySelector('.label').innerText = "ES HOY!";
            return;
        }

        // Animated number update if it changed
        if (daysElement.innerText != days) {
            daysElement.style.transform = 'scale(1.2)';
            daysElement.innerText = days < 10 ? '0' + days : days;
            setTimeout(() => {
                daysElement.style.transform = 'scale(1)';
            }, 200);
        }
    }

    refresh();
    // Update every hour to be efficient
    setInterval(refresh, 3600000);
}

document.addEventListener('DOMContentLoaded', updateCountdown);

// Add a touch of interactivity - change jitter on touch
document.addEventListener('touchstart', () => {
    document.getElementById('days-number').style.animationDuration = '0.05s';
});

document.addEventListener('touchend', () => {
    document.getElementById('days-number').style.animationDuration = '0.2s';
});
