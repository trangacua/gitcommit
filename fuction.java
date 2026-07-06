import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmClockApp extends JFrame {
    private JLabel currentTimeLabel;
    private JComboBox<String> hourSelect;
    private JComboBox<String> minuteSelect;
    private JComboBox<String> ampmSelect;
    private JButton setAlarmBtn;
    private JButton clearAlarmBtn;
    private JLabel alarmStatus;
    private String alarmTime = null;
    private boolean isAlarmPlaying = false;

    public AlarmClockApp() {
        setTitle("Ứng dụng Báo Thức");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));
        setLocationRelativeTo(null);

        // --- Khởi tạo các thành phần ---
        currentTimeLabel = new JLabel("00:00:00 AM");
        currentTimeLabel.setFont(new Font("Arial", Font.BOLD, 24));

        hourSelect = new JComboBox<>();
        hourSelect.addItem("Hour");
        // 1. Tự động tạo danh sách Giờ từ 12 đến 01
        for (int i = 12; i > 0; i--) {
            String val = (i < 10) ? "0" + i : String.valueOf(i);
            hourSelect.addItem(val);
        }

        minuteSelect = new JComboBox<>();
        minuteSelect.addItem("Minute");
        // 2. Tự động tạo danh sách Phút từ 59 đến 00
        for (int i = 59; i >= 0; i--) {
            String val = (i < 10) ? "0" + i : String.valueOf(i);
            minuteSelect.addItem(val);
        }

        ampmSelect = new JComboBox<>(new String[]{"AM/PM", "AM", "PM"});

        setAlarmBtn = new JButton("Đặt Báo Thức");
        clearAlarmBtn = new JButton("Tắt Báo Thức");
        clearAlarmBtn.setVisible(false); // Ban đầu ẩn nút Tắt tương tự style.display = "none"

        alarmStatus = new JLabel("Chưa đặt báo thức");
        alarmStatus.setForeground(Color.GRAY);

        // Thêm các thành phần vào giao diện
        add(currentTimeLabel);
        add(new JLabel("\n")); // Xuống dòng giả lập
        add(hourSelect);
        add(minuteSelect);
        add(ampmSelect);
        add(setAlarmBtn);
        add(clearAlarmBtn);
        add(alarmStatus);

        // --- 3. Cập nhật đồng hồ hệ thống và kiểm tra báo thức mỗi giây ---
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int h = calendar.get(Calendar.HOUR); // Định dạng 12h tự động
                int m = calendar.get(Calendar.MINUTE);
                int s = calendar.get(Calendar.SECOND);
                String ampm = (calendar.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM";

                // Định dạng góc 12h (nếu bằng 0 thì chuyển thành 12)
                if (h == 0) h = 12;

                // Thêm số 0 phía trước
                String hStr = (h < 10) ? "0" + h : String.valueOf(h);
                String mStr = (m < 10) ? "0" + m : String.valueOf(m);
                String sStr = (s < 10) ? "0" + s : String.valueOf(s);

                // Hiển thị thời gian hiện tại
                String timeString = hStr + ":" + mStr + ":" + sStr + " " + ampm;
                currentTimeLabel.setText(timeString);

                // Kiểm tra báo thức
                if (alarmTime != null && alarmTime.equals(hStr + ":" + mStr + " " + ampm) && sStr.equals("00") && !isAlarmPlaying) {
                    isAlarmPlaying = true;
                    
                    // Trong Java Swing, để phát âm thanh bíp đơn giản từ hệ thống:
                    Toolkit.getDefaultToolkit().beep(); 
                    
                    alarmStatus.setText("⏰ DẬY THÔI NÀO!!! ⏰");
                    alarmStatus.setForeground(Color.RED);
                    clearAlarmBtn.setText("Dừng Chuông");
                }
            }
        });
        timer.start();

        // --- 4. Xử lý sự kiện khi bấm nút "Đặt Báo Thức" ---
        setAlarmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Kiểm tra xem người dùng đã chọn đủ thông tin chưa
                if (hourSelect.getSelectedIndex() == 0 || minuteSelect.getSelectedIndex() == 0 || ampmSelect.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn đầy đủ Giờ, Phút và AM/PM để đặt báo thức!");
                    return;
                }

                // Lưu cấu hình báo thức
                alarmTime = hourSelect.getSelectedItem() + ":" + minuteSelect.getSelectedItem() + " " + ampmSelect.getSelectedItem();
                isAlarmPlaying = false;

                // Khóa các ô chọn và thay đổi trạng thái nút bấm
                hourSelect.setEnabled(false);
                minuteSelect.setEnabled(false);
                ampmSelect.setEnabled(false);
                setAlarmBtn.setVisible(false);
                clearAlarmBtn.setVisible(true);
                clearAlarmBtn.setText("Tắt Báo Thức");

                alarmStatus.setText("Đã hẹn giờ lúc: " + alarmTime);
                alarmStatus.setForeground(new Color(46, 204, 113)); // Màu xanh lục lá
            }
        });

        // --- 5. Xử lý sự kiện khi bấm nút "Tắt Báo Thức" / "Dừng Chuông" ---
        clearAlarmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Reset các biến trạng thái
                alarmTime = null;
                isAlarmPlaying = false;

                // Mở khóa các ô chọn và đưa về giá trị mặc định
                hourSelect.setEnabled(true);
                minuteSelect.setEnabled(true);
                ampmSelect.setEnabled(true);
                hourSelect.setSelectedIndex(0);
                minuteSelect.setSelectedIndex(0);
                ampmSelect.setSelectedIndex(0);

                // Thay đổi lại giao diện nút bấm ban đầu
                setAlarmBtn.setVisible(true);
                clearAlarmBtn.setVisible(false);

                alarmStatus.setText("Chưa đặt báo thức");
                alarmStatus.setForeground(Color.GRAY);
            }
        });
    }

    public static void main(String[] args) {
        // Chạy ứng dụng trên Event Dispatch Thread để đảm bảo an toàn giao diện
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AlarmClockApp().setVisible(true);
            }
        });
    }
}
