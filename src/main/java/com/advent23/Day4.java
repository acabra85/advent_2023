package com.advent23;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 */
public class Day4 extends AdventDayBase {
    public Day4(String fileName) {
        super(fileName);
    }

    @Override
    public AdventResult solve() {
        long sum = 0;
        for(String line: this.lines) {
            sum += parseCard(line).getCardValue();
        }
        return AdventResult.ofLong(sum);
    }

    @Override
    public AdventResult solvePart2() {
        ArrayList<Card> deck  = new ArrayList<>();
        for(String line: this.lines) {
            deck.add(parseCard(line));
        }
        return AdventResult.ofLong(solveDeck(deck));
    }

    private static Card parseCard(String line) {
        String[] cardStr = line.split(":");
        String[] sides = cardStr[1].split(" \\| ");
        int id = Integer.parseInt(cardStr[0].split("\\s+")[1]);
        Set<Integer> left = Arrays.stream(sides[0].split("\\s+"))
                .filter(v).map(Integer::parseInt).collect(Collectors.toSet());
        Set<Integer> right = Arrays.stream(sides[1].split("\\s+"))
                .filter(v).map(Integer::parseInt).collect(Collectors.toSet());
        return new Card(id, left, right);
    }

    final static Predicate<String> v = (s) -> s != null && !s.isEmpty();

    private Long solveDeck(ArrayList<Card> deck) {
        int[] copies = new int[deck.size()];
        for (Card card : deck) {
            int matches = card.matches();
            if (matches > 0) {
                for (int i = 0; i < matches; ++i) {
                    ++copies[card.id + i];
                }
                for (int i = 0; i < copies[card.id - 1]; ++i) {
                    for (int j = 0; j < card.matches(); ++j) {
                        ++copies[card.id + j];
                    }
                }
            }
        }
        return (long) (Arrays.stream(copies).reduce(0, (a, b) -> a + b) + deck.size());
    }
    static class Card {
        private final int id;
        private final Set<Integer> winning;
        private final Set<Integer> have;
        private int match = -1;

        public Card(int id, Set<Integer> winning, Set<Integer> have){
            this.id = id;
            this.winning = winning;
            this.have = have;
            int match = -1;
        }
        long getCardValue() {
            if (winning.isEmpty() || have.isEmpty()) {
                return 0;
            }
            long cardValue = 0;
            for (Integer i : have) {
                if (winning.contains(i)) {
                    if (cardValue == 0) {
                        cardValue = 1;
                    } else {
                        cardValue *= 2;
                    }
                }
            }
            return cardValue;
        }

        @Override
        public String toString() {
            return id+"";
        }

        int matches() {
            if (this.match <0) {
                int match = 0;
                for (Integer i : have) {
                    if (winning.contains(i)) {
                        ++match;
                    }
                }
                this.match = match;
            }
            return match;
        }
    }
}
