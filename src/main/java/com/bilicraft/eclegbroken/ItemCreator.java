package com.bilicraft.eclegbroken;

import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class ItemCreator {
    private static Random random = new Random();
    public static ItemStack makeGaoJiPickaxe(){
        ItemStack stack = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA+"镐击镐");
        meta.setLore(ImmutableList.of("只要可以举起镐子，便能轻易击碎黑曜石的存在..."));
        meta.addEnchant(Enchantment.DIG_SPEED,5,true);
        meta.addEnchant(Enchantment.DURABILITY,3,true);
        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS,3,true);
        Damageable damageable = (Damageable)meta;
        damageable.setDamage(random.nextInt(30)+1);
        stack.setItemMeta(meta);
        return stack;
    }
}
