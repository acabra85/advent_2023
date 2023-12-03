package com.advent23;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Day3 extends ProblemBase {
    public Day3(String fileName) {
        super(fileName);
    }

    @Override
    public AdventResult solve() throws IOException {String line;
        AdventEngine.Builder ab = new AdventEngine.Builder();
        while((line = this.help.next()) != null) {
            ab.accept(line);
        }
        final AdventEngine build = ab.build();
        //System.out.println(build);
        return new AdventResult(build.sumParts());
    }

    record Coord(int i, int j) {}
    record AdventEngine(ArrayList<EngineLine> lines, int cols) {
        private static final ArrayList<Coord> COORDS = new ArrayList<>(){{
            add(new Coord(-1, -1));
            add(new Coord(-1, 0));
            add(new Coord(-1, 1));
            add(new Coord(0, -1));
            add(new Coord(0, 1));
            add(new Coord(1, -1));
            add(new Coord(1, 0));
            add(new Coord(1, 1));
        }};
        private static String strRep = null;

        public long sumParts() {
            long sum = 0;
            for (int i = 0; i < lines.size(); ++i) {
                final EngineLine engineLine = lines.get(i);
                for (int j = 0; j < engineLine.length; ++j) {
                    final char ch = engineLine.charAt(j);
                    if(!AdventEngine.isDigit(ch) && ch != '.') {
                        sum += this.processSum(i, j);
                    }
                }
            }
            return sum;
        }

        private long processSum(int i, int j) {
            long sum = 0;
            for (Coord coord : COORDS) {
                EnginePartNumber epn = getNumber(i+coord.i, j+coord.j);
                if (epn != null && !epn.counted) {
                    sum += epn.value;
                    epn.count();
                }
            }
            return sum;
        }

        @Override
        public String toString() {
            if (strRep == null) {
                StringBuilder sb = new StringBuilder();
                for (EngineLine line : lines) {
                    sb.append(line.chars).append('\n');
                }
                strRep = sb.toString();
            }
            return strRep;
        }

        private EnginePartNumber getNumber(int i, int j) {
            if (isValid(i, j)) {
                final EngineLine engineLine = lines.get(i);
                if (AdventEngine.isDigit(engineLine.charAt(j))) {
                    for (EnginePartNumber num : engineLine.nums) {
                        if (num.is(j)) {
                            return num;
                        }
                    }
                }
            }
            return null;
        }

        private static boolean isDigit(char character) {
            return character >= '0' && character <= '9';
        }

        private boolean isValid(int i, int j) {
            if (i < 0 || i >= this.lines.size()
                    || j < 0) return false;
            final EngineLine engineLine = this.lines.get(i);
            return j < engineLine.chars.length;
        }

        public static class Builder {
            ArrayList<EngineLine> iParts = new ArrayList<>();

            public void accept(String toCharArray) {
                final char[] chars = toCharArray.toCharArray();
                StringBuilder sb = new StringBuilder();
                ArrayList<EnginePartNumber> nums = new ArrayList<>();
                for (int i = 0; i < chars.length; ++i) {
                    if (AdventEngine.isDigit(chars[i])) {
                        sb.append(chars[i]);
                    } else if (!sb.isEmpty()) {
                        final String numberStr = sb.toString();
                        nums.add(new EnginePartNumber(Long.parseLong(numberStr), i-numberStr.length(), i-1));
                        sb = new StringBuilder();
                    }
                }
                if (!sb.isEmpty()) {
                    final String numberStr = sb.toString();
                    nums.add(new EnginePartNumber(Long.parseLong(numberStr), chars.length-numberStr.length(), chars.length-1));
                }

                iParts.add(new EngineLine(chars, toCharArray.length(), nums));
            }

            public AdventEngine build() {
                final AdventEngine adventEngine = new AdventEngine(iParts, iParts.get(0).length());
                iParts = null;
                return adventEngine;
            }
        }
    }

    static class EnginePartNumber {
        private final long value;
        private final int startIdx;
        private final int endIdx;
        private boolean counted;
        public String str;

        EnginePartNumber(long value, int startIdx, int endIdx) {
            this.value = value;
            this.counted = false;
            this.startIdx = startIdx;
            this.endIdx = endIdx;
            this.str = value + ":" + startIdx +","+endIdx;
        }

        @Override
        public String toString() {
            return this.str;
        }
        public void count() {
            this.counted = true;
        }

        public boolean is(int j) {
            return j>=startIdx && j <= endIdx;
        }
    }
    record EngineLine(char[] chars, int length, ArrayList<EnginePartNumber> nums){
        public char charAt(int idx) {
            return chars[idx];
        }
    }
}
