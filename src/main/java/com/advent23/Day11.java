package com.advent23;

import com.advent23.helper.Point;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.stream.Collectors;

/**
 *
 */
public class Day11 extends AdventDayBase{
    public Day11(String fileName) {
        super(fileName);
    }

    @Override
    public AdventResult solve() throws Throwable {
        return AdventResult.ofLong(AdventUniverseImage.of(new ArrayList<>(lines)).sumFactorGalaxyDistances(2));
    }

    @Override
    public AdventResult solvePart2() throws Throwable {
        return AdventResult.ofLong(AdventUniverseImage.of(new ArrayList<>(lines)).sumFactorGalaxyDistances(1000000));
    }

    private static class AdventUniverseImage {
        private final List<String> lines;

        public AdventUniverseImage() {
            this.lines = new ArrayList<>();
        }
        public AdventUniverseImage(List<String> lines) {
            this.lines = lines;
        }

        private List<Integer> colsAffected(List<String> lines) {
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
            return control;
        }

        private List<Integer> rowsAffected(List<String> base) {
            List<Integer> rowsAffected = new ArrayList<>();
            int rows = 0;
            for (String line : base) {
                if (line.indexOf('#') == -1) {
                    rowsAffected.add(rows);
                }
                ++rows;
            }
            return rowsAffected;
        }

        public static AdventUniverseImage of(List<String> base) {
            return new AdventUniverseImage(base);
        }

        public void addLine(String line) {
            this.lines.add(line);
        }


        private ArrayDeque<Galaxy> getGalaxies() {
            ArrayDeque<Galaxy> galaxies = new ArrayDeque<>();
            int idx = 0;
            for (int i = 0; i < lines.size(); ++i) {
                for (int j = 0; j < lines.get(0).length(); ++j) {
                    char c = lines.get(i).charAt(j);
                    if (c != '.') {
                        galaxies.add(new Galaxy(++idx, Point.of(i, j)));
                    }
                }
            }
            return galaxies;
        }

        public Long sumFactorGalaxyDistances(int factor) {
            List<Integer> rowsAffected = rowsAffected(this.lines);
            List<Integer> colsAffected = colsAffected(this.lines);
            ArrayDeque<Galaxy> galaxies = getGalaxiesTransformed(rowsAffected, colsAffected, factor);
            return sumDistances(galaxies);
        }

        private ArrayDeque<Galaxy> getGalaxiesTransformed(List<Integer> rowsAffected, List<Integer> colsAffected, int factor) {
            ArrayDeque<Galaxy> galaxies = getGalaxies();
            return galaxies.stream()
                    .map((g) -> g.transform(rowsAffected, colsAffected, factor))
                    .collect(Collectors.toCollection(ArrayDeque::new));
        }


        private record Galaxy (int id, Point coord){
            public long manhattan(Galaxy g) {
                return coord.manhattan(g.coord);
            }

            public Galaxy transform(List<Integer> rowsAffected, List<Integer> colsAffected, int factor) {
                int newX = getTransformedX(rowsAffected, factor);
                int newY = getTransformedY(colsAffected, factor);
                return new Galaxy(id, new Point(newX, newY));
            }

            private int getTransformedY(List<Integer> colsAffected, int factor) {
                int countCols = 0;
                for (Integer i : colsAffected) {
                    if (coord.y() < i) {
                        break;
                    }
                    ++countCols;
                }
                return countCols == 0 ? coord.y() : coord.y() + factor * countCols - countCols;
            }

            private int getTransformedX(List<Integer> rowsAffected, int factor) {
                int countRow = 0;
                for (Integer i : rowsAffected) {
                    if (coord.x() < i) {
                       break;
                    }
                    ++countRow;
                }
                return countRow == 0 ? coord.x() : coord().x() + factor * countRow - countRow;
            }
        }

        private void printUniverse() {
            System.out.println();
            for (String line : lines) {
                System.out.println(line);
            }
        }

        public Long sumGalaxyDistances() {
            ArrayDeque<Galaxy> galaxies = getGalaxies();
            return sumDistances(galaxies);
        }

        private static long sumDistances(ArrayDeque<Galaxy> galaxies) {
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
