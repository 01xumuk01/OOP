package ru.nsu.oop.recordbook;

/** Виды контроля, которые можно указать в зачётке. */
public enum ControlType {
    /** Задание. */
    ASSIGNMENT,
    /** Контрольная работа. */
    CONTROL_WORK,
    /** Коллоквиум. */
    COLLOQUIUM,
    /** Экзамен. */
    EXAM,
    /** Дифференцированный зачёт с числовой оценкой. */
    DIFF_TEST,
    /** Обычный зачёт. */
    TEST,
    /** Защита отчёта по практике. */
    PRACTICE_REPORT_DEFENSE,
    /** Защита выпускной квалификационной работы. */
    THESIS_DEFENSE
}