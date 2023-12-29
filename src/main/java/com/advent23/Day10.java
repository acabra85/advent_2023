package com.advent23;

import com.advent23.helper.Pair;
import com.advent23.helper.Point;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.*;
import java.util.function.Function;

public class Day10 extends AdventDayBase {
    public Day10(String fileName) {
        super(fileName);
    }

    private Map<Character, Function<Direction, Direction>> PIPES_MAP = new HashMap<>(){{
        put('J', (d) -> {
            if (d == Direction.RIGHT) return Direction.UP;
            if (d == Direction.DOWN) return  Direction.LEFT;
            throw new RuntimeException();
        });
        put('|', (d) -> {
            if (d == Direction.UP) return Direction.UP;
            if (d == Direction.DOWN) return  Direction.DOWN;
            throw new RuntimeException();
        });
        put('F', (d) -> {
            if (d == Direction.UP) return Direction.RIGHT;
            if (d == Direction.LEFT) return  Direction.DOWN;
            throw new RuntimeException();
        });
        put('-', (d) -> {
            if (d == Direction.LEFT) return Direction.LEFT;
            if (d == Direction.RIGHT) return  Direction.RIGHT;
            throw new RuntimeException();
        });
        put('L', (d) -> {
            if (d == Direction.DOWN) return Direction.RIGHT;
            if (d == Direction.LEFT) return  Direction.UP;
            throw new RuntimeException();
        });
        put('7', (d) -> {
            if (d == Direction.RIGHT) return Direction.DOWN;
            if (d == Direction.UP) return  Direction.LEFT;
            throw new RuntimeException();
        });
        put('.', null);
    }};

    private record PipeStart(Point start, List<char[]> board, Direction from, Direction to){}

    private Direction getDirection(char key, PipeStart pipeStart, Point nextPos, Node node) {
        Function<Direction, Direction> fun = Objects.requireNonNull(PIPES_MAP.get(key));
        return fun.apply(node.to);
    }

    private Point getPair(Node node) {
        Direction to = node.to();
        return new Point(node.point.x() + to.x(), node.point.y() + to.y());
    }

    private PipeStart preProcess() {
        List<char[]> board = new ArrayList<>();
        Point pos = null;
        Pair<Direction, Direction> fromTo = getFromTo(lines.removeFirst().split("\\s+"));
        int i = 0;
        for (String line : lines) {
            int idx = pos == null ? line.indexOf('S') : -1;
            if (idx >= 0) {
                pos = new Point(i, idx);
            }
            board.add(line.toCharArray());
            ++i;
        }
        return new PipeStart(Objects.requireNonNull(pos), board, fromTo.left(), fromTo.right());
    }

    private static Pair<Direction, Direction> getFromTo(String[] split) {
        return new Pair<>(Direction.valueOf(split[0]),Direction.valueOf(split[1]));
    }

    record Node(Point point, int steps, Direction to){}

    private enum Direction {
        DOWN, UP, LEFT, RIGHT;

        private static final Map<Direction, Direction> rev = new HashMap<>(){{
            put(DOWN, UP);
            put(UP, DOWN);
            put(LEFT, RIGHT);
            put(RIGHT, LEFT);
        }};
        private static final Map<Direction, List<Direction>> rem = new HashMap<>(){{
            put(DOWN, List.of(DOWN, LEFT, RIGHT));
            put(UP, List.of(UP, LEFT, RIGHT));
            put(LEFT, List.of(DOWN, LEFT, UP));
            put(RIGHT, List.of(DOWN, UP, RIGHT));
        }};

        public Direction reverse() {
            return rev.get(this);
        }

        public List<Direction> getRemaining() {
            return rem.get(this);
        }

        public Integer y() {
            return switch (this) {
                case UP, DOWN -> 0;
                case LEFT -> -1;
                case RIGHT -> 1;
                default -> throw new RuntimeException("unknown value" + this.name());
            };
        }

        public Integer x() {
            return switch (this) {
                case LEFT,RIGHT -> 0;
                case UP -> -1;
                case DOWN -> 1;
                default -> throw new RuntimeException("unknown value" + this.name());
            };
        }
    }


    @Override
    public AdventResult solve() throws Throwable {
        PipeStart pipeStart = preProcess();
        Stack<Node> q = new Stack<>();
        q.push(new Node(pipeStart.start, 0, pipeStart.to));
        Node node = null;
        // put the first node
        // if the node repeats return half
        // if not, identify where you came from and choose the available node as next
        while(!q.isEmpty()) {
            node = q.pop();
            Point nextPos = getPair(node);
            char key = pipeStart.board.get(nextPos.x())[nextPos.y()];
            if (key == 'S') {
                return AdventResult.ofStr((node.steps + 1)  / 2);
            }
            q.push(new Node(nextPos, node.steps + 1, getDirection(key, pipeStart, nextPos, node)));
        }
        return AdventResult.ofLong(-1L);
    }

    @Override
    public AdventResult solvePart2() throws Throwable {
        PipeStart pipeStart = preProcess();
        Long totalDots = pipeStart.board.stream()
                .map(l -> new String(l).chars().mapToObj(i->(char)i).filter(x -> x == '.').count())
                .reduce(0L, Long::sum);
        Stack<Node> q = new Stack<>();
        q.push(new Node(pipeStart.start, 0, pipeStart.to));
        Node node = null;
        // put the first node
        // if the node repeats return half
        // if not, identify where you came from and choose the available node as next
        Set<Point> border = new HashSet<>();

        Path2D path = new Path2D.Double();
        boolean isFirst = true;
        while(!q.isEmpty()) {
            node = q.pop();
            if (!border.contains(node.point)) {
                if (isFirst) {
                    isFirst = false;
                    path.moveTo(node.point.x(), node.point.y());
                } else {
                    path.lineTo(node.point.x(), node.point.y());
                }
            }
            border.add(node.point);
            Point nextPos = getPair(node);
            char key = pipeStart.board.get(nextPos.x())[nextPos.y()];
            if (key == 'S') {
                return AdventResult.ofLong((new AdventShape(path, border)).countInternalTiles(pipeStart.board));
            }
            q.push(new Node(nextPos, node.steps + 1, getDirection(key, pipeStart, nextPos, node)));
        }
        return AdventResult.ofLong(totalDots);
    }

    private record AdventShape(Path2D path, Set<Point> shape) {
        public Long countInternalTiles(List<char[]> board) {
            java.awt.geom.Area area = new Area(this.path);
            long count = 0L;
            for (int x = 0; x < board.size(); ++x) {
                for (int y = 0; y < board.get(x).length; ++y) {
                    if (!this.shape.contains(Point.of(x, y)) && area.contains(x, y)) {
                        ++count;
                    }
                }
            }
            return count;
        }

        private boolean inside(Point p) {
            return false;
        }
    }
}
