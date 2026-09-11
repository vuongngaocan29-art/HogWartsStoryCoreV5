# HOGWARTS STORY CORE 0.4.0 — HỆ THỐNG HỌC PHÉP QUA NPC

Bản này nâng cấp source 0.3.2 theo đúng cơ chế: **NPC -> nhận bài -> luyện phép tạm -> nộp bài -> mở khoá vĩnh viễn**.

## Thay đổi trong core

- `practice-spells`: phép luyện tạm, lưu trong profile để không mất khi restart giữa bài.
- SpellManager chỉ cho cast khi phép nằm trong `spells` hoặc `practice-spells`.
- Không còn tự dạy Lumos sau Lễ Phân Loại.
- `/hogwarts practice <player> <spell> on|off` cho BetonQuest mở/tắt buổi luyện.
- `/hogwarts xp <player> <amount>` cho quest thưởng Magic XP.
- `/hogwarts unteach <player> <spell>` để admin reset khi test.
- Placeholder mới: `%hogwarts_has_spell_<id>%`, `%hogwarts_practice_spell_<id>%` (dấu gạch dưới được đổi sang gạch ngang trong id).
- `config.yml` đã có cast-hook cho toàn bộ phép tiêu chuẩn trong chương trình.

## File quest

`QuestPackages/hogwarts_spell_school/`

Xem `README.md` và `NPC-SETUP.md` trong đó.
