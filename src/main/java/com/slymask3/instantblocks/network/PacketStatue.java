package com.slymask3.instantblocks.network;

import com.slymask3.instantblocks.block.instant.BlockInstantStatue;
import com.slymask3.instantblocks.util.BuildHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketStatue {
	int _x, _y, _z;
	String _username;
	boolean _head, _body, _armLeft, _armRight, _legLeft, _legRight;
	boolean _rgb;

	public PacketStatue(int x, int y, int z, String username, boolean head, boolean body, boolean armLeft, boolean armRight, boolean legLeft, boolean legRight, boolean rgb) {
		_x = x;
		_y = y;
		_z = z;
		_username = username;
		_head = head;
		_body = body;
		_armLeft = armLeft;
		_armRight = armRight;
		_legLeft = legLeft;
		_legRight = legRight;
		_rgb = rgb;
	}

	public static void encode(PacketStatue message, FriendlyByteBuf buffer) {
		buffer.writeInt(message._x);
		buffer.writeInt(message._y);
		buffer.writeInt(message._z);
		buffer.writeUtf(message._username);
		buffer.writeBoolean(message._head);
		buffer.writeBoolean(message._body);
		buffer.writeBoolean(message._armLeft);
		buffer.writeBoolean(message._armRight);
		buffer.writeBoolean(message._legLeft);
		buffer.writeBoolean(message._legRight);
		buffer.writeBoolean(message._rgb);
	}

	public static PacketStatue decode(FriendlyByteBuf buffer) {
		int x = buffer.readInt();
		int y = buffer.readInt();
		int z = buffer.readInt();
		String username = buffer.readUtf();
		boolean head = buffer.readBoolean();
		boolean body = buffer.readBoolean();
		boolean armLeft = buffer.readBoolean();
		boolean armRight = buffer.readBoolean();
		boolean legLeft = buffer.readBoolean();
		boolean legRight = buffer.readBoolean();
		boolean rgb = buffer.readBoolean();
		return new PacketStatue(x,y,z,username,head,body,armLeft,armRight,legLeft,legRight,rgb);
	}

	public static class Handler {
		public static void handle(PacketStatue message, Supplier<NetworkEvent.Context> context) {
			context.get().enqueueWork(() -> {
				Player player = context.get().getSender();
				Level world = player.getLevel();

				BlockInstantStatue block = (BlockInstantStatue) BuildHelper.getBlock(world,message._x, message._y, message._z);
				boolean built = block.build(world, message._x, message._y, message._z, player, message._username, message._head, message._body, message._armLeft, message._armRight, message._legLeft, message._legRight, message._rgb);
				if(built) {
					block.afterBuild(world, message._x, message._y, message._z, player);
				}
			});
			context.get().setPacketHandled(true);
		}
	}
}