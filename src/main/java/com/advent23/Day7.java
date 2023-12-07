package com.advent23;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;

/**
 *
 */
public class Day7  extends AdventDayBase {

    record Hand(String value, long bid, HandType type) {}
    public Day7(String file) {
        super(file);
    }

    private static final HashMap<Character, Integer> LETTER_RANK = new HashMap<>() {{
        put('A', 12);
        put('K', 11);
        put('Q', 10);
        put('J', 9);
        put('T', 8);
        put('9', 7);
        put('8', 6);
        put('7', 5);
        put('6', 4);
        put('5', 3);
        put('4', 2);
        put('3', 1);
        put('2', 0);
    }};

    private static final HashMap<Character, Integer> LETTER_RANK_JOKER = new HashMap<>() {{
        put('A', 12);
        put('K', 11);
        put('Q', 10);
        put('T', 9);
        put('9', 8);
        put('8', 7);
        put('7', 6);
        put('6', 5);
        put('5', 4);
        put('4', 3);
        put('3', 2);
        put('2', 1);
        put('J', 0);
    }};
    @Override
    public AdventResult solve() throws Throwable {
        return AdventResult.ofLong(calculateHandEarnings(HandType::classify, LETTER_RANK));
    }

    @Override
    public AdventResult solvePart2() throws Throwable {
        return AdventResult.ofLong(calculateHandEarnings(HandType::classifyNew, LETTER_RANK_JOKER));
    }

    private long calculateHandEarnings(Function<String, HandType> classify, HashMap<Character, Integer> letterRank) {
        final HashMap<String, Hand> valueToHand = new HashMap<>();
        for (String line : lines) {
            String[] split = line.split("\\s+");
            String value = split[0];
            valueToHand.put(value, new Hand(value, Long.parseLong(split[1]), classify.apply(value)));
        }

        Comparator<? super String> HAND_COMPARATOR = (a, b) -> {
            HandType typeA = valueToHand.get(a).type;
            HandType typeB = valueToHand.get(b).type;
            if (typeA.rank == typeB.rank) {
                char[] arrA = a.toCharArray();
                char[] arrB = b.toCharArray();
                for (int i = 0; i < arrA.length; ++i) {
                    if(arrA[i] != arrB[i]) {
                        return letterRank.get(arrA[i]) - letterRank.get(arrB[i]);
                    }
                }
                throw new RuntimeException("not possible same rank equal values: " + a + ":" + b);
            }
            return typeA.rank - typeB.rank;
        };
        LongAdder la = new LongAdder();
        AtomicInteger ai = new AtomicInteger(1);
        valueToHand.keySet().stream().sorted(HAND_COMPARATOR).forEachOrdered(s -> {
            la.add(ai.getAndIncrement() * valueToHand.get(s).bid);
        });
        return la.sum();
    }

    enum HandType {
        FiveKind(7),
        FourKind(6),
        FullHouse(5),
        Three(4),
        TwoPair(3),
        OnePair(2),
        High(1);

        final int rank;
        HandType(int i) {
            this.rank = i;
        }

        static HandType classify(String value) {
            HashMap<Character, Integer> counter = new HashMap<>();
            int max = Integer.MIN_VALUE;
            for (char c : value.toCharArray()) {
                counter.merge(c, 1, Integer::sum);
                Integer i = counter.get(c);
                max = Math.max(i, max);
            }
            Set<Character> unique = counter.keySet();
            switch (unique.size()) {
                case 1:
                    return FiveKind;
                case 5:
                    return High;
                case 2:
                    return switch (max) {
                        case 4 -> FourKind;
                        case 3 -> FullHouse;
                        default -> throw new RuntimeException("unclassified: " + value);
                    };
                case 3:
                    if (max == 3) {
                        return Three;
                    }
                    if (max == 2) {
                        return TwoPair;
                    }
                    throw new RuntimeException("uncla: " + value);
                case 4:
                    return OnePair;
                default:
                    throw new RuntimeException("uncla" + value);
            }
        }

        public static HandType classifyNew(String valueX) {
            int js = 0;
            StringBuilder valueNoJs = new StringBuilder();
            for (char c : valueX.toCharArray()) {
                if (c == 'J') {
                    ++js;
                } else {
                    valueNoJs.append(c);
                }
            }
            if (js == 0) {
                return classify(valueX);
            }
            if (js == 5 || js == 4) return FiveKind;

            HashMap<Character, Integer> counter = new HashMap<>();
            int max = Integer.MIN_VALUE;
            for (char c : valueNoJs.toString().toCharArray()) {
                counter.merge(c, 1, Integer::sum);
                Integer i = counter.get(c);
                max = Math.max(i, max);
            }
            Set<Character> unique = counter.keySet();
            if (unique.size() == 1) return FiveKind;

            if (js == 3) return FourKind;
            if (js == 2) {
                if (unique.size() == 3) {
                    return Three;
                }
                return FourKind;
            }
            return switch (unique.size()) {
                case 4 -> OnePair;
                case 3 -> Three;
                case 2 -> (max == 3) ? FourKind : FullHouse;
                default -> throw new RuntimeException("uncovered case " + valueNoJs);
            };
        }
    }
}
