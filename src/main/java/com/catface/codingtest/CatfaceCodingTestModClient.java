package com.catface.codingtest;

import com.catface.codingtest.playback.CameraController;
import com.catface.codingtest.playback.CameraPlayback;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CatfaceCodingTestMod.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = CatfaceCodingTestMod.MODID, value = Dist.CLIENT)
public class CatfaceCodingTestModClient {
    public CatfaceCodingTestModClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        CatfaceCodingTestMod.LOGGER.info("HELLO FROM CLIENT SETUP");
        CatfaceCodingTestMod.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    private static CameraController cameraController;
    private static CameraPlayback cameraPlayback;

    public static void init() {
        cameraController = new CameraController();
        cameraPlayback = new CameraPlayback(cameraController);
    }

    public static CameraPlayback getCameraPlayback() {
        if (cameraPlayback == null) {
            init();
        }
        return cameraPlayback;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (cameraPlayback != null && cameraPlayback.isPlaying()) {
            cameraPlayback.update();
        }
    }
}
