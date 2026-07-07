// 1. Lấy các phần tử giao diện từ HTML để tương tác
const currentTimeElement = document.getElementById("currentTime"); // Đã sửa lỗi tên biến tại đây
const hourSelect = document.getElementById("hours");
const minuteSelect = document.getElementById("minutes");
const ampmSelect = document.getElementById("ampm");
const setAlarmBtn = document.getElementById("setAlarmBtn");
const clearAlarmBtn = document.getElementById("clearAlarmBtn");
const alarmStatus = document.getElementById("alarmStatus");
const alarmSound = document.getElementById("alarmSound");

let alarmTime = null; // Biến lưu thời gian báo thức đã đặt (Ví dụ: "07:30 AM")
let isAlarmPlaying = false; // Biến kiểm tra xem chuông có đang reo hay không

// 2. Tự động tạo danh sách Giờ (12 đến 01) và Phút (59 đến 00) vào các ô select
for (let i = 12; i > 0; i--) {
    let val = i < 10 ? "0" + i : i;
    let option = `<option value="${val}">${val}</option>`;
    hourSelect.insertAdjacentHTML("beforeend", option);
}

for (let i = 59; i >= 0; i--) {
    let val = i < 10 ? "0" + i : i;
    let option = `<option value="${val}">${val}</option>`;
    minuteSelect.insertAdjacentHTML("beforeend", option);
}

// 3. Cập nhật đồng hồ và kiểm tra báo thức liên tục mỗi giây
setInterval(() => {
    let date = new Date();
    let h = date.getHours();
    let m = date.getMinutes();
    let s = date.getSeconds();
    let ampm = "AM";

    // Chuyển đổi từ định dạng 24 giờ sang 12 giờ (AM/PM)
    if (h >= 12) {
        h = h - 12;
        ampm = "PM";
    }
    h = h == 0 ? 12 : h;

    // Thêm số 0 phía trước nếu số nhỏ hơn 10 (Ví dụ: 7 thành "07")
    h = h < 10 ? "0" + h : h;
    m = m < 10 ? "0" + m : m;
    s = s < 10 ? "0" + s : s;

    // Hiển thị thời gian hiện tại lên màn hình
    let timeString = `${h}:${m}:${s} ${ampm}`;
    currentTimeElement.innerText = timeString;

    // KIỂM TRA BÁO THỨC: Nếu trùng Giờ:Phút, đúng giây "00" và chuông chưa reo
    if (alarmTime === `${h}:${m} ${ampm}` && s === "00" && !isAlarmPlaying) {
        isAlarmPlaying = true;
        alarmSound.play().catch(error => {
            console.log("Trình duyệt chặn tự động phát âm thanh. Cần tương tác trước.");
        });
        alarmStatus.innerText = "⏰ DẬY THÔI NÀO!!! ⏰";
        alarmStatus.style.color = "#e74c3c";
        clearAlarmBtn.innerText = "Dừng Chuông";
    }
}, 1000);

// 4. Xử lý sự kiện khi người dùng nhấn nút "Đặt Báo Thức"
setAlarmBtn.addEventListener("click", () => {
    // Kiểm tra xem người dùng đã chọn đủ thông tin chưa
    if (hourSelect.value === "Hour" || minuteSelect.value === "Minute" || ampmSelect.value === "AM/PM") {
        alert("Vui lòng chọn đầy đủ Giờ, Phút và AM/PM để đặt báo thức!");
        return;
    }

    // Lưu chuỗi thời gian đặt báo thức
    alarmTime = `${hourSelect.value}:${minuteSelect.value} ${ampmSelect.value}`;
    isAlarmPlaying = false;
    
    // Khóa các ô chọn (không cho người dùng sửa khi đang bật báo thức)
    hourSelect.disabled = true;
    minuteSelect.disabled = true;
    ampmSelect.disabled = true;
    
    // Ẩn nút "Đặt Báo Thức", hiện nút "Tắt Báo Thức"
    setAlarmBtn.style.display = "none";
    clearAlarmBtn.style.display = "block";
    clearAlarmBtn.innerText = "Tắt Báo Thức";
    
    // Cập nhật trạng thái chữ màu xanh
    alarmStatus.innerText = `Đã hẹn giờ lúc: ${alarmTime}`;
    alarmStatus.style.color = "#2ecc71";
});

// 5. Xử lý sự kiện khi người dùng nhấn nút "Tắt Báo Thức / Dừng Chuông"
clearAlarmBtn.addEventListener("click", () => {
    alarmTime = null; // Xóa thời gian đã đặt
    isAlarmPlaying = false;
    alarmSound.pause(); // Tắt nhạc chuông
    alarmSound.currentTime = 0; // Tua nhạc chuông về giây đầu tiên

    // Mở khóa lại các ô chọn
    hourSelect.disabled = false;
    minuteSelect.disabled = false;
    ampmSelect.disabled = false;
    
    // Hiện lại nút "Đặt Báo Thức", ẩn nút "Tắt Báo Thức"
    setAlarmBtn.style.display = "block";
    clearAlarmBtn.style.display = "none";
    
    // Reset dòng chữ trạng thái và các ô chọn về mặc định
    alarmStatus.innerText = "Chưa đặt báo thức";
    alarmStatus.style.color = "#7f8c8d";
    
    hourSelect.value = "Hour";
    minuteSelect.value = "Minute";
    ampmSelect.value = "AM/PM";
});