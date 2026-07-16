package me.stivendarsi.orbit.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.stivendarsi.orbit.orbit.data.OrbitData;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import static me.stivendarsi.orbit.Orbit.mainHandler;
import static me.stivendarsi.orbit.Orbit.orbitInstance;

public class OrbitPlaceholders extends PlaceholderExpansion {

    @Override
    @NotNull
    public String getAuthor() {
        return String.join(", ", orbitInstance().getPluginMeta().getAuthors()); //
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "orbit";
    }

    @Override
    @NotNull
    public String getVersion() {
        return orbitInstance().getPluginMeta().getVersion(); //
    }

    @Override
    public boolean persist() {
        return true; // 
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("current")) {
            OrbitData orbitData = mainHandler().orbitHandler().getCurrentOrbit();
            if (orbitData != null) return orbitData.identifier();
            return "";
        }
        return null; // 
    }
}