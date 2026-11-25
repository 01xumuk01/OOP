package ru.nsu.oop.recordbook;

/**
 * Запланированный контроль по дисциплине. Служит ключом для поиска выставленной оценки.
 *
 * @param discipline название дисциплины
 * @param semester номер семестра, в котором запланирован контроль
 * @param controlType вид контроля
 */
public record Assessment(
        String discipline,
        int semester,
        ControlType controlType
) {

}