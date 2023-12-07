package com.advent23;

import com.advent23.helper.AdventRangeGrinder;
import com.advent23.helper.MinNumber;
import com.advent23.helper.MinReducer;
import com.advent23.helper.Pair;
import com.advent23.helper.Range;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Day5 extends AdventDayBase {

    private AdventAgriculture adventAgriculture;

    public Day5(String fileName) {
        super(fileName);
    }

    private AdventAgriculture parseAdventAgriculture() {
        AdventAgriculture ag = new AdventAgriculture();
        final List<String> collect = lines.stream().filter((s) -> !s.isEmpty()).collect(Collectors.toList());
        for (int i = 0; i >= 0 && i < collect.size(); ++i) {
            String line = collect.get(i);
            if (line.startsWith("seeds: ")) {
                ag.readSeeds(AdventDayBase.toLongArray(line.substring("seeds: ".length())));
            }
            if (line.startsWith("seed-")) {
                i = ag.readSeedToSoil(collect, i+1);
            }
            if (line.startsWith("soil-")) {
                i = ag.readSoilToFertilizer(collect, i+1);
            }
            if (line.startsWith("ferti")) {
                i = ag.readFertilizerToWater(collect, i+1);
            }
            if (line.startsWith("water")) {
                i = ag.readWaterToLight(collect, i+1);
            }
            if (line.startsWith("light")) {
                i = ag.readLightToTemperature(collect, i+1);
            }
            if (line.startsWith("temp")) {
                i = ag.readTemperatureToHumidity(collect, i+1);
            }
            if (line.startsWith("humidity")) {
                ag.readHumidityToLocation(collect, i+1);
                return ag;
            }
        }
        return ag;
    }

    @Override
    public AdventResult solve() {
        this.adventAgriculture = parseAdventAgriculture();
        long minLocation = Long.MAX_VALUE;
        for (Long seed : adventAgriculture.seedsToPlant) {
            minLocation = Math.min(minLocation, adventAgriculture.resolveLocation(seed));
        }
        return AdventResult.ofLong(minLocation);
    }

    @Override
    public AdventResult solvePart2() {
        if (this.adventAgriculture == null) {
            this.adventAgriculture = parseAdventAgriculture();
        }
        ArrayDeque<Long> seeds = new ArrayDeque<>(this.adventAgriculture.seedsToPlant);
        List<Range> ranges = splitRangeSeeds(seeds);
        int nThreads = Runtime.getRuntime().availableProcessors();
        try (ExecutorService executor = Executors.newFixedThreadPool(nThreads)) {
            ArrayList<CompletableFuture<Long>> cfs = new ArrayList<>(ranges.size());
            long rIdx = 0;
            if (AdventRangeGrinder.REPORT_PROGRESS) {
                System.out.println();
            }
            for (Range range : ranges) {
                AdventRangeGrinder adventRangeGrinder
                        = new AdventRangeGrinder(this.adventAgriculture::resolveLocation, range, ++rIdx, ranges.size());
                cfs.add(adventRangeGrinder.getCF());
                CompletableFuture.runAsync(adventRangeGrinder, executor);
            }
            if (AdventRangeGrinder.REPORT_PROGRESS) {
                System.out.println();
            }
            return AdventResult.ofLong(new MinReducer<>(((s) -> s.min(Long::compare)), -1L).getMin(cfs));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Range> splitRangeSeeds(ArrayDeque<Long> seeds) {
        List<Range> ranges = new ArrayList<>();
        while(!seeds.isEmpty()) {
            ranges.addLast(new Range(seeds.peekFirst(), seeds.removeFirst() + seeds.peekFirst() - 1L, seeds.removeFirst()));
        }
        return Range.transform(ranges, BULK_SIZE);
    }

    static class AdventAgriculture {
        List<Long> seedsToPlant;
        LinkedHashMap<Range, Range> soilToFertilizer;
        private LinkedHashMap<Range, Range> fertilizerToWater;
        private LinkedHashMap<Range, Range> waterToLight;
        private LinkedHashMap<Range, Range> lightToTemperature;
        private LinkedHashMap<Range, Range> temperatureToHumidity;
        private LinkedHashMap<Range, Range> humidityToLocation;
        private LinkedHashMap<Range, Range> seedToSoil;

        public void readSeeds(List<Long> seeds) {
            this.seedsToPlant = seeds;
        }

        public int readSeedToSoil(List<String> collect, int start) {
            this.seedToSoil = new LinkedHashMap<>();
            return processMap(this.seedToSoil, collect, start, "soil-to");
        }

        public int readSoilToFertilizer(List<String> collect, int start) {
            this.soilToFertilizer = new LinkedHashMap<>();
            int i = processMap(this.soilToFertilizer, collect, start, "fertilizer-");
            return i;
        }

        /**
         * I want to merge the maps, since the there is only 1 direction and too many jumps
         * so instead of jumping from seed-soil-water-light-temperature-humidity-location
         * do directly seed-location.
         */
        private LinkedHashMap<Range, Range> mergeMap(LinkedHashMap<Range, Range> a, LinkedHashMap<Range, Range> b) {
            Set<Range> rangesA = a.keySet();
            Set<Range> rangesB = b.keySet();
            for (Range keyA : rangesA) {
                Range valueA = a.get(keyA);
                for (Range keyB : rangesB) {
                    if (valueA.touches(keyB)) {
                        valueA.merge(keyB);
                    }
                }
            }
            return a;
        }

        private int readFertilizerToWater(List<String> collect, int start) {
            this.fertilizerToWater = new LinkedHashMap<>();
            return processMap(this.fertilizerToWater, collect, start, "water-");
        }

        private int readWaterToLight(List<String> collect, int start) {
            this.waterToLight = new LinkedHashMap<>();
            return processMap(this.waterToLight, collect, start, "light-");
        }

        private int readLightToTemperature(List<String> collect, int start) {
            this.lightToTemperature = new LinkedHashMap<>();
            return processMap(this.lightToTemperature, collect, start, "temperature-");
        }

        private int readTemperatureToHumidity(List<String> collect, int start) {
            this.temperatureToHumidity = new LinkedHashMap<>();
            return processMap(this.temperatureToHumidity, collect, start, "humidity-");
        }

        private int readHumidityToLocation(List<String> collect, int start) {
            this.humidityToLocation = new LinkedHashMap<>();
            return processMap(this.humidityToLocation, collect, start, null);
        }

        private int processMap(LinkedHashMap<Range, Range> map, List<String> collect, int start, String stopKey) {
            ArrayList<Pair<Range, Range>> pairs = new ArrayList<>();
            for (int i = start; i >= 0 && i < collect.size(); ++i) {
                final String line = collect.get(i);
                if (stopKey != null && line.startsWith(stopKey)) {
                    pairs.sort(Comparator.comparingLong(a -> a.left().start()));
                    pairs.forEach((p) -> map.put(p.left(), p.right()));
                    return i - 1;
                }
                final List<Long> integers = AdventDayBase.toLongArray(line);
                AdventMapInfo ami = new AdventMapInfo(integers.get(0), integers.get(1), integers.get(2));
                Range from = new Range(ami.source, ami.source + ami.len - 1, ami.len);
                Range to = new Range(ami.dest, ami.dest + ami.len - 1, ami.len);
                pairs.add(new Pair(from, to));
            }
            pairs.sort(Comparator.comparingLong(a -> a.left().start()));
            pairs.forEach((p) -> map.put(p.left(), p.right()));
            return -1;
        }

        long resolveLocation(Long seed) {
            long soil = resolve(seedToSoil, seed);
            long fert = resolve(soilToFertilizer, soil);
            long water = resolve(fertilizerToWater, fert);
            long light = resolve(waterToLight, water);
            long tem = resolve(lightToTemperature, light);
            long hum = resolve(temperatureToHumidity, tem);
            return resolve(humidityToLocation, hum);
        }

        private Long resolve(LinkedHashMap<Range, Range> ranges, Long search) {
            for (Map.Entry<Range, Range> range : ranges.entrySet()) {
                if (range.getKey().belongs(search)) {
                    return range.getValue().end() - range.getKey().end() + search;
                }
            }
            return search;
        }

    }
    record AdventMapInfo(long dest, long source, long len){}

    static final int BULK_SIZE = 4000000;

}
