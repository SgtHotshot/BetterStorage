package net.mcft.copy.betterstorage.content;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.entity.EntityFrienderman;
import cpw.mods.fml.common.registry.EntityRegistry;

public final class Entities {
	
	private Entities() {  }
	
	public static void register() {
		
		EntityRegistry.registerModEntity(EntityFrienderman.class, "Frienderman", 1, BetterStorage.instance, 64, 4, true);
		
		Addon.registerEntitesAll();
		
	}
	
}
