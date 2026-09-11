# HOGWARTS — TOÀN BỘ NHIỆM VỤ NPC

Package này dành cho BetonQuest 3.2 + Citizens + HogwartsStoryCore 0.4.3+.

## Phần thưởng
- Mỗi bài phép hoàn thành: +3 Điểm Nhà +25 Galleon + Magic XP + Mảnh Ma Thuật.
- Mỗi nhiệm vụ môn học NPC: +3 Điểm Nhà +25 Galleon + Magic XP.
- Thi cuối Năm 1–6: +10 Điểm Nhà +100 Galleon.
- Thi tốt nghiệp Năm 7: +25 Điểm Nhà +500 Galleon.

## NPC và nhiệm vụ môn học
### Flitwick (ID 3)
- Năm 1: **Độ chính xác của cổ tay** — Tới khu luyện Bùa chú và chạm vào Bia Phép Thuật (TARGET) để hiệu chỉnh đũa.
- Năm 2: **Nhịp điệu của bùa chú** — Tại lớp Bùa chú, tương tác với Chuông Luyện Phép (BELL) để hoàn thành bài nhịp chú.
- Năm 3: **Kiểm soát chú ngữ** — Đọc giáo trình ở Bục Giảng (LECTERN) trong lớp Bùa chú rồi quay lại báo cáo.
- Năm 4: **Triệu tập có kiểm soát** — Tới Rương Thực Hành (CHEST) trong lớp Bùa chú và kiểm tra mục tiêu triệu tập.
- Năm 5: **Kiểm soát âm thanh** — Tương tác với Khối Nốt Nhạc (NOTE_BLOCK) trong phòng luyện để kiểm tra độ chính xác.
- Năm 6: **Bùa chú nâng cao** — Hoàn thành bài hiệu chỉnh cuối tại Bia Phép Thuật (TARGET) của lớp Bùa chú.
### Quirrell (ID 4)
- Năm 1: **Phản xạ phòng vệ** — Trong thế giới sinh tồn, đánh bại 3 quái vật luyện tập thuộc loại Zombie hoặc Skeleton.
### Lockhart (ID 5)
- Năm 2: **Đấu tay đôi căn bản** — Đánh bại tổng cộng 5 Zombie, Skeleton hoặc Spider để chứng minh phản xạ chiến đấu.
### Lupin (ID 6)
- Năm 3: **Giữ bình tĩnh** — Đánh bại 6 quái vật luyện tập (Zombie, Skeleton hoặc Spider) rồi quay lại gặp Lupin.
### Moody (ID 7)
- Năm 4: **Cảnh giác liên tục** — Đánh bại 8 quái vật luyện tập thuộc nhóm Zombie, Skeleton, Spider hoặc Creeper.
### Umbridge (ID 8)
- Năm 5: **Kiểm tra lý thuyết phòng vệ** — Tới Bục Giảng (LECTERN) ở phòng Phòng Chống Nghệ Thuật Hắc Ám và hoàn thành bài đọc.
### Snape (ID 9)
- Năm 6: **Độc dược nâng cao** — Tới vạc thực hành (CAULDRON) trong hầm Độc dược và hoàn thành bước kiểm tra cuối.
- Năm 1: **Độc dược cơ bản** — thu thập 3 Nether Wart + 1 Spider Eye (quest có sẵn, nay có thưởng).
### McGonagall (ID 10)
- Năm 7: **Biến hình chính xác** — Tới Bàn Rèn (SMITHING_TABLE) trong lớp Biến hình và hoàn thành bài kiểm tra kiểm soát.
- Năm 1: **Biến hình cơ bản** — tương tác ANVIL tại lớp Biến hình (quest có sẵn, nay có thưởng).
### Madam Hooch (ID 11)
- Năm 4: **Đường bay nâng cao** — Bay tới checkpoint nâng cao ở sân bay rồi quay lại gặp Madam Hooch.
- Năm 1: **Bay cơ bản** — đạt checkpoint độ cao (quest có sẵn, nay có thưởng).
### Sprout (ID 12)
- Năm 3: **Nhà kính nâng cao** — Tương tác với COMPOSTER tại nhà kính để hoàn thành bài nhận biết môi trường trồng cây.
- Năm 1: **Thảo dược cơ bản** — thu thập 5 dương xỉ (quest có sẵn, nay có thưởng).

### Dumbledore (ID 13)
- Có kỳ hỏi đáp cuối mỗi Năm 1–7. Phải hoàn thành toàn bộ phép + nhiệm vụ môn học của năm đó mới được thi.
- Đậu Năm 1–6 sẽ tự mở năm tiếp theo. Đậu Năm 7 sẽ tốt nghiệp.

## Block/địa điểm cần đặt trên map
- Gần `loc_charms`: TARGET, BELL, LECTERN, CHEST, NOTE_BLOCK.
- Gần `loc_dada`: LECTERN.
- Gần `loc_potions`: CAULDRON.
- Gần `loc_transfiguration`: ANVIL và SMITHING_TABLE.
- Gần `loc_greenhouse`: COMPOSTER.
- Checkpoint bay nâng cao: `loc_flying_advanced` (mặc định 20;95;100;world).

Nếu map của bạn khác, sửa các tọa độ trong `package.yml -> constants` trước khi chơi.
