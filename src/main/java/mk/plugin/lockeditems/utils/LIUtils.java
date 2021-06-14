package mk.plugin.lockeditems.utils;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LIUtils {
	
	public static String UNLOCKED_LINE;
	public static int LOCK_FEE;
	public static List<String> BLOCKED_COMMANDS;
	
	public static boolean isLocked(ItemStack i) {
		return !isUnlocked(i);
	}
	
	private static boolean isUnlocked(ItemStack i) {
		if (i == null) return true;
		if (!i.hasItemMeta()) return true;
		if (!i.getItemMeta().hasDisplayName()) return true;
		if (!i.getItemMeta().hasLore()) return false;
		return i.getItemMeta().getLore().contains(UNLOCKED_LINE);
	}

	public static boolean haveOwnerName(ItemStack item) {
		if (!isLocked(item)) return false;
		if (!item.hasItemMeta()) return false;
		if (!item.getItemMeta().hasDisplayName()) return false;
		return item.getItemMeta().getDisplayName().matches("(.*)§c§l\\[\\w+]");
	}

	public static boolean isOwner(ItemStack item, String o) {
		return !haveOwnerName(item) ? true : item.getItemMeta().getDisplayName().endsWith(" §c§l[" + o + "]");
	}

	public static void addOwner(ItemStack item, String o) {
		if (!haveOwnerName(item) && isLocked(item)) {
			ItemMeta im = item.getItemMeta();
			im.setDisplayName(im.getDisplayName() + " §c§l[" + o + "]");
			item.setItemMeta(im);
		}

	}

	public static void removeOwner(ItemStack item, String o) {
		if (isOwner(item, o) && isLocked(item)) {
			if (!item.hasItemMeta()) return;
			if (!item.getItemMeta().hasDisplayName()) return;
			ItemMeta im = item.getItemMeta();
			im.setDisplayName(im.getDisplayName().replace(" §c§l[" + o + "]", ""));
			item.setItemMeta(im);
		}

	}

	public static void checkInventory(Player player) {
		ItemStack[] itemInvs = player.getInventory().getContents();

		for (int i = 0; i < itemInvs.length; ++i) {
			ItemStack item = itemInvs[i];
			if (isLocked(item)) {
				if (isOwner(item, player.getName())) {
					removeOwner(item, player.getName());
				} else {
					String itemname = item.getItemMeta().getDisplayName();
					player.sendMessage("§aVật phẩm §f" + itemname + " §akhông phải đồ của bạn");
					player.getWorld().dropItem(player.getLocation(), item);
					itemInvs[i] = null;
				}
			}
		}

		player.getInventory().setContents(itemInvs);
		ItemStack[] armors = player.getInventory().getArmorContents();

		for (int i = 0; i < armors.length; ++i) {
			ItemStack item = armors[i];
			if (isLocked(item)) {
				if (isOwner(item, player.getName())) {
					removeOwner(item, player.getName());
				} else {
					String itemname = item.getItemMeta().getDisplayName();
					player.sendMessage("§aVật phẩm §f" + itemname + " §akhông phải đồ của bạn");
					player.getWorld().dropItem(player.getLocation(), item);
					itemInvs[i] = null;
				}
			}
		}

		player.getInventory().setArmorContents(armors);
	}

	public static boolean containsCommand(String cmd, List<String> cmds) {
		for (String s : cmds) {
			if (s.equalsIgnoreCase(cmd)) {
				return true;
			}
		}
		return false;
	}
	
	public static void unlock(ItemStack is) {
		if (is == null) return;
		ItemMeta meta = is.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore.contains(LIUtils.UNLOCKED_LINE)) return;
		lore.add(LIUtils.UNLOCKED_LINE);
		meta.setLore(lore);
		is.setItemMeta(meta);
	}
	
	public static void lock(ItemStack is) {
		if (is == null) return;
		if (!is.hasItemMeta()) return;
		if (!is.getItemMeta().hasLore()) return;
		if (is.getItemMeta().getLore().contains(LIUtils.UNLOCKED_LINE)) {
			ItemMeta meta = is.getItemMeta();
			List<String> lore = meta.getLore();
			lore.remove(LIUtils.UNLOCKED_LINE);
			meta.setLore(lore);
			is.setItemMeta(meta);
		}
	}
	
	
}
