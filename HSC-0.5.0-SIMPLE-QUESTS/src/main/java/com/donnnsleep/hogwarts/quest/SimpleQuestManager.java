package com.donnnsleep.hogwarts.quest;

import com.donnnsleep.hogwarts.HogwartsStoryCore;
import com.donnnsleep.hogwarts.magic.SpellCastEvent;
import com.donnnsleep.hogwarts.model.StudentProfile;
import com.donnnsleep.hogwarts.util.Msg;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Quest engine đơn giản:
 * - Click Citizens NPC -> tự nhận nhiệm vụ
 * - CAST: cast phép thành công
 * - COLLECT: NPC tự thả item riêng cho player
 * - KILL: NPC tự spawn mob riêng cho player
 * - đủ mục tiêu -> tự hoàn thành + tự thưởng
 *
 * Không dùng BetonQuest cho quest Hogwarts.
 */
public class SimpleQuestManager implements Listener {

    private final HogwartsStoryCore plugin;
    private final Map<String, SimpleQuestDefinition> quests = new LinkedHashMap<>();
    private final Map<Integer, List<SimpleQuestDefinition>> byNpc = new HashMap<>();

    private final NamespacedKey ownerKey;
    private final NamespacedKey questKey;

    public SimpleQuestManager(HogwartsStoryCore plugin) {
        this.plugin = plugin;
        this.ownerKey = new NamespacedKey(plugin, "simple_quest_owner");
        this.questKey = new NamespacedKey(plugin, "simple_quest_id");
    }

    public void load() {
        File file = new File(plugin.getDataFolder(), "simplequests.yml");
        if (!file.exists()) plugin.saveResource("simplequests.yml", false);

        quests.clear();
        byNpc.clear();

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = cfg.getConfigurationSection("missions");
        if (root == null) {
            plugin.getLogger().warning("simplequests.yml không có section missions.");
            return;
        }

        for (String id : root.getKeys(false)) {
            ConfigurationSection s = root.getConfigurationSection(id);
            if (s == null || !s.getBoolean("enabled", true)) continue;

            try {
                SimpleQuestType type = SimpleQuestType.valueOf(
                        s.getString("type", "CAST").toUpperCase(Locale.ROOT));

                Material material = Material.matchMaterial(s.getString("collect.material", "PAPER"));
                if (material == null) material = Material.PAPER;

                EntityType entityType;
                try {
                    entityType = EntityType.valueOf(
                            s.getString("mob.type", "ZOMBIE").toUpperCase(Locale.ROOT));
                } catch (Exception ex) {
                    entityType = EntityType.ZOMBIE;
                }

                SimpleQuestDefinition q = new SimpleQuestDefinition(
                        id,
                        s.getInt("npc-id"),
                        Math.max(1, s.getInt("min-year", 1)),
                        Math.min(7, s.getInt("max-year", 7)),
                        type,
                        s.getString("title", id),
                        s.getString("description", ""),
                        Math.max(1, s.getInt("goal", 1)),

                        s.getString("cast.spell", "any").toLowerCase(Locale.ROOT),
                        s.getString("cast.teach-spell", "").toLowerCase(Locale.ROOT),

                        material,
                        s.getString("collect.name", "&aVật phẩm nhiệm vụ"),

                        entityType,
                        s.getString("mob.name", "&cQuái luyện tập"),
                        Math.max(1.0, s.getDouble("mob.health", 20.0)),
                        Math.max(0.0, s.getDouble("mob.damage", 3.0)),

                        Math.max(0, s.getInt("reward.house-points", 3)),
                        Math.max(0, s.getInt("reward.money", 25)),
                        Math.max(0, s.getInt("reward.xp", 50))
                );

                quests.put(id, q);
                byNpc.computeIfAbsent(q.npcId(), k -> new ArrayList<>()).add(q);
            } catch (Exception ex) {
                plugin.getLogger().warning("Không nạp được mission '" + id + "': " + ex.getMessage());
            }
        }

        plugin.getLogger().info("SimpleQuests đã nạp " + quests.size() + " nhiệm vụ.");
    }

    public boolean handlesNpc(int npcId) {
        return byNpc.containsKey(npcId);
    }

    public SimpleQuestDefinition get(String id) {
        return id == null ? null : quests.get(id);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNpcRight(NPCRightClickEvent e) {
        if (!handlesNpc(e.getNPC().getId())) return;
        e.setCancelled(true);
        handleNpcClick(e.getClicker(), e.getNPC().getId(), e.getNPC().getEntity().getLocation());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNpcLeft(NPCLeftClickEvent e) {
        if (!handlesNpc(e.getNPC().getId())) return;
        e.setCancelled(true);
        handleNpcClick(e.getClicker(), e.getNPC().getId(), e.getNPC().getEntity().getLocation());
    }

    private void handleNpcClick(Player player, int npcId, Location npcLocation) {
        StudentProfile p = plugin.getProfiles().get(player);
        if (p == null) return;

        // Dọn tracker cũ từ BetonQuest nếu ID không tồn tại trong hệ mới.
        if (p.hasTrackedQuest() && !quests.containsKey(p.getTrackedQuestId())) {
            p.clearTrackedQuest();
            plugin.getProfiles().save(p);
        }

        if (p.hasTrackedQuest()) {
            SimpleQuestDefinition active = quests.get(p.getTrackedQuestId());
            if (active != null) {
                if (active.npcId() == npcId) {
                    player.sendMessage(Msg.msg("&d✦ &f" + active.title()
                            + " &7— &e" + p.getTrackedQuestProgress() + "&7/&e" + active.goal()));

                    // Nếu vật phẩm/quái bị mất, click lại NPC để tạo lại phần còn thiếu.
                    if (active.type() == SimpleQuestType.COLLECT || active.type() == SimpleQuestType.KILL) {
                        respawnRemaining(player, active, npcLocation);
                        player.sendMessage(Msg.msg("&7Đã tạo lại phần mục tiêu còn thiếu quanh giáo sư."));
                    } else {
                        player.sendMessage(Msg.msg("&7Hãy niệm phép để hoàn thành bài."));
                    }
                } else {
                    player.sendMessage(Msg.msg("&cEm đang làm nhiệm vụ &f" + active.title()
                            + "&c. Hãy hoàn thành trước khi nhận bài khác."));
                }
                return;
            }
        }

        SimpleQuestDefinition next = nextQuest(player, npcId, p);
        if (next == null) {
            player.sendMessage(Msg.msg("&7Giáo sư hiện chưa có nhiệm vụ phù hợp với năm học của em."));
            return;
        }

        startQuest(player, p, next, npcLocation);
    }

    private SimpleQuestDefinition nextQuest(Player player, int npcId, StudentProfile p) {
        List<SimpleQuestDefinition> list = byNpc.getOrDefault(npcId, List.of());
        for (SimpleQuestDefinition q : list) {
            if (!q.supportsYear(p.getYear())) continue;
            if (p.hasFlag(doneFlag(q))) continue;
            return q;
        }
        return null;
    }

    private void startQuest(Player player, StudentProfile p, SimpleQuestDefinition q, Location npcLoc) {
        if (q.type() == SimpleQuestType.CAST && !p.hasWand()) {
            player.sendMessage(Msg.msg("&cEm cần có đũa phép trước khi học bài này."));
            return;
        }

        cleanupQuestEntities(player, q.id());

        p.setTrackedQuestId(q.id());
        p.setTrackedQuestName(q.title());
        p.setTrackedQuestSource("simple");
        p.setTrackedQuestKey("");
        p.setTrackedQuestGoal(q.goal());
        p.setTrackedQuestProgress(0);

        if (q.type() == SimpleQuestType.CAST
                && q.castSpell() != null
                && !q.castSpell().isBlank()
                && !q.castSpell().equalsIgnoreCase("any")) {
            var spell = plugin.getSpells().get(q.castSpell());
            if (spell != null) {
                p.startPractice(spell.id());
                p.setActiveSpell(spell.id());
            }
        }

        plugin.getProfiles().save(p);

        player.sendMessage(Msg.color(""));
        player.sendMessage(Msg.msg("&d&l✦ NHIỆM VỤ MỚI"));
        player.sendMessage(Msg.msg("&f" + q.title()));
        if (!q.description().isBlank()) player.sendMessage(Msg.msg("&7" + q.description()));

        switch (q.type()) {
            case CAST -> player.sendMessage(Msg.msg("&eMục tiêu: &fniệm phép thành công "
                    + q.goal() + " lần."));
            case COLLECT -> {
                player.sendMessage(Msg.msg("&eMục tiêu: &fnhặt đủ " + q.goal()
                        + " vật phẩm do giáo sư tạo ra."));
                spawnCollectItems(player, q, npcLoc, q.goal());
            }
            case KILL -> {
                player.sendMessage(Msg.msg("&eMục tiêu: &fđánh bại " + q.goal()
                        + " quái luyện tập do giáo sư triệu hồi."));
                spawnMobs(player, q, npcLoc, q.goal());
            }
        }

        player.sendMessage(Msg.msg("&7Tiến độ sẽ tự cập nhật trên TAB."));
        player.sendMessage(Msg.color(""));
    }

    @EventHandler
    public void onSpellCast(SpellCastEvent e) {
        Player player = e.getPlayer();
        StudentProfile p = plugin.getProfiles().get(player);
        if (p == null || !p.hasTrackedQuest()) return;

        SimpleQuestDefinition q = quests.get(p.getTrackedQuestId());
        if (q == null || q.type() != SimpleQuestType.CAST) return;

        if (!q.castSpell().equalsIgnoreCase("any")
                && !q.castSpell().equalsIgnoreCase(e.getSpell().id())) {
            player.sendActionBar(Msg.color("&7Bài này cần niệm &f" + q.castSpell() + "&7."));
            return;
        }

        progress(player, p, q, 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        Item item = e.getItem();

        String owner = item.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING);
        String questId = item.getPersistentDataContainer().get(questKey, PersistentDataType.STRING);
        if (owner == null || questId == null) return;

        if (!owner.equals(player.getUniqueId().toString())) {
            e.setCancelled(true);
            return;
        }

        StudentProfile p = plugin.getProfiles().get(player);
        SimpleQuestDefinition q = quests.get(questId);
        if (p == null || q == null || q.type() != SimpleQuestType.COLLECT
                || !questId.equals(p.getTrackedQuestId())) {
            e.setCancelled(true);
            item.remove();
            return;
        }

        progress(player, p, q, 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onQuestMobDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity target)) return;

        String owner = target.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING);
        String questId = target.getPersistentDataContainer().get(questKey, PersistentDataType.STRING);
        if (owner == null || questId == null) return;

        Player attacker = null;
        if (e.getDamager() instanceof Player p) {
            attacker = p;
        } else if (e.getDamager() instanceof Projectile projectile
                && projectile.getShooter() instanceof Player p) {
            attacker = p;
        }

        // Quái nhiệm vụ chỉ nhận sát thương từ đúng người sở hữu nhiệm vụ.
        if (attacker == null || !owner.equals(attacker.getUniqueId().toString())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuestMobDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();

        String owner = entity.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING);
        String questId = entity.getPersistentDataContainer().get(questKey, PersistentDataType.STRING);
        if (owner == null || questId == null) return;

        e.getDrops().clear();
        e.setDroppedExp(0);

        Player killer = entity.getKiller();
        if (killer == null || !owner.equals(killer.getUniqueId().toString())) return;

        StudentProfile p = plugin.getProfiles().get(killer);
        SimpleQuestDefinition q = quests.get(questId);
        if (p == null || q == null || q.type() != SimpleQuestType.KILL
                || !questId.equals(p.getTrackedQuestId())) return;

        progress(killer, p, q, 1);
    }

    private void progress(Player player, StudentProfile p, SimpleQuestDefinition q, int amount) {
        int now = p.addTrackedQuestProgress(amount);
        plugin.getProfiles().save(p);

        player.sendActionBar(Msg.color(progressBar(now, q.goal())
                + " &f" + q.title() + " &e" + now + "&7/&e" + q.goal()));

        if (now >= q.goal()) {
            completeQuest(player, p, q);
        }
    }

    private void completeQuest(Player player, StudentProfile p, SimpleQuestDefinition q) {
        cleanupQuestEntities(player, q.id());

        if (q.type() == SimpleQuestType.CAST
                && q.castSpell() != null
                && !q.castSpell().equalsIgnoreCase("any")) {
            p.stopPractice(q.castSpell());
        }

        if (q.teachSpell() != null && !q.teachSpell().isBlank()) {
            var spell = plugin.getSpells().get(q.teachSpell());
            if (spell != null) {
                p.learn(spell.id());
                p.setActiveSpell(spell.id());
                player.sendMessage(Msg.msg("&b✦ Đã học vĩnh viễn: &f" + spell.incantation()));
            }
        }

        int beforeLevel = p.getMagicLevel();
        if (q.rewardXp() > 0) p.addXp(q.rewardXp());

        p.addFlag(doneFlag(q));
        p.clearTrackedQuest();
        plugin.getProfiles().save(p);

        if (q.rewardHousePoints() > 0) {
            plugin.getPoints().awardPlayer(player, q.rewardHousePoints(),
                    "hoàn thành nhiệm vụ " + q.title());
        }

        if (q.rewardMoney() > 0) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "eco give " + player.getName() + " " + q.rewardMoney());
        }

        player.sendMessage(Msg.color(""));
        player.sendMessage(Msg.msg("&a&l✓ HOÀN THÀNH: &f" + q.title()));
        if (q.rewardHousePoints() > 0)
            player.sendMessage(Msg.msg("&e+" + q.rewardHousePoints() + " Điểm Nhà"));
        if (q.rewardMoney() > 0)
            player.sendMessage(Msg.msg("&6+" + q.rewardMoney() + " tiền"));
        if (q.rewardXp() > 0)
            player.sendMessage(Msg.msg("&b+" + q.rewardXp() + " Magic XP"));
        if (p.getMagicLevel() > beforeLevel)
            player.sendMessage(Msg.msg("&d✦ Cấp phép thuật của em đã lên &f" + p.getMagicLevel() + "&d!"));
        player.sendMessage(Msg.color(""));

        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.15f);
    }

    private void respawnRemaining(Player player, SimpleQuestDefinition q, Location npcLoc) {
        int progress = Math.max(0, plugin.getProfiles().get(player).getTrackedQuestProgress());
        int remaining = Math.max(0, q.goal() - progress);
        if (remaining <= 0) return;

        cleanupQuestEntities(player, q.id());

        if (q.type() == SimpleQuestType.COLLECT) {
            spawnCollectItems(player, q, npcLoc, remaining);
        } else if (q.type() == SimpleQuestType.KILL) {
            spawnMobs(player, q, npcLoc, remaining);
        }
    }

    private void spawnCollectItems(Player player, SimpleQuestDefinition q, Location center, int amount) {
        World world = center.getWorld();
        if (world == null) return;

        for (int i = 0; i < amount; i++) {
            Location loc = nearbySpawn(center, i, amount);
            ItemStack stack = new ItemStack(q.collectMaterial(), 1);
            ItemMeta meta = stack.getItemMeta();
            meta.displayName(Msg.color(q.collectName()));
            stack.setItemMeta(meta);

            Item item = world.dropItem(loc, stack);
            item.setPickupDelay(10);
            item.getPersistentDataContainer().set(ownerKey, PersistentDataType.STRING,
                    player.getUniqueId().toString());
            item.getPersistentDataContainer().set(questKey, PersistentDataType.STRING, q.id());
        }
    }

    private void spawnMobs(Player player, SimpleQuestDefinition q, Location center, int amount) {
        World world = center.getWorld();
        if (world == null) return;

        for (int i = 0; i < amount; i++) {
            Location loc = nearbySpawn(center, i, amount);
            Entity entity = world.spawnEntity(loc, q.mobType());
            if (!(entity instanceof LivingEntity living)) {
                entity.remove();
                continue;
            }

            living.customName(Msg.color(q.mobName()));
            living.setCustomNameVisible(true);
            living.setRemoveWhenFarAway(false);

            var maxHealth = living.getAttribute(Attribute.MAX_HEALTH);
            if (maxHealth != null) {
                maxHealth.setBaseValue(q.mobHealth());
                living.setHealth(Math.min(q.mobHealth(), maxHealth.getValue()));
            }

            var attack = living.getAttribute(Attribute.ATTACK_DAMAGE);
            if (attack != null && q.mobDamage() > 0) {
                attack.setBaseValue(q.mobDamage());
            }

            living.getPersistentDataContainer().set(ownerKey, PersistentDataType.STRING,
                    player.getUniqueId().toString());
            living.getPersistentDataContainer().set(questKey, PersistentDataType.STRING, q.id());

            if (living instanceof Mob mob) {
                mob.setTarget(player);
            }
        }
    }

    private Location nearbySpawn(Location center, int index, int total) {
        World world = center.getWorld();
        if (world == null) return center;

        double angle = (Math.PI * 2.0 * index / Math.max(1, total))
                + ThreadLocalRandom.current().nextDouble(-0.25, 0.25);
        double radius = 3.0 + ThreadLocalRandom.current().nextDouble(0.0, 2.5);

        Location base = center.clone().add(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
        base.setX(Math.floor(base.getX()) + 0.5);
        base.setZ(Math.floor(base.getZ()) + 0.5);

        // Tìm vị trí không bị kẹt trong block, ưu tiên cùng cao độ với NPC.
        for (int dy = 2; dy >= -2; dy--) {
            Location tryLoc = base.clone().add(0, dy, 0);
            if (tryLoc.getBlock().isPassable()
                    && tryLoc.clone().add(0, 1, 0).getBlock().isPassable()
                    && !tryLoc.clone().add(0, -1, 0).getBlock().isPassable()) {
                return tryLoc;
            }
        }

        return center.clone().add(0, 1, 0);
    }

    private void cleanupQuestEntities(Player player, String questId) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                String owner = entity.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING);
                String id = entity.getPersistentDataContainer().get(questKey, PersistentDataType.STRING);
                if (owner == null || id == null) continue;

                if (owner.equals(player.getUniqueId().toString()) && id.equals(questId)) {
                    entity.remove();
                }
            }
        }
    }

    private String doneFlag(SimpleQuestDefinition q) {
        return "simplequest." + q.id() + ".done";
    }

    private String progressBar(int done, int goal) {
        int slots = 10;
        int filled = (int) Math.round((done * slots) / (double) Math.max(1, goal));
        filled = Math.max(0, Math.min(slots, filled));
        return "&a" + "■".repeat(filled) + "&8" + "■".repeat(slots - filled);
    }
}
