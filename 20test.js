// CẤU HÌNH CHUNG (chạy 1 lần trước tất cả test)
const fs = require('fs');
const path = require('path');
const { JSDOM } = require('jsdom');

// Đọc toàn bộ code HTML bạn cung cấp
const htmlContent = fs.readFileSync(path.resolve(__dirname, 'bao-thuc.html'), 'utf8');

let dom;
let window;
let document;

// Trước MỖI test: reset DOM, nạp lại code, giả lập thời gian
beforeEach(() => {
  // 1. Tạo DOM ảo từ code HTML của bạn
  dom = new JSDOM(htmlContent, {
    runScripts: 'dangerously', // Cho phép chạy <script> bên trong HTML
    pretendToBeVisual: true
  });
  window = dom.window;
  document = window.document;

  // 2. Giả lập bộ đếm thời gian (kiểm soát hoàn toàn setInterval/setTimeout)
  jest.useFakeTimers();
  jest.spyOn(window, 'setInterval').mockImplementation(window.setInterval);

  // 3. Giả lập Audio (không tải file thật từ internet)
  jest.spyOn(window.HTMLMediaElement.prototype, 'play').mockResolvedValue();
  jest.spyOn(window.HTMLMediaElement.prototype, 'pause').mockImplementation();
  Object.defineProperty(window.HTMLMediaElement.prototype, 'currentTime', {
    writable: true,
    value: 0
  });

  // 4. Giả lập alert (không hiện cửa sổ thật)
  window.alert = jest.fn();

  // 5. Giả lập console.log (để kiểm tra lỗi âm thanh)
  console.log = jest.fn();
});

// Sau mỗi test: dọn dẹp
afterEach(() => {
  jest.clearAllMocks();
  jest.useRealTimers();
});