package com.bilicraft.eclegbroken.watcher;

import com.bilicraft.eclegbroken.ECArmBroken;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class DayTimeWatcher extends BukkitRunnable {
    boolean todayRolled = false;
    private ECArmBroken plugin;

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
        World world = Bukkit.getWorlds().get(0); //Main world
        if (world.getTime() > 14 * 1000 && !todayRolled) {
            plugin.getRoll().systemRoll();
            plugin.getLogger().info("新的一天ROLL!");
            todayRolled = true;
        } else if (world.getTime() < 14 * 1000 && todayRolled) {
            todayRolled = false;
        }
    }
}
