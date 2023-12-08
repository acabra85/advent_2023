package com.advent23;

import com.advent23.helper.FileHelper;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * @author Agustin Cabra on 12/1/23.
 * @since
 */
public class Solver {

    record DayResult(int id, String p1Test, String p2Test) {}
    static final List<DayResult> DAYS = List.of(
//            new DayResult(1, "142", null),
//            new DayResult(2,"8", "2286"),
//            new DayResult(3,"4361", "467835"),
//            new DayResult(4,"13", "30"),
//            new DayResult(5,"35", "46"),
//            new DayResult(6,"288", "71503"),
//            new DayResult(7,"6440", "5905"),
              new DayResult(8, "6", "6")
    );
    public static void main(String[] args) throws Throwable {
        DAYS.forEach(Solver::validate);
    }

    private static void validate(DayResult dayResult) {
        int day = dayResult.id;
        String fileName = String.format("input_d%d.txt", day);
        String fileNameTest = String.format("input_d%dt.txt", day);
        String fileNameTest2 = String.format("input_d%dt2.txt", day);
        String fileNamePart2 = String.format("input_d%dp2.txt", day);
        try {
            Class<?> dayClass = Solver.class.getClassLoader().loadClass(String.format("com.advent23.Day%d", day));
            Constructor<?> constructor = dayClass.getConstructor(String.class);
            System.out.printf("Day%d ", day);
            System.out.printf("P1T: %s", day, validateResult(day, dayResult.p1Test, ((Solvable) constructor.newInstance(fileNameTest)).solve().val()));
            System.out.printf(" P1: %s", validateResult(day, null, ((Solvable) constructor.newInstance(fileName)).solve().val()));
            String P2T = FileHelper.exists(fileNameTest2) ? fileNameTest2 : fileNameTest;
            String P2 = FileHelper.exists(fileNamePart2) ? fileNamePart2 : fileName;
            System.out.printf(" P2T: %s", validateResult(day, dayResult.p2Test, ((Solvable) constructor.newInstance(P2T)).solvePart2().val()));
            System.out.printf(" P2: %s%n", validateResult(day, null, ((Solvable) constructor.newInstance(P2)).solvePart2().val()));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static Object validateResult(int dayId, Object expected, Object actual) {
        if (expected != null && !expected.toString().equals(actual.toString())) {
            throw new AssertionError("Day%d: expected: %s, but got: %s".formatted(dayId, expected, actual));
        }
        return actual;
    }
}
