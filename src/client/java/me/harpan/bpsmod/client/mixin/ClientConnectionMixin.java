package me.harpan.bpsmod.client.mixin;

import net.minecraft.block.AirBlock;
import net.minecraft.block.CactusBlock;
import net.minecraft.client.MinecraftClient;
import me.harpan.bpsmod.BpsTracker;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"))
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {

        if (!(packet instanceof PlayerActionC2SPacket actionPacket)) {
            return;
        }

        if (actionPacket.getAction() != PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) {
            return;
        }

        var pos = actionPacket.getPos();
        var block = client.world.getBlockState(pos).getBlock();
        if (!(block instanceof AirBlock || block instanceof CactusBlock)) { // instabreaks + cactus
            return;
        }

        BpsTracker.onBlockBroken(pos);
    }
}