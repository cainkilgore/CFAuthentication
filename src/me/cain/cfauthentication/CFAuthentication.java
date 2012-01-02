package me.cain.cfauthentication;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class CFAuthentication extends JavaPlugin {

	Logger log = Logger.getLogger("Minecraft");
	public static Configuration cfg;

	public void onEnable()
	{
		cfg = new Configuration(new File(this.getDataFolder(), "players.yml"));
		log.info("[CFAuthentication] Authentication has been enabled.");
		log.info("[CFAuthentication] " + this.getDescription().getVersion() + " is loaded on your server.");
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvent(Type.PLAYER_MOVE, new PListener(), Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_JOIN, new PListener(), Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_QUIT, new PListener(), Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_CHAT, new PListener(), Priority.Normal, this);
		pm.registerEvent(Type.ENTITY_DAMAGE, new EListener(), Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_BREAK, new BListener(), Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_PLACE, new BListener(), Priority.Normal, this);
		cfg.load();
		cfg.save();
	}

	public void onDisable()
	{
		log.info("[CFAuthentication] Authentication has been disabled.");
	}

	public static String hex(byte[] array) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; ++i) {
			sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).toUpperCase().substring(1,3));
		}
		return sb.toString();
	}


	public static String md5(String message) { 
		try { 
			MessageDigest md = MessageDigest.getInstance("MD5"); 
			return hex (md.digest(message.getBytes("CP1252"))); 
		} catch (NoSuchAlgorithmException e) { } catch (UnsupportedEncodingException e) { } 
		return null;
	}

	public boolean onCommand(CommandSender s, Command c, String l, String [] args)
	{
		Player p = ((Player) s);
		if(l.equalsIgnoreCase("login"))
		{
			if(cfg.getProperty("player." + p.getName() + ".loggedin").equals(true))
			{
				p.sendMessage(ChatColor.RED + "Error: You are already logged in.");
			}
			else
			{
				if(args.length < 1)
				{
					p.sendMessage("/login password");
				}
				else
				{
					if(cfg.getProperty("player." + p.getName() + ".password") != null)
					{
						if(cfg.getProperty("player." + p.getName() + ".password").equals(md5(args[0])))
						{
							cfg.setProperty("player." + p.getName() + ".loggedin", true);
							p.sendMessage(ChatColor.GREEN + "Successfully logged in.");
							log.info("[CFAuthentication] " + p.getName() + " has logged in.");
							cfg.save();
						}
						else
						{
							p.sendMessage(ChatColor.RED + "Error: Incorrect password.");
						}
					}
					else
					{
						p.sendMessage(ChatColor.RED + "Error: You must register first.");
					}
				}
			}
		}

		if(l.equalsIgnoreCase("register"))
		{
			if(cfg.getProperty("player." + p.getName() + ".password") != null)
			{
				p.sendMessage(ChatColor.RED + "Error: You are already registered.");
			}
			else
			{
				if(args.length < 1)
				{
					p.sendMessage("/register [password]");
				}
				else
				{
					cfg.setProperty("player." + p.getName() + ".password", md5(args[0]));
					p.sendMessage(ChatColor.GREEN + "Successfully registered.");
					log.info("[CFAuthentication] " + p.getName() + " successfully registered.");
					cfg.setProperty("player." + p.getName() + ".loggedin", true);
					cfg.save();
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + p.getName() + " has registered!");
				}
			}
		}

		if(l.equalsIgnoreCase("unregister"))
		{
			if(p.isOp())
			{
				if(args.length < 1)
				{
					p.sendMessage("/unregister [name]");
				}
				else
				{
					if(cfg.getProperty("player." + args[0]) != null)
					{
						cfg.removeProperty("player." + args[0]);
						p.sendMessage(ChatColor.GREEN + args[0] + " has been unregistered!");
						log.info("[CFAuthentication] " + args[0] + " has been unregistered.");
						cfg.save();

						if(Bukkit.getServer().getPlayer(args[0]).isOnline())
						{
							Bukkit.getServer().getPlayer(args[0]).kickPlayer("You have been unregistered.");
						}
						else
						{
							return false;
						}
					}
					else
					{
						p.sendMessage(ChatColor.RED + "Error: This player never registered before.");
					}
				}
			}
			else
			{
				p.sendMessage(ChatColor.RED + "Error: You must be OP to unregister people.");
			}
		}

		if(l.equalsIgnoreCase("changepassword"))
		{
			if(args.length < 2)
			{
				p.sendMessage(ChatColor.RED + "/changepassword [old] [new]");
			}
			else
			{
				if(cfg.getProperty("player." + p.getName()) != null)
				{
					if(cfg.getProperty("player." + p.getName() + ".password").equals(md5(args[0])))
					{
						cfg.setProperty("player." + p.getName() + ".password", md5(args[1]));
						p.kickPlayer("Password changed! Login to re-authenticate.");
						cfg.save();
					}
					else
					{
						p.sendMessage(ChatColor.RED + "Error: Incorrect Password.");
					}
				}
				else
				{
					p.sendMessage(ChatColor.RED + "Error: Please register first.");
				}
			}
		}
		
		if(l.equalsIgnoreCase("forceregister"))
		{
			if(args.length < 2)
			{
				p.sendMessage(ChatColor.RED + "/forceregister [username] [password]");
			}
			else
			{
				if(p.isOp())
				{
					if(cfg.getProperty("player." + args[0]) != null)
					{
						p.sendMessage(ChatColor.RED + "Error: This account already exists.");
					}
					else
					{
						cfg.setProperty("player." + args[0] + ".password", md5(args[1]));
						p.sendMessage(ChatColor.GREEN + "Force-registered " + args[0] + "!");
						cfg.save();
					}
				}
				else
				{
					p.sendMessage(ChatColor.RED + "Error: You must be OP to use this.");
				}
			}
		}
		return false;
	}

}
