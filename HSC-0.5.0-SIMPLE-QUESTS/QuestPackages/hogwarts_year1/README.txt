HOGWARTS YEAR1 COMPATIBILITY SHIM

Giữ folder này tại:
plugins/BetonQuest/QuestPackages/hogwarts_year1/

Đây KHÔNG phải hogwarts_year1 cũ.

Nó:
- KHÔNG tạo NPC
- KHÔNG có hội thoại
- KHÔNG có quest
- KHÔNG chiếm Citizens ID 1-13
- chỉ giữ tên package hogwarts_year1 tồn tại

Lý do:
Một số config PlaceholderAPI/TAB cũ trên server vẫn gọi:
hogwarts_year1:tag:year1_started
hogwarts_year1:tag:year1_charms_done
hogwarts_year1:tag:year1_flying_started
hogwarts_year1:point:charms_practice:amount
v.v.

Nếu xóa package hogwarts_year1 hoàn toàn, BetonQuest 3.2 sẽ spam:
'reference the non-existent package hogwarts_year1'

Quest học phép thật vẫn nằm ở:
hogwarts_spell_school

Package này chỉ là shim PlaceholderAPI; toàn bộ nhiệm vụ thật nằm trong hogwarts_spell_school.
