package ru.nsu.oop.recordbook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Проверяет расчёты электронной зачётки и загрузку результатов. */
class RecordBookTest {
    @Test
    void averagesGradesAndIgnoresPassFailResults() {
        var book = new RecordBook(
                List.of("Матан, 1, экзамен", "Алгебра, 1, дифф зачёт",
                        "Физкультура, 1, зачёт", "Английский, 1, зачёт"),
                1, true,
                List.of("Матан,1, ЭКЗ,5", "Алгебра, 1, дифф зачет, 4",
                        "Физкультура, 1, зачет, 1", "Английский, 1, зачёт, 0")
        );

        assertEquals(4.5, book.getAverageScore(), 0.000001);
    }

    @Test
    void returnsZeroWhenThereAreNoNumericGrades() {
        var book = new RecordBook(
                List.of("Физкультура, 1, зачёт", "Матан, 1, экзамен"),
                1, true, List.of("Физкультура, 1, зачёт, 1")
        );

        assertEquals(0.0, book.getAverageScore(), 0.000001);
    }

    @ParameterizedTest(name = "семестр {0}, бюджет {1}, экзамены {2}/{3}: {4}")
    @CsvSource({
        "4, false, 4, 5, true",
        "4, true,  4, 5, false",
        "2, false,  ,  , false",
        "4, false, 3, 5, false",
        "4, false, 4, 2, false",
        "4, false, 4,  , false"
    })
    void checksBudgetTransfer(
            int semester, boolean budget, Integer secondExam, Integer thirdExam, boolean expected
    ) {
        final var plan = List.of("История, 1, экзамен", "Матан, 2, экзамен", "ОС, 3, экзамен",
                "Алгебра, 3, дифф зачёт", "Сети, 4, экзамен");
        var results = new ArrayList<>(List.of("История, 1, экзамен, 3"));
        if (semester == 4) {
            results.add("Алгебра, 3, дифф зачёт, 3");
        }
        addGrade(results, "Матан, 2, экзамен", secondExam);
        addGrade(results, "ОС, 3, экзамен", thirdExam);

        var book = new RecordBook(plan, semester, budget, results);
        assertEquals(expected, book.canTransferToBudget());
        assertEquals(expected, book.canTransferToBudget(), "Проверка не меняет форму обучения");
    }

    @ParameterizedTest(name = "итоговые 5/5/{0}/{1}, ВКР {2}: {3}")
    @CsvSource({
        "4,  ,  , true",
        "4, 5,  , true",
        "4, 4,  , false",
        "3, 5,  , false",
        "2, 5,  , false",
        "4, 5, 5, true",
        "4, 5, 4, false",
        "4, 4, 5, false"
    })
    void predictsHonorsDegree(
            int physics, Integer systems, Integer thesis, boolean expected
    ) {
        var plan = List.of("Матан, 2, экзамен", "Матан, 1, дифф зачёт",
                "Алгебра, 1, дифф зачёт", "Алгебра, 2, экзамен",
                "Физика, 2, дифф зачёт", "ОС, 3, экзамен",
                "Физкультура, 3, зачёт", "ВКР, 8, защита ВКР");
        var results = new ArrayList<>(List.of(
                "Матан, 1, дифф зачёт, 3", "Матан, 2, экзамен, 5",
                "Алгебра, 1, дифф зачёт, 3", "Алгебра, 2, экзамен, 5",
                "Физика, 2, дифф зачёт, " + physics
        ));
        addGrade(results, "ОС, 3, экзамен", systems);
        addGrade(results, "ВКР, 8, защита ВКР", thesis);

        var book = new RecordBook(plan, thesis == null ? 3 : 8, true, results);
        assertEquals(expected, book.canGetRedDiploma());
    }

    @Test
    void refusesHonorsDegreeWithoutThesisInPlan() {
        var book = new RecordBook(
                List.of("Матан, 1, экзамен"), 1, true, List.of("Матан, 1, экзамен, 5")
        );

        assertFalse(book.canGetRedDiploma());
    }

    @Test
    void refusesHonorsDegreeWithoutFinalGradesInPlan() {
        var book = new RecordBook(List.of("ВКР, 8, защита ВКР"), 8, true, List.of());

        assertFalse(book.canGetRedDiploma());
    }

    @ParameterizedTest(name = "семестр {0}, экзамен {1}, дифзачёт {2}, зачёт {3}: {4}")
    @CsvSource({
        "8, 4, 5, 1, true",
        "8, 3, 5, 1, false",
        "8, 5, 2, 1, false",
        "8, 5, 5, 0, false",
        "8,  , 5, 1, false",
        "8, 5,  , 1, false",
        "8, 5, 5,  , false",
        "1,  ,  ,  , false",
        "9, 4, 5, 1, false"
    })
    void checksScholarship(
            int semester, Integer exam, Integer diffTest, Integer pass, boolean expected
    ) {
        final var plan = List.of("ОС, 7, экзамен", "Сети, 7, дифф зачёт", "Английский, 7, зачёт",
                "ОС, 7, задание", "ОС, 7, контрольная", "ОС, 7, коллоквиум",
                "Практика, 7, защита практики", "Матан, 6, экзамен", "ВКР, 8, защита ВКР");
        var results = new ArrayList<String>();
        if (semester != 1) {
            results.addAll(List.of("ОС, 7, задание, 2", "ОС, 7, контрольная, 2",
                    "ОС, 7, коллоквиум, 2", "Практика, 7, защита практики, 2",
                    "Матан, 6, экзамен, 3"));
        }
        addGrade(results, "ОС, 7, экзамен", exam);
        addGrade(results, "Сети, 7, дифф зачёт", diffTest);
        addGrade(results, "Английский, 7, зачёт", pass);

        var book = new RecordBook(plan, semester, false, results);
        assertEquals(expected, book.canGetEnhancedScholarship());
    }

    @Test
    void acceptsScholarshipWhenNoRelevantControlsWereScheduled() {
        var book = new RecordBook(
                List.of("Сети, 2, контрольная", "Сети, 3, экзамен"), 3, true, List.of()
        );

        assertTrue(book.canGetEnhancedScholarship());
    }

    @ParameterizedTest(name = "{0} / {1}")
    @CsvSource(delimiter = '|', value = {
        "Матан, 1 | Матан, 1, экзамен, 5",
        "Матан, 1, неизвестно | Матан, 1, экзамен, 5",
        "Матан, 1, экзамен | Матан, 1, экзамен",
        "Матан, 1, экзамен | Матан, 1, неизвестно, 5",
        "Матан, 1, экзамен | Матан, 2, экзамен, 5",
        "Матан, 1, экзамен | Матан, 1, экзамен, пять",
        "Матан, 1, экзамен | Матан, 1, экзамен, 1",
        "Матан, 1, экзамен | Матан, 1, экзамен, 6",
        "Алгебра, 1, дифф зачёт | Алгебра, 1, дифф зачёт, 1",
        "Физкультура, 1, зачёт | Физкультура, 1, зачёт, 2",
        "ВКР, 8, защита ВКР | ВКР, 8, защита ВКР, 6"
    })
    void rejectsInvalidInput(String planEntry, String resultEntry) {
        assertThrows(IllegalArgumentException.class,
                () -> new RecordBook(List.of(planEntry), 1, true, List.of(resultEntry)));
    }

    private static void addGrade(List<String> results, String assessment, Integer grade) {
        if (grade != null) {
            results.add(assessment + ", " + grade);
        }
    }
}