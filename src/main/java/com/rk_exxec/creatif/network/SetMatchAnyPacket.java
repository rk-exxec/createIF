package com.rk_exxec.creatif.network;

import com.rk_exxec.creatif.CreateContentFilter;
import com.rk_exxec.creatif.filter.ContentFilterMenu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SetMatchAnyPacket(int containerId, boolean value) {

    public static void encode(SetMatchAnyPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.containerId);
        buffer.writeBoolean(packet.value);
    }

    public static SetMatchAnyPacket decode(FriendlyByteBuf buffer) {
        return new SetMatchAnyPacket(buffer.readInt(), buffer.readBoolean());
    }

    public static void handle(SetMatchAnyPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || player.containerMenu.containerId != packet.containerId)
                return;
            if (player.containerMenu instanceof ContentFilterMenu menu)
                menu.setMatchAny(packet.value);
        });
        context.setPacketHandled(true);
    }

    public static void send(int containerId, boolean value) {
        CreateContentFilter.CHANNEL.sendToServer(new SetMatchAnyPacket(containerId, value));
    }
}