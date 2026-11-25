package ru.nsu.oop;

import java.util.List;
import ru.nsu.oop.recordbook.RecordBook;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> educationalProgram = List.of(
                "Математический анализ, 1, Экзамен",
                "Линейная алгебра, 1, Экзамен",
                "Основы программирования, 1, Дифф зачет",
                "Физическая культура, 1, Зачет",
                "ООП, 2, Экзамен"
        );

        Integer currentSemester = 2;
        Boolean budgetBasedEducation = true;

        List<String> currentResults = List.of(
                "Математический анализ, 1, Экзамен, 5",
                "Линейная алгебра, 1, Экзамен, 4",
                "Основы программирования, 1, Дифф зачет, 5",
                "Физическая культура, 1, Зачет, 1"
        );

        RecordBook test = new RecordBook(
                educationalProgram,
                currentSemester,
                budgetBasedEducation,
                currentResults
        );

        System.out.println(test.getAverageScore());
    }
}