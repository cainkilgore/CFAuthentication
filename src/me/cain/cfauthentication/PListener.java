package me.cain.cfauthentication;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PListener extends PlayerListener {
	
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		CFAuthentication.cfg.setProperty("player." + p.getName() + ".loggedin", false);
		CFAuthentication.cfg.save();
		
		if(CFAuthentication.cfg.getProperty("player." + p.getName() + ".password") != null)
		{
			p.sendMessage(ChatColor.RED + "[CFAuthentication] Please login!");
			p.sendMessage(ChatColor.RED + "[CFAuthentication] /login [password]");
			CFAuthentication.cfg.setProperty("player." + p.getName() + ".loggedin", false);
			CFAuthentication.cfg.save();
		}
		else
		{
			p.sendMessage(ChatColor.RED + "[CFAuthentication] Please register!");
			p.sendMessage(ChatColor.RED + "[CFAuthentication] /register [password]");
			CFAuthentication.cfg.setProperty("player." + p.getName() + ".loggedin", false);
			CFAuthentication.cfg.save();
		}
		
		return;
	}
	
	public void onPlayerChat(PlayerChatEvent e)
	{
		Player p = e.getPlayer();
		if(CFAuthentication.cfg.getProperty("player." + p.getName() + ".loggedin").equals(false))
		{
			e.setCancelled(true);
		}
		else
		{
			return;
		}
	}
	
	public void onPlayerMove(PlayerMoveEvent e)
	{
		Player p = e.getPlayer();
		if(CFAuthentication.cfg.getProperty("player." + p.getName() + ".password") == null) {
			p.sendMessage("Please register! /register [password]");
		}
		
		if(CFAuthentication.cfg.getProperty("player." + p.getName() + ".loggedin").equals(false))
		{
			e.setCancelled(true);
		}
		else {
		return;
		}
	}
	
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		Player p = e.getPlayer();
		CFAuthentication.cfg.setProperty("player." + p.getName() + ".loggedin", false);
		return;
	}

}
