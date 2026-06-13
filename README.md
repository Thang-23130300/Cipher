# Commit Rules

## Commit theo format

```text
type(scope): message
```

## Ví dụ

```text
feat(signature): add submit signature servlet
fix(order): fix redirect alert
refactor(product): move query logic to DAO
test(signature): add verify test cases
chore(config): update properties
docs(readme): add commit rules
```

## Mục tiêu

Mục tiêu là để lịch sử Git dễ đọc, dễ review.

Khi nhìn vào commit message, nhóm có thể biết commit đó đang thêm chức năng, sửa lỗi, refactor code, thêm test hay sửa tài liệu.

## Các loại commit được dùng

| Type | Khi nào dùng? | Ví dụ |
|---|---|---|
| `feat` | Thêm chức năng mới | `feat(cart): add checkout button` |
| `fix` | Sửa lỗi hoặc chỉnh hành vi sai | `fix(order): fix invalid status after submit` |
| `refactor` | Di chuyển, tách class, đổi cấu trúc code nhưng không đổi chức năng | `refactor(product): move query logic to DAO` |
| `test` | Thêm hoặc sửa file test | `test(signature): add verify signature test cases` |
| `chore` | Việc phụ trợ, cấu hình, cleanup, không ảnh hưởng trực tiếp chức năng | `chore(config): update application properties` |
| `docs` | Sửa tài liệu, README, ghi chú | `docs(readme): add commit message rules` |
| `style` | Chỉnh format code, indent, spacing, không đổi logic | `style(order): format order JSP layout` |

## Lưu ý khi commit

- Message nên viết ngắn gọn, rõ nghĩa.
- Không dùng commit message quá chung chung như `update code`, `fix bug`, `done`.
- Scope nên là tên module đang sửa.

Ví dụ scope nên dùng:

```text
cart
order
signature
product
config
readme
```

## Ví dụ commit nên dùng

```text
feat(signature): add submit signature servlet
fix(order): fix signature status update
refactor(signature): move verify logic to service
test(signature): add invalid signature test
docs(readme): add commit convention
```

## Ví dụ commit không nên dùng

```text
update code
fix bug
done
abc
commit lan 1
```
