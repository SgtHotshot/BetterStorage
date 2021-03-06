package net.mcft.copy.betterstorage.attachment;

import net.mcft.copy.betterstorage.api.IKey;
import net.mcft.copy.betterstorage.api.ILock;
import net.mcft.copy.betterstorage.api.ILockable;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LockAttachment extends ItemAttachment {
	
	public int hit = 0;
	
	public float wiggle = 0;
	public float wiggleStrength = 0.0F;
	
	public LockAttachment(TileEntity tileEntity, int subId) {
		super(tileEntity, subId);
		if (!(tileEntity instanceof ILockable))
			throw new IllegalArgumentException("tileEntity must be ILockable.");
	}
	
	@Override
	public boolean boxVisible(EntityPlayer player) {
		ItemStack holding = ((player != null) ? player.getCurrentEquippedItem() : null);
		return ((item != null) || StackUtils.isLock(holding));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IAttachmentRenderer getRenderer() { return LockAttachmentRenderer.instance; }
	
	@Override
	public void update() {
		hit = Math.max(0, hit - 1);
		if (tileEntity.worldObj.isRemote) {
			wiggle++;
			wiggleStrength = Math.max(0.0F, wiggleStrength * 0.9F - 0.1F);
		}
	}
	
	@Override
	public boolean interact(EntityPlayer player, EnumAttachmentInteraction type) {
		ItemStack holding = player.getCurrentEquippedItem();
		return ((type == EnumAttachmentInteraction.attack)
				? attack(player, holding)
				: use(player, holding));
	}
	
	private boolean attack(EntityPlayer player, ItemStack holding) {
		if (((ILockable)tileEntity).getLock() == null) return false;
		
		if (player.worldObj.isRemote)
			wiggleStrength = Math.min(20.0F, wiggleStrength + 12.0F);
		
		if ((hit <= 0) && canHurtLock(holding)) {
			hit = 10;
			tileEntity.worldObj.playSound(
					tileEntity.xCoord + 0.5, tileEntity.yCoord + 0.5, tileEntity.zCoord + 0.5,
					"random.break", 0.2F, 2.5F, false);
		}
		return true;
	}
	
	private boolean use(EntityPlayer player, ItemStack holding) {
		if (player.worldObj.isRemote) return false;
		
		ILockable lockable = (ILockable)tileEntity;
		ItemStack lock = lockable.getLock();
		
		if (lock == null) {
			if (StackUtils.isLock(holding) && lockable.isLockValid(holding)) {
				lockable.setLock(holding);
				player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
				return true;
			}
		} else if (StackUtils.isKey(holding)) {
			IKey keyType = (IKey)holding.getItem();
			ILock lockType = (ILock)lock.getItem();
			
			boolean success = keyType.unlock(holding, lock, true);
			lockType.onUnlock(lock, holding, lockable, player, success);
			if (!success) return true;
			
			if (player.isSneaking()) {
				lockable.setLock(null);
				AxisAlignedBB box = getHighlightBox();
				double x = (box.minX + box.maxX) / 2;
				double y = (box.minY + box.maxY) / 2;
				double z = (box.minZ + box.maxZ) / 2;
				EntityItem item = WorldUtils.spawnItem(player.worldObj, x, y, z, lock);
			} else lockable.useUnlocked(player);
			
			return true;
		}
		
		return false;
	}
	
	private boolean canHurtLock(ItemStack stack) {
		if (stack == null) return false;
		Item item = stack.getItem();
		return ((item instanceof ItemSword) ||
		        (item instanceof ItemPickaxe) ||
		        (item instanceof ItemAxe));
	}
	
}
