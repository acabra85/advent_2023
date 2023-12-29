package com.advent23;

import com.advent23.helper.FileHelper;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * @author Agustin Cabra on 12/1/23.
 * @since
 */
public class Solver {

    record DayResult(int id, boolean toRun, Class<?> dayClass, String p1Test, String p2Test, String p2Test2, String p1, String p2) {
        static DayResultBuilder b() {
            return new DayResultBuilder();
        }

        public boolean toRun() {
            return this.toRun;
        }

        private static class DayResultBuilder {
            private Class<?> cls = null;
            private String p1T;
            private String p1 = null;
            private String p2T = null;
            private String p2 = null;
            private String p2t2 = null;
            private boolean run = false;
            DayResultBuilder clazz(Class<?> cls) {
                this.cls = cls;
                return this;
            }
            DayResultBuilder withP1T(String p1T){
                this.p1T = p1T;
                return this;
            }
            DayResultBuilder withP2T(String p2T){
                this.p2T = p2T;
                return this;
            }
            DayResultBuilder withP1(String p1){
                this.p1 = p1;
                return this;
            }
            DayResultBuilder withP2(String p2){
                this.p2 = p2;
                return this;
            }

            DayResultBuilder toRun() {
                this.run = true;
                return this;
            }

            DayResultBuilder withP2T2(String p2t2) {
                this.p2t2 = p2t2;
                return this;
            }

            DayResult build() {
                int id = Integer.parseInt(this.cls.getSimpleName().substring("Day".length()));
                return new DayResult(id, this.run, this.cls, this.p1T, this.p2T, this.p2t2, this.p1, this.p2);
            }
        }
    }
    static final List<DayResult> DAYS = List.of(
            DayResult.b().clazz(Day1.class).withP1T("142").build(),
            DayResult.b().clazz(Day2.class).withP1T("8").withP2T("2286").build(),
            DayResult.b().clazz(Day3.class).withP1T("4361").withP2T("467835").build(),
            DayResult.b().clazz(Day4.class).withP1T("13").withP2T("30").build(),
            DayResult.b().clazz(Day5.class).withP1T("35").withP2T("46").build(),
            DayResult.b().clazz(Day6.class).withP1T("288").withP2T("71503").build(),
            DayResult.b().clazz(Day7.class).withP1T("6440").withP2T("5905").build(),
            DayResult.b().clazz(Day8.class)
                .withP1T("6").withP2T("6").withP1("20093").withP2("22103062509257").build(),
            DayResult.b().clazz(Day9.class)
                    .withP1T("114").withP2T("2").withP1("1702218515").withP2("925").build(),
            DayResult.b().clazz(Day10.class)
                    .withP1T("8").withP1("7173").withP2T("10").withP2T2("126").withP2("291").build(),
            DayResult.b().clazz(Day11.class).withP1T("374").withP1("10033566").toRun().build()
    );
    public static void main(String[] args) {
        DAYS.stream()
                .filter(DayResult::toRun)
                .forEach(Solver::validate);
    }

    private static void validate(DayResult dayResult) {
        int day = dayResult.id;
        String fileName = String.format("input_d%d.txt", day);
        String fileNameTest = String.format("input_d%dt.txt", day);
        String fileNameTest2 = String.format("input_d%dt2.txt", day);
        String fileNamePart2Test2 = String.format("input_d%dt3.txt", day);
        String fileNamePart2 = String.format("input_d%dp2.txt", day);
        try {
            Constructor<?> constructor = dayResult.dayClass().getConstructor(String.class);
            System.out.printf("Day%d ", day);
            System.out.printf("P1T: %s", validateResult(day, dayResult.p1Test, ((Solvable) constructor.newInstance(fileNameTest)).solve().val()));
            System.out.printf(" P1: %s", validateResult(day, dayResult.p1, ((Solvable) constructor.newInstance(fileName)).solve().val()));
            String P2T = FileHelper.exists(fileNameTest2) ? fileNameTest2 : fileNameTest;
            String P2 = FileHelper.exists(fileNamePart2) ? fileNamePart2 : fileName;
            String P2T2 = FileHelper.exists(fileNamePart2Test2) ? fileNamePart2Test2 : fileNameTest;
            System.out.printf(" P2T: %s", validateResult(day, dayResult.p2Test, ((Solvable) constructor.newInstance(P2T)).solvePart2().val()));
            if (FileHelper.exists(fileNamePart2Test2)) {
                System.out.printf(" P2T2: %s", validateResult(day, dayResult.p2Test2, ((Solvable) constructor.newInstance(P2T2)).solvePart2().val()));
            }
            System.out.printf(" P2: %s%n", validateResult(day, dayResult.p2, ((Solvable) constructor.newInstance(P2)).solvePart2().val()));
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static Object validateResult(int dayId, Object expected, Object actual) {
        if (expected != null && actual != null && !expected.toString().equals(actual.toString())) {
            (new AssertionError("[ERROR! >>> Day%d]: expected: %s, but got: %s".formatted(dayId, expected, actual))).printStackTrace();
        }
        return actual;
    }
}
