package net.mcft.copy.betterstorage.client.gui;

import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.utils.RenderUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBetterStorage extends GuiContainer {
	
	public final ContainerBetterStorage container;
	public final String title;
	
	private final int columns;
	private final int rows;
	
	public int getColumns() { return columns; }
	public int getRows() { return rows; }
	
	public GuiBetterStorage(ContainerBetterStorage container) {
		super(container);
		
		this.container = container;
		IInventory inv = container.inventory;
		title = (inv.isInvNameLocalized() ? inv.getInvName() : StatCollector.translateToLocal(inv.getInvName()));
		columns = container.getColumns();
		rows = container.getRows();
		
		xSize = 14 + columns * 18;
		ySize = 100 + rows * 18 + container.separation;
		
		container.setUpdateGui(this);
	}
	public GuiBetterStorage(EntityPlayer player, int columns, int rows, IInventory inventory) {
		this(new ContainerBetterStorage(player, inventory, columns, rows));
	}
	public GuiBetterStorage(EntityPlayer player, int columns, int rows, String title, boolean localized) {
		this(player, columns, rows, new InventoryBasic(title, localized, columns * rows));
	}
	public GuiBetterStorage(EntityPlayer player, int columns, int rows, String title) {
		this(player, columns, rows, title, false);
	}
	
	protected ResourceLocation getResource() {
		if (columns <= 9) return new ResourceLocation("textures/gui/container/generic_54.png");
		else return Resources.reinforcedChestContainer;
	}
	
	protected int getTextureWidth() { return 256; }
	protected int getTextureHeight() { return 256; }
	
	public void update(int par1, int par2) {  }
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRenderer.drawString(title, 8, 6, 0x404040);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8 + (xSize - 176) / 2, ySize - 94, 0x404040);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1, 1, 1, 1);
		mc.renderEngine.func_110577_a(getResource());
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		int w = getTextureWidth();
		int h = getTextureHeight();
		int m = 93 + container.separation;
		RenderUtils.drawTexturedModalRect(x, y, 0, 0, xSize, ySize - m, zLevel, w, h);
		RenderUtils.drawTexturedModalRect(x, y + ySize - m, 0, 115, xSize, m, zLevel, w, h);
	}
	
}
