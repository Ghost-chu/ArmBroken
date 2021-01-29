package com.bilicraft.eclegbroken;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class TimeCapsule {
    private final ECArmBroken plugin;
    private final File file;
    private YamlConfiguration records;
    public TimeCapsule (ECArmBroken plugin){
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(),"records.yml");
        reload();
    }

    public void reload(){
        this.records = YamlConfiguration.loadConfiguration(this.file);
    }

    public void save(){
        try{
            this.records.save(this.file);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void newRecord(Player player, String content){
        records.set(player.getUniqueId().toString()+".name",player.getName());
        records.set(player.getUniqueId().toString()+".time",System.currentTimeMillis());
        records.set(player.getUniqueId().toString()+".content",content);
        save();
    }
}
