let alarmTime = null;
let isAlarmPlaying = false; 

setInterval(() => {

    if (alarmTime === `${h}:${m} ${ampm}` && !isAlarmPlaying) {
        alarmSound.play().catch(error => {
            console.log("Trình duyệt chặn tự động phát âm thanh.");
        });
        alarmStatus.innerText = "⏰ DẬY THÔI NÀO!!! ⏰";
        alarmStatus.style.color = "#e74c3c";
        isAlarmPlaying = true; 
    }
}, 1000);

clearAlarmBtn.addEventListener("click", () => {
    alarmTime = null;
    isAlarmPlaying = false; 
    alarmSound.pause();
});