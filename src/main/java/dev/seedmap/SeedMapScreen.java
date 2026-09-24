package dev.seedmap;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class SeedMapScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget seedField;
    private WorldDimension dimension = WorldDimension.OVERWORLD;
    private List<StructureCandidate> candidates = List.of();
    private int centerChunkX;
    private int centerChunkZ;

    public SeedMapScreen(Screen parent) {
        super(Text.literal("Seed Map"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = 22;
        seedField = new TextFieldWidget(textRenderer, left, 30, 260, 20, Text.literal("World seed"));
        seedField.setText(SeedMapClient.hasKnownSeed() ? Long.toString(SeedMapClient.getKnownSeed()) : "");
        seedField.setPlaceholder(Text.literal("Enter world seed"));
        addDrawableChild(seedField);
        addDrawableChild(ButtonWidget.builder(Text.literal("Locate"), button -> locate())
                .dimensions(left + 268, 30, 78, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Overworld"), button -> select(WorldDimension.OVERWORLD))
                .dimensions(left, 58, 106, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Nether"), button -> select(WorldDimension.NETHER))
                .dimensions(left + 112, 58, 106, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("End"), button -> select(WorldDimension.END))
                .dimensions(left + 224, 58, 106, 20).build());
        if (SeedMapClient.hasKnownSeed()) {
            locate();
        }
    }

    private void select(WorldDimension next) {
        dimension = next;
        locate();
    }

    private void locate() {
        long seed;
        try {
            seed = Long.parseLong(seedField.getText().trim());
        } catch (NumberFormatException ignored) {
            candidates = List.of();
            return;
        }
        SeedMapClient.rememberSeed(seed);
        MinecraftClient client = MinecraftClient.getInstance();
        BlockPos pos = client.player == null ? BlockPos.ORIGIN : client.player.getBlockPos();
        centerChunkX = pos.getX() >> 4;
        centerChunkZ = pos.getZ() >> 4;
        candidates = SeedMapLocator.locate(seed, dimension.key, centerChunkX, centerChunkZ, 512);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.fill(12, 12, width - 12, height - 12, 0xE91A2428);
        context.drawTextWithShadow(textRenderer, title, 22, 18, 0xF4E9D8);
        context.drawTextWithShadow(textRenderer, Text.literal("Dimension: " + dimension.label), 22, 88, 0xB7D5CF);
        drawMap(context);
        context.drawTextWithShadow(textRenderer, Text.literal("M: close   |   click a marker for coordinates"), 22, height - 24, 0x9EB7B1);
        super.render(context, mouseX, mouseY, delta);
    }

    private void drawMap(DrawContext context) {
        int mapLeft = 370;
        int mapTop = 30;
        int mapRight = width - 28;
        int mapBottom = height - 38;
        context.fill(mapLeft, mapTop, mapRight, mapBottom, 0xFF26383A);
        for (int x = mapLeft; x < mapRight; x += 32) {
            context.fill(x, mapTop, x + 1, mapBottom, 0x183F6260);
        }
        for (int y = mapTop; y < mapBottom; y += 32) {
            context.fill(mapLeft, y, mapRight, y + 1, 0x183F6260);
        }
        int centerX = (mapLeft + mapRight) / 2;
        int centerY = (mapTop + mapBottom) / 2;
        context.fill(centerX - 3, centerY - 3, centerX + 4, centerY + 4, 0xFFF0C674);
        int scale = Math.max(1, Math.min(mapRight - mapLeft, mapBottom - mapTop) / 1024);
        for (int index = 0; index < candidates.size(); index++) {
            StructureCandidate candidate = candidates.get(index);
            int x = centerX + (candidate.chunkX() - centerChunkX) * scale;
            int y = centerY + (candidate.chunkZ() - centerChunkZ) * scale;
            if (x < mapLeft + 4 || x > mapRight - 4 || y < mapTop + 4 || y > mapBottom - 4) {
                continue;
            }
            int color = 0xFF6CC0A8 + (index % 3) * 0x00101000;
            context.fill(x - 3, y - 3, x + 4, y + 4, color);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    private enum WorldDimension {
        OVERWORLD("Overworld", World.OVERWORLD),
        NETHER("Nether", World.NETHER),
        END("End", World.END);

        private final String label;
        private final net.minecraft.registry.RegistryKey<World> key;

        WorldDimension(String label, net.minecraft.registry.RegistryKey<World> key) {
            this.label = label;
            this.key = key;
        }
    }
}
