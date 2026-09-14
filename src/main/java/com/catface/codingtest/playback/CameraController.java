package com.catface.codingtest.playback;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameType;


public class CameraController {

    private final Minecraft minecraft;

    private GameType previousGame;

    public CameraController() {
        this.minecraft = Minecraft.getInstance();
    }

    public void setCameraPosition(
            double x,
            double y,
            double z,
            float yaw,
            float pitch) {
        if (minecraft.gameRenderer == null) {
            return;
        }

        Entity camera = minecraft.getCameraEntity();

        if(camera == null) {
            return;
        }

        camera.setPos(x, y, z);
        camera.setYRot(yaw);
        camera.setXRot(pitch);
    }

    public ServerPlayer getServerPlayer() {
        MinecraftServer server = minecraft.getSingleplayerServer();
        if (server == null || minecraft.player == null) return null;

        return server.getPlayerList().getPlayer(minecraft.player.getUUID());
    }

    public void startCamera() {
        ServerPlayer player = getServerPlayer();
        if (player == null) return;

        previousGame = player.gameMode.getGameModeForPlayer();
        player.setGameMode(GameType.SPECTATOR);
    }

    public void stopCamera() {
        ServerPlayer player = getServerPlayer();
        if (player == null || previousGame == null) return;

        player.setGameMode(previousGame);
        previousGame = null;
    }
}
