package me.harpan.bpsmod.client;

import me.harpan.bpsmod.BpsTracker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class BpsmodClient implements ClientModInitializer {

    private static boolean visible = true;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(_ -> BpsTracker.onClientTick());

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath("bpsmod", "hud"),
                (drawContext, _) -> {
                    if (!visible) {
                        return;
                    }

                    Minecraft client = Minecraft.getInstance();
                    if (client.player == null) {
                        return;
                    }

                    Font font = client.font;

                    String bpsText = String.format("BPS: %.5f", BpsTracker.getBps());

                    double totalSeconds = BpsTracker.getTimeElapsed();
                    int minutes = (int) (totalSeconds / 60);
                    double seconds = (totalSeconds % 60);
                    String timeText = String.format("TIME: %02d:%05.2f", minutes, seconds);

                    String totalText = "TOTAL: " + BpsTracker.getBlocksBroken();

                    int width = drawContext.guiWidth();

                    int x1 = width - font.width(bpsText) - 5;
                    int x2 = width - font.width(timeText) - 5;
                    int x3 = width - font.width(totalText) - 5;

                    int HO = 35;
                    drawContext.text(font, bpsText, x1, HO, 0xFFFFFFFF, true);
                    drawContext.text(font, timeText, x2, HO + 10, 0xFFFFFFFF, true);
                    drawContext.text(font, totalText, x3, HO + 20, 0xFFFFFFFF, true);
                }
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, _) -> {
            dispatcher.register(ClientCommands.literal("bps-reset")
                    .executes(context -> {
                        BpsTracker.reset();
                        context.getSource().sendFeedback(Component.literal("§aBPS Tracker has been reset."));
                        return 1;
                    })
            );
            dispatcher.register(ClientCommands.literal("bps-show")
                    .executes(context -> {
                        visible = true;
                        context.getSource().sendFeedback(Component.literal("§aBPS HUD is now visible."));
                        return 1;
                    })
            );
            dispatcher.register(ClientCommands.literal("bps-hide")
                    .executes(context -> {
                        visible = false;
                        context.getSource().sendFeedback(Component.literal("§cBPS HUD is now hidden."));
                        return 1;
                    })
            );
        });
    }
}