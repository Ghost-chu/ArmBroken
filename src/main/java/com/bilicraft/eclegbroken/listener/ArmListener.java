package com.bilicraft.eclegbroken.listener;

import com.bilicraft.eclegbroken.ECArmBroken;
import com.bilicraft.eclegbroken.ItemCreator;
import com.bilicraft.eclegbroken.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Collection;
import java.util.Random;

import static org.bukkit.ChatColor.*;

public class ArmListener implements Listener {
    private final Random random = new Random();
    private final ECArmBroken plugin;

    public ArmListener(ECArmBroken plugin) {
        this.plugin = plugin;
    }

    //我的胳膊断了
    @EventHandler(ignoreCancelled = true)
    public void mineBlock(BlockBreakEvent event) {
        if (isGaoJiPickaxe(event.getPlayer().getInventory().getItemInMainHand()))
            return;
        Material material = event.getBlock().getType();
        //Mineable blocks
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
        if(material == Material.GRASS)
            return;
        event.setCancelled(true);
    }

    private boolean isGaoJiPickaxe(ItemStack stack) {
        if(stack.getType()!=Material.DIAMOND_AXE){
            return false;
        }
        if (!stack.getItemMeta().hasDisplayName()) {
            return false;
        }
        return stack.getItemMeta().getDisplayName().equals(AQUA + "镐击镐");
    }

    //你的就是我的
    @EventHandler(ignoreCancelled = true)
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

    @EventHandler(ignoreCancelled = true)
    public void enemySpawning(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof Monster))
            return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        AttributeInstance maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        AttributeInstance maxAttack = entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (event.getEntity().getType() != EntityType.ENDER_DRAGON) {
            if (maxAttack != null) {
                //提升65%怪物攻击力
                maxAttack.setBaseValue(maxAttack.getBaseValue() * 1.65d);
            }
            if (maxHealth != null) {
                //提升75%怪物血量
                maxHealth.setBaseValue(maxHealth.getBaseValue() * 1.75d);
            }
        }
        entity.setHealth(entity.getMaxHealth());
    }

    @EventHandler(ignoreCancelled = true)
    public void enderDragonAttacking(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof EnderDragon)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        EnderDragon dragon = (EnderDragon) event.getDamager();

        Collection<PotionEffect> effectCollection = ((Player) event.getEntity()).getActivePotionEffects();
        if (effectCollection.stream().map(PotionEffect::getType)
                .anyMatch(effectType -> effectType.equals(PotionEffectType.JUMP) || effectType.equals(PotionEffectType.INCREASE_DAMAGE))) {
            //叠伤害 - 增加50%攻击力
            event.setDamage(event.getDamage() + event.getDamage() * 0.5);
            event.getEntity().sendMessage(LIGHT_PURPLE+"[伤害增强] "+YELLOW+"你的药水效果给你带来了破绽，末影龙额外对你造成了 "
                    + Util.formatDouble(event.getDamage() * 0.5) +"点伤害！");
        }

        //星爆弃疗斩 随机对玩家造成致命伤害 添加玩家HP给末影龙
        if (random.nextInt(50) == 0) {
            double healthCanAdd = Math.min(dragon.getMaxHealth() - dragon.getHealth(), ((Player) event.getEntity()).getHealth());
            ((Player) event.getEntity()).damage(100d, dragon);
            ((Player) event.getEntity()).setHealth(0.0d);
            dragon.setHealth(dragon.getHealth() + healthCanAdd);
            Bukkit.getOnlinePlayers().stream().filter(player->player.getWorld().equals(event.getDamager().getWorld()))
                    .forEach(sender->sender.sendMessage(LIGHT_PURPLE+"[星爆弃疗斩] "+YELLOW+"末影龙发动技能，对 "
                            +event.getEntity().getName()+" 造成了 "+Util.formatDouble(100.0d)+" 点伤害"));
            return;
        }
        //攻击时几率恢复末影水晶
        if (random.nextInt(20) == 0) {
            DragonBattle battle = dragon.getDragonBattle();
            if (battle != null) {
                Bukkit.getOnlinePlayers().stream().filter(player->player.getWorld().equals(event.getDamager().getWorld()))
                        .forEach(sender->sender.sendMessage(LIGHT_PURPLE+"[水晶恢复]"+YELLOW+" 末影龙发动技能，恢复了所有末影水晶"));
                battle.resetCrystals();
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void enderDragonDefending(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof EnderDragon)) {
            return;
        }
        EnderDragon dragon = (EnderDragon) event.getEntity();
        double health25 = dragon.getMaxHealth() * 0.25;
        double health10 = dragon.getMaxHealth() * 0.10;
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
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void explode(BlockExplodeEvent event) {
        event.setYield(1.0f);
    }

    @EventHandler(ignoreCancelled = true)
    public void explode(EntityExplodeEvent event) {
        event.setYield(1.0f);
    }

    @EventHandler(ignoreCancelled = true)
    public void entityDeath(EntityDeathEvent event) {
        if (random.nextInt(10000) == 0 &&
                event.getEntity() instanceof Monster &&
                event.getEntity().getLastDamageCause() != null &&
                event.getEntity().getLastDamageCause().getEntity() instanceof Player) {
            event.getDrops().add(ItemCreator.makeGaoJiPickaxe());
        }
    }


}
