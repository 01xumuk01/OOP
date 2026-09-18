package ru.nsu.oop;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Проверяет запуск приложения с настоящими объектами и выход после раунда.
 */
class MainSmokeTest extends BlackjackTestSupport {
    @Test
    void mainRunsOneRoundAndExits() {
        // Без блэкджека: первый 0 завершает ход, второй завершает игру.
        // При блэкджеке первый 0 сразу завершает игру.
        setInput("0\n0\n");
        Main.main(new String[0]);
        assertTrue(output().contains("Добро пожаловать"));
        assertTrue(output().contains("Раунд 1"));
        assertTrue(output().contains("Счёт "));
        assertFalse(output().contains("Раунд 2"));
        assertTrue(output().contains("Счёт 1 : 0")
                || output().contains("Счёт 0 : 1")
                || output().contains("Счёт 0 : 0"));
    }
}
