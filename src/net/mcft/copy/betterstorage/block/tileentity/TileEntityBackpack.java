package net.mcft.copy.betterstorage.block.tileentity;

import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.misc.Constants;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;

public class TileEntityBackpack extends TileEntityContainer {
	
	public ItemStack stack;
	
	/** Affects if items drop when the backpack is destroyed. */
	public boolean equipped = false;
	
	public boolean brokenInCreative = false;
	
	// Equipping / unequipping
	
	public boolean equip(EntityLivingBase carrier) {
		equipped = true;
		equipped = worldObj.setBlockToAir(xCoord, yCoord, zCoord);
		if (equipped) ItemBackpack.setBackpack(carrier, stack, contents);
		return equipped;
	}
	
	public void unequip(EntityLivingBase carrier) {
		if (!worldObj.isRemote) {
			ItemStack[] items = ItemBackpack.getBackpackData(carrier).contents;
			// Move items from the player backpack data to this tile entity.
			if (items != null) System.arraycopy(items, 0, contents, 0, items.length);
		}
		ItemBackpack.removeBackpack(carrier);
	}
	
	// TileEntityContainer stuff
	
	@Override
	public String getName() { return Constants.containerBackpack; }
	@Override
	public int getRows() { return Config.backpackRows; }
	
	// Update entity
	
	@Override
	protected float getLidSpeed() { return 0.2F; }
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		double x = xCoord + 0.5;
		double y = yCoord + 0.5;
		double z = zCoord + 0.5;
		
		String sound = Block.soundSnowFootstep.getStepSound();
		// Play sound when opening
		if ((lidAngle > 0.0F) && (prevLidAngle <= 0.0F))
			worldObj.playSoundEffect(x, y, z, sound, 1.0F, 0.5F);
		// Play sound when closing
		if ((lidAngle < 0.2F) && (prevLidAngle >= 0.2F))
			worldObj.playSoundEffect(x, y, z, sound, 0.8F, 0.3F);
	}
	
	// Tile entity synchronization
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setCompoundTag("stack", stack.writeToNBT(new NBTTagCompound()));
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, compound);
	}
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		NBTTagCompound compound = packet.customParam1;
		stack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("stack"));
	}
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		stack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("stack"));
	}
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setCompoundTag("stack", stack.writeToNBT(new NBTTagCompound()));
	}
	
}
