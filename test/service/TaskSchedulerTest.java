package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TaskSchedulerTest должен: ")
class TaskSchedulerTest {
    TaskScheduler taskScheduler;

    @BeforeEach
    void init() {
        taskScheduler = new TaskScheduler();
    }

    @DisplayName("Создавать сетку расписания на год с шагом 15 минут")
    @Test
    void shouldScheduleTimeSlots() {
        LocalDateTime time = LocalDateTime.of(2024, 1, 1, 0, 0);

        Map<LocalDateTime, Boolean> timeSlots = taskScheduler.scheduleTimeSlots();

        assertEquals(35136, timeSlots.size(), "Количество ячеек не соответствует году"); //2024 високосный год, для не високосного 35040
        assertEquals(false, timeSlots.get(time), "Ячейка отдает не верное булево значение");
    }

    @DisplayName("Округлять время до шага 15 минут вперед")
    @Test
    void shouldRoundUpTimePlus() {
        LocalDateTime time = LocalDateTime.of(2021, 1, 1, 1, 9, 59);
        LocalDateTime timeSample = LocalDateTime.of(2021, 1, 1, 1, 15);

        LocalDateTime timeCheck = taskScheduler.roundUpTime(time);

        assertEquals(timeSample, timeCheck, "Время не совпадает");
    }

    @DisplayName("Округлять время до шага 15 минут назад")
    @Test
    void shouldRoundUpTimeMinus() {
        LocalDateTime time = LocalDateTime.of(2021, 1, 1, 1, 5, 59);
        LocalDateTime timeSample = LocalDateTime.of(2021, 1, 1, 1, 0);

        LocalDateTime timeCheck = taskScheduler.roundUpTime(time);

        assertEquals(timeSample, timeCheck, "Время не совпадает");
    }

    @DisplayName("Округлять время до шага 15 минут (граничное значение)")
    @Test
    void shouldRoundUpTime() {
        LocalDateTime time = LocalDateTime.of(2021, 1, 1, 1, 0, 59);
        LocalDateTime timeSample = LocalDateTime.of(2021, 1, 1, 1, 0);

        LocalDateTime timeCheck = taskScheduler.roundUpTime(time);

        assertEquals(timeSample, timeCheck, "Время не совпадает");
    }
}