package com.bilicraft.eclegbroken.listener;

import com.bilicraft.eclegbroken.ECArmBroken;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class RollListener implements Listener {
    private final ECArmBroken plugin;

    public RollListener(ECArmBroken plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void foodLevelChanges(FoodLevelChangeEvent event){
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if(player.getFoodLevel() >= event.getFoodLevel())
            return;
        plugin.getRoll().foodRoll(event);
    }
}
