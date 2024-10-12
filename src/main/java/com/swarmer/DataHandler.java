package com.swarmer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class DataHandler {
    public static String PLUGIN_DIRECTORY = System.getProperty("user.home").replace("\\", "/") + "/.runelite/swarmer/";

    public static List<String> getRaidList() {
        try {
            if (!Files.exists(Path.of(PLUGIN_DIRECTORY))) {
                Files.createDirectories(Path.of(PLUGIN_DIRECTORY));
                return null;
            }
        } catch (Exception e) {}

        List<String> raids = new ArrayList<String>();
        try (Stream<Path> files = Files.list(Path.of(PLUGIN_DIRECTORY))) {
            for (Path file : files.collect(toList())) {
                if (file.getFileName().toString().endsWith(".json")) {
                    raids.add(file.getFileName().toString().replace(".json", ""));
                }
            }
        }
        catch (Exception e) {
            return null;
        }
        return raids;

    }

    public static List<RaidData> getRaidData(String raid) {

        try {
            if (!Files.exists(Path.of(PLUGIN_DIRECTORY))) {
                Files.createDirectories(Path.of(PLUGIN_DIRECTORY));
                return null;
            }
        } catch (Exception e) {}

        if (!Files.exists(Path.of(PLUGIN_DIRECTORY + raid + ".json"))) {
            return null;
        }

        Gson gson = new Gson();
        try (FileReader reader = new FileReader(PLUGIN_DIRECTORY + raid + ".json")) {
            Type listType = new TypeToken<java.util.List<RaidData>>() {}.getType();

            return gson.fromJson(reader, listType);
        } catch (Exception e) {}
        return null;
    }

    public static void saveRaidData(String raid, List<RaidData> raidDataList) {
        Gson gson = new Gson();
        try {
            if (!Files.exists(Path.of(PLUGIN_DIRECTORY))) {
                Files.createDirectories(Path.of(PLUGIN_DIRECTORY));
            }
            if (!Files.exists(Path.of(PLUGIN_DIRECTORY + raid + ".json"))) {
                Files.createFile(Path.of(PLUGIN_DIRECTORY + raid + ".json"));
            }
            Files.writeString(Path.of(PLUGIN_DIRECTORY + raid + ".json"), gson.toJson(raidDataList));
        } catch (Exception e) {}
    }
}
