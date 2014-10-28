package com.slymask3.instantblocks.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.slymask3.instantblocks.handler.ConfigurationHandler;
import com.slymask3.instantblocks.init.ModBlocks;
import com.slymask3.instantblocks.reference.Colors;
import com.slymask3.instantblocks.reference.Names;
import com.slymask3.instantblocks.reference.Strings;
import com.slymask3.instantblocks.sound.SoundTypeLiquid;
import com.slymask3.instantblocks.utility.BuildHelper;

public class BlockInstantLava extends BlockIB {
	
    public BlockInstantLava() {
        super(ModBlocks.ibLava, Names.Blocks.IB_LAVA, Material.lava, new SoundTypeLiquid("random.fizz", 1.0F, 1.0F), 0.5F);
        setTextures(Blocks.lava);
        setTextureBooleans(false, false, false, false, false, false);
        setLightLevel(1.0F);
    }
    
	public static int checkLava = 0;
	
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		if (checkLava == 0) {
			triggerLavaMixEffects(world, x, y ,z, random);
			checkLava = 30;
		}
	}
	
	protected void triggerLavaMixEffects(World world, int x, int y, int z, Random rand) {
        BuildHelper.sound(world, "random.fizz", x, y, z);
        
        for (int l = 0; l < 8; ++l) {
            world.spawnParticle("largesmoke", (double)x + Math.random(), (double)y + 1.2D, (double)z + Math.random(), 0.0D, 0.0D, 0.0D);
        }
    }
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z) {
        float f = 0.0625F;
        return AxisAlignedBB.getBoundingBox((double)((float)x + f), (double)y, (double)((float)z + f), (double)((float)(x + 1) - f), (double)((float)(y + 1) - f), (double)((float)(z + 1) - f));
    }
	
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity entity) {
        entity.attackEntityFrom(DamageSource.lava, 1);
        entity.setFire(5);
    }
	
	public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }
    
    public void build(World world, int x, int y, int z, EntityPlayer player) {
    	Block liquid2 = Blocks.lava; // FIX TO lavaStill? 1.7.10
		Block liquid = Blocks.lava; // FIX TO lavaMoving? 1.7.10
		Block air = Blocks.air;
		
		int max = 6;
		
		if (ConfigurationHandler.simpleWL == true) {
			BuildHelper.checkLiquid5S(world, x, y, z, liquid);
			BuildHelper.buildLiquid4S(world, x, y, z, liquid);
			BuildHelper.checkLiquid5UndoS(world, x, y, z, liquid);
		} else {
			BuildHelper.checkLiquid5(world, x, y, z, liquid);
			BuildHelper.buildLiquid4(world, x, y, z, liquid);
			BuildHelper.checkLiquid5Undo(world, x, y, z, liquid);
		}
		
		if (BuildHelper.built == true) {
			world.setBlock(x, y, z, liquid);
			BuildHelper.keepBlocks(world, x, y, z, ModBlocks.ibLava);
			BuildHelper.sound(world, ConfigurationHandler.sound, x, y, z);
			BuildHelper.effectFull(world, "reddust", x, y, z);
			BuildHelper.msg(player, "\u00a7aInstant Lava created with " + (BuildHelper.counter + 1) + " lava blocks.", Colors.a);
			BuildHelper.xp(world, player, ConfigurationHandler.xp);
			if (ConfigurationHandler.useWands == true) {
				player.getCurrentEquippedItem().damageItem(1, player);
			}
		} else {
			if (world.getBlock(x+1, y, z) != Blocks.air && world.getBlock(x-1, y, z) != Blocks.air && world.getBlock(x, y, z+1) != Blocks.air && world.getBlock(x, y, z-1) != Blocks.air && world.getBlock(x, y-1, z) != Blocks.air) {
				world.setBlock(x, y, z, liquid);
				BuildHelper.keepBlocks(world, x, y, z, ModBlocks.ibLava);
				BuildHelper.sound(world, ConfigurationHandler.sound, x, y, z);
				BuildHelper.effectFull(world, "reddust", x, y, z);
				BuildHelper.msg(player, Strings.lavaCreate1, Colors.a);
				BuildHelper.xp(world, player, ConfigurationHandler.xp);
				if (ConfigurationHandler.useWands == true) {
					player.getCurrentEquippedItem().damageItem(1, player);
				}
			} else {
				BuildHelper.msg(player, "\u00a7cPrevented from creating over " + ConfigurationHandler.max + " lava blocks.", Colors.c);
			}
		}
		
		BuildHelper.counter = 0;
		BuildHelper.c2 = 0;
		BuildHelper.c5 = 0;
		BuildHelper.built = false;
    }
}