package com.bilicraft.eclegbroken.listener;

import com.bilicraft.eclegbroken.ECArmBroken;
import com.bilicraft.eclegbroken.Util;
import com.google.gson.Gson;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.*;

import static org.bukkit.ChatColor.*;

public class ArmListener implements Listener {
    private final Random random = new Random();
    private final ECArmBroken plugin;

    public ArmListener(ECArmBroken plugin) {
        this.plugin = plugin;
    }

    //我的胳膊断了
    @EventHandler
    public void mineBlock(BlockBreakEvent event) {
        PotionEffect effect = event.getPlayer().getPotionEffect(PotionEffectType.FAST_DIGGING);
        if(effect != null){
            if(random.nextInt(10) == 1)
                return;
        }

        Material material = event.getBlock().getType();
        //Mineable blocks
        if (!material.isSolid() || material.isTransparent())
            return;
        if (Tag.SAPLINGS.isTagged(material))
            return;
        if (Tag.CROPS.isTagged(material))
            return;
        if (Tag.LEAVES.isTagged(material))
            return;
        if (Tag.SIGNS.isTagged(material))
            return;
        if (Tag.TALL_FLOWERS.isTagged(material))
            return;
        if (Tag.FLOWERS.isTagged(material))
            return;
        if (Tag.BEEHIVES.isTagged(material))
            return;
        if (Tag.SAND.isTagged(material))
            return;
        if (Tag.WALL_SIGNS.isTagged(material))
            return;
        if (material == Material.GRASS)
            return;
        if (material == Material.MELON)
            return;
        if (material == Material.PUMPKIN)
            return;
        event.setCancelled(true);
    }

    //你的就是我的
    @EventHandler
    public void dropAllItems(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof InventoryHolder))
            return;
        ItemStack[] contents = ((InventoryHolder) event.getEntity()).getInventory().getContents();
        for (ItemStack stack : contents) {
            if (event.getDrops().contains(stack))
                continue;
            event.getDrops().add(stack);
        }
    }

    private final Gson gson = new Gson();

    @EventHandler
    public void craftCrystal(CraftItemEvent event) {
        if (event.getRecipe().getResult().getType() == Material.END_CRYSTAL)
            event.setCancelled(true);
    }

    @EventHandler
    public void enderCrystalExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof EnderCrystal) {
            List<String> crystals = plugin.getConfig().getStringList("crystals");
            crystals.add(gson.toJson(event.getEntity().getLocation().serialize()));
            plugin.getConfig().set("crystals", crystals);
            plugin.saveConfig();
        }
    }

    private void respawnAllCrystals() {
        List<String> crystals = plugin.getConfig().getStringList("crystals");
        crystals.forEach(str -> {
            Location loc = Location.deserialize(gson.fromJson(str, Map.class));
            loc.getWorld().spawnEntity(loc, EntityType.ENDER_CRYSTAL);
        });
        plugin.getConfig().set("crystals", null);
        plugin.saveConfig();
    }


    @EventHandler
    public void targeting(EntityTargetEvent event) {
        if (event.getEntity().getWorld().getEnvironment() != World.Environment.THE_END)
            return;
        if (!(event.getEntity() instanceof Monster) && !(event.getEntity() instanceof Boss))
            return;
        List<Player> playersInTheWorld = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (p.getWorld().equals(event.getEntity().getWorld()))
                if (p.getGameMode() == GameMode.SURVIVAL)
                    playersInTheWorld.add(p);
        });
        if (playersInTheWorld.isEmpty()) {
            event.setTarget(null);
            return;
        }
        Player nearPlayer = null;
        double nearDistance = Double.MAX_VALUE;
        for (Player player : playersInTheWorld) {
            if (nearPlayer == null) {
                nearPlayer = player;
                nearDistance = event.getEntity().getLocation().distanceSquared(player.getLocation());
                continue;
            }
            if (event.getEntity().getLocation().distanceSquared(player.getLocation()) < nearDistance) {
                nearPlayer = player;
                nearDistance = event.getEntity().getLocation().distanceSquared(player.getLocation());
            }
        }
        event.setTarget(nearPlayer);
    }

//    @EventHandler
//    public void enemySpawning(EntitySpawnEvent event) {
////        if(event.getEntity() instanceof Wither){
////            if(event.getLocation().getWorld().getEnvironment() == World.Environment.THE_END){
////                event.setCancelled(true);
////                return;
////            }
////        }
//        if (!(event.getEntity() instanceof Monster))
//            return;
//        LivingEntity entity = (LivingEntity) event.getEntity();
//        AttributeInstance maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
//        AttributeInstance maxAttack = entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
//        if (event.getEntity().getType() != EntityType.ENDER_DRAGON) {
//            if (maxAttack != null) {
//                //提升65%怪物攻击力
//                maxAttack.setBaseValue(maxAttack.getBaseValue() * 1.65d);
//            }
//            if (maxHealth != null) {
//                //提升75%怪物血量
//                maxHealth.setBaseValue(maxHealth.getBaseValue() * 1.75d);
//            }
//        }
//        entity.setHealth(entity.getMaxHealth());
//    }


    @EventHandler
    public void enderDragonTargeting(EntityTargetEvent event) {
        if (!(event.getEntity() instanceof EnderDragon))
            return;
        if (!(event.getTarget() instanceof Player))
            return;
        EnderDragon dragon = (EnderDragon) event.getEntity();
        Player target = (Player) event.getTarget();
        if (dragon.getHealth() * 0.8 > dragon.getMaxHealth())
            return;
        if (random.nextInt(20) != 0)
            return;
        target.sendMessage(LIGHT_PURPLE + "[闪电风暴] " + YELLOW + "末影龙发动技能，召唤了闪电风暴！");
        for (int i = 0; i < 10; i++) {
            target.getWorld().strikeLightning(target.getLocation().add(random.nextInt(10),random.nextInt(10),random.nextInt(10)));
        }
    }

    @EventHandler
    public void enderDragonAttacking(EntityDamageByEntityEvent event) {
        EnderDragon dragon;
        if (event.getDamager().getType() == EntityType.ENDER_DRAGON)
            dragon = (EnderDragon) event.getDamager();
        else if (event.getDamager().getType() == EntityType.DRAGON_FIREBALL)
            dragon = (EnderDragon) ((DragonFireball) event.getDamager()).getShooter();
        else
            return;
        if (dragon == null)
            return;

        Collection<PotionEffect> effectCollection = ((Player) event.getEntity()).getActivePotionEffects();
        if (effectCollection.stream().map(PotionEffect::getType)
                .anyMatch(effectType -> effectType.equals(PotionEffectType.JUMP) || effectType.equals(PotionEffectType.INCREASE_DAMAGE))) {
            //叠伤害 - 增加30%攻击力
            event.setDamage(event.getDamage() + (event.getDamage() * 0.3));
            event.getEntity().sendMessage(LIGHT_PURPLE + "[伤害增强] " + YELLOW + "你的药水效果给你带来了破绽，末影龙额外对你造成了 "
                    + Util.formatDouble(event.getDamage() * 0.3) + "点伤害！");
        }

        //星爆弃疗斩 随机对玩家造成致命伤害 添加玩家HP给末影龙
        if (random.nextInt(15) == 0) {
            double healthCanAdd = Math.min(dragon.getMaxHealth() - dragon.getHealth(), ((Player) event.getEntity()).getHealth() * 15);
            ((Player) event.getEntity()).damage(10d, dragon);
            ((Player) event.getEntity()).setHealth(0.0d);
            dragon.setHealth(dragon.getHealth() + healthCanAdd);
            Bukkit.getOnlinePlayers().stream().filter(player -> player.getWorld().equals(event.getDamager().getWorld()))
                    .forEach(sender -> sender.sendMessage(LIGHT_PURPLE + "[星爆弃疗斩] " + YELLOW + "末影龙发动技能，对 "
                            + event.getEntity().getName() + " 造成了 " + Util.formatDouble(100.0d) + " 点伤害"));
            return;
        }
        //攻击时几率恢复末影水晶
        if (random.nextInt(3) == 0) {
            Bukkit.getOnlinePlayers().stream().filter(player -> player.getWorld().equals(event.getDamager().getWorld()))
                    .forEach(sender -> sender.sendMessage(LIGHT_PURPLE + "[水晶恢复] " + YELLOW + "末影龙发动技能，恢复了所有末影水晶"));
            respawnAllCrystals();
        }
    }

    @EventHandler
    public void enderDragonDefending(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof EnderDragon)) {
            return;
        }
        EnderDragon dragon = (EnderDragon) event.getEntity();
        double health25 = dragon.getMaxHealth() * 0.25;
        double health10 = dragon.getMaxHealth() * 0.10;
        //攻击时几率恢复末影水晶
        if (random.nextInt(40) == 0) {
            Bukkit.getOnlinePlayers().stream().filter(player -> player.getWorld().equals(event.getDamager().getWorld()))
                    .forEach(sender -> sender.sendMessage(LIGHT_PURPLE + "[水晶恢复] " + YELLOW + "末影龙发动技能，恢复了所有末影水晶"));
            respawnAllCrystals();
        }

        //星爆弃疗斩 随机对玩家造成致命伤害 添加玩家HP给末影龙
        if (random.nextInt(100) == 0) {
            double healthCanAdd = Math.min(dragon.getMaxHealth() - dragon.getHealth(), ((Player) event.getEntity()).getHealth() * 15);
            ((Player) event.getEntity()).damage(19.0d, dragon);
            ((Player) event.getEntity()).setHealth(0.0d);
            dragon.setHealth(dragon.getHealth() + healthCanAdd);
            Bukkit.getOnlinePlayers().stream().filter(player -> player.getWorld().equals(event.getDamager().getWorld()))
                    .forEach(sender -> sender.sendMessage(LIGHT_PURPLE + "[星爆弃疗斩] " + YELLOW + "末影龙发动技能，对 "
                            + event.getEntity().getName() + " 造成了 " + Util.formatDouble(19.0d) + " 点伤害"));
            return;
        }

        if (event.getFinalDamage() >= dragon.getHealth() && !(event.getDamager() instanceof Player)) {
            //致命伤害 对末影龙会造成致命的伤害，只能由玩家直接造成，否则则会保留1HP血量。
            event.setCancelled(true);
            dragon.setHealth(1.0f);
            //让玩家以为自己真的砍到了末影龙（
            dragon.damage(0.0f, event.getDamager());
            return;
        }
        double healthAfterDamaging = dragon.getHealth() - event.getFinalDamage();
        if (healthAfterDamaging <= health10) {
            if (!(event.getDamager() instanceof Player)) {
                //10%血量免疫非玩家【直接】伤害
                event.setCancelled(true);
                return;
            }
        }
        if (healthAfterDamaging <= health25) {
            if (!(event.getDamager() instanceof Player)) {
                if (!(event.getDamager() instanceof Projectile)) {
                    //25%以下血量免疫非玩家伤害
                    event.setCancelled(true);
                    return;
                }
                ProjectileSource source = ((Projectile) event.getDamager()).getShooter();
                if (source instanceof Player) {
                    //25%以下血量免疫非玩家伤害 - 非玩家投掷物
                    event.setCancelled(true);
                    return;
                }
            }
            if (random.nextInt(10) == 0) {
                Bukkit.getOnlinePlayers().stream().filter(player -> player.getWorld().equals(event.getDamager().getWorld()))
                        .forEach(sender -> {
                            sender.sendMessage(LIGHT_PURPLE + "[末影诅咒] " + YELLOW + "末影龙发动技能，你被末影龙诅咒了！");
                            sender.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 600, 2));
                            sender.setFoodLevel(0);
                            sender.setSaturation(0.0f);
                            sender.setVelocity(new Vector(0, 5, 0));
                        });
            }

        }
    }

    @EventHandler
    public void explode(BlockExplodeEvent event) {
        event.setYield(1.0f);
    }

    @EventHandler
    public void explode(EntityExplodeEvent event) {
        event.setYield(1.0f);
    }

    @EventHandler
    public void enderFireballExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof DragonFireball))
            return;
        Collection<Entity> entities = event.getEntity().getWorld().getNearbyEntities(event.getLocation(), 10, 10, 10);
        entities.forEach(entity -> entity.getVelocity().add(new Vector(0, 2.0, 0)));
        event.getLocation().getWorld().createExplosion(event.getLocation(), 5F, true, true);

    }

    @EventHandler
    public void entityDeath(EntityDeathEvent event) {
//        if (event.getEntity() instanceof Boss) {
//            event.getDrops().add(ItemCreator.makeGaoJiPickaxe());
//        }
//
//        if(event.getEntity() instanceof Monster){
//           if( event.getDroppedExp() > 0){
//               if(random.nextInt(300) == 0)
//                   event.getDrops().add(ItemCreator.makeGaoJiPickaxe());
//           }
//        }

        if (event.getEntity() instanceof EnderDragon) {
            Bukkit.broadcastMessage(GOLD + "" + BOLD + "[末影龙已被击杀] 屠龙勇士是：" + event.getEntity().getLastDamageCause().getEntity().getName());
        }

        if (event.getEntity() instanceof Creeper) {
            for (ItemStack drop : event.getDrops()) {
                if (drop.getType() == Material.GUNPOWDER)
                    return;
            }
            event.getDrops().add(new ItemStack(Material.GUNPOWDER));
        }
    }
}
