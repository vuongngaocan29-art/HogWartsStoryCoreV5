package com.donnnsleep.hogwarts.model;

import java.util.*;

/**
 * Hồ sơ học sinh Hogwarts. Đây là "sổ điểm" của mỗi người chơi:
 * Nhà, năm học, cấp phép thuật, mana, đũa phép, phép đã học, tiến độ cốt truyện.
 */
public class StudentProfile {

    private final UUID uuid;
    private String name;

    private House house;               // null = chưa Phân Loại
    private int year = 1;              // 1..7
    private int magicLevel = 1;
    private int magicXp = 0;

    private double mana = 100;
    private double maxMana = 100;

    private Wand wand;                 // null = chưa có đũa

    private final Set<String> learnedSpells = new LinkedHashSet<>();

    /**
     * Phép đang được giáo sư cho phép luyện tạm thời trong một bài học.
     * Người chơi có thể niệm để làm bài, nhưng chưa được tính là đã học.
     */
    private final Set<String> practiceSpells = new LinkedHashSet<>();

    private String activeSpell;

    /** Cột mốc cốt truyện đã hoàn thành, ví dụ "year1.letter", "year1.sorting". */
    private final Set<String> storyFlags = new LinkedHashSet<>();

    /** Nhiệm vụ gần nhất đang được theo dõi để hiển thị trên TAB. */
    private String trackedQuestId;
    private String trackedQuestName;
    private String trackedQuestSource; // point | objective | binary
    private String trackedQuestKey;
    private int trackedQuestGoal = 0;
    private int trackedQuestProgress = 0;

    /** Điểm cá nhân đã đóng góp cho Nhà trong năm học hiện tại. */
    private int contributedPoints = 0;

    public StudentProfile(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    // ---- getters / setters ----
    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public House getHouse() { return house; }
    public void setHouse(House house) { this.house = house; }
    public boolean isSorted() { return house != null; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = Math.max(1, Math.min(7, year)); }

    public int getMagicLevel() { return magicLevel; }
    public void setMagicLevel(int magicLevel) { this.magicLevel = magicLevel; }

    public int getMagicXp() { return magicXp; }
    public void setMagicXp(int magicXp) { this.magicXp = magicXp; }

    public double getMana() { return mana; }
    public void setMana(double mana) { this.mana = Math.max(0, Math.min(maxMana, mana)); }

    public double getMaxMana() { return maxMana; }
    public void setMaxMana(double maxMana) { this.maxMana = maxMana; }

    public Wand getWand() { return wand; }
    public void setWand(Wand wand) { this.wand = wand; }
    public boolean hasWand() { return wand != null; }

    public Set<String> getLearnedSpells() { return learnedSpells; }
    public boolean hasLearned(String spellId) { return learnedSpells.contains(spellId.toLowerCase()); }
    public boolean learn(String spellId) { return learnedSpells.add(spellId.toLowerCase()); }
    public boolean unlearn(String spellId) { return learnedSpells.remove(spellId.toLowerCase()); }

    public Set<String> getPracticeSpells() { return practiceSpells; }
    public boolean isPracticing(String spellId) {
        return spellId != null && practiceSpells.contains(spellId.toLowerCase());
    }
    public boolean startPractice(String spellId) {
        return spellId != null && practiceSpells.add(spellId.toLowerCase());
    }
    public boolean stopPractice(String spellId) {
        return spellId != null && practiceSpells.remove(spellId.toLowerCase());
    }
    public void clearPracticeSpells() { practiceSpells.clear(); }

    public String getActiveSpell() { return activeSpell; }
    public void setActiveSpell(String activeSpell) { this.activeSpell = activeSpell; }

    public Set<String> getStoryFlags() { return storyFlags; }
    public boolean hasFlag(String flag) { return storyFlags.contains(flag); }
    public void addFlag(String flag) { storyFlags.add(flag); }
    public void removeFlag(String flag) { storyFlags.remove(flag); }

    public String getTrackedQuestId() { return trackedQuestId; }
    public void setTrackedQuestId(String trackedQuestId) { this.trackedQuestId = trackedQuestId; }

    public String getTrackedQuestName() { return trackedQuestName; }
    public void setTrackedQuestName(String trackedQuestName) { this.trackedQuestName = trackedQuestName; }

    public String getTrackedQuestSource() { return trackedQuestSource; }
    public void setTrackedQuestSource(String trackedQuestSource) { this.trackedQuestSource = trackedQuestSource; }

    public String getTrackedQuestKey() { return trackedQuestKey; }
    public void setTrackedQuestKey(String trackedQuestKey) { this.trackedQuestKey = trackedQuestKey; }

    public int getTrackedQuestGoal() { return trackedQuestGoal; }
    public void setTrackedQuestGoal(int trackedQuestGoal) { this.trackedQuestGoal = Math.max(0, trackedQuestGoal); }

    public int getTrackedQuestProgress() { return trackedQuestProgress; }
    public void setTrackedQuestProgress(int trackedQuestProgress) {
        int cap = trackedQuestGoal > 0 ? trackedQuestGoal : Integer.MAX_VALUE;
        this.trackedQuestProgress = Math.max(0, Math.min(cap, trackedQuestProgress));
    }
    public int addTrackedQuestProgress(int amount) {
        setTrackedQuestProgress(this.trackedQuestProgress + amount);
        return this.trackedQuestProgress;
    }

    public boolean hasTrackedQuest() {
        return trackedQuestId != null && !trackedQuestId.isBlank()
                && trackedQuestName != null && !trackedQuestName.isBlank()
                && trackedQuestGoal > 0;
    }

    public void clearTrackedQuest() {
        trackedQuestId = null;
        trackedQuestName = null;
        trackedQuestSource = null;
        trackedQuestKey = null;
        trackedQuestGoal = 0;
        trackedQuestProgress = 0;
    }

    public int getContributedPoints() { return contributedPoints; }
    public void addContributedPoints(int p) { this.contributedPoints += p; }
    public void setContributedPoints(int p) { this.contributedPoints = p; }

    // ---- XP / Level ----
    /** XP cần để lên level tiếp theo. Tăng dần theo level. */
    public int xpToNextLevel() {
        return 100 + (magicLevel - 1) * 50;
    }

    /** Cộng XP, trả về số level đã lên. */
    public int addXp(int amount) {
        this.magicXp += amount;
        int levelsGained = 0;
        while (magicXp >= xpToNextLevel()) {
            magicXp -= xpToNextLevel();
            magicLevel++;
            levelsGained++;
            maxMana += 10; // mỗi level +10 mana tối đa
        }
        return levelsGained;
    }

    /** Mana tối đa thực tế sau khi tính lõi đũa phép. */
    public double effectiveMaxMana() {
        double mult = wand != null ? wand.getCore().getManaMult() : 1.0;
        return maxMana * mult;
    }
}
