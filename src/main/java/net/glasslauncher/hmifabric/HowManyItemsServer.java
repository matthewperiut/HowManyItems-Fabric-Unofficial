package net.glasslauncher.hmifabric;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.entity.Item;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.server.MinecraftServer;
import net.modificationstation.stationapi.api.event.registry.MessageListenerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.network.ModdedPacketHandler;
import net.modificationstation.stationapi.api.packet.Message;
import net.modificationstation.stationapi.api.packet.PacketHelper;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.ModID;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.server.event.network.PlayerLoginEvent;
import net.modificationstation.stationapi.api.util.Colours;
import net.modificationstation.stationapi.api.util.Null;
import net.modificationstation.stationapi.api.util.math.ColorHelper;

public class HowManyItemsServer {

    @Entrypoint.ModID
    public static final ModID MODID = Null.get();

    @EventListener
    public void handleLogin(PlayerLoginEvent event) {
        if (((ModdedPacketHandler) event.player.packetHandler).isModded()) {
            Message customData = new Message(Identifier.of("hmifabric:handshake"));
            customData.booleans = new boolean[]{true};
            PacketHelper.sendTo(((MinecraftServer) FabricLoader.getInstance().getGameInstance()).serverPlayerConnectionManager.getServerPlayer(event.loginPacket.username), customData);
        }
    }

    @EventListener
    public void registerMessageListeners(MessageListenerRegistryEvent event) {
        Registry.register(event.registry, Identifier.of(MODID, "giveItem"), HowManyItemsServer::handleGivePacket);
        Registry.register(event.registry, Identifier.of(MODID, "heal"), HowManyItemsServer::handleHealPacket);
    }

    public static void handleGivePacket(PlayerBase player, Message packet) {
        if (((MinecraftServer) FabricLoader.getInstance().getGameInstance()).serverPlayerConnectionManager.isOp(player.name)) {
            Object[] objects = packet.deserializeObjects();
            if(objects == null || objects.length < 1) {
                player.sendMessage(Colours.RED + "No items found to spawn?");
                return;
            }
            ItemInstance itemInstance = (ItemInstance) objects[0]; // Is this stupid? I have no idea.
            Item itemEntity = new Item(player.level, player.x, player.y, player.z, itemInstance);
            player.level.spawnEntity(itemEntity);
            player.sendMessage(Colours.GRAY + "Spawned some " + itemInstance.getType().getTranslatedName() + "...");
        }
        else if (!((MinecraftServer) FabricLoader.getInstance().getGameInstance()).serverPlayerConnectionManager.isOp(player.name)) {
            player.sendMessage(Colours.RED + "You need to be opped in order to give yourself items!");
        }
    }

    public static void handleHealPacket(PlayerBase player, Message packet) {
        if (((MinecraftServer) FabricLoader.getInstance().getGameInstance()).serverPlayerConnectionManager.isOp(player.name)) {
            player.addHealth(Integer.MAX_VALUE/2); // High to allow mods that mess with player health to be supported.
        }
    }
}
