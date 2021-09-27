package mk.plugin.lockeditems.listener;

import com.google.common.collect.Lists;
import mk.plugin.lockeditems.main.MainLockedItems;
import mk.plugin.lockeditems.utils.LIUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.LlamaInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class LIListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		LIUtils.checkInventory(player);
	}

	@EventHandler
	public void onCloseInventory(InventoryCloseEvent e) {
		Player player = (Player) e.getPlayer();
		LIUtils.checkInventory(player);
	}

	@EventHandler
	public void onHeldItem(PlayerItemHeldEvent e) {
		Player player = e.getPlayer();
		LIUtils.checkInventory(player);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDrop(PlayerDropItemEvent e) {
		var player = e.getPlayer();
		var is = e.getItemDrop().getItemStack();
		if (LIUtils.isLocked(is)) {
			e.setCancelled(true);
			player.sendMessage("§cKhông thể quăng đồ khóa");
			player.sendMessage("§cNếu muốn vứt đồ, có thể dùng lệnh /trash");
		}
//		if (!e.isCancelled()) {
//			ItemStack i = e.getItemDrop().getItemStack();
//			Player p = e.getPlayer();
//			LIUtils.addOwner(i, p.getName());
//		}

	}


	@EventHandler
	public void onPickup(EntityPickupItemEvent e) {
		if (e.isCancelled()) return;
		ItemStack i = e.getItem().getItemStack();
		if (e.getEntityType() == EntityType.PLAYER) {
			Player player = (Player) e.getEntity();
			if (player.hasPermission("lockeditems.ignore")) return;

			if (LIUtils.isLocked(i)) {
				if (!LIUtils.isOwner(i, player.getName())) {
					e.setCancelled(true);
					player.sendActionBar("§cCó vẻ như bạn đang cố gắng nhặt đồ khóa của người khác");
					return;
				}
				else LIUtils.removeOwner(i, player.getName());
			}

		}
		else {
			if (LIUtils.isLocked(i)) e.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemFrame(PlayerInteractEntityEvent e) {
		Player player = e.getPlayer();
		ItemStack i = player.getInventory().getItem(e.getHand());
		Entity et = e.getRightClicked();
		if (LIUtils.isLocked(i) && (et instanceof ItemFrame || player.isSneaking())) {
			LIUtils.addOwner(i, player.getName());
		}

	}
	
	@EventHandler
	public void onEquipArmorStand(PlayerInteractAtEntityEvent e) {
		Player player = e.getPlayer();
		ItemStack i = player.getInventory().getItem(e.getHand());
		Entity et = e.getRightClicked();
		if (LIUtils.isLocked(i) && et instanceof ArmorStand) {
			LIUtils.addOwner(i, player.getName());
		}

	}

	@EventHandler
	public void onEquipArmorStand(PlayerArmorStandManipulateEvent e) {
		Player player = e.getPlayer();
		ItemStack i = e.getArmorStandItem();
		if (!LIUtils.isOwner(i, player.getName())) {
			e.setCancelled(true);
			player.sendMessage("§cKhông phải đồ bạn!");
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		if (!e.getKeepInventory()) {
			List<ItemStack> list = Lists.newArrayList(e.getDrops());
			for (ItemStack item : list) {
				if (LIUtils.isLocked(item) && !LIUtils.haveOwnerName(item)) {
					e.getDrops().remove(item);
					LIUtils.addOwner(item, player.getName());
					player.getWorld().dropItemNaturally(player.getLocation(), item);
				}
			}

		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getInventory() instanceof LlamaInventory) {
			e.setCancelled(true);
			return;
		}
		ClickType clicktype = e.getClick();
		if (clicktype.equals(ClickType.NUMBER_KEY)) {
			e.setCancelled(true);
		} else {
			InventoryType it = e.getInventory().getType();

			// Ec
			{
				var current = e.getCurrentItem();
				if (LIUtils.isLocked(current) && !LIUtils.isOwner(current, e.getWhoClicked().getName())) {
					e.setCancelled(true);
					e.getWhoClicked().sendMessage("§cBạn không sở hữu đồ này!");
					return;
				}
			}


			// New 1.14+ and special inventories
			if (List.of(InventoryType.BARREL, InventoryType.SMOKER,
					InventoryType.BLAST_FURNACE, InventoryType.GRINDSTONE,
					InventoryType.STONECUTTER, InventoryType.SMITHING,
					InventoryType.DISPENSER).contains(it)) {
				if (e.getWhoClicked().hasPermission("lockeditems.admin")) return;
				var current = e.getCurrentItem();
				if (LIUtils.isLocked(current)) {
					e.setCancelled(true);
					e.getWhoClicked().sendMessage("§cĐồ khóa!");
					return;
				}
			}

			ItemStack cur;
			ItemStack click;
			if (!it.equals(InventoryType.CHEST) && !it.equals(InventoryType.HOPPER) && !it.equals(InventoryType.DROPPER)
					&& !it.equals(InventoryType.DISPENSER) && !it.equals(InventoryType.BREWING)
					&& !it.equals(InventoryType.FURNACE) && !it.equals(InventoryType.SHULKER_BOX)) {
				if (it.equals(InventoryType.ANVIL) && e.getRawSlot() == 2) {
					ItemStack i = e.getInventory().getItem(1);
					cur = e.getInventory().getItem(0);
					click = e.getInventory().getItem(2);
					if (LIUtils.isLocked(i) && !i.getType().equals(Material.BOOK)
							&& !i.getType().equals(Material.ENCHANTED_BOOK)) {
						e.setCancelled(true);
					}

					if (cur != null && cur.hasItemMeta() && click != null && click.hasItemMeta()
							&& click.getItemMeta().hasDisplayName()) {
						String name = click.getItemMeta().getDisplayName();
						ItemMeta im = cur.getItemMeta();
						if (im.hasDisplayName() && !im.getDisplayName().equals(name) || !im.hasDisplayName()) {
							e.setCancelled(true);
						}
					}

					if (LIUtils.isLocked(cur) && !click.getItemMeta().hasDisplayName()) {
						e.setCancelled(true);
					}
				}

			} else {
				Player player = (Player) e.getWhoClicked();
				if (!player.hasPermission("lockeditems.ignore")) {
					cur = e.getCursor();
					click = e.getCurrentItem();
					if (e.getClickedInventory() == player.getOpenInventory().getTopInventory()) {
						if (LIUtils.isLocked(click)) {
							if (!LIUtils.isOwner(click, player.getName())) {
								e.setCancelled(true);
							} else {
								if (e.isShiftClick() && LIUtils.haveOwnerName(click)) {
									LIUtils.removeOwner(click, player.getName());
								}

							}
						}
					} else if (!LIUtils.isOwner(click, player.getName())) {
						e.setCancelled(true);
					} else {
						LIUtils.addOwner(click, player.getName());
						if (LIUtils.isLocked(cur) && LIUtils.haveOwnerName(cur)) {
							e.setCancelled(true);
							if (e.getClickedInventory() != null && LIUtils.isOwner(cur, player.getName())) {
								LIUtils.removeOwner(cur, player.getName());
								player.setItemOnCursor(click);
								player.getInventory().setItem(e.getSlot(), cur);
								player.updateInventory();
								return;
							}
						}

					}
				}
			}
		}
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item != null && !item.getType().equals(Material.AIR) && LIUtils.isLocked(item)) {
			String command = e.getMessage().substring(1).split(" ")[0];
			if (LIUtils.containsCommand(command, LIUtils.BLOCKED_COMMANDS) || command.contains(":")) {
				e.setCancelled(true);
				player.sendMessage("§cKhông thể sử dụng lệnh này khi đang cầm item khóa!");
			}
		}

	}

}
