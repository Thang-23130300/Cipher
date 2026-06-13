**Commit theo format:**
type(scope): message

Ví dụ:
feat(signature): add submit signature servlet
fix(order): fix redirect alert
refactor(product): move query logic to DAO
test(signature): add verify test cases
chore(config): update properties
docs(readme): add commit rules

Mục tiêu là để lịch sử Git dễ đọc, dễ review, biết commit nào thêm chức năng, commit nào sửa lỗi, commit 

**Các loại commit được dùng**
Type	Khi nào dùng?	Ví dụ
feat	Thêm chức năng mới	feat(cart): add checkout button
fix	Sửa lỗi hoặc chỉnh hành vi sai	fix(order): fix invalid status after submit
refactor	Di chuyển, tách class, đổi cấu trúc code nhưng không đổi chức năng	refactor(product): move query logic to DAO
test	Thêm hoặc sửa file test	test(signature): add verify signature test cases
chore	Việc phụ trợ, cấu hình, cleanup, không ảnh hưởng trực tiếp chức năng	chore(config): update application properties
docs	Sửa tài liệu, README, ghi chú	docs(readme): add commit message rules
style	Chỉnh format code, indent, spacing, không đổi logic	style(order): format order JSP layout
