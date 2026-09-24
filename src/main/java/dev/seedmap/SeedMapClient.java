package dev.seedmap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

public final class SeedMapClient implements ClientModInitializer {
    public static final String MOD_ID = "seed_map";
    private static KeyBinding openMapKey;
    private static long lastKnownSeed;
    private static boolean hasKnownSeed;

    @Override
    public void onInitializeClient() {
        openMapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.seed_map.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M,
                KeyBinding.Category.create(Identifier.of(MOD_ID, "main"))));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMapKey.wasPressed()) {
                client.setScreen(new SeedMapScreen(client.currentScreen));
            }
            updateSeedFromIntegratedServer(client);
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden || client.currentScreen != null) {
                return;
            }
            String status = hasKnownSeed ? "Seed Map: " + lastKnownSeed + "  [M]" : "Seed Map: press [M] and enter a seed";
            drawContext.drawTextWithShadow(client.textRenderer, Text.literal(status), 8, 8, 0xD8E7E5);
        });
    }

    private static void updateSeedFromIntegratedServer(MinecraftClient client) {
        if (client.getServer() != null && client.world != null) {
            lastKnownSeed = client.getServer().getOverworld().getSeed();
            hasKnownSeed = true;
        }
    }

    public static long getKnownSeed() {
        return lastKnownSeed;
    }

    public static boolean hasKnownSeed() {
        return hasKnownSeed;
    }

    public static void rememberSeed(long seed) {
        lastKnownSeed = seed;
        hasKnownSeed = true;
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
