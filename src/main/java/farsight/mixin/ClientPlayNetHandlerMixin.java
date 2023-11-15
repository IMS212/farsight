package farsight.mixin;

import farsight.FarsightClientChunkManager;
import farsight.FarsightMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPlayNetHandlerMixin
{
    @Shadow
    private ClientLevel level;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Redirect(method = "handleSetChunkCacheRadius", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundSetChunkCacheRadiusPacket;getRadius()I"))
    private int onViewDistChange(final ClientboundSetChunkCacheRadiusPacket sUpdateViewDistancePacket)
    {
        return FarsightMod.config.getCommonConfig().maxRenderDistance;
    }

    @Redirect(method = "handleLogin", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundLoginPacket;chunkRadius()I"))
    private int onJoinGame(final ClientboundLoginPacket sJoinGamePacket)
    {
        return FarsightMod.config.getCommonConfig().maxRenderDistance;
    }

    @Inject(method = "handleForgetLevelChunk", at = @At("HEAD"), cancellable = true)
    private void onChunkUnload(
      final ClientboundForgetLevelChunkPacket clientboundForgetLevelChunkPacket,
      final CallbackInfo ci)
    {
        PacketUtils.ensureRunningOnSameThread(clientboundForgetLevelChunkPacket, (ClientPacketListener) (Object) this, this.minecraft);
        ClientChunkCache clientChunkManager = level.getChunkSource();
        if (clientChunkManager instanceof FarsightClientChunkManager && ((FarsightClientChunkManager) clientChunkManager).delayUnload(clientboundForgetLevelChunkPacket))
        {
            ((FarsightClientChunkManager) clientChunkManager).packetListener = (ClientPacketListener) (Object) this;
            ci.cancel();
        }
    }
}
