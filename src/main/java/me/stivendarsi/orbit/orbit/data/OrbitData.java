package me.stivendarsi.orbit.orbit.data;

import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;

public class OrbitData {
    private String identifier;
    private LocalDateTime start;
    private LocalDateTime end;
    private int maxLevel;
    private int levelMultiplier;
    private Pair<Prize, Prize>[] tiers;
}
