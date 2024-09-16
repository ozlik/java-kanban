package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Эпик")
public class EpicTest {
    Epic epic;

@Test
@DisplayName("должен совпадать со своей копией")
void shouldEqualsWithCopy() {
   epic = new Epic("Тестовый эпик", "Описание тестового эпика");
    Epic epicExpected = new Epic("Тестовый эпик", "Описание тестового эпика");
    assertEqualsEpic(epicExpected, epic, "эпики не совпадают");
}

    private static void assertEqualsEpic (Epic expected, Epic actual, String message) {
        assertEquals(expected.getTitle(), actual.getTitle(), message + " , title");
        assertEquals(expected.getDescription(), actual.getDescription(), message + " , description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + " , status");
        assertEquals(expected.getType(), actual.getType(), message + " , type");
    }
}
