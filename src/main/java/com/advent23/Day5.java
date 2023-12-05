package com.advent23;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Day5 extends AdventDayBase {

    static final boolean REPORT_PROGRESS = true;
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
                ag.readSeeds(AdventDayBase.asLong(line.substring("seeds: ".length())));
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
        try (ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
            ArrayList<CompletableFuture<Long>> cfs = new ArrayList<>(ranges.size());
            long rIdx = 0;
            for (Range range : ranges) {
                AdventRangeGrinder adventRangeGrinder = new AdventRangeGrinder(this.adventAgriculture, range, ++rIdx, ranges.size());
                cfs.add(adventRangeGrinder.getCF());
                CompletableFuture.runAsync(adventRangeGrinder, executor);
            }
            return AdventResult.ofLong(getMinFromCompletableFutures(cfs));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Range> splitRangeSeeds(ArrayDeque<Long> seeds) {
        List<Range> ranges = new ArrayList<>();
        while(!seeds.isEmpty()) {
            ranges.addLast(new Range(seeds.peekFirst(), seeds.removeFirst() + seeds.peekFirst() - 1L, seeds.removeFirst()));
        }
        ranges = ranges.stream().map(r -> {
            if (r.size > Range.BULK_SIZE) {
                return r.split();
            }
            return List.of(r);
        }).flatMap(List::stream).toList();
        return ranges;
    }

    private static Long getMinFromCompletableFutures(ArrayList<CompletableFuture<Long>> cfs) throws InterruptedException, ExecutionException {
        final CompletableFuture<Void> failFast = CompletableFuture.allOf(cfs.toArray(new CompletableFuture[cfs.size()]));
        final CompletableFuture<?> failure = new CompletableFuture<>();
        cfs.forEach(f -> f.exceptionally(ex -> {
            failure.completeExceptionally(ex);
            return null;
        }));
        failure.exceptionally(ex -> {
            cfs.forEach(f -> f.cancel(true));
            return null;
        });
        return CompletableFuture.anyOf(failure, failFast)
                .thenApply(
                        v -> cfs.stream()
                                    .map(CompletableFuture::join)
                                    .min(Long::compare)
                                    .get()
                        ).exceptionally(err -> {
                            System.out.println("error: " + err);
                            return -1L;
                })
                .get();
    }

    static class MinNumber {
        long value;
        MinNumber() {
            value = Long.MAX_VALUE;
        }
        long update(long check) {
            value = Math.min(value, check);
            return value;
        }

        Long getValue() {
            return value;
        }
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
            ArrayList<Pair> pairs = new ArrayList<>();
            for (int i = start; i >= 0 && i < collect.size(); ++i) {
                final String line = collect.get(i);
                if (stopKey != null && line.startsWith(stopKey)) {
                    pairs.sort(Comparator.comparingLong(a -> a.from.start));
                    pairs.forEach((p) -> map.put(p.from, p.to));
                    return i - 1;
                }
                final List<Long> integers = AdventDayBase.asLong(line);
                AdventMapInfo ami = new AdventMapInfo(integers.get(0), integers.get(1), integers.get(2));
                Range from = new Range(ami.source, ami.source + ami.len - 1, ami.len);
                Range to = new Range(ami.dest, ami.dest + ami.len - 1, ami.len);
                pairs.add(new Pair(from, to));
            }
            pairs.sort(Comparator.comparingLong(a -> a.from.start));
            pairs.forEach((p) -> map.put(p.from, p.to));
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
                    return range.getValue().end - range.getKey().end + search;
                }
            }
            return search;
        }

    }
    record Pair(Range from, Range to) {}
    record AdventMapInfo(long dest, long source, long len){}
    record Range(long start, long end, long size){
        boolean belongs(long q) {
            return start <= q && q <= end;
        }

        public boolean touches(Range other) {
            return belongs(other.start) || belongs(other.end);
        }

        public void merge(Range other) {

        }
        static final int BULK_SIZE = 4000000;

        public List<Range> split() {
            long mod = size % BULK_SIZE;
            int pieces = Double.valueOf((size * 1.0) / (BULK_SIZE * 1.0)).intValue();
            long curr = this.start;
            long newEnd = 0;
            List<Range> ranges = new ArrayList<>(pieces + (mod > 0 ? 1 : 0));
            for (long i = 0; i < pieces; ++i) {
                newEnd = curr + BULK_SIZE -1;
                ranges.add(new Range(curr, newEnd, BULK_SIZE));
                curr = newEnd + 1;
            }
            if (mod > 0) {
                ranges.add(new Range(curr, curr + mod - 1, mod));
            }
            return ranges;
        }
    }
    static class AdventRangeGrinder implements Runnable{
        private final CompletableFuture<Long> cf;
        private final Day5.Range seeds;
        private final long id;
        private final int totalSize;
        private Day5.AdventAgriculture ag;

        public AdventRangeGrinder(Day5.AdventAgriculture adventAgriculture, Day5.Range seeds, long idx, int size) {
            this.cf = new CompletableFuture<>();
            this.seeds = seeds;
            this.ag = adventAgriculture;
            this.id = idx;
            this.totalSize = size;
        }

        @Override
        public void run() {
            Day5.MinNumber min = new Day5.MinNumber();
            for (long seed = seeds.start(); seed <= seeds.end(); ++seed) {
                min.update(this.ag.resolveLocation(seed));
            }
            if (REPORT_PROGRESS) {
                System.out.printf("\n %.2f %s",((this.id*1.0) / (this.totalSize * 1.0)) * 100, "%");
            }
            this.cf.complete(min.getValue());
        }

        public CompletableFuture<Long> getCF() {
            return this.cf;
        }
    }
}
