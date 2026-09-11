
## Bảng màu NPC

Package đã có sẵn `BANG-MAU-NPC.yml` để giữ màu NPC/UI đồng bộ.
Tên `quester` trong các file `conversations/*.yml` đã được áp màu trực tiếp.
Các lệnh Citizens tương ứng nằm trong `NPC-COLOR-COMMANDS.txt`.

# Hogwarts Spell School — 7 năm học

Package này được thiết kế cho **HogwartsStoryCore 0.4.0+**, BetonQuest 2.2+, Citizens và PlaceholderAPI.

## Mục tiêu

- Người chơi **không được học phép chỉ vì lên level**.
- Giáo sư mở quyền **luyện tạm thời** cho đúng một phép.
- Người chơi niệm thành công phép đó 3 lần.
- Quay lại NPC nộp bài.
- BetonQuest gọi `/hogwarts teach <player> <spell>`; lúc đó phép mới được lưu vĩnh viễn vào `players/<uuid>.yml`.
- Nếu bỏ bài giữa chừng, phép chỉ nằm trong `practice-spells`, không nằm trong `spells`.

Package có **39 phép tiêu chuẩn** lấy trực tiếp từ `spells.yml` của source. Ba lời nguyền `Imperio, Crucio, Avada Kedavra` không nằm trong chương trình giáo sư vì core mặc định khóa chúng bằng `magic.allow-unforgivable: false`.

## NPC mẫu

| ID mẫu | NPC | Vai trò |
|---:|---|---|
| 3 | Flitwick | Bùa chú, Năm I–VII |
| 4 | Quirrell | DADA Năm I |
| 5 | Lockhart | DADA Năm II |
| 6 | Lupin | DADA Năm III |
| 7 | Moody | DADA Năm IV |
| 8 | Umbridge | DADA Năm V |
| 9 | Snape | DADA Năm VI |
| 10 | McGonagall | Phòng vệ nâng cao Năm VII |
| 11 | Madam Hooch | Bài bay phụ |
| 12 | Sprout | Bài Thảo dược phụ |
| 13 | Dumbledore | Xét hoàn thành năm / lên năm |

**ID chỉ là mẫu.** Sau khi tạo NPC, dùng `/npc id` rồi sửa `npcs:` trong `package.yml`.

## Cài đặt

1. Dừng server.
2. Thay JAR HogwartsStoryCore bằng bản 0.4.0 sau khi build source này.
3. Copy `hogwarts_spell_school` vào `plugins/BetonQuest/QuestPackages/`.
4. Tạo NPC Citizens và sửa ID trong `package.yml`.
5. Khởi động server, chạy `/q reload` nếu chỉ vừa sửa quest. Khi thay JAR nên restart server đầy đủ.
6. Dùng `/hogwarts debug` để kiểm tra Citizens, BetonQuest, PlaceholderAPI.

## Tránh xung đột package cũ

Package cũ `hogwarts_year1` cũng từng bind Flitwick/Quirrell và từng dạy phép ngay đầu bài. Nếu đang dùng package đó, **không để hai package cùng bind cùng một Citizens ID**. Cách an toàn nhất là bỏ các NPC giáo sư khỏi `npcs:` của package cũ hoặc tạm tắt package cũ trong lúc kiểm tra hệ thống mới.

## Test nhanh

```text
/hogwarts year <player> 1
/hogwarts xp <player> 500
/hogwarts unteach <player> lumos
```

Sau đó nói chuyện Flitwick → nhận bài Lumos → `/spell lumos` → cầm đũa niệm 3 lần → quay lại Flitwick.

Kiểm tra dữ liệu player:

```yaml
spells:
  - lumos
practice-spells: []
```

Trước khi nộp bài thì `lumos` chỉ xuất hiện trong `practice-spells`.

## Chương trình Năm I đầy đủ

Để được Dumbledore cho lên Năm II, học sinh phải hoàn thành: toàn bộ phép Năm I + Biến hình với McGonagall + Độc dược với Snape + Lớp Bay với Madam Hooch + Thảo dược với Sprout.

## Yêu cầu level

Điều kiện level dùng BetonQuest `numbercompare` với `%ph.hogwarts_level%`. Điều kiện năm học dùng `%ph.hogwarts_year_number%`. Các ngưỡng nằm trong `conditions.yml` và có thể chỉnh dễ dàng.

## Cân bằng tiến độ

Magic XP mỗi bài tăng theo năm: Năm I 150, II 225, III 300, IV 375, V 450, VI 525, VII 600 (chưa tính XP mặc định khi học phép và khi cast). Mức này được chọn để người chơi có thể mở bài kế tiếp bằng cách học thật, không phải cày hàng trăm lần cast.
