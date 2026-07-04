

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class AlarmClock {

    // ---------- Trang thai dung chung giua cac luong ----------
    private static final Object lock = new Object();

    private static int alarmHour = -1;      // 1-12
    private static int alarmMinute = -1;    // 0-59
    private static String alarmAmPm = "";   // "AM" hoac "PM"
    private static boolean alarmIsSet = false;

    private static final AtomicBoolean alarmRinging = new AtomicBoolean(false);
    private static final AtomicBoolean running = new AtomicBoolean(true);

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.ENGLISH);

    public static void main(String[] args) {
        System.out.println("===== HE THONG BAO THUC (Java console) =====");

        Thread clockThread = new Thread(AlarmClock::clockLoop, "clock-thread");
        clockThread.setDaemon(true);
        clockThread.start();

        inputLoop(); // chay tren luong chinh, doc tu ban phim

        running.set(false);
        alarmRinging.set(false);

        System.out.println("\nDa thoat chuong trinh. Tam biet!");
    }

    // ---------- Luong hien thi dong ho + kiem tra bao thuc ----------
    private static void clockLoop() {
        while (running.get()) {
            LocalTime now = LocalTime.now();
            String full = now.format(TIME_FMT);

            int h12 = now.getHour() % 12;
            if (h12 == 0) h12 = 12;
            int minute = now.getMinute();
            int second = now.getSecond();
            String ampm = (now.getHour() >= 12) ? "PM" : "AM";

            synchronized (lock) {
                System.out.print("\rGio hien tai: " + full + "        ");
                System.out.flush();

                if (alarmIsSet && !alarmRinging.get()
                        && h12 == alarmHour && minute == alarmMinute
                        && ampm.equals(alarmAmPm) && second == 0) {
                    alarmRinging.set(true);
                }
            }

            if (alarmRinging.get()) {
                System.out.println("\n\u23F0 DAY THOI NAO!!! \u23F0   (go 'clear' roi Enter de tat chuong)");
                while (alarmRinging.get() && running.get()) {
                    beep();
                    sleep(500);
                }
            }

            sleep(1000);
        }
    }

    private static void beep() {
        System.out.print('\u0007'); // ky tu chuong console
        System.out.flush();
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ---------- Luong (chinh) nhap lenh tu nguoi dung ----------
    private static void printHelp() {
        System.out.println("\nLenh dieu khien:");
        System.out.println("  set    -> Dat bao thuc moi (nhap gio/phut/AM-PM)");
        System.out.println("  clear  -> Tat bao thuc / dung chuong");
        System.out.println("  status -> Xem trang thai bao thuc");
        System.out.println("  help   -> Hien lai danh sach lenh");
        System.out.println("  exit   -> Thoat chuong trinh\n");
    }

    private static void inputLoop() {
        printHelp();
        Scanner scanner = new Scanner(System.in);

        while (running.get() && scanner.hasNextLine()) {
            String cmd = scanner.nextLine().trim();

            switch (cmd) {
                case "set": {
                    try {
                        System.out.print("Nhap gio (1-12): ");
                        int hour = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Nhap phut (0-59): ");
                        int minute = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Nhap AM hoac PM: ");
                        String ampm = scanner.nextLine().trim().toUpperCase(Locale.ENGLISH);

                        if (hour < 1 || hour > 12 || minute < 0 || minute > 59
                                || !(ampm.equals("AM") || ampm.equals("PM"))) {
                            System.out.println("Vui long chon day du va dung dinh dang Gio/Phut/AM-PM!");
                            break;
                        }

                        synchronized (lock) {
                            alarmHour = hour;
                            alarmMinute = minute;
                            alarmAmPm = ampm;
                            alarmIsSet = true;
                            alarmRinging.set(false);
                        }

                        System.out.printf("Da hen gio luc: %02d:%02d %s%n", hour, minute, ampm);
                    } catch (NumberFormatException e) {
                        System.out.println("Gia tri nhap khong hop le. Vui long thu lai.");
                    }
                    break;
                }

                case "clear": {
                    synchronized (lock) {
                        alarmIsSet = false;
                        alarmHour = -1;
                        alarmMinute = -1;
                        alarmAmPm = "";
                        alarmRinging.set(false);
                    }
                    System.out.println("Chua dat bao thuc");
                    break;
                }

                case "status": {
                    synchronized (lock) {
                        if (alarmIsSet) {
                            System.out.printf("Bao thuc dang dat luc: %02d:%02d %s%s%n",
                                    alarmHour, alarmMinute, alarmAmPm,
                                    alarmRinging.get() ? "  (dang reo!)" : "");
                        } else {
                            System.out.println("Chua dat bao thuc");
                        }
                    }
                    break;
                }

                case "help":
                    printHelp();
                    break;

                case "exit":
                    running.set(false);
                    alarmRinging.set(false);
                    return;

                default:
                    if (!cmd.isEmpty()) {
                        System.out.println("Lenh khong hop le. Go 'help' de xem danh sach lenh.");
                    }
                    break;
            }
        }
    }
}
