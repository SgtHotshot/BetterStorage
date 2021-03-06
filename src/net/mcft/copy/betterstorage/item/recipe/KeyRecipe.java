package net.mcft.copy.betterstorage.item.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.mcft.copy.betterstorage.content.Items;
import net.mcft.copy.betterstorage.utils.InventoryUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class KeyRecipe extends ComboRecipe {
	
	private static ItemStack dummyResult = new ItemStack(Items.key);
	
	public KeyRecipe(int width, int height, ItemStack[] recipe) {
		super(width, height, recipe, dummyResult);
	}
	
	@Override
	public boolean canMirror() { return (width > 1); }
	
	@Override
	public boolean matches(InventoryCrafting crafting, World world) {
		if (!super.matches(crafting, world)) return false;
		// Just a key in the crafting matrix is not a valid recipe.
		int items = 0;
		for (int i = 0; i < crafting.getSizeInventory(); i++)
			if (crafting.getStackInSlot(i) != null) items++;
		return (items > 1);
	}
	
	@Override
	public boolean checkShapelessItems(InventoryCrafting crafting, List<ItemStack> shapelessItems) {
		// See if this is modifying a key or duplicating it.
		boolean modifyKey = (getRecipeSize() == 1);
		List<ItemStack> keys = InventoryUtils.findItems(crafting, Items.key);
		// Not a valid recipe if there's more than one key.
		if (keys.size() > 1) return false;
		ItemStack key = ((keys.size() > 0) ? keys.get(0) : null);
		int numIron = InventoryUtils.countItems(crafting, Item.ingotIron);
		// Not a valid recipe if any shapeless item
		// other than a key or dye is used.
		for (ItemStack stack : shapelessItems) {
			Item item = stack.getItem();
			if ((item != Items.key) &&
			    (item != Item.dyePowder)) return false;
		}
		return true;
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafting) {
		// See if this is modifying a key or duplicating it.
		boolean modifyKey = (getRecipeSize() == 1);
		ItemStack key = InventoryUtils.findItem(crafting, Items.key);
		List<ItemStack> dyes = InventoryUtils.findItems(crafting, Item.dyePowder);
		ItemStack result = (modifyKey ? key.copy() : new ItemStack(Items.key));
		if (key != null)
			result.setItemDamage(key.getItemDamage());
		if (dyes.size() >= 8)
			StackUtils.set(result, (byte)1, "fullColor");
		// Apply color
		if (dyes.size() > 0) {
			float r = 0, g = 0, b = 0;
			int amount = 0;
			for (ItemStack dye : dyes) {
				int colorIndex = BlockColored.getBlockFromDye(dye.getItemDamage());
				float[] color = EntitySheep.fleeceColorTable[colorIndex];
				r += color[0];
				g += color[1];
				b += color[2];
				amount++;
			}
			int rr = (int)(Math.min(1, r / amount) * 255);
			int gg = (int)(Math.min(1, g / amount) * 255);
			int bb = (int)(Math.min(1, b / amount) * 255);
			int color = ((rr << 16) | (gg << 8) | bb);
			StackUtils.set(result, color, "color");
		}
		return result;
	}
	
	public static KeyRecipe createKeyRecipe(Object... recipe) {
		int width = 0;
		int height = 0;
		List<String> recipeStrings = new ArrayList<String>();
		Map<Character, ItemStack> itemMap = new HashMap<Character, ItemStack>();
		char lastChar = ' ';
		for (Object obj : recipe) {
			if (obj instanceof String) {
				String str = (String)obj;
				width = Math.max(width, str.length());
				height++;
				recipeStrings.add(str);
			} else if (obj instanceof Character)
				lastChar = (Character)obj;
			else if (obj instanceof ItemStack)
				itemMap.put(lastChar, (ItemStack)obj);
			else if (obj instanceof Item)
				itemMap.put(lastChar, new ItemStack((Item)obj));
			else if (obj instanceof Block)
				itemMap.put(lastChar, new ItemStack((Block)obj));
		}
		ItemStack[] recipeItems = new ItemStack[width * height];
		for (int y = 0; y < height; y++) {
			String recipeString = recipeStrings.get(y);
			for (int x = 0; x < width; x++) {
				ItemStack item = null;
				if (x < recipeString.length()) {
					char chr = recipeString.charAt(x);
					if (itemMap.containsKey(chr))
						item = itemMap.get(chr);
				}
				recipeItems[x + y * width] = item;
			}
		}
		return new KeyRecipe(width, height, recipeItems);
	}
	
}
