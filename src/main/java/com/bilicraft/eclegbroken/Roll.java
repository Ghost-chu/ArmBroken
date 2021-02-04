package com.bilicraft.eclegbroken;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Roll {
    private final ECArmBroken plugin;
    private final Random random = new Random();
    private final Map<Integer, Map.Entry<String, String>> rollMap = new HashMap<>(); // 存储ROLL点数对应符号

    public Roll(ECArmBroken plugin) {
        this.plugin = plugin;
        this.rollMap.put(0, new AbstractMap.SimpleEntry<>("F", "糟透了"));
        this.rollMap.put(1, new AbstractMap.SimpleEntry<>("E", "真糟糕"));
        this.rollMap.put(2, new AbstractMap.SimpleEntry<>("D", "海星"));
        this.rollMap.put(3, new AbstractMap.SimpleEntry<>("C", "还不错"));
        this.rollMap.put(4, new AbstractMap.SimpleEntry<>("B", "幸运"));
        this.rollMap.put(5, new AbstractMap.SimpleEntry<>("A", "非常幸运"));
    }

    /**
     * 系统骰子，每个MC日下午2点启动
     */
    public void systemRoll() {
        Map.Entry<Integer, Map.Entry<String, String>> roll = getRandom();
        playRollAnimation(new ArrayList<>(Bukkit.getOnlinePlayers()), () -> {
            sendTitle(new ArrayList<>(Bukkit.getOnlinePlayers()), roll.getValue().getValue(), roll.getValue().getValue(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            if (roll.getKey() < 2) {
                //糟透了
                PotionEffectType[] badLuck = {PotionEffectType.HUNGER,
                        PotionEffectType.BAD_OMEN,
                        PotionEffectType.SLOW,
                        PotionEffectType.SLOW_DIGGING,
                        PotionEffectType.UNLUCK,
                        PotionEffectType.WEAKNESS,
                };
                plugin.getWatcher().setEffectList(ImmutableList.of(createRandomLevelPotionEffect(badLuck[random.nextInt(badLuck.length)]), createRandomLevelPotionEffect(badLuck[random.nextInt(badLuck.length)])));
            } else if (roll.getKey() < 4) {
                //还可以
                plugin.getWatcher().setEffectList(Collections.emptyList());
            } else {
                //欧气爆棚
                PotionEffectType[] goodLuck = {PotionEffectType.ABSORPTION,
                        PotionEffectType.DAMAGE_RESISTANCE,
                        PotionEffectType.FAST_DIGGING,
                        PotionEffectType.FIRE_RESISTANCE,
                        PotionEffectType.HEALTH_BOOST,
                        PotionEffectType.JUMP,
                        PotionEffectType.LUCK,
                        PotionEffectType.NIGHT_VISION,
                        PotionEffectType.HERO_OF_THE_VILLAGE,
                        PotionEffectType.REGENERATION,
                        PotionEffectType.SPEED,
                        PotionEffectType.WATER_BREATHING,
                        PotionEffectType.CONDUIT_POWER
                };
                plugin.getWatcher().setEffectList(ImmutableList.of(createRandomLevelPotionEffect(goodLuck[random.nextInt(goodLuck.length)]), createRandomLevelPotionEffect(goodLuck[random.nextInt(goodLuck.length)])));

            }

        });

    }

    /**
     * Roll下决定恢复多少饱食度
     */
    public void foodRoll(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        event.setCancelled(true);
        int newFoodLevel = event.getFoodLevel();
        int addedFoodLevel = newFoodLevel - ((Player) event.getEntity()).getFoodLevel();

        Map.Entry<Integer, Map.Entry<String, String>> roll = getRandom();
        playRollAnimation(ImmutableList.of(player), () -> {
                    sendTitle(ImmutableList.of(player), roll.getValue().getKey(), roll.getValue().getValue(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                    int feed;
                    float saturation;
                    if (roll.getKey() < 2) {
                        feed = 0;
                        saturation = 3;
                    } else if (roll.getKey() < 4) {
                        feed = 1;
                        saturation = 5;
                    } else {
                        feed = 2;
                        saturation = 10;
                    }
                    player.setFoodLevel(((Player) event.getEntity()).getFoodLevel()+feed);
                    player.setSaturation(((Player) event.getEntity()).getSaturation()+saturation);
                }
        );

    }

    private PotionEffect createRandomLevelPotionEffect(PotionEffectType effectType) {
        return new PotionEffect(effectType, 240, random.nextInt(2) );
    }

    public Map.Entry<Integer, Map.Entry<String, String>> getRandom() {
        Set<Map.Entry<Integer, Map.Entry<String, String>>> all = this.rollMap.entrySet();
        return new ArrayList<>(all).get(this.random.nextInt(this.rollMap.size()));
    }

    public void sendTitle(List<Player> playerList, String title, String subtitle, Sound sound) {
        //放点声音和特效
        playerList.forEach(player -> {
            player.sendTitle(ChatColor.GOLD + title, subtitle, 0, 80, 0);
            if(sound == null)
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 0.5f, 1.0f);
            else
                player.playSound(player.getLocation(), sound, 0.5f, 1.0f);
        });
    }

    public void sendRandomRollTitle(List<Player> playerList) {
        Map.Entry<Integer, Map.Entry<String, String>> random = getRandom();
        sendTitle(playerList, random.getValue().getKey(), random.getValue().getValue(),null);
    }


    public void playRollAnimation(List<Player> playerList, Runnable callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                sendRandomRollTitle(playerList);
                try {
                    for (int i = 0; i < 30; i++) {
                        sendRandomRollTitle(playerList);
                        Thread.sleep(70);
                    }
                    callback.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
