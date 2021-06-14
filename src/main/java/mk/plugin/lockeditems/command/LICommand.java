package mk.plugin.lockeditems.command;

import mk.plugin.lockeditems.main.MainLockedItems;
import mk.plugin.lockeditems.utils.LIUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LICommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		
		try {
			
			if (args[0].equalsIgnoreCase("reload")) {
				MainLockedItems.getMain().reloadConfig();
				sender.sendMessage("§aReloaded");
			}
			
			else if (args[0].equalsIgnoreCase("lock")) {
				Player player = (Player) sender;
				ItemStack is = player.getInventory().getItemInMainHand();
				LIUtils.lock(is);
				player.updateInventory();
				player.sendMessage("§aKhóa thành công!");
			}
			
			else if (args[0].equalsIgnoreCase("unlock")) {
				Player player = (Player) sender;
				ItemStack is = player.getInventory().getItemInMainHand();
				LIUtils.unlock(is);
				player.updateInventory();
				player.sendMessage("§aMở khóa thành công!");
			}
			
		}
		catch (ArrayIndexOutOfBoundsException e) {
			sendHelp(sender);
		}
		
		return false;
	}
	
	public void sendHelp(CommandSender sender) {
		sender.sendMessage("");
		sender.sendMessage("§2/lockeditems reload: §aReload plugin");
		sender.sendMessage("§2/lockeditems lock: §aLock your item");
		sender.sendMessage("§2/lockeditems unlock: §aUnlock your item");
		sender.sendMessage("§2/lockeditems lockgui <player>: §aOpen lock gui");
		sender.sendMessage("");
	}

}
