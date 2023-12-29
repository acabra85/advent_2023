package com.advent23;

import com.advent23.helper.Point;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;

/**
 *
 */
public class Day11 extends AdventDayBase{
    public Day11(String fileName) {
        super(fileName);
    }

    @Override
    public AdventResult solve() throws Throwable {
        List<String> base = new ArrayList<>();
        for (String line : lines) {
            base.add(line);
            if (!line.contains("#")) {
                base.add(line);
            }
        }
        return AdventResult.ofLong(AdventUniverseImage.of(base).sumGalaxyDistances());
    }

    @Override
    public AdventResult solvePart2() throws Throwable {
        return AdventResult.ofLong(-1L);
    }

    private static class AdventUniverseImage {
        private final List<String> lines;
        private final ArrayDeque<Galaxy> galaxies;

        public AdventUniverseImage() {
            this.lines = new ArrayList<>();
            galaxies = new ArrayDeque<>();
        }
        public AdventUniverseImage(List<String> lines) {
            this.lines = lines;
            galaxies = new ArrayDeque<>();
        }

        public static AdventUniverseImage of(List<String> lines) {
            List<Integer> control = new ArrayList<>();
            for (int i = 0; i < lines.get(0).length(); ++i) {
                boolean allEmpty = true;
                for (int j = 0; j < lines.size() && allEmpty; ++j) {
                    if (lines.get(j).charAt(i) != '.') {
                        allEmpty = false;
                    }
                }
                if (allEmpty) {
                    control.add(i);
                }
            }
            AdventUniverseImage other = new AdventUniverseImage();
            if (control.isEmpty()) {
                return new AdventUniverseImage(lines);
            }
            for (String line : lines) {
                StringBuilder sb = new StringBuilder();
                for (int y = 0; y < line.length(); ++y) {
                    char c = line.charAt(y);
                    sb.append(c);
                    if (control.contains(y)) {
                        sb.append(".");
                    }
                }
                other.addLine(sb.toString());
            }
            return other;
        }

        public void addLine(String line) {
            this.lines.add(line);
        }


        private void addGalaxies() {
            int idx = 0;
            for (int i = 0; i < lines.size(); ++i) {
                for (int j = 0; j < lines.get(0).length(); ++j) {
                    char c = lines.get(i).charAt(j);
                    if (c != '.') {
                        addGalaxy(++idx, Point.of(i, j));
                    }
                }
            }
        }

        private void addGalaxy(int id, Point coord) {
            this.galaxies.add(new Galaxy(id, coord));
        }

        private record Galaxy (int id, Point coord){
            public int manhattan(Galaxy g) {
                return coord.manhattan(g.coord);
            }
        }

        private void printUniverse() {
            System.out.println();
            for (String line : lines) {
                System.out.println(line);
            }
        }

        public Long sumGalaxyDistances() {
            addGalaxies();
            LongSummaryStatistics lsm  = new LongSummaryStatistics();
            while(!galaxies.isEmpty()) {
                Galaxy first = galaxies.removeFirst();
                if (galaxies.isEmpty()) {
                    return lsm.getSum();
                }
                for (Galaxy g : galaxies) {
                    lsm.accept(first.manhattan(g));
                }
            }
            return lsm.getSum();
        }
    }
}
