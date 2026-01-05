function updateCountdown() {
    // Target date: January 17, 2026 at 9:00 PM (21:00)
    const targetDate = new Date('January 17, 2026 21:00:00').getTime();

    const units = {
        days: document.getElementById('days'),
        hours: document.getElementById('hours'),
        minutes: document.getElementById('minutes'),
        seconds: document.getElementById('seconds')
    };

    function refresh() {
        const now = new Date().getTime();
        const distance = targetDate - now;

        if (distance < 0) {
            Object.values(units).forEach(el => el.innerText = "00");
            return;
        }

        const d = Math.floor(distance / (1000 * 60 * 60 * 24));
        const h = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const m = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const s = Math.floor((distance % (1000 * 60)) / 1000);

        const values = {
            days: d < 10 ? '0' + d : d,
            hours: h < 10 ? '0' + h : h,
            minutes: m < 10 ? '0' + m : m,
            seconds: s < 10 ? '0' + s : s
        };

        for (const key in units) {
            if (units[key].innerText !== String(values[key])) {
                units[key].innerText = values[key];
                // Subtle pop effect on change
                units[key].style.transform = 'scale(1.1)';
                setTimeout(() => {
                    units[key].style.transform = 'scale(1)';
                }, 100);
            }
        }
    }

    refresh();
    setInterval(refresh, 1000);
}

document.addEventListener('DOMContentLoaded', updateCountdown);

// Add a touch of interactivity - change jitter on touch for all units
const setJitter = (duration) => {
    document.querySelectorAll('.countdown-header h1').forEach(h1 => {
        h1.style.animationDuration = duration;
    });
};

document.addEventListener('touchstart', () => setJitter('0.05s'));
document.addEventListener('touchend', () => setJitter('0.15s'));
