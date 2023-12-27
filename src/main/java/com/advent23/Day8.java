package com.advent23;

import com.advent23.helper.CompletableHelper;
import com.advent23.helper.MaxNumber;
import com.advent23.helper.Pair;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 */
public class Day8 extends AdventDayBase {

    public static final String START = "AAA";
    public static final String DEST = "ZZZ";
    public static final Predicate<String> ENDS_WITH_Z = p -> p.charAt(2) == 'Z';
    public static final Predicate<String> END_WITH_A = k -> k.charAt(2) == 'A';
    private static final boolean BRUTE_FORCE = false;

    public Day8(String fileName) {
        super(fileName);
    }

    private long findMinimalSteps(StepIterator steps, Map<String, Pair<String, String>> map) {
        ArrayDeque<Node> q = new ArrayDeque<>();
        q.add(new Node(map.get(START), 1));
        Node node;
        String dest;
        while(!q.isEmpty()) {
             node = q.removeFirst();
             dest = steps.next() == 'L' ? node.val.left() : node.val.right();
             if (dest.equals(DEST)) {
                 return node.step;
             }
             q.addLast(new Node(map.get(dest), node.step +1));
        }
        return -1;
    }
    private long findSimultaneousSteps(String stepStr, final Map<String, Pair<String, String>> map) throws Exception {
        List<String> aKeys = map.keySet().stream().filter(END_WITH_A).collect(Collectors.toList());
        if (!BRUTE_FORCE) {
            List<Long> counters = new ArrayList<>();
            for (String aKey : aKeys) {
                counters.add(findStepsNeededToReachZ(stepStr, map, aKey));
            }
            return counters.stream().reduce(1L, Day8::lcm);
        }
        return solveByBruteForce(stepStr, map, aKeys);
    }

    // This turns out to be a pitfall, as the answer is in the order of magnitude 10_000_000_000_000
    private long solveByBruteForce(String stepStr, Map<String, Pair<String, String>> map, List<String> aKeys) throws Exception {
        try (ExecutorService ex = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
            List<GhostGrinderState> ghostGrinders = getGhostGrinderStates(stepStr, map, aKeys, ex);
            Pair<Long, Long> count = countTotalSteps(ghostGrinders, ghostGrinders.getFirst().totalSteps);
            while (count.left() != aKeys.size()) {
                ghostGrinders = getGhostGrinderStates(ghostGrinders, map, ex, count.right());
                count = countTotalSteps(ghostGrinders, ghostGrinders.getFirst().totalSteps);
            }
            return ghostGrinders.get(0).totalSteps;
        } catch (Exception e) {
            throw e;
        }
    }

    private long findStepsNeededToReachZ(String stepStr, Map<String, Pair<String, String>> map, String aKey) {
        StepIterator s = new StepIterator(stepStr);
        String key = aKey;
        Pair<String, String> currPair = map.get(key);
        int counter = 1;
        int i = 0;
        List<Integer> counters = new ArrayList<>();
        while(i < 4) {
            key = currPair.getStr(s.next());
            if (key.charAt(2) == 'Z') {
                counters.add(counter);
                counter = 0;
                ++i;
            }
            currPair = map.get(key);
            ++counter;
        }
        return counters.get(0);
    }

    private List<GhostGrinderState> getGhostGrinderStates(
            List<GhostGrinderState> ghostGrindersStates, Map<String, Pair<String, String>> map, ExecutorService ex,
            long max
    ) throws Exception {
        ArrayList<CompletableFuture<GhostGrinderState>> cfs = new ArrayList<>(ghostGrindersStates.size());
        for (GhostGrinderState ghostGrindersState : ghostGrindersStates) {
            final GhostGrinder ghostGrinder = new GhostGrinder(ghostGrindersState, map, max);
            cfs.add(ghostGrinder.getCF());
            CompletableFuture.runAsync(ghostGrinder, ex);
        }
        return CompletableHelper.getGhostGrinders(cfs);
    }

    private Pair<Long, Long> countTotalSteps(List<GhostGrinderState> ghostGrinders, long first) {
        MaxNumber maxNumber = new MaxNumber();
        return new Pair<>(ghostGrinders.stream().filter(g -> {
            maxNumber.update(g.totalSteps);
            return g.totalSteps == first;
        }).count(), maxNumber.getValue());
    }

    private List<GhostGrinderState> getGhostGrinderStates(String stepStr, Map<String, Pair<String, String>> map, List<String> aKeys, ExecutorService ex) throws Exception {
        ArrayList<CompletableFuture<GhostGrinderState>> cfs = new ArrayList<>(aKeys.size());
        for (String aKey : aKeys) {
            final GhostGrinder ghostGrinder = new GhostGrinder(aKey, map, stepStr);
            cfs.add(ghostGrinder.getCF());
            CompletableFuture.runAsync(ghostGrinder, ex);
        }
        return CompletableHelper.getGhostGrinders(cfs);
    }

    static class GhostGrinder implements Runnable {
        private final StepIterator step;
        private final Map<String, Pair<String, String>> map;
        private Pair<String, String> curr;
        private long steps;
        private final CompletableFuture<GhostGrinderState> cf;

        GhostGrinder(String aKey, Map<String, Pair<String, String>> map, String stepStr) {
            this.cf = new CompletableFuture<>();
            this.step = StepIterator.of(stepStr);
            this.map = map;
            this.curr = map.get(aKey);
            this.steps = 0;
        }

        GhostGrinder(GhostGrinderState ggs, Map<String, Pair<String, String>> map, long max) {
            this.cf = ggs.totalSteps < max ? new CompletableFuture<>() : CompletableFuture.completedFuture(ggs);
            this.step = ggs.step;
            this.map = map;
            this.curr = map.get(ggs.key);
            this.steps = ggs.totalSteps;
        }

        @Override
        public void run() {
            try {
                if(this.cf.isDone()) {
                    return;
                }
                String key = curr.getStr(step.next());
                ++this.steps;
                while(key.charAt(2) != 'Z') {
                    curr = map.get(key);
                    key = curr.getStr(step.next());
                    ++this.steps;
                }
                this.cf.complete(new GhostGrinderState(this.steps, step, key));
            } catch (Throwable t) {
                System.out.println("failure: " + t);
                this.cf.completeExceptionally(t);
            }
        }

        public CompletableFuture<GhostGrinderState> getCF() {
            return this.cf;
        }
    }

    public record GhostGrinderState(long totalSteps, StepIterator step, String key) {}

    private long findSimultaneousStepsq(StepIterator step, Map<String, Pair<String, String>> map) {
        ArrayDeque<NodeList> q = new ArrayDeque<>(List.of(getANodes(map)));
        NodeList node;
        List<String> keys;
        while (!q.isEmpty()) {
            node = q.removeFirst();
            keys = node.getValues(step.next());
            if (keys.stream().allMatch(ENDS_WITH_Z)) {
                return node.step;
            }
            q.add(new NodeList(keys.stream()
                    .map(map::get).collect(Collectors.toList()), node.step + 1));
        }
        return -1;
    }

    private NodeList getANodes(Map<String, Pair<String, String>> map) {
        return new NodeList(map.keySet().stream()
                .filter(END_WITH_A)
                .map(map::get)
                .collect(Collectors.toList()), 1);
    }

    private record Node(Pair<String, String> val, int step) {}
    private record NodeList(List<Pair<String, String>> val, int step) {
        public List<String> getValues(char next) {
            return val.stream()
                    .map(p -> next == 'L' ? p.left() : p.right())
                    .collect(Collectors.toList());
        }
    }

    private static class StepIterator {

        private int nextIdx;
        private final char[] step;

        StepIterator(String step) {
            this.nextIdx = 0;
            this.step = step.toCharArray();
        }

        public static StepIterator of(String steps) {
            return new StepIterator(steps);
        }

        public char current() {
            return step[nextIdx];
        }

        public char next() {
            char c = step[nextIdx];
            if (nextIdx + 1 == step.length) {
                nextIdx = 0;
                return c;
            }
            ++nextIdx;
            return c;
        }
    }

    private Map<String, Pair<String, String>> buildMap(ArrayDeque<String> lines) {
        lines.removeFirst();
        Map<String, Pair<String, String>> map = new HashMap<>();
        while(!lines.isEmpty()) {
            String[] split = lines.removeFirst().split(" = ");
            map.put(split[0], Pair.fromNode(split[1]));
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    public AdventResult solve() throws Throwable {
        return AdventResult.ofLong(findMinimalSteps(StepIterator.of(lines.removeFirst()), buildMap(lines)));
    }

    @Override
    public AdventResult solvePart2() throws Throwable {
        return AdventResult.ofLong(findSimultaneousSteps(lines.removeFirst(), buildMap(lines)));
    }

    /**
     * ref: <a href="https://www.baeldung.com/java-least-common-multiple">...</a>
     */
    public static long lcm(long number1, long number2) {
        if (number1 == 1) return number2;
        if (number1 == 0 || number2 == 0) {
            return 0;
        }
        long absNumber1 = Math.abs(number1);
        long absNumber2 = Math.abs(number2);
        long absHigherNumber = Math.max(absNumber1, absNumber2);
        long absLowerNumber = Math.min(absNumber1, absNumber2);
        long lcm = absHigherNumber;
        while (lcm % absLowerNumber != 0) {
            lcm += absHigherNumber;
        }
        return lcm;
    }
}
