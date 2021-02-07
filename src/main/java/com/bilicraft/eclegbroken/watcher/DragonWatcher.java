package com.bilicraft.eclegbroken.watcher;

import com.bilicraft.eclegbroken.ECArmBroken;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EnderDragon;
import org.bukkit.scheduler.BukkitRunnable;

public class DragonWatcher extends BukkitRunnable {
    private final ECArmBroken plugin;
    public DragonWatcher(ECArmBroken plugin){
        this.plugin = plugin;
    }
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            if(world.getEnvironment() != World.Environment.THE_END)
                continue;
            world.getEntities().stream().filter(entity -> entity instanceof EnderDragon).forEach(edragon->{
                EnderDragon dragon = (EnderDragon)edragon;
                AttributeInstance maxHealth = dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                AttributeInstance maxAttack = dragon.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
                if (maxAttack != null && maxAttack.getBaseValue() != maxAttack.getDefaultValue() * 3.00d) {
                    //提升300%末影龙攻击力
                    maxAttack.setBaseValue(maxAttack.getDefaultValue() * 3.00d);
                    plugin.getLogger().info("末影龙攻击力已设置为 >> "+maxAttack.getDefaultValue() * 3.00d);

                }
                if (maxHealth != null && maxHealth.getBaseValue() != 15000.0d) {
                    //提升末影龙血量至5000HP
                    maxHealth.setBaseValue(15000.0d);
                    dragon.setHealth(15000.0d);
                    plugin.getLogger().info("末影龙血量已设置为 >> "+dragon.getMaxHealth());
                }
            });
        }
    }
}
