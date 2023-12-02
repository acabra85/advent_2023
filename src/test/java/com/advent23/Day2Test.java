package com.advent23;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;

import static org.assertj.core.api.Assertions.assertThat;


class Day2Test {

    @BeforeEach
    public void setup() {}

    @Test
    public void parseGameMultipleSets() {
        Day2.Game actual = Day2.parseLine("Game 99: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green");
        ArrayDeque<Day2.Reveal> q = actual.q();

        assertThat(actual.id()).isEqualTo(99);
        assertThat(q.size()).isEqualTo(3);
        assertThat(q.peekFirst().blue()).isEqualTo(3);
        assertThat(q.peekFirst().red()).isEqualTo(4);
        assertThat(q.removeFirst().green()).isEqualTo(0);
        assertThat(q.peekFirst().blue()).isEqualTo(6);
        assertThat(q.peekFirst().red()).isEqualTo(1);
        assertThat(q.removeFirst().green()).isEqualTo(2);
        assertThat(q.peekFirst().blue()).isEqualTo(0);
        assertThat(q.peekFirst().red()).isEqualTo(0);
        assertThat(q.removeFirst().green()).isEqualTo(2);
    }

    @Test
    public void parseGameSingleSet() {
        Day2.Game actual = Day2.parseLine("Game 100: 8 blue, 4 red, 6 red");
        ArrayDeque<Day2.Reveal> q = actual.q();

        assertThat(actual.id()).isEqualTo(100);
        assertThat(q.size()).isEqualTo(1);
        assertThat(q.peekFirst().blue()).isEqualTo(8);
        assertThat(q.peekFirst().red()).isEqualTo(10);
        assertThat(q.peekFirst().green()).isEqualTo(0);
    }

    @Test
    public void parseGameSingleSetSingleCube() {
        Day2.Game actual = Day2.parseLine("Game 200: 8 green");
        ArrayDeque<Day2.Reveal> q = actual.q();

        assertThat(actual.id()).isEqualTo(200);
        assertThat(q.size()).isEqualTo(1);
        assert q.peekFirst() != null;
        assertThat(q.peekFirst().blue()).isEqualTo(0);
        assertThat(q.peekFirst().red()).isEqualTo(0);
        assertThat(q.peekFirst().green()).isEqualTo(8);
    }

    @Test
    public void parseGameNoReveals() {
        Day2.Game actual = Day2.parseLine("Game 201:");
        ArrayDeque<Day2.Reveal> q = actual.q();

        assertThat(actual.id()).isEqualTo(201);
        assertThat(q).isNull();
    }
}