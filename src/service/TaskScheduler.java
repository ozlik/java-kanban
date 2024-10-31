package service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class TaskScheduler {
    static final int INTERVAL_MINUTES = 15;
    static final LocalDateTime START_DATE = LocalDateTime.of(2024, 1, 1, 0, 0);
    static final LocalDateTime END_DATE = START_DATE.plusYears(1);

    public Map<LocalDateTime, Boolean> scheduleTimeSlots() {
        Map<LocalDateTime, Boolean> timeSlots = new HashMap<>();
        LocalDateTime current = START_DATE;
        while (current.isBefore(END_DATE)) {
            timeSlots.put(current, false); // Изначально все интервалы свободны
            current = current.plusMinutes(INTERVAL_MINUTES);
        }
        return timeSlots;
    }

    public LocalDateTime roundUpTime(LocalDateTime time) {
        LocalDateTime timeWithoutSeconds = time.truncatedTo(ChronoUnit.MINUTES);
        int remainder = timeWithoutSeconds.getMinute() % 15;
        if (remainder >= 7) {
            return timeWithoutSeconds.plusMinutes(15 - remainder);
        }
        return timeWithoutSeconds.minusMinutes(remainder);
    }
}
