package ru.nsu.oop.recordbook;

import java.util.*;

import static java.lang.Math.max;

/**
 * Электронная зачётная книжка студента с учебным планом и выставленными результатами.
 * Записи плана хранятся отдельно от оценок, чтобы учитывать и предстоящие контроли.
 */
public class RecordBook {

    private final List<Assessment> educationalProgram;
    private int currentSemester;
    private Boolean budgetBasedEducation;
    private final Map<Assessment, Integer> currentResults;
    private final List<Assessment> finalAssessments;

    private int finalSemester;
    private Assessment VKR;

    /**
     * Создаёт зачётку по плану и уже выставленным результатам.
     *
     * <p>Строка плана содержит название дисциплины, номер семестра и вид контроля.
     * Строка результата дополнительно содержит целую оценку. Поля разделены запятыми.
     * Для экзамена, дифзачёта и ВКР допустимы оценки от 2 до 5.
     * Для обычного зачёта 0 означает «не зачтено», а 1 — «зачтено».
     * Результат должен соответствовать записи в плане.
     *
     * @param educationalProgram строки учебного плана
     * @param currentSemester номер текущего семестра, начиная с 1
     * @param budgetBasedEducation {@code true}, если студент учится на бюджете
     * @param currentResults строки с выставленными результатами
     * @throws IllegalArgumentException если строка имеет неверный формат, вид контроля
     *     неизвестен, контроль отсутствует в плане или оценка выходит за допустимые пределы
     */
    public RecordBook (
            List<String> educationalProgram,
            Integer currentSemester,
            Boolean budgetBasedEducation,
            List<String> currentResults
    ) {
        this.finalSemester = 0;
        this.finalAssessments = new ArrayList<>();
        Map<String, Assessment> latestByDiscipline = new HashMap<>();
        this.educationalProgram = new ArrayList<>();
        for (String i : educationalProgram) {
            String[] parts = Arrays.stream(i.split(","))
                    .map(String::trim)
                    .toArray(String[]::new);
            if (parts.length != 3) {
                throw new IllegalArgumentException(
                        "Неправильный формат входных данных для образовательной программы: " + i
                );
            }

            ControlType controlType = switch (parts[2].toLowerCase())
            {
                case "задание", "таск", "таска" -> ControlType.ASSIGNMENT;
                case "контрольная", "кр", "контроша" -> ControlType.CONTROL_WORK;
                case "коллоквиум", "коллок" -> ControlType.COLLOQUIUM;
                case "экзамен", "экз", "пытка" -> ControlType.EXAM;
                case "дифф зачет", "дифф зачёт" -> ControlType.DIFF_TEST;
                case "зачет", "зачёт" -> ControlType.TEST;
                case "защита отчета по практике", "защита практики", "практика" -> ControlType.PRACTICE_REPORT_DEFENSE;
                case "защита вкр", "диплом", "вкр" -> ControlType.THESIS_DEFENSE;
                default -> throw new IllegalArgumentException("Неизвестная форма контроля: " + parts[2]);
            };

            Assessment base = new Assessment(parts[0], Integer.parseInt(parts[1]), controlType);
            this.educationalProgram.add(base);

            if (controlType == ControlType.EXAM || controlType == ControlType.DIFF_TEST) {
                Assessment previous = latestByDiscipline.get(base.discipline());

                if (previous == null || base.semester() > previous.semester()) {
                    latestByDiscipline.put(base.discipline(), base);
                }
            }

            if (base.controlType() == ControlType.THESIS_DEFENSE) {
                this.VKR = base;
            }

            this.finalSemester = max(base.semester(), this.finalSemester);
        }

        this.finalAssessments.addAll(latestByDiscipline.values());
        this.currentSemester = currentSemester;
        this.budgetBasedEducation = budgetBasedEducation;

        this.currentResults = new HashMap<>();
        for (String i : currentResults) {

            String[] parts = Arrays.stream(i.split(","))
                    .map(String::trim)
                    .toArray(String[]::new);

            if (parts.length != 4) {
                throw new IllegalArgumentException(
                        "Неправильный формат входных данных для оценок: " + i
                );
            }

            ControlType controlType = switch (parts[2].toLowerCase())
            {
                case "задание", "таск", "таска" -> ControlType.ASSIGNMENT;
                case "контрольная", "кр", "контроша" -> ControlType.CONTROL_WORK;
                case "коллоквиум", "коллок" -> ControlType.COLLOQUIUM;
                case "экзамен", "экз", "пытка" -> ControlType.EXAM;
                case "дифф зачет", "дифф зачёт" -> ControlType.DIFF_TEST;
                case "зачет", "зачёт" -> ControlType.TEST;
                case "защита отчета по практике", "защита практики", "практика" -> ControlType.PRACTICE_REPORT_DEFENSE;
                case "защита вкр", "диплом", "вкр" -> ControlType.THESIS_DEFENSE;
                default -> throw new IllegalArgumentException("Неизвестная форма контроля: " + parts[2]);
            };

            int mark = Integer.parseInt(parts[3]);
            Assessment base = new Assessment(parts[0], Integer.parseInt(parts[1]), controlType);

            if (!this.educationalProgram.contains(base)) {
                throw new IllegalArgumentException(
                        "Вась, ты чо дал, нет твоего "
                        + base.toString() + " в образовательной программе!"
                );
            }

            if
            (
                    (controlType.equals(ControlType.TEST) && (mark != 0 && mark != 1))
                            ||
                    (
                            (controlType.equals(ControlType.EXAM) ||
                             controlType.equals(ControlType.DIFF_TEST) ||
                             controlType.equals(ControlType.THESIS_DEFENSE))
                                    &&
                            (mark < 2 || mark > 5)
                    )
            ) {
                throw new IllegalArgumentException(
                        "Неправильный формат оценки для " + base.toString()
                );
            }

            this.currentResults.put(base, mark);
        }
    }

    /**
     * Вычисляет среднее арифметическое выставленных оценок, пропуская значения 0 и 1.
     *
     * @return средний балл или 0.0, если нет оценок для расчёта
     */
    public Double getAverageScore() {
        int sumScore = 0;
        double countScore = 0.0;

        for (int mark : currentResults.values()) {
            if (mark != 0 && mark != 1)  {
                sumScore += mark;
                countScore += 1.0;
            }
        }

        if (countScore == 0.0) {
            return 0.0;
        }

        return (sumScore / countScore);
    }

    /**
     * Проверяет, можно ли перейти с платного обучения на бюджет.
     * Начиная с третьего семестра все запланированные экзамены за два предыдущих
     * семестра должны быть сданы на 4 или 5.
     *
     * @return {@code true}, если студент на платном и проходит проверку экзаменов
     */
    public Boolean canTransferToBudget() {
        if (budgetBasedEducation) {
            return false;
        }

        if (currentSemester < 3) {
            return false;
        }

        Boolean base = true;
        for (Assessment assessment : educationalProgram) {
            if (
                    assessment.semester() != currentSemester - 1 &&
                    assessment.semester() != currentSemester - 2
            ) {
                continue;
            }

            Integer mark = currentResults.get(assessment);

            if (
                    assessment.controlType() == ControlType.EXAM &&
                    (mark == null || mark < 4)
            ) {
                base = false;
                break;
            }
        }

        if (base) {
            return true;
        }

        return false;
    }

    /**
     * Проверяет, остаётся ли возможность получить диплом с отличием.
     *
     * <p>У каждой дисциплины учитывается последний по семестру экзамен или дифзачёт
     * из плана. Если оба стоят в одном семестре, учитывается первый из них в плане.
     * Среди итоговых оценок не должно быть оценок ниже 4. При проверке порога в 75%
     * ещё не выставленные итоговые оценки считаются возможными пятёрками.
     *
     * <p>Защита ВКР должна быть в плане и проверяется отдельно от доли пятёрок.
     * Если оценка за ВКР уже выставлена, она должна быть равна 5.
     *
     * @return {@code true}, если по текущим результатам диплом с отличием ещё возможен
     */
    public Boolean canGetRedDiploma() {
        int countMarks = finalAssessments.size();
        int countGreatMarks = 0;

        if (countMarks == 0) {
            return false;
        }

        for (Assessment i : finalAssessments) {
            Integer mark = currentResults.get(i);
            if (mark == null) {
                countGreatMarks += 1;
                continue;
            }

            if (mark < 4) {
                return false;
            }

            if (mark == 5) {
                countGreatMarks += 1;
            }
        }

        if (VKR == null) {
            return false;
        }

        if (!currentResults.containsKey(VKR)) {
            return countGreatMarks * 4 >= countMarks * 3;
        }

        if (currentResults.get(VKR) != 5) {
            return false;
        }

        return countGreatMarks * 4 >= countMarks * 3;
    }

    /**
     * Проверяет возможность получить повышенную стипендию по правилу этой модели.
     *
     * <p>За предыдущий семестр все экзамены и дифзачёты должны быть сданы на 4 или 5,
     * а обычные зачёты закрыты. Если результат запланированного контроля отсутствует,
     * условие не выполнено. Если за предыдущий семестр таких контролей в плане нет,
     * проверка результатов считается пройденной. Форма обучения не учитывается.
     *
     * @return {@code false} на первом семестре или после последнего семестра плана;
     *     в остальных случаях результат проверки предыдущего семестра
     */
    public Boolean canGetEnhancedScholarship() {
        if (currentSemester == 1 || currentSemester > finalSemester) {
            return false;
        }

        for (Assessment i : educationalProgram) {
            if (i.semester() != (currentSemester - 1)) {
                continue;
            }

            ControlType controlType = i.controlType();

            if (!(controlType.equals(ControlType.EXAM) ||
                controlType.equals(ControlType.DIFF_TEST) ||
                controlType.equals(ControlType.TEST))) {
                continue;
            }

            Integer mark = currentResults.get(i);

            if (mark == null) {
                return false;
            }

            if (controlType.equals(ControlType.EXAM) ||
                controlType.equals(ControlType.DIFF_TEST)) {
                if (mark < 4) {
                    return false;
                }
            } else if (mark != 1) {
                return false;
            }
        }

        return true;
    }
}
