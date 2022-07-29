package com.slymask3.instantblocks.network;

import com.slymask3.instantblocks.block.InstantBlock;
import com.slymask3.instantblocks.block.entity.*;
import com.slymask3.instantblocks.network.packet.*;
import com.slymask3.instantblocks.util.ClientHelper;
import com.slymask3.instantblocks.util.Helper;
import com.slymask3.instantblocks.util.SchematicHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PacketHelper {
    public enum PacketID { CLIENT, SKYDIVE, STATUE, HARVEST, TREE, SCHEMATIC, SCHEMATIC_UPDATE, SKYDIVE_UPDATE, TREE_UPDATE }

    private static void activate(InstantPacket message, Level world, Player player) {
        if(message.activate) {
            InstantBlock block = (InstantBlock)Helper.getBlock(world,message.pos);
            block.activate(world,message.pos,player);
        }
    }

    public static void handleClient(ClientPacket message, Player player) {
        if(message.particles != ClientHelper.Particles.NONE.ordinal()) {
            Level world = player.getLevel();
            ClientHelper.playSound(world, message.pos, ClientHelper.Particles.values()[message.particles]);
            ClientHelper.showParticles(world, message.pos, ClientHelper.Particles.values()[message.particles]);
        }
        if(!message.message.isEmpty()) {
            ClientHelper.sendMessage(player, message.message, message.variable);
        }
    }

    public static void handleSkydive(SkydivePacket message, Player player) {
        Level world = player.getLevel();
        SkydiveBlockEntity blockEntity = (SkydiveBlockEntity)world.getBlockEntity(message.pos);
        if(blockEntity != null) {
            blockEntity.update(message.colors, message.radius, message.teleport, message.colorSetsIndex);
            activate(message, world, player);
        }
    }

    public static void handleStatue(StatuePacket message, Player player) {
        Level world = player.getLevel();
        StatueBlockEntity blockEntity = (StatueBlockEntity)world.getBlockEntity(message.pos);
        if(blockEntity != null) {
            blockEntity.update(message._username, message._head, message._body, message._armLeft, message._armRight, message._legLeft, message._legRight, message._rgb);
            activate(message, world, player);
        }
    }

    public static void handleHarvest(HarvestPacket message, Player player) {
        Level world = player.getLevel();
        HarvestBlockEntity blockEntity = (HarvestBlockEntity)world.getBlockEntity(message.pos);
        if(blockEntity != null) {
            blockEntity.update(message._logOak, message._logSpruce, message._logBirch, message._logJungle, message._logAcacia, message._logDark, message._wheat, message._carrot, message._potato, message._cactus, message._pumpkin, message._melon, message._sugarcane, message._cocoa, message._mushroom, message._netherwart, message._replant);
            activate(message, world, player);
        }
    }

    public static void handleTree(TreePacket message, Player player) {
        Level world = player.getLevel();
        TreeBlockEntity blockEntity = (TreeBlockEntity)world.getBlockEntity(message.pos);
        if(blockEntity != null) {
            blockEntity.update(message.tree, message.hollowLogs, message.hollowLeaves, message.airInside, message.hugeTreesIndex);
            activate(message, world, player);
        }
    }

    public static void handleSchematic(SchematicPacket message, Player player) {
        Level world = player.getLevel();
        SchematicBlockEntity blockEntity = (SchematicBlockEntity)world.getBlockEntity(message.pos);
        if(blockEntity != null) {
            blockEntity.update(message._schematic, message._center, message._air);
            activate(message, world, player);
        }
    }

    public static void handleSchematicUpdate(SchematicUpdatePacket message, Player player) {
        SchematicHelper.SCHEMATICS_LIST = message.schematics;
        ClientHelper.showScreen(ClientHelper.Screen.SCHEMATIC,player,player.getLevel(),message.pos);
    }

    public static void handleSkydiveUpdate(SkydiveUpdatePacket message, Player player) {
        ClientHelper.SKYDIVE_PRESETS = message.presets;
        ClientHelper.showScreen(ClientHelper.Screen.SKYDIVE,player,player.getLevel(),message.pos);
    }

    public static void handleTreeUpdate(TreeUpdatePacket message, Player player) {
        ClientHelper.HUGE_TREES = message.hugeTrees;
        ClientHelper.showScreen(ClientHelper.Screen.TREE,player,player.getLevel(),message.pos);
    }
}