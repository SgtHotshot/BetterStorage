package net.mcft.copy.betterstorage.inventory;

import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public class InventoryBackpackEquipped extends InventoryWrapper {
	
	public final EntityLivingBase carrier;
	public final EntityPlayer player;
	
	public InventoryBackpackEquipped(EntityLivingBase carrier, EntityPlayer player, IInventory inventory) {
		super(inventory);
		this.carrier = carrier;
		this.player = player;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		boolean carriesBackpack = (ItemBackpack.getBackpackItems(carrier, player).equals(base)); 
		double distance = player.getDistanceToEntity(carrier);
		double direction = DirectionUtils.angleDifference(carrier.renderYawOffset + 90.0F,
		                                                  DirectionUtils.angleBetween(carrier, player));
		return (carrier.isEntityAlive() && carriesBackpack &&
		        ((distance < 2) && (Math.abs(direction) > 135)) || (carrier == player));
	}
	
	@Override
	public void onInventoryChanged() {
		if(!(base instanceof InventoryStacks)) return;
		ItemBackpack.updateHasItems(carrier, ItemBackpack.getBackpackData(carrier));
	}
	
	@Override
	public void openChest() {
		int playersUsing = ++ItemBackpack.getBackpackData(carrier).playersUsing;
		ItemBackpack.setBackpackOpen(carrier, (playersUsing > 0));
	}
	@Override
	public void closeChest() {
		int playersUsing = --ItemBackpack.getBackpackData(carrier).playersUsing;
		ItemBackpack.setBackpackOpen(carrier, (playersUsing > 0));
	}
	
}
