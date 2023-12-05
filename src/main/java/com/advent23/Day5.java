package com.advent23;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
        ArrayDeque<Range> ranges = new ArrayDeque<>();
        while(!seeds.isEmpty()) {
            ranges.addLast(new Range(seeds.removeFirst(), seeds.removeFirst()));
        }
        long minLocation = Long.MAX_VALUE;
        for (Range range : ranges) {
            for (int i = 0; i < range.end; ++i) {
                minLocation = Math.min(minLocation, adventAgriculture.resolveLocation(range.start + i));
            }
        }
        return AdventResult.ofLong(minLocation);
    }

    private static class AdventAgriculture {
        List<Long> seedsToPlant;
        HashMap<Range, AdventMapInfo> soilToFertilizer;
        private HashMap<Range, AdventMapInfo> fertilizerToWater;
        private HashMap<Range, AdventMapInfo> waterToLight;
        private HashMap<Range, AdventMapInfo> lightToTemperature;
        private HashMap<Range, AdventMapInfo> temperatureToHumidity;
        private HashMap<Range, AdventMapInfo> humidityToLocation;
        private HashMap<Range, AdventMapInfo> seedToSoil;

        public void readSeeds(List<Long> seeds) {
            this.seedsToPlant = seeds;
        }

        public int readSeedToSoil(List<String> collect, int start) {
            this.seedToSoil = new HashMap<>();
            return processMap(this.seedToSoil, collect, start, "soil-to");
        }

        public int readSoilToFertilizer(List<String> collect, int start) {
            this.soilToFertilizer = new HashMap<>();
            return processMap(this.soilToFertilizer, collect, start, "fertilizer-");
        }

        private int readFertilizerToWater(List<String> collect, int start) {
            this.fertilizerToWater = new HashMap<>();
            return processMap(this.fertilizerToWater, collect, start, "water-");
        }

        private int readWaterToLight(List<String> collect, int start) {
            this.waterToLight = new HashMap<>();
            return processMap(this.waterToLight, collect, start, "light-");
        }

        private int readLightToTemperature(List<String> collect, int start) {
            this.lightToTemperature = new HashMap<>();
            return processMap(this.lightToTemperature, collect, start, "temperature-");
        }

        private int readTemperatureToHumidity(List<String> collect, int start) {
            this.temperatureToHumidity = new HashMap<>();
            return processMap(this.temperatureToHumidity, collect, start, "humidity-");
        }

        private int readHumidityToLocation(List<String> collect, int start) {
            this.humidityToLocation = new HashMap<>();
            return processMap(this.humidityToLocation, collect, start, null);
        }

        private int processMap(HashMap<Range, AdventMapInfo> map, List<String> collect, int start, String stopKey) {
            for (int i = start; i >= 0 && i < collect.size(); ++i) {
                final String line = collect.get(i);
                if (stopKey != null && line.startsWith(stopKey)) {
                    return i - 1;
                }
                final List<Long> integers = AdventDayBase.asLong(line);
                AdventMapInfo ami = new AdventMapInfo(integers.get(0), integers.get(1), integers.get(2));
                map.put(new Range(ami.source, ami.source + ami.len), ami);
            }
            return -1;
        }

        public long resolveLocation(Long seed) {
            long soil = resolve(seedToSoil.entrySet(), seed);
            long fert = resolve(soilToFertilizer.entrySet(), soil);
            long water = resolve(fertilizerToWater.entrySet(), fert);
            long light = resolve(waterToLight.entrySet(), water);
            long tem = resolve(lightToTemperature.entrySet(), light);
            long hum = resolve(temperatureToHumidity.entrySet(), tem);
            return resolve(humidityToLocation.entrySet(), hum);
        }

        private Long resolve(Set<Map.Entry<Range, AdventMapInfo>> ranges, Long search) {
            TreeMap<Range, AdventMapInfo> tm = new TreeMap<>();
            tm.d
            for (Map.Entry<Range, AdventMapInfo> range : ranges) {
                if (range.getKey().belongs(search)) {
                    final long offset = range.getKey().end - search;
                    return range.getValue().dest + range.getValue().len - offset;
                }
            }
            return search;
        }
    }
    record AdventMapInfo(long dest, long source, long len){}
    record Range(long start, long end){
        boolean belongs(long q) {
            return start <= q && q <= end;
        }
    }
}
