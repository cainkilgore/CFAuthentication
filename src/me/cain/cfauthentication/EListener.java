package me.cain.cfauthentication;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class EListener extends EntityListener {
	
	public void onEntityDamage(EntityDamageEvent e)
	{
		if(e instanceof Player)
		{
			if(CFAuthentication.cfg.getProperty("player." + ((Player) e).getName() + ".loggedin").equals(false))
			{
				e.setCancelled(true);
				((Player) e).setHealth(20);
			}
			else
			{
				return;
			}
		}
		else
		{
			return;
		}
	}

}
