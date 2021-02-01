package com.bilicraft.eclegbroken;

import com.bilicraft.eclegbroken.listener.ArmListener;
import com.bilicraft.eclegbroken.listener.RollListener;
import com.bilicraft.eclegbroken.watcher.DayTimeWatcher;
import com.bilicraft.eclegbroken.watcher.DragonWatcher;
import com.bilicraft.eclegbroken.watcher.PotionEffectWatcher;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("FieldMayBeFinal")
public final class ECArmBroken extends JavaPlugin {

    private PotionEffectWatcher watcher = new PotionEffectWatcher();
    private Roll roll = new Roll(this);
    private ArmListener listener = new ArmListener(this);
    private TimeCapsule capsule = new TimeCapsule(this);

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(listener,this);
        watcher.runTaskTimer(this,0,1);
        new DragonWatcher(this).runTaskTimer(this,0,10);
        Bukkit.getPluginManager().registerEvents(new RollListener(this),this);
        new DayTimeWatcher(this).runTaskTimer(this,0,5);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public PotionEffectWatcher getWatcher() {
        return watcher;
    }

    public Roll getRoll() {
        return roll;
    }

    public TimeCapsule getCapsule() {
        return capsule;
    }
}
