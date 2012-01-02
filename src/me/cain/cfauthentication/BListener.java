package me.cain.cfauthentication;

import org.bukkit.ChatColor;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BListener extends BlockListener {
	
	public void onBlockPlace(BlockPlaceEvent e)
	{
		if(CFAuthentication.cfg.getProperty("player." + e.getPlayer().getName() + ".loggedin").equals(false))
		{
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "Error: Please login first.");
			e.getPlayer().sendMessage(ChatColor.RED + "/login [password]");
		}
		else
		{
			return;
		}
	}
	
	public void onBlockBreak(BlockBreakEvent e)
	{
		if(CFAuthentication.cfg.getProperty("player." + e.getPlayer().getName() + ".loggedin").equals(false))
		{
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "Error: Please login first.");
			e.getPlayer().sendMessage(ChatColor.RED + "/login [password]");
		}
		else
		{
			return;
		}
	}

}
