package com.advent23;

import com.advent23.helper.Helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AdventDayBase implements Solvable {
    protected final ArrayDeque<String> lines;
    public AdventDayBase(String fileName) {
        InputStream in = AdventDayBase.class.getClassLoader().getResourceAsStream(fileName);
        OutputStreamWriter out = new OutputStreamWriter(System.out);
        Helper help = new Helper(new BufferedReader(new InputStreamReader(in)), new PrintWriter(out));
        ArrayDeque<String> result = null;
        try {
            result = AdventDayBase.parse(help);
        } catch (IOException e) {
            result =  new ArrayDeque<>();
        }
        this.lines = result;
    }

    public static ArrayDeque<String> parse(final Helper help) throws IOException {
        String line;
        ArrayDeque<String> lines = new ArrayDeque<>();
        while((line = help.next())!= null) {
            lines.add(line);
        }
        return lines;
    }

    public static List<Integer> toIntList(String line, String regex) {
        final String[] split = line.split(regex);
        return Arrays.stream(split).map(Integer::parseInt).collect(Collectors.toList());
    }

    protected static List<Long> toLongList(String line, Integer skip, Optional<String> regex) {
        final String[] split = line.split(regex.orElse("\\s+"));
        final int start = skip == null ? 0 : skip;
        List<Long> holding = new ArrayList<>(split.length - start);
        for (int i = start, j=0; i < split.length; ++i, ++j) {
            holding.add(Long.parseLong(split[i]));
        }
        return holding;
    }

    protected static long[] toLongArray(String line, Integer skip, Optional<String> regex) {
        final String[] split = line.split(regex.orElse("\\s+"));
        final int start = skip == null ? 0 : skip;
        long[] holding = new long[split.length - start];
        for (int i = start, j=0; i < split.length; ++i, ++j) {
            holding[j] = Long.parseLong(split[i]);
        }
        return holding;
    }

    protected static int[] toIntArray(String line, Optional<Integer> skip) {
        final String[] split = line.split("\\s+");
        final Integer start = skip.orElse(0);
        int[] arr = new int[split.length - start];
        for (int i = start, j=0; i < split.length; ++i, ++j) {
            arr[j] = Integer.parseInt(split[i]);
        }
        return arr;
    }

    protected static String asJoinedString(String line, Optional<String> delim, Optional<Integer> skip) {
        final String[] split = line.split("\\s+");
        final Integer start = skip.orElse(0);
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < split.length; ++i) {
            sb.append(split[i]);
        }
        return String.join(delim.orElse(""), sb.toString());
    }

    protected static Long toLong(String line) {
        return Long.parseLong(line);
    }

    protected static List<Long> toLongList(String line) {
        return toLongList(line, 0, Optional.empty());
    }

    protected static long[] toLongArray(String line, int skip) {
        return toLongArray(line, skip, Optional.empty());
    }

    protected AdventResult ofLong(long sum) {
        return AdventResult.ofLong(sum);
    }
}
