# HogwartsStoryCore 0.5.0 — Simple Quests

Bản này thay hệ nhiệm vụ Hogwarts cũ bằng hệ thống trực tiếp trong Core.

## Không còn cần BetonQuest cho quest Hogwarts

Ba loại nhiệm vụ:

### CAST
Click NPC → mở phép luyện tạm → cast đủ số lần → tự hoàn thành.

### COLLECT
Click NPC → NPC tự thả item quanh vị trí của mình → nhặt đủ → tự hoàn thành.

### KILL
Click NPC → NPC tự spawn mob luyện tập quanh mình → giết đủ → tự hoàn thành.

## Điểm quan trọng

- Không TARGET
- Không tọa độ cố định
- Không phải quay lại NPC nộp
- Không dùng placeholder BetonQuest để tính tiến độ
- TAB đọc trực tiếp tiến độ từ StudentProfile
- Nếu item/mob bị mất: click lại đúng NPC → tự tạo lại phần còn thiếu
- Mỗi player có item/mob nhiệm vụ riêng bằng PersistentDataContainer

## NPC IDs

3 Flitwick  
4 Quirrell  
5 Lockhart  
6 Lupin  
7 Moody  
8 Umbridge  
9 Snape  
10 McGonagall  
11 Hooch  
12 Sprout  
13 Dumbledore

## Cài

1. Build `HogwartsStoryCore-0.5.0.jar`
2. STOP server
3. Xóa JAR HogwartsStoryCore cũ
4. Upload JAR 0.5.0
5. Xóa/đổi tên hai package quest Hogwarts cũ:
   - `plugins/BetonQuest/QuestPackages/hogwarts_spell_school`
   - `plugins/BetonQuest/QuestPackages/hogwarts_year1`
6. START server
7. Core tự tạo:
   `plugins/HogwartsStoryCore/simplequests.yml`

Không cần `/bq reload` cho quest Hogwarts nữa.

## Chỉnh nhiệm vụ

Mọi quest nằm trong `plugins/HogwartsStoryCore/simplequests.yml`.

Ví dụ CAST:
```yaml
my_spell:
  npc-id: 3
  type: CAST
  goal: 1
  cast:
    spell: lumos
    teach-spell: lumos
```

Ví dụ COLLECT:
```yaml
my_collect:
  npc-id: 12
  type: COLLECT
  goal: 5
  collect:
    material: FERN
    name: "&aMẫu cây"
```

Ví dụ KILL:
```yaml
my_kill:
  npc-id: 4
  type: KILL
  goal: 5
  mob:
    type: ZOMBIE
    name: "&5Bóng tối"
    health: 20
    damage: 3
```
