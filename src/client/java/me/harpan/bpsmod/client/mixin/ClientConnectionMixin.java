package me.harpan.bpsmod.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.CactusBlock;
import me.harpan.bpsmod.BpsTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ClientConnectionMixin {
    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), remap = false)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {

        if (!(packet instanceof ServerboundPlayerActionPacket actionPacket)) {
            return;
        }

        if (actionPacket.getAction() != ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            return;
        }

        var pos = actionPacket.getPos();
        var block = client.level.getBlockState(pos).getBlock();
        if (!(block instanceof AirBlock || block instanceof CactusBlock)) { // instabreaks + cactus
            return;
        }

        BpsTracker.onBlockBroken(pos);
    }
}