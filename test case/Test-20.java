import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
public class AlarmClockTest {
    static class AlarmClock {
        private String alarmTime = null;
        private boolean isAlarmPlaying = false;

        public String formatTime(int hour24, int minute, int second) {
            String ampm = (hour24 >= 12) ? "PM" : "AM";
            int h = hour24 % 12;
            if (h == 0) h = 12;
            return String.format("%02d:%02d:%02d %s", h, minute, second, ampm);
        }
        public String formatAlarmKey(int hour24, int minute) {
            String ampm = (hour24 >= 12) ? "PM" : "AM";
            int h = hour24 % 12;
            if (h == 0) h = 12;
            return String.format("%02d:%02d %s", h, minute, ampm);
        }
        public boolean setAlarm(String hour, String minute, String ampm) {
            if ("Hour".equals(hour) || "Minute".equals(minute) || "AM/PM".equals(ampm)) {
                return false; 
            }
            this.alarmTime = hour + ":" + minute + " " + ampm;
            this.isAlarmPlaying = false;
            return true;
        }
        public void clearAlarm() {
            this.alarmTime = null;
            this.isAlarmPlaying = false;
        }
        public boolean shouldRing(int hour24, int minute, int second) {
            if (alarmTime == null || isAlarmPlaying) return false;
            String key = formatAlarmKey(hour24, minute);
            return alarmTime.equals(key) && second == 0;
        }
        public void triggerAlarm() {
            this.isAlarmPlaying = true;
        }

        public String getAlarmTime()    { return alarmTime; }
        public boolean isAlarmPlaying() { return isAlarmPlaying; }
    }

    private AlarmClock clock;

    @BeforeEach
    void setUp() {
        clock = new AlarmClock();
    }

    @Test
    @DisplayName("TC01 - Giờ nửa đêm (0h) phải hiển thị 12:00:00 AM")
    void tc01_midnight_formats_as_12AM() {
        assertEquals("12:00:00 AM", clock.formatTime(0, 0, 0));
    }

    @Test
    @DisplayName("TC02 - Giờ trưa (12h) phải hiển thị 12:00:00 PM")
    void tc02_noon_formats_as_12PM() {
        assertEquals("12:00:00 PM", clock.formatTime(12, 0, 0));
    }

    @Test
    @DisplayName("TC03 - 13h00 phải hiển thị 01:00:00 PM")
    void tc03_13h_formats_as_1PM() {
        assertEquals("01:00:00 PM", clock.formatTime(13, 0, 0));
    }

    @Test
    @DisplayName("TC04 - 9h05 phải có định dạng số 0 ở đầu (09:05:07 AM)")
    void tc04_single_digit_padded_with_zero() {
        assertEquals("09:05:07 AM", clock.formatTime(9, 5, 7));
    }

    @Test
    @DisplayName("TC05 - 23h59 phải hiển thị 11:59:59 PM")
    void tc05_end_of_day_formats_correctly() {
        assertEquals("11:59:59 PM", clock.formatTime(23, 59, 59));
    }
    @Test
    @DisplayName("TC06 - Đặt báo thức hợp lệ trả về true và lưu đúng giờ")
    void tc06_valid_alarm_set_successfully() {
        boolean result = clock.setAlarm("07", "30", "AM");
        assertTrue(result);
        assertEquals("07:30 AM", clock.getAlarmTime());
    }

    @Test
    @DisplayName("TC07 - Chưa chọn Giờ (Hour) thì không đặt được")
    void tc07_missing_hour_returns_false() {
        assertFalse(clock.setAlarm("Hour", "30", "AM"));
    }

    @Test
    @DisplayName("TC08 - Chưa chọn Phút (Minute) thì không đặt được")
    void tc08_missing_minute_returns_false() {
        assertFalse(clock.setAlarm("07", "Minute", "AM"));
    }

    @Test
    @DisplayName("TC09 - Chưa chọn AM/PM thì không đặt được")
    void tc09_missing_ampm_returns_false() {
        assertFalse(clock.setAlarm("07", "30", "AM/PM"));
    }

    @Test
    @DisplayName("TC10 - Sau khi đặt, isAlarmPlaying phải là false")
    void tc10_after_set_alarm_not_playing() {
        clock.setAlarm("08", "00", "AM");
        assertFalse(clock.isAlarmPlaying());
    }

    @Test
    @DisplayName("TC11 - Sau khi tắt, alarmTime phải là null")
    void tc11_after_clear_alarm_time_is_null() {
        clock.setAlarm("07", "00", "AM");
        clock.clearAlarm();
        assertNull(clock.getAlarmTime());
    }

    @Test
    @DisplayName("TC12 - Sau khi tắt, isAlarmPlaying phải là false")
    void tc12_after_clear_not_playing() {
        clock.setAlarm("07", "00", "AM");
        clock.triggerAlarm();
        clock.clearAlarm();
        assertFalse(clock.isAlarmPlaying());
    }

    @Test
    @DisplayName("TC13 - Đúng giờ và giây = 0 thì chuông reo")
    void tc13_exact_alarm_time_triggers_ring() {
        clock.setAlarm("07", "30", "AM");
        assertTrue(clock.shouldRing(7, 30, 0));
    }

    @Test
    @DisplayName("TC14 - Đúng giờ nhưng giây khác 0 thì không reo")
    void tc14_correct_time_non_zero_second_no_ring() {
        clock.setAlarm("07", "30", "AM");
        assertFalse(clock.shouldRing(7, 30, 5));
    }

    @Test
    @DisplayName("TC15 - Sai giờ thì không reo")
    void tc15_wrong_time_no_ring() {
        clock.setAlarm("07", "30", "AM");
        assertFalse(clock.shouldRing(8, 30, 0));
    }

    @Test
    @DisplayName("TC16 - Chưa đặt báo thức thì không reo")
    void tc16_no_alarm_set_no_ring() {
        assertFalse(clock.shouldRing(7, 30, 0));
    }

    @Test
    @DisplayName("TC17 - Đang reo rồi thì không reo lại (chống lặp)")
    void tc17_already_playing_no_double_trigger() {
        clock.setAlarm("07", "30", "AM");
        clock.triggerAlarm(); // giả lập đang kêu
        assertFalse(clock.shouldRing(7, 30, 0));
    }

    @Test
    @DisplayName("TC18 - Báo thức PM đúng giờ chiều thì reo")
    void tc18_pm_alarm_triggers_correctly() {
        clock.setAlarm("03", "00", "PM");
        assertTrue(clock.shouldRing(15, 0, 0)); // 15h = 3 PM
    }

    @Test
    @DisplayName("TC19 - Báo thức 12 AM (nửa đêm) hoạt động đúng")
    void tc19_midnight_12am_alarm_works() {
        clock.setAlarm("12", "00", "AM");
        assertTrue(clock.shouldRing(0, 0, 0)); // 0h = 12 AM
    }

    @Test
    @DisplayName("TC20 - Báo thức 12 PM (trưa) hoạt động đúng")
    void tc20_noon_12pm_alarm_works() {
        clock.setAlarm("12", "00", "PM");
        assertTrue(clock.shouldRing(12, 0, 0)); // 12h = 12 PM
    }
}