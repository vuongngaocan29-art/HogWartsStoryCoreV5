package com.donnnsleep.hogwarts.quest;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public record SimpleQuestDefinition(
        String id,
        int npcId,
        int minYear,
        int maxYear,
        SimpleQuestType type,
        String title,
        String description,
        int goal,

        String castSpell,
        String teachSpell,

        Material collectMaterial,
        String collectName,

        EntityType mobType,
        String mobName,
        double mobHealth,
        double mobDamage,

        int rewardHousePoints,
        int rewardMoney,
        int rewardXp
) {
    public boolean supportsYear(int year) {
        return year >= minYear && year <= maxYear;
    }
}
