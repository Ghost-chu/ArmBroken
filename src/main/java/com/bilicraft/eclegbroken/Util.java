package com.bilicraft.eclegbroken;

import org.bukkit.ChatColor;

import java.text.DecimalFormat;

public class Util {
    private static final DecimalFormat formatter = new java.text.DecimalFormat("#.00");
    public static String formatDouble(double d){
        return ChatColor.GREEN+formatter.format(d)+ChatColor.YELLOW;
    }
}
