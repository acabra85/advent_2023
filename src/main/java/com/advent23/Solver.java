package com.advent23;

import com.advent23.helper.FileHelper;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * @author Agustin Cabra on 12/1/23.
 * @since
 */
public class Solver {

    record DayResult(int id, Class<?> dayClass, String p1Test, String p2Test, String p1, String p2) {
        public static DayResultBuilder builder() {
            return new DayResultBuilder();
        }

        private static class DayResultBuilder {
            private int day = -1;
            private Class<?> cls = null;
            private String p1T;
            private String p1 = null;
            private String p2T = null;
            private String p2 = null;
            public DayResultBuilder day(int i) {
                return this;
            }

            public DayResultBuilder withClass(Class<?> cls) {
                this.cls = cls;
                return this;
            }
            public DayResultBuilder withP1T(String p1T){
                this.p1T = p1T;
                return this;
            }
            public DayResultBuilder withP2T(String p2T){
                this.p2T = p2T;
                return this;
            }
            public DayResultBuilder withP1(String p1){
                this.p1 = p1;
                return this;
            }
            public DayResultBuilder withP2(String p2){
                this.p2 = p2;
                return this;
            }

            public DayResult build() {
                return new DayResult(this.day, this.cls, this.p1T, this.p2T, this.p1, this.p2);
            }
        }
    }
    static final List<DayResult> DAYS = List.of(
//            new DayResult(1, Day1.class, "142", null, null, null),
//            new DayResult(2, Day2.class,"8", "2286", null, null),
//            new DayResult(3, Day3.class,"4361", "467835", null, null),
//            new DayResult(4, Day4.class,"13", "30", null, null),
//            new DayResult(5, Day5.class,"35", "46", null, null),
//            new DayResult(6, Day6.class,"288", "71503", null, null),
//            new DayResult(7, Day7.class,"6440", "5905", null, null),
//              new DayResult(8, Day8.class, "6", "6", "20093", "22103062509257")
//              new DayResult(9, Day9.class, "114", "2", "1702218515", "925")
            DayResult.builder().day(10).withClass(Day10.class).build()
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
            Constructor<?> constructor = dayResult.dayClass().getConstructor(String.class);
            System.out.printf("Day%d ", day);
            System.out.printf("P1T: %s", validateResult(day, dayResult.p1Test, ((Solvable) constructor.newInstance(fileNameTest)).solve().val()));
            System.out.printf(" P1: %s", validateResult(day, dayResult.p1, ((Solvable) constructor.newInstance(fileName)).solve().val()));
            String P2T = FileHelper.exists(fileNameTest2) ? fileNameTest2 : fileNameTest;
            String P2 = FileHelper.exists(fileNamePart2) ? fileNamePart2 : fileName;
            System.out.printf(" P2T: %s", validateResult(day, dayResult.p2Test, ((Solvable) constructor.newInstance(P2T)).solvePart2().val()));
            System.out.printf(" P2: %s%n", validateResult(day, dayResult.p2, ((Solvable) constructor.newInstance(P2)).solvePart2().val()));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static Object validateResult(int dayId, Object expected, Object actual) {
        if (expected != null && actual != null && !expected.toString().equals(actual.toString())) {
            throw new AssertionError("Day%d: expected: %s, but got: %s".formatted(dayId, expected, actual));
        }
        return actual;
    }
}
