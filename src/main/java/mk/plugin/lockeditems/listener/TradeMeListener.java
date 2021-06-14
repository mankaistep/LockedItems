package mk.plugin.lockeditems.listener;

import me.Zrips.TradeMe.TradeMe;
import mk.plugin.lockeditems.main.MainLockedItems;
import mk.plugin.lockeditems.utils.LIUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class TradeMeListener implements Listener {

    @EventHandler
    public void onClickTradeMe(InventoryClickEvent e) {
        if (MainLockedItems.HAS_TRADEME) {
            ItemStack item = e.getCursor();
            Player player = (Player) e.getWhoClicked();
            if (e.getSlot() == 40 && e.getInventory().getType() == InventoryType.CRAFTING
                    && !LIUtils.isOwner(item, player.getName())) {
                e.setCancelled(true);
                player.sendMessage("§cĐồ này không phải của bạn huhu");
            }

            if (e.getClickedInventory() == e.getWhoClicked().getOpenInventory().getTopInventory()
                    && LIUtils.isLocked(item) && TradeMe.getInstance().getGUIManager().isOpenedGui(player)) {
                e.setCancelled(true);
                player.sendMessage("§cKhông thể giao dịch đồ khóa");
            }

        }
    }

    @EventHandler
    public void onClickTradeMe2(InventoryDragEvent e) {
        if (MainLockedItems.HAS_TRADEME) {
            Player player = (Player) e.getWhoClicked();
            if (TradeMe.getInstance().getGUIManager().isOpenedGui(player)) {
                e.setCancelled(true);
                player.sendMessage("§cKhông thể rải đồ khi trade!");
            }

        }
    }

    @EventHandler
    public void onTradeShiftClickTradeMe(InventoryOpenEvent e) {
        if (MainLockedItems.HAS_TRADEME) {
            Player player = (Player) e.getPlayer();
            Bukkit.getScheduler().runTaskAsynchronously(MainLockedItems.getMain(), () -> {
                if (TradeMe.getInstance().getGUIManager().isOpenedGui(player)) {
                    TradeMe.getInstance().getGUIManager().getGui(player).setAllowShift(false);
                }

            });
        }
    }
}
