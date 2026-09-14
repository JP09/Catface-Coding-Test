package com.catface.codingtest.manager;

import com.catface.codingtest.CatfaceCodingTestMod;
import com.catface.codingtest.model.CameraKeyframe;
import com.catface.codingtest.model.KeyframeSequence;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KeyframeStorage {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // DTOs decouple the on-disk format from the domain model, so renaming/refactoring
    // CameraKeyframe later doesn't silently corrupt save files.
    private record KeyframeDto (String name, double x, double y, double z, float yaw, float pitch) {}
    private record SequenceDto (String name, List<KeyframeDto> keyframes) {}

    private static Path getFile(MinecraftServer server) {
        Path dir = server.getWorldPath(LevelResource.ROOT).resolve("catfacecodingtestmod");
        return dir.resolve("camkey_sequences.json");
    }

    public static void save(MinecraftServer server, KeyframeManager manager) {
        try{
            Path file = getFile(server);
            Files.createDirectories(file.getParent());

            List<SequenceDto> out = new ArrayList<>();
            for (Map.Entry<String, KeyframeSequence> entry : manager.getSequences().entrySet()) {
                List<KeyframeDto> kfs = new ArrayList<>();
                for (CameraKeyframe kf : entry.getValue().getKeyframes()) {
                    kfs.add(new KeyframeDto(kf.getName(), kf.getX(), kf.getY(), kf.getZ(), kf.getYaw(), kf.getPitch()));
                }
                out.add(new SequenceDto(entry.getKey(), kfs));
            }

            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(out, writer);
            }
        } catch (IOException e) {
            CatfaceCodingTestMod.LOGGER.error("Failed to save camkey sequences", e);
        }
    }

    public static void load(MinecraftServer server, KeyframeManager manager) {
        Path file = getFile(server);
        if (!Files.exists(file)) return;

        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            List<SequenceDto> in = GSON.fromJson(reader, new TypeToken<List<SequenceDto>>() {}.getType());
            if (in == null) return;

            for (SequenceDto seqDto : in) {
                KeyframeSequence sequence = manager.getOrCreateSequence(seqDto.name());
                for (KeyframeDto kfDto : seqDto.keyframes()) {
                    sequence.addKeyframe(new CameraKeyframe(
                            kfDto.name(), kfDto.x(), kfDto.y(), kfDto.z(), kfDto.yaw(), kfDto.pitch()));
                }
            }
        } catch (IOException e) {
            CatfaceCodingTestMod.LOGGER.error("Failed to load camkey sequences", e);
        }
    }
}
