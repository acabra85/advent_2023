package com.advent23;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.InputMismatchException;

public class Day2 extends ProblemBase {
    public Day2(String fileName) {
        super(fileName);
    }

    @Override
    public AdventResult solve() throws IOException {
       String line;
       long sum = 0;
       while((line = this.help.next()) != null) {
            Game game = parseLine(line);
            if (game.isPossible()) {
                sum += game.id;
            }
       }
       return new AdventResult(sum);
    }

    static Game parseLine(String line) {
        if (!line.contains(": ")) {
            int id = Integer.parseInt(line.split(":")[0].split(" ")[1]);
            return new Game(id, null);
        }
        String[] gameValues = line.split(": ");
        int id = Integer.parseInt(gameValues[0].split(" ")[1]);
        ArrayDeque<Reveal> q =  new ArrayDeque<>();
        if (gameValues[1].contains(";")) {
            String[] reveals = gameValues[1].split("; ");
            for (String reveal : reveals) {
                processCubes(q, reveal);
            }
        } else {
            processCubes(q, gameValues[1]);
        }
        return new Game(id, q);
    }

    private static void processCubes(ArrayDeque<Reveal> q, String reveal) {
        if (reveal.contains(",")) {
            q.add(new RevealBuilder().cubes(reveal.split(", ")).build());
        } else {
            q.add(new RevealBuilder().cube(reveal.split(" ")).build());
        }
    }

    public AdventResult solveMinimal() throws IOException {
        String line;
        long sum = 0;
        while((line = this.help.next()) != null) {
            Game game = parseLine(line);
            sum += game.powerMinimalCubes();
        }
        return AdventResult.ofLong(sum);
    }

    record Game(int id, ArrayDeque<Reveal> q) {

        public boolean isPossible() {
            if (this.q == null) {
                return true;
            }
            for (Reveal reveal : this.q) {
                if (14 - reveal.blue < 0
                        || 13 - reveal.green < 0
                        || 12 - reveal.red < 0
                ) {
                    return false;
                }
            }
            return true;
        }

        public long powerMinimalCubes() {
            if (q == null) {
                return 0;
            }
            long r = 0;
            long g = 0;
            long b = 0;
            for (Reveal reveal : q) {
                r = Math.max(r, reveal.red());
                g = Math.max(g, reveal.green());
                b = Math.max(b, reveal.blue());
            }
            return r * g * b;
        }
    }

    record Reveal(int red, int blue, int green) {}

    private static class RevealBuilder {
        private int red = 0;
        private int green = 0;
        private int blue = 0;

        public RevealBuilder red(int num) {
            this.red += num;
            return this;
        }

        public RevealBuilder blue(int num) {
            this.blue += num;
            return this;
        }

        public RevealBuilder green(int num) {
            this.green += num;
            return this;
        }

        public Reveal build() {
            return new Reveal(this.red, this.blue, this.green);
        }

        public RevealBuilder cube(String[] cubeComps) {
            int num = Integer.parseInt(cubeComps[0]);
            if(cubeComps[1].charAt(0) == 'r') {
                return red(num);
            }
            if(cubeComps[1].charAt(0) == 'g') {
                return green(num);
            }
            if(cubeComps[1].charAt(0) == 'b') {
                return blue(num);
            }
            throw new InputMismatchException("color of cube unknown: " + cubeComps[1]);
        }

        public RevealBuilder cubes(String[] cubes) {
            for (String cube : cubes) {
                String[] cubeComps = cube.split(" ");
                this.cube(cubeComps);
            }
            return this;
        }
    }
}
