package me.harpan.bpsmod.client;

import me.harpan.bpsmod.BpsTracker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BpsmodClient implements ClientModInitializer {

    private static boolean visible = true;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> BpsTracker.onClientTick());

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.of("bpsmod", "hud"),
                (drawContext, tickCounter) -> {
                    if (!visible) {
                        return;
                    }

                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player == null) {
                        return;
                    }

                    TextRenderer textRenderer = client.textRenderer;

                    String bpsText = String.format("BPS: %.5f", BpsTracker.getBps());

                    double totalSeconds = BpsTracker.getTimeElapsed();
                    int minutes = (int) (totalSeconds / 60);
                    double seconds = (totalSeconds % 60);
                    String timeText = String.format("TIME: %02d:%05.2f", minutes, seconds);

                    String totalText = "TOTAL: " + BpsTracker.getBlocksBroken();

                    int width = drawContext.getScaledWindowWidth();

                    int x1 = width - textRenderer.getWidth(bpsText) - 5;
                    int x2 = width - textRenderer.getWidth(timeText) - 5;
                    int x3 = width - textRenderer.getWidth(totalText) - 5;

                    int HO = 35;
                    drawContext.drawText(textRenderer, bpsText, x1, HO, 0xFFFFFFFF, true);
                    drawContext.drawText(textRenderer, timeText, x2, HO + 10, 0xFFFFFFFF, true);
                    drawContext.drawText(textRenderer, totalText, x3, HO + 20, 0xFFFFFFFF, true);
                }
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("bps-reset")
                    .executes(context -> {
                        BpsTracker.reset();
                        context.getSource().sendFeedback(Text.literal("§aBPS Tracker has been reset."));
                        return 1;
                    })
            );
            dispatcher.register(ClientCommandManager.literal("bps-show")
                    .executes(context -> {
                        visible = true;
                        context.getSource().sendFeedback(Text.literal("§aBPS HUD is now visible."));
                        return 1;
                    })
            );
            dispatcher.register(ClientCommandManager.literal("bps-hide")
                    .executes(context -> {
                        visible = false;
                        context.getSource().sendFeedback(Text.literal("§cBPS HUD is now hidden."));
                        return 1;
                    })
            );
        });
    }
}