/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80407 (8.4.7)
 Source Host           : localhost:3306
 Source Schema         : souvenirDB

 Target Server Type    : MySQL
 File Encoding         : 65001

 Date: 03/03/2026 13:45:04
*/

-- create database souvenirDB;
use souvenirDB;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for addresses
-- ----------------------------
DROP TABLE IF EXISTS `addresses`;
CREATE TABLE `addresses`  (
                              `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
                              `user_id` int UNSIGNED NOT NULL,
                              `address_detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                              `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                              `district` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                              `ward` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                              PRIMARY KEY (`id`) USING BTREE,
                              INDEX `fk_address_user`(`user_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of addresses
-- ----------------------------
INSERT INTO `addresses` VALUES (1, 1, 'xxx', 'Gia Lai', 'Bình Triệu ', 'Hà Nội ');
INSERT INTO `addresses` VALUES (2, 1, 'xxx', 'Gia Lai', 'Bình Triệu ', 'Hà Nội ');
INSERT INTO `addresses` VALUES (3, 1, 'xxx', 'Gia Lai', 'Bình Triệu ', 'Hà Nội ');
INSERT INTO `addresses` VALUES (4, 1, 'xxx', 'Gia Lai', 'Bình Triệu ', 'Hà Nội ');
INSERT INTO `addresses` VALUES (5, 1, 'KTX Khu B', 'Gia Lai', 'Bình Triệu ', 'Hà Nội ');

-- ----------------------------
-- Table structure for cart_details
-- ----------------------------
DROP TABLE IF EXISTS `cart_details`;
CREATE TABLE `cart_details`  (
                                 `product_id` int UNSIGNED NOT NULL,
                                 `cart_id` int UNSIGNED NOT NULL,
                                 `quantity` int NOT NULL DEFAULT 1,
                                 PRIMARY KEY (`product_id`, `cart_id`) USING BTREE,
                                 INDEX `fk_cart_detail_cart`(`cart_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = FIXED;

-- ----------------------------
-- Records of cart_details
-- ----------------------------

-- ----------------------------
-- Table structure for carts
-- ----------------------------
DROP TABLE IF EXISTS `carts`;
CREATE TABLE `carts`  (
                          `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
                          `user_id` int UNSIGNED NOT NULL,
                          `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (`id`) USING BTREE,
                          INDEX `fk_cart_user`(`user_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = FIXED;

-- ----------------------------
-- Records of carts
-- ----------------------------

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
                               `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
                               `category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                               `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                               `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               `deleted_at` datetime NULL,
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories`
(`id`, `category_name`, `image`, `created_at`, `updated_at`, `deleted_at`)
VALUES
(1, 'Đặc sản Nem - Chả - Tré', 'nem-cha-tre.png', NOW(), NOW(), NULL),

(2, 'Bánh kẹo đặc sản', 'banh-keo-dac-san.png', NOW(), NOW(), NULL),

(3, 'Danh tửu Bầu Đá', 'danh-tuu-bau-da.png', NOW(), NOW(), NULL),

(4, 'Hải sản khô & Chế biến sẵn', 'hai-san-kho-va-che-bien.png', NOW(), NOW(), NULL),

(5, 'Làng nghề Thủ công mỹ nghệ', 'lang-nghe-thu-cong.png', NOW(), NOW(), NULL),

(6, 'Quà lưu niệm Văn hóa & Võ thuật', 'van-hoa-va-vo-thuat.png', NOW(), NOW(), NULL),

(7, 'Đặc sản Xứ Dừa', 'dac-san-xu-dua.png', NOW(), NOW(), NULL),

(8, 'Nông sản & Trà thảo mộc', 'nong-san-va-tra.png', NOW(), NOW(), NULL);

-- ----------------------------
-- Table structure for order_details
-- ----------------------------
DROP TABLE IF EXISTS `order_details`;
CREATE TABLE `order_details`  (
                                  `order_id` int UNSIGNED NOT NULL,
                                  `product_id` int UNSIGNED NOT NULL,
                                  `quantity` int NOT NULL,
                                  `price_at_purchase` decimal(15, 2) NOT NULL,
                                  PRIMARY KEY (`order_id`, `product_id`) USING BTREE,
                                  INDEX `fk_order_detail_product`(`product_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = FIXED;

-- ----------------------------
-- Records of order_details
-- ----------------------------

-- ----------------------------
-- Table structure for order_status
-- ----------------------------
DROP TABLE IF EXISTS `order_status`;
CREATE TABLE `order_status`  (
                                 `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
                                 `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_status
-- ----------------------------

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
                           `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
                           `user_id` int UNSIGNED NOT NULL,
                           `address_id` int UNSIGNED NOT NULL,
                           `order_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `total_amount` decimal(15, 2) NOT NULL,
                           `status_id` int UNSIGNED NOT NULL,
                           PRIMARY KEY (`id`) USING BTREE,
                           INDEX `fk_order_user`(`user_id`) USING BTREE,
                           INDEX `fk_order_address`(`address_id`) USING BTREE,
                           INDEX `fk_order_status`(`status_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = FIXED;

-- ----------------------------
-- Records of orders
-- ----------------------------

-- ----------------------------
-- Table structure for payment_cards
-- ----------------------------
DROP TABLE IF EXISTS `payment_cards`;
CREATE TABLE `payment_cards`  (
                                  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
                                  `user_id` int UNSIGNED NOT NULL,
                                  `card_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                  `card_number` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                  `expiry_date` date NOT NULL,
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE INDEX `card_number`(`card_number`) USING BTREE,
                                  INDEX `fk_card_user`(`user_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of payment_cards
-- ----------------------------

-- ----------------------------
-- Table structure for product_specifications
-- ----------------------------
DROP TABLE IF EXISTS `product_specifications`;
CREATE TABLE `product_specifications`  (
                                           `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
                                           `product_id` int UNSIGNED NOT NULL,
                                           `spec_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                           `spec_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                           PRIMARY KEY (`id`) USING BTREE,
                                           INDEX `fk_spec_product`(`product_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 859 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_specifications
-- ----------------------------
INSERT INTO `product_specifications` VALUES (1, 1, 'Quy cách', 'Cây gói lá, bó rơm');
INSERT INTO `product_specifications` VALUES (2, 1, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (3, 1, 'Hạn sử dụng', '14 ngày từ NSX');
INSERT INTO `product_specifications` VALUES (4, 1, 'Bảo quản', 'Ngăn mát tủ lạnh');
INSERT INTO `product_specifications` VALUES (5, 1, 'Chất bảo quản', 'Không');
INSERT INTO `product_specifications` VALUES (6, 2, 'Khối lượng', '500g');
INSERT INTO `product_specifications` VALUES (7, 2, 'Quy cách', 'Hũ nhựa PET');
INSERT INTO `product_specifications` VALUES (8, 2, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (9, 2, 'Hạn sử dụng', '14 ngày từ NSX');
INSERT INTO `product_specifications` VALUES (10, 2, 'Bảo quản', 'Ngăn mát tủ lạnh');
INSERT INTO `product_specifications` VALUES (11, 2, 'Chất bảo quản', 'Không');
INSERT INTO `product_specifications` VALUES (12, 3, 'Khối lượng', '1kg');
INSERT INTO `product_specifications` VALUES (13, 3, 'Quy cách', 'Hũ nhựa PET');
INSERT INTO `product_specifications` VALUES (14, 3, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (15, 3, 'Hạn sử dụng', '14 ngày từ NSX');
INSERT INTO `product_specifications` VALUES (16, 3, 'Bảo quản', 'Ngăn mát tủ lạnh');
INSERT INTO `product_specifications` VALUES (17, 3, 'Chất bảo quản', 'Không');
INSERT INTO `product_specifications` VALUES (18, 4, 'Quy cách', 'Gói lá ổi truyền thống');
INSERT INTO `product_specifications` VALUES (19, 4, 'Xuất xứ', 'Chợ Huyện, Bình Định');
INSERT INTO `product_specifications` VALUES (20, 4, 'Hạn sử dụng', '5 – 7 ngày');
INSERT INTO `product_specifications` VALUES (21, 4, 'Bảo quản', 'Ngăn mát tủ lạnh');
INSERT INTO `product_specifications` VALUES (22, 4, 'Chất bảo quản', 'Không');
INSERT INTO `product_specifications` VALUES (23, 5, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (24, 5, 'VSATTP', '105/2023/NNPTNN - BD');
INSERT INTO `product_specifications` VALUES (25, 5, 'Hạn sử dụng', '30 ngày từ NSX');
INSERT INTO `product_specifications` VALUES (26, 5, 'Thành phần', '75% thịt nạc heo + 25% bì heo & tai heo + ớt xiêm xanh');
INSERT INTO `product_specifications` VALUES (27, 5, 'Chất bảo quản', 'Không');
INSERT INTO `product_specifications` VALUES (28, 5, 'HDSD', 'Theo bao bì');
INSERT INTO `product_specifications` VALUES (29, 6, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (30, 6, 'Thành phần', 'Thịt bò tươi, gia vị truyền thống');
INSERT INTO `product_specifications` VALUES (31, 6, 'Hạn sử dụng', '30 ngày từ NSX');
INSERT INTO `product_specifications` VALUES (32, 6, 'Chất bảo quản', 'Không');
INSERT INTO `product_specifications` VALUES (33, 6, 'Bảo quản', 'Ngăn mát tủ lạnh');
INSERT INTO `product_specifications` VALUES (34, 7, 'Xuất xứ', 'Chợ Huyện – Bình Định');
INSERT INTO `product_specifications` VALUES (35, 7, 'Khối lượng', '550g');
INSERT INTO `product_specifications` VALUES (36, 7, 'Thành phần', 'Thịt heo, bì heo, lá ổi, gia vị');
INSERT INTO `product_specifications` VALUES (37, 7, 'Hạn sử dụng', '15 – 20 ngày từ NSX');
INSERT INTO `product_specifications` VALUES (38, 7, 'Chất bảo quản', 'Không');
INSERT INTO `product_specifications` VALUES (39, 7, 'Bảo quản', 'Ngăn mát tủ lạnh');
INSERT INTO `product_specifications` VALUES (40, 8, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (41, 8, 'Khối lượng', '500 g');
INSERT INTO `product_specifications` VALUES (42, 8, 'Thành phần', '100% thịt nạc heo');
INSERT INTO `product_specifications` VALUES (43, 8, 'Hạn sử dụng', '30 ngày');
INSERT INTO `product_specifications` VALUES (44, 8, 'Đóng gói', 'Lá chuối');
INSERT INTO `product_specifications` VALUES (45, 8, 'Không chứa', 'Hàn the, chất bảo quản');
INSERT INTO `product_specifications` VALUES (46, 9, 'Xuất xứ', 'Quy Nhơn');
INSERT INTO `product_specifications` VALUES (47, 9, 'Thành phần', 'Chả cá, rau răm');
INSERT INTO `product_specifications` VALUES (48, 9, 'Hình thức', 'Cuốn sẵn');
INSERT INTO `product_specifications` VALUES (49, 9, 'Khối lượng', 'Phần tiêu chuẩn');
INSERT INTO `product_specifications` VALUES (50, 9, 'Cách dùng', 'Ăn kèm bánh tráng, nước chấm');
INSERT INTO `product_specifications` VALUES (51, 9, 'Bảo quản', 'Ngăn mát tủ lạnh');
INSERT INTO `product_specifications` VALUES (52, 10, 'Trọng lượng', '500gr');
INSERT INTO `product_specifications` VALUES (53, 10, 'Đóng gói', 'Hút chân không');
INSERT INTO `product_specifications` VALUES (54, 10, 'Bảo quản', 'Ngăn đông tủ lạnh');
INSERT INTO `product_specifications` VALUES (55, 10, 'Hạn sử dụng', '2 tháng');
INSERT INTO `product_specifications` VALUES (56, 10, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (57, 11, 'Đóng gói', 'Gói truyền thống');
INSERT INTO `product_specifications` VALUES (58, 11, 'Bảo quản', 'Ngăn mát hoặc ngăn đông');
INSERT INTO `product_specifications` VALUES (59, 11, 'Hạn sử dụng', '30 ngày');
INSERT INTO `product_specifications` VALUES (60, 11, 'Xuất xứ', 'Chợ Huyện, Bình Định');
INSERT INTO `product_specifications` VALUES (61, 12, 'Thành phần', 'Tôm tươi, thịt, gia vị truyền thống');
INSERT INTO `product_specifications` VALUES (62, 12, 'Đóng gói', 'Hút chân không');
INSERT INTO `product_specifications` VALUES (63, 12, 'Bảo quản', 'Ngăn đông tủ lạnh');
INSERT INTO `product_specifications` VALUES (64, 12, 'Hạn sử dụng', '2 tháng');
INSERT INTO `product_specifications` VALUES (65, 12, 'Xuất xứ', 'Huế');
INSERT INTO `product_specifications` VALUES (66, 13, 'Trọng lượng', '500gr');
INSERT INTO `product_specifications` VALUES (67, 13, 'Thành phần', 'Tôm đất nguyên con, bánh tráng, gia vị');
INSERT INTO `product_specifications` VALUES (68, 13, 'Bảo quản', 'Ngăn đông tủ lạnh');
INSERT INTO `product_specifications` VALUES (69, 13, 'Hạn sử dụng', '2 tháng');
INSERT INTO `product_specifications` VALUES (70, 13, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (71, 14, 'Thành phần', 'Thịt heo, tai heo, gia vị');
INSERT INTO `product_specifications` VALUES (72, 14, 'Đóng gói', 'Hút chân không');
INSERT INTO `product_specifications` VALUES (73, 14, 'Bảo quản', 'Ngăn mát hoặc ngăn đông');
INSERT INTO `product_specifications` VALUES (74, 14, 'Hạn sử dụng', '30 ngày');
INSERT INTO `product_specifications` VALUES (75, 14, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (76, 15, 'Thành phần', 'Da heo, thịt heo, gia vị');
INSERT INTO `product_specifications` VALUES (77, 15, 'Đóng gói', 'Hút chân không');
INSERT INTO `product_specifications` VALUES (78, 15, 'Bảo quản', 'Ngăn mát hoặc ngăn đông');
INSERT INTO `product_specifications` VALUES (79, 15, 'Hạn sử dụng', '30 ngày');
INSERT INTO `product_specifications` VALUES (80, 15, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (81, 16, 'Trọng lượng', '500gr');
INSERT INTO `product_specifications` VALUES (82, 16, 'Thành phần', 'Thịt heo, bột quế, gia vị');
INSERT INTO `product_specifications` VALUES (83, 16, 'Đóng gói', 'Hút chân không');
INSERT INTO `product_specifications` VALUES (84, 16, 'Bảo quản', 'Ngăn đông tủ lạnh');
INSERT INTO `product_specifications` VALUES (85, 16, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (86, 17, 'Trọng lượng', '500gr');
INSERT INTO `product_specifications` VALUES (87, 17, 'Thành phần', 'Cá tươi, gia vị');
INSERT INTO `product_specifications` VALUES (88, 17, 'Đóng gói', 'Hút chân không');
INSERT INTO `product_specifications` VALUES (89, 17, 'Bảo quản', 'Ngăn đông tủ lạnh');
INSERT INTO `product_specifications` VALUES (90, 17, 'Xuất xứ', 'Quy Nhơn, Bình Định');
INSERT INTO `product_specifications` VALUES (91, 18, 'Trọng lượng', '500gr');
INSERT INTO `product_specifications` VALUES (92, 18, 'Thành phần', 'Tôm đất nguyên con, bánh tráng, gia vị');
INSERT INTO `product_specifications` VALUES (93, 18, 'Bảo quản', 'Ngăn đông tủ lạnh');
INSERT INTO `product_specifications` VALUES (94, 18, 'Hạn sử dụng', '2 tháng');
INSERT INTO `product_specifications` VALUES (95, 18, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (96, 19, 'Trọng lượng', '500gr');
INSERT INTO `product_specifications` VALUES (97, 19, 'Thành phần', 'Thịt bò, gân bò, gia vị');
INSERT INTO `product_specifications` VALUES (98, 19, 'Đóng gói', 'Hút chân không');
INSERT INTO `product_specifications` VALUES (99, 19, 'Bảo quản', 'Ngăn đông tủ lạnh');
INSERT INTO `product_specifications` VALUES (100, 19, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (101, 20, 'Trọng lượng', '500gr');
INSERT INTO `product_specifications` VALUES (102, 20, 'Thành phần', 'Cá tươi, gia vị');
INSERT INTO `product_specifications` VALUES (103, 20, 'Đóng gói', 'Hút chân không');
INSERT INTO `product_specifications` VALUES (104, 20, 'Bảo quản', 'Ngăn đông tủ lạnh');
INSERT INTO `product_specifications` VALUES (105, 20, 'Xuất xứ', 'Quy Nhơn, Bình Định');
INSERT INTO `product_specifications` VALUES (106, 21, 'Trọng lượng', '500gr');
INSERT INTO `product_specifications` VALUES (107, 21, 'Thành phần', 'Cá tươi, gia vị');
INSERT INTO `product_specifications` VALUES (108, 21, 'Đóng gói', 'Hút chân không');
INSERT INTO `product_specifications` VALUES (109, 21, 'Bảo quản', 'Ngăn đông tủ lạnh');
INSERT INTO `product_specifications` VALUES (110, 21, 'Xuất xứ', 'Quy Nhơn, Bình Định');
INSERT INTO `product_specifications` VALUES (111, 22, 'Trọng lượng', '500gr');
INSERT INTO `product_specifications` VALUES (112, 22, 'Thành phần', 'Tôm đất, bánh tráng, gia vị');
INSERT INTO `product_specifications` VALUES (113, 22, 'Đóng gói', 'Hút chân không');
INSERT INTO `product_specifications` VALUES (114, 22, 'Bảo quản', 'Ngăn đông tủ lạnh');
INSERT INTO `product_specifications` VALUES (115, 22, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (116, 23, 'Trọng lượng', '500gr');
INSERT INTO `product_specifications` VALUES (117, 23, 'Thành phần', 'Thịt heo, gia vị');
INSERT INTO `product_specifications` VALUES (118, 23, 'Đóng gói', 'Gói lá truyền thống');
INSERT INTO `product_specifications` VALUES (119, 23, 'Bảo quản', 'Ngăn mát hoặc ngăn đông');
INSERT INTO `product_specifications` VALUES (120, 23, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (121, 24, 'Trọng lượng', '500gr');
INSERT INTO `product_specifications` VALUES (122, 24, 'Thành phần', 'Thịt bò, gia vị');
INSERT INTO `product_specifications` VALUES (123, 24, 'Đóng gói', 'Hút chân không');
INSERT INTO `product_specifications` VALUES (124, 24, 'Bảo quản', 'Ngăn đông tủ lạnh');
INSERT INTO `product_specifications` VALUES (125, 24, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (126, 25, 'Thành phần', 'Thịt heo, tai heo, gia vị');
INSERT INTO `product_specifications` VALUES (127, 25, 'Đóng gói', 'Gói truyền thống');
INSERT INTO `product_specifications` VALUES (128, 25, 'Bảo quản', 'Ngăn mát tủ lạnh');
INSERT INTO `product_specifications` VALUES (129, 25, 'Hạn sử dụng', '30 ngày');
INSERT INTO `product_specifications` VALUES (130, 25, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (131, 26, 'Thành phần', 'Lá gai, bột nếp, đậu xanh, dừa');
INSERT INTO `product_specifications` VALUES (132, 26, 'Đóng gói', 'Hộp');
INSERT INTO `product_specifications` VALUES (133, 26, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (134, 26, 'Hạn sử dụng', '7 ngày');
INSERT INTO `product_specifications` VALUES (135, 26, 'Xuất xứ', 'Quy Nhơn, Bình Định');
INSERT INTO `product_specifications` VALUES (136, 27, 'Thành phần', 'Lá gai, bột nếp, đậu xanh, dừa');
INSERT INTO `product_specifications` VALUES (137, 27, 'Đóng gói', 'Hộp');
INSERT INTO `product_specifications` VALUES (138, 27, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (139, 27, 'Hạn sử dụng', '7 ngày');
INSERT INTO `product_specifications` VALUES (140, 27, 'Xuất xứ', 'Quy Nhơn, Bình Định');
INSERT INTO `product_specifications` VALUES (141, 28, 'Thành phần', 'Bột nếp, dừa, đường');
INSERT INTO `product_specifications` VALUES (142, 28, 'Đóng gói', 'Hộp');
INSERT INTO `product_specifications` VALUES (143, 28, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (144, 28, 'Hạn sử dụng', '10 ngày');
INSERT INTO `product_specifications` VALUES (145, 28, 'Xuất xứ', 'Tam Quan, Bình Định');
INSERT INTO `product_specifications` VALUES (146, 29, 'Thành phần', 'Lá gai, bột nếp, đậu xanh, dừa');
INSERT INTO `product_specifications` VALUES (147, 29, 'Đóng gói', 'Giấy vàng (Gold)');
INSERT INTO `product_specifications` VALUES (148, 29, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (149, 29, 'Hạn sử dụng', '7 ngày');
INSERT INTO `product_specifications` VALUES (150, 29, 'Xuất xứ', 'Quy Nhơn, Bình Định');
INSERT INTO `product_specifications` VALUES (151, 30, 'Thành phần', 'Đậu xanh, đường');
INSERT INTO `product_specifications` VALUES (152, 30, 'Đóng gói', 'Hộp');
INSERT INTO `product_specifications` VALUES (153, 30, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (154, 30, 'Hạn sử dụng', '15 ngày');
INSERT INTO `product_specifications` VALUES (155, 30, 'Xuất xứ', 'Quy Nhơn, Bình Định');
INSERT INTO `product_specifications` VALUES (156, 31, 'Thành phần', 'Dừa, bột nếp, đường');
INSERT INTO `product_specifications` VALUES (157, 31, 'Đóng gói', 'Gói');
INSERT INTO `product_specifications` VALUES (158, 31, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (159, 31, 'Hạn sử dụng', '30 ngày');
INSERT INTO `product_specifications` VALUES (160, 31, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (161, 32, 'Thành phần', 'Bột gạo, dừa, mè');
INSERT INTO `product_specifications` VALUES (162, 32, 'Đóng gói', 'Gói');
INSERT INTO `product_specifications` VALUES (163, 32, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (164, 32, 'Hạn sử dụng', '30 ngày');
INSERT INTO `product_specifications` VALUES (165, 32, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (166, 33, 'Thành phần', 'Lá gai, bột nếp, đậu xanh, dừa');
INSERT INTO `product_specifications` VALUES (167, 33, 'Đóng gói', 'Gói lá tự nhiên');
INSERT INTO `product_specifications` VALUES (168, 33, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (169, 33, 'Hạn sử dụng', '7 ngày');
INSERT INTO `product_specifications` VALUES (170, 33, 'Xuất xứ', 'Quy Nhơn, Bình Định');
INSERT INTO `product_specifications` VALUES (171, 34, 'Thành phần', 'Đậu phộng (lạc), mạch nha, đường');
INSERT INTO `product_specifications` VALUES (172, 34, 'Đóng gói', 'Gói');
INSERT INTO `product_specifications` VALUES (173, 34, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (174, 34, 'Hạn sử dụng', '30 ngày');
INSERT INTO `product_specifications` VALUES (175, 34, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (176, 35, 'Thành phần', 'Bột, mạch nha, đậu phộng, mè');
INSERT INTO `product_specifications` VALUES (177, 35, 'Đóng gói', 'Gói');
INSERT INTO `product_specifications` VALUES (178, 35, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (179, 35, 'Hạn sử dụng', '30 ngày');
INSERT INTO `product_specifications` VALUES (180, 35, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (181, 36, 'Thành phần', 'Dừa tươi, đường, mạch nha');
INSERT INTO `product_specifications` VALUES (182, 36, 'Đóng gói', 'Gói');
INSERT INTO `product_specifications` VALUES (183, 36, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (184, 36, 'Hạn sử dụng', '30 ngày');
INSERT INTO `product_specifications` VALUES (185, 36, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (186, 37, 'Thành phần', 'Nếp, mắm, đường, gia vị');
INSERT INTO `product_specifications` VALUES (187, 37, 'Đóng gói', 'Gói');
INSERT INTO `product_specifications` VALUES (188, 37, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (189, 37, 'Hạn sử dụng', '15 ngày');
INSERT INTO `product_specifications` VALUES (190, 37, 'Xuất xứ', 'Quy Nhơn, Bình Định');
INSERT INTO `product_specifications` VALUES (191, 38, 'Thành phần', 'Bột gạo, cốt dừa, mè');
INSERT INTO `product_specifications` VALUES (192, 38, 'Đóng gói', 'Gói');
INSERT INTO `product_specifications` VALUES (193, 38, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (194, 38, 'Hạn sử dụng', '30 ngày');
INSERT INTO `product_specifications` VALUES (195, 38, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (196, 39, 'Thành phần', 'Bột gạo, dừa, mè');
INSERT INTO `product_specifications` VALUES (197, 39, 'Đóng gói', 'Gói');
INSERT INTO `product_specifications` VALUES (198, 39, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (199, 39, 'Hạn sử dụng', '30 ngày');
INSERT INTO `product_specifications` VALUES (200, 39, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (201, 40, 'Khối lượng', '730g');
INSERT INTO `product_specifications` VALUES (202, 40, 'Thành phần', 'Khoai lang, đường');
INSERT INTO `product_specifications` VALUES (203, 40, 'Đóng gói', 'Hộp');
INSERT INTO `product_specifications` VALUES (204, 40, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (205, 40, 'Xuất xứ', 'Quy Nhơn, Bình Định');
INSERT INTO `product_specifications` VALUES (206, 41, 'Thành phần', 'Dừa, bột nếp, đường');
INSERT INTO `product_specifications` VALUES (207, 41, 'Đóng gói', 'Hộp');
INSERT INTO `product_specifications` VALUES (208, 41, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (209, 41, 'Hạn sử dụng', '30 ngày');
INSERT INTO `product_specifications` VALUES (210, 41, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (211, 42, 'Thành phần', 'Bột gạo, nước dừa, mắm nhĩ');
INSERT INTO `product_specifications` VALUES (212, 42, 'Đóng gói', 'Gói');
INSERT INTO `product_specifications` VALUES (213, 42, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (214, 42, 'Hạn sử dụng', '30 ngày');
INSERT INTO `product_specifications` VALUES (215, 42, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (216, 43, 'Số lượng', '10 bánh / túi');
INSERT INTO `product_specifications` VALUES (217, 43, 'Thành phần', 'Bột gạo, dừa');
INSERT INTO `product_specifications` VALUES (218, 43, 'Tình trạng', 'Chưa nướng');
INSERT INTO `product_specifications` VALUES (219, 43, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (220, 43, 'Xuất xứ', 'Tam Quan, Bình Định');
INSERT INTO `product_specifications` VALUES (221, 44, 'Số lượng', '10 bánh / set');
INSERT INTO `product_specifications` VALUES (222, 44, 'Thành phần', 'Bột gạo, dừa, tỏi, ớt, gia vị');
INSERT INTO `product_specifications` VALUES (223, 44, 'Tình trạng', 'Dùng liền');
INSERT INTO `product_specifications` VALUES (224, 44, 'Đóng gói', 'Set');
INSERT INTO `product_specifications` VALUES (225, 44, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (226, 45, 'Thành phần', 'Bột gạo, mè đen');
INSERT INTO `product_specifications` VALUES (227, 45, 'Đóng gói', 'Gói');
INSERT INTO `product_specifications` VALUES (228, 45, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (229, 45, 'Hạn sử dụng', '30 ngày');
INSERT INTO `product_specifications` VALUES (230, 45, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (231, 46, 'Khối lượng', '500gr');
INSERT INTO `product_specifications` VALUES (232, 46, 'Thành phần', 'Bột gạo');
INSERT INTO `product_specifications` VALUES (233, 46, 'Đóng gói', 'Hút chân không');
INSERT INTO `product_specifications` VALUES (234, 46, 'Bảo quản', 'Ngăn mát tủ lạnh');
INSERT INTO `product_specifications` VALUES (235, 46, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (236, 47, 'Khối lượng', '500gr');
INSERT INTO `product_specifications` VALUES (237, 47, 'Thành phần', 'Bột gạo');
INSERT INTO `product_specifications` VALUES (238, 47, 'Đóng gói', 'Hút chân không');
INSERT INTO `product_specifications` VALUES (239, 47, 'Bảo quản', 'Nơi khô ráo hoặc ngăn mát');
INSERT INTO `product_specifications` VALUES (240, 47, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (241, 48, 'Khối lượng', '500gr');
INSERT INTO `product_specifications` VALUES (242, 48, 'Thành phần', 'Bột gạo');
INSERT INTO `product_specifications` VALUES (243, 48, 'Đóng gói', 'Hút chân không');
INSERT INTO `product_specifications` VALUES (244, 48, 'Bảo quản', 'Ngăn mát tủ lạnh');
INSERT INTO `product_specifications` VALUES (245, 48, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (246, 49, 'Thành phần', 'Bột gạo, tôm, hành phi');
INSERT INTO `product_specifications` VALUES (247, 49, 'Đóng gói', 'Ly');
INSERT INTO `product_specifications` VALUES (248, 49, 'Bảo quản', 'Ngăn mát tủ lạnh');
INSERT INTO `product_specifications` VALUES (249, 49, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (250, 50, 'Khối lượng', '500gr');
INSERT INTO `product_specifications` VALUES (251, 50, 'Thành phần', 'Bột gạo');
INSERT INTO `product_specifications` VALUES (252, 50, 'Đóng gói', 'Hút chân không');
INSERT INTO `product_specifications` VALUES (253, 50, 'Bảo quản', 'Nơi khô ráo hoặc ngăn mát');
INSERT INTO `product_specifications` VALUES (254, 50, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (255, 51, 'Khối lượng', '500gr');
INSERT INTO `product_specifications` VALUES (256, 51, 'Thành phần', 'Bột mì');
INSERT INTO `product_specifications` VALUES (257, 51, 'Đóng gói', 'Hút chân không');
INSERT INTO `product_specifications` VALUES (258, 51, 'Bảo quản', 'Nơi khô ráo hoặc ngăn mát');
INSERT INTO `product_specifications` VALUES (259, 51, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (260, 52, 'Khối lượng', '1.6kg');
INSERT INTO `product_specifications` VALUES (261, 52, 'Thành phần', 'Nếp, đậu xanh, thịt heo');
INSERT INTO `product_specifications` VALUES (262, 52, 'Đóng gói', 'Gói lá chuối');
INSERT INTO `product_specifications` VALUES (263, 52, 'Bảo quản', 'Ngăn mát tủ lạnh');
INSERT INTO `product_specifications` VALUES (264, 52, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (265, 53, 'Dung tích', '500ml');
INSERT INTO `product_specifications` VALUES (266, 53, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (267, 53, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (268, 53, 'Bao bì', 'Chai sứ Long Phụng');
INSERT INTO `product_specifications` VALUES (269, 53, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (270, 54, 'Dung tích', '500ml');
INSERT INTO `product_specifications` VALUES (271, 54, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (272, 54, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (273, 54, 'Bao bì', 'Chai sứ Rồng nhỏ');
INSERT INTO `product_specifications` VALUES (274, 54, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (275, 55, 'Dung tích', '2.5 lít');
INSERT INTO `product_specifications` VALUES (276, 55, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (277, 55, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (278, 55, 'Bao bì', 'Bình sứ màu xanh');
INSERT INTO `product_specifications` VALUES (279, 55, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (280, 56, 'Dung tích', '2.5 lít');
INSERT INTO `product_specifications` VALUES (281, 56, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (282, 56, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (283, 56, 'Bao bì', 'Bình sứ màu đen');
INSERT INTO `product_specifications` VALUES (284, 56, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (285, 57, 'Dung tích', '500ml');
INSERT INTO `product_specifications` VALUES (286, 57, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (287, 57, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (288, 57, 'Bao bì', 'Chai sứ rồng nhỏ');
INSERT INTO `product_specifications` VALUES (289, 57, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (290, 58, 'Dung tích', '650ml');
INSERT INTO `product_specifications` VALUES (291, 58, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (292, 58, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (293, 58, 'Bao bì', 'Chai sứ Long Phụng màu hồng');
INSERT INTO `product_specifications` VALUES (294, 58, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (295, 59, 'Dung tích', '500ml');
INSERT INTO `product_specifications` VALUES (296, 59, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (297, 59, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (298, 59, 'Bao bì', 'Chai sứ rồng nhỏ màu xanh bút bi');
INSERT INTO `product_specifications` VALUES (299, 59, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (300, 60, 'Dung tích', '500ml');
INSERT INTO `product_specifications` VALUES (301, 60, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (302, 60, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (303, 60, 'Bao bì', 'Bình sứ thuyền chim màu hồng');
INSERT INTO `product_specifications` VALUES (304, 60, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (305, 61, 'Dung tích', '500ml');
INSERT INTO `product_specifications` VALUES (306, 61, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (307, 61, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (308, 61, 'Bao bì', 'Bình sứ thuyền chim màu xanh rêu');
INSERT INTO `product_specifications` VALUES (309, 61, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (310, 62, 'Dung tích', '2.5 lít');
INSERT INTO `product_specifications` VALUES (311, 62, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (312, 62, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (313, 62, 'Bao bì', 'Bình sứ màu xanh rêu');
INSERT INTO `product_specifications` VALUES (314, 62, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (315, 63, 'Dung tích', '500ml');
INSERT INTO `product_specifications` VALUES (316, 63, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (317, 63, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (318, 63, 'Bao bì', 'Chai sứ rồng nhỏ màu hồng');
INSERT INTO `product_specifications` VALUES (319, 63, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (320, 64, 'Dung tích', '650ml');
INSERT INTO `product_specifications` VALUES (321, 64, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (322, 64, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (323, 64, 'Bao bì', 'Bình sứ dáng chum màu nâu');
INSERT INTO `product_specifications` VALUES (324, 64, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (325, 65, 'Dung tích', '350ml');
INSERT INTO `product_specifications` VALUES (326, 65, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (327, 65, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (328, 65, 'Bao bì', 'Chai sứ dáng hồ lô');
INSERT INTO `product_specifications` VALUES (329, 65, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (330, 66, 'Dung tích', '500ml');
INSERT INTO `product_specifications` VALUES (331, 66, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (332, 66, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (333, 66, 'Bao bì', 'Bình sứ ba bầu màu xanh ngọc');
INSERT INTO `product_specifications` VALUES (334, 66, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (335, 67, 'Dung tích', '500ml');
INSERT INTO `product_specifications` VALUES (336, 67, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (337, 67, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (338, 67, 'Bao bì', 'Bình sứ thuyền chim màu đen');
INSERT INTO `product_specifications` VALUES (339, 67, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (340, 68, 'Dung tích', '2.5 lít');
INSERT INTO `product_specifications` VALUES (341, 68, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (342, 68, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (343, 68, 'Bao bì', 'Bình sứ màu xanh ngọc');
INSERT INTO `product_specifications` VALUES (344, 68, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (345, 69, 'Dung tích', '350ml');
INSERT INTO `product_specifications` VALUES (346, 69, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (347, 69, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (348, 69, 'Bao bì', 'Chai sứ dáng hồ lô màu xanh rêu');
INSERT INTO `product_specifications` VALUES (349, 69, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (350, 70, 'Dung tích', '2.5 lít');
INSERT INTO `product_specifications` VALUES (351, 70, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (352, 70, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (353, 70, 'Bao bì', 'Bình sứ màu nhớt');
INSERT INTO `product_specifications` VALUES (354, 70, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (355, 71, 'Dung tích', '2.5 lít');
INSERT INTO `product_specifications` VALUES (356, 71, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (357, 71, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (358, 71, 'Bao bì', 'Bình sứ dáng hồ lô');
INSERT INTO `product_specifications` VALUES (359, 71, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (360, 72, 'Dung tích', '350ml');
INSERT INTO `product_specifications` VALUES (361, 72, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (362, 72, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (363, 72, 'Bao bì', 'Chai sứ dáng hồ lô màu xanh ngọc');
INSERT INTO `product_specifications` VALUES (364, 72, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (365, 73, 'Dung tích', '1 lít');
INSERT INTO `product_specifications` VALUES (366, 73, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (367, 73, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (368, 73, 'Bao bì', 'Bình sứ dáng thuyền lớn màu đen');
INSERT INTO `product_specifications` VALUES (369, 73, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (370, 74, 'Dung tích', '1 lít');
INSERT INTO `product_specifications` VALUES (371, 74, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (372, 74, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (373, 74, 'Bao bì', 'Bình sứ dáng thuyền lớn màu xanh ngọc');
INSERT INTO `product_specifications` VALUES (374, 74, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (375, 75, 'Dung tích', '500ml');
INSERT INTO `product_specifications` VALUES (376, 75, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (377, 75, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (378, 75, 'Bao bì', 'Bình sứ ba bầu màu đen');
INSERT INTO `product_specifications` VALUES (379, 75, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (380, 76, 'Dung tích', '500ml');
INSERT INTO `product_specifications` VALUES (381, 76, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (382, 76, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (383, 76, 'Bao bì', 'Bình sứ ba bầu màu xanh rêu');
INSERT INTO `product_specifications` VALUES (384, 76, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (385, 77, 'Dung tích', '650ml');
INSERT INTO `product_specifications` VALUES (386, 77, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (387, 77, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (388, 77, 'Bao bì', 'Chai sứ Long Phụng màu đen');
INSERT INTO `product_specifications` VALUES (389, 77, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (390, 78, 'Dung tích', '500ml');
INSERT INTO `product_specifications` VALUES (391, 78, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (392, 78, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (393, 78, 'Bao bì', 'Bình sứ ba bầu màu xanh bút bi');
INSERT INTO `product_specifications` VALUES (394, 78, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (395, 79, 'Dung tích', '650ml');
INSERT INTO `product_specifications` VALUES (396, 79, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (397, 79, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (398, 79, 'Bao bì', 'Bình sứ dáng chum màu xanh bút bi');
INSERT INTO `product_specifications` VALUES (399, 79, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (400, 80, 'Dung tích', '650ml');
INSERT INTO `product_specifications` VALUES (401, 80, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (402, 80, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (403, 80, 'Bao bì', 'Chai sứ Long Phụng màu xanh rêu');
INSERT INTO `product_specifications` VALUES (404, 80, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (405, 81, 'Dung tích', '1 lít');
INSERT INTO `product_specifications` VALUES (406, 81, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (407, 81, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (408, 81, 'Bao bì', 'Bình sứ dáng thuyền lớn màu da lươn');
INSERT INTO `product_specifications` VALUES (409, 81, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (410, 82, 'Dung tích', '500ml');
INSERT INTO `product_specifications` VALUES (411, 82, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (412, 82, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (413, 82, 'Bao bì', 'Bình sứ thuyền chim màu da lươn');
INSERT INTO `product_specifications` VALUES (414, 82, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (415, 83, 'Dung tích', '500ml');
INSERT INTO `product_specifications` VALUES (416, 83, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (417, 83, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (418, 83, 'Bao bì', 'Chai sứ dáng hồ lô (chai trung)');
INSERT INTO `product_specifications` VALUES (419, 83, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (420, 84, 'Dung tích', '1 lít');
INSERT INTO `product_specifications` VALUES (421, 84, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (422, 84, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (423, 84, 'Bao bì', 'Bình sứ dáng thuyền lớn màu xanh bút bi');
INSERT INTO `product_specifications` VALUES (424, 84, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (425, 85, 'Dung tích', '500ml');
INSERT INTO `product_specifications` VALUES (426, 85, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (427, 85, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (428, 85, 'Bao bì', 'Chai sứ rồng nhỏ màu đen');
INSERT INTO `product_specifications` VALUES (429, 85, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (430, 86, 'Dung tích', '500ml');
INSERT INTO `product_specifications` VALUES (431, 86, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (432, 86, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (433, 86, 'Bao bì', 'Bình sứ thuyền chim màu xanh ngọc');
INSERT INTO `product_specifications` VALUES (434, 86, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (435, 87, 'Dung tích', '500ml');
INSERT INTO `product_specifications` VALUES (436, 87, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (437, 87, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (438, 87, 'Bao bì', 'Bình sứ thuyền chim màu xanh bút bi');
INSERT INTO `product_specifications` VALUES (439, 87, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (440, 88, 'Dung tích', '350ml');
INSERT INTO `product_specifications` VALUES (441, 88, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (442, 88, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (443, 88, 'Bao bì', 'Chai sứ dáng hồ lô màu hồng');
INSERT INTO `product_specifications` VALUES (444, 88, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (445, 89, 'Dung tích', '1 lít');
INSERT INTO `product_specifications` VALUES (446, 89, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (447, 89, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (448, 89, 'Bao bì', 'Bình sứ dáng thuyền lớn màu xanh rêu');
INSERT INTO `product_specifications` VALUES (449, 89, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (450, 90, 'Dung tích', '350ml');
INSERT INTO `product_specifications` VALUES (451, 90, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (452, 90, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (453, 90, 'Bao bì', 'Chai sứ dáng hồ lô màu da lươn');
INSERT INTO `product_specifications` VALUES (454, 90, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (455, 91, 'Dung tích', '350ml');
INSERT INTO `product_specifications` VALUES (456, 91, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (457, 91, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (458, 91, 'Bao bì', 'Chai sứ dáng hồ lô màu đen');
INSERT INTO `product_specifications` VALUES (459, 91, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (460, 92, 'Dung tích', '650ml');
INSERT INTO `product_specifications` VALUES (461, 92, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (462, 92, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (463, 92, 'Bao bì', 'Bình sứ dáng chum màu trắng');
INSERT INTO `product_specifications` VALUES (464, 92, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (465, 93, 'Dung tích', '650ml');
INSERT INTO `product_specifications` VALUES (466, 93, 'Nồng độ', '≈ 52%');
INSERT INTO `product_specifications` VALUES (467, 93, 'Nguyên liệu', 'Gạo, men truyền thống');
INSERT INTO `product_specifications` VALUES (468, 93, 'Bao bì', 'Chai sứ Long Phụng màu xanh bút bi');
INSERT INTO `product_specifications` VALUES (469, 93, 'Xuất xứ', 'Bình Định');
INSERT INTO `product_specifications` VALUES (470, 94, 'Khối lượng', '150g');
INSERT INTO `product_specifications` VALUES (471, 94, 'Quy cách', 'Hộp');
INSERT INTO `product_specifications` VALUES (472, 94, 'Xuất xứ', 'Song Phương');
INSERT INTO `product_specifications` VALUES (473, 94, 'Loại', 'Tôm xẻ tẩm sấy');
INSERT INTO `product_specifications` VALUES (474, 94, 'Bảo quản', 'Nơi khô ráo hoặc ngăn mát tủ lạnh');
INSERT INTO `product_specifications` VALUES (475, 94, 'Chất bảo quản', 'Không');
INSERT INTO `product_specifications` VALUES (476, 95, 'Khối lượng', '500g');
INSERT INTO `product_specifications` VALUES (477, 95, 'Quy cách', 'Một nắng');
INSERT INTO `product_specifications` VALUES (478, 95, 'Xuất xứ', 'Song Phương');
INSERT INTO `product_specifications` VALUES (479, 95, 'Loại', 'Cá thu một nắng');
INSERT INTO `product_specifications` VALUES (480, 95, 'Hạn sử dụng', '6 – 12 tháng');
INSERT INTO `product_specifications` VALUES (481, 95, 'Bảo quản', 'Ngăn đá (-18°C)');
INSERT INTO `product_specifications` VALUES (482, 96, 'Size', '5-7 con');
INSERT INTO `product_specifications` VALUES (483, 96, 'Xuất xứ', 'Việt Nam');
INSERT INTO `product_specifications` VALUES (484, 96, 'Khối lượng', 'Theo đặt hàng');
INSERT INTO `product_specifications` VALUES (485, 96, 'Quy cách', 'Khô hút chân không');
INSERT INTO `product_specifications` VALUES (486, 96, 'Hạn sử dụng', '6 tháng');
INSERT INTO `product_specifications` VALUES (487, 96, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (488, 97, 'Xuất xứ', 'Cà Mau / Phú Quốc / Phan Thiết');
INSERT INTO `product_specifications` VALUES (489, 97, 'Khối lượng', 'Theo đơn đặt hàng');
INSERT INTO `product_specifications` VALUES (490, 97, 'Loại', 'Tôm thẻ khô thiên nhiên');
INSERT INTO `product_specifications` VALUES (491, 97, 'Quy cách', 'Hút chân không');
INSERT INTO `product_specifications` VALUES (492, 97, 'HSD', '12 tháng kể từ ngày đóng gói');
INSERT INTO `product_specifications` VALUES (493, 97, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (494, 98, 'Nguyên liệu', 'Ức mỡ cá dứa');
INSERT INTO `product_specifications` VALUES (495, 98, 'Quy cách', 'Phơi 3 nắng');
INSERT INTO `product_specifications` VALUES (496, 98, 'Xuất xứ', 'Hương Ba Tri');
INSERT INTO `product_specifications` VALUES (497, 98, 'Hạn sử dụng', '6 tháng');
INSERT INTO `product_specifications` VALUES (498, 98, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (499, 98, 'Chất bảo quản', 'Không');
INSERT INTO `product_specifications` VALUES (500, 99, 'Khối lượng', '500g / 1kg / 2kg tùy chọn');
INSERT INTO `product_specifications` VALUES (501, 99, 'Loại', 'Khô ruột vịt');
INSERT INTO `product_specifications` VALUES (502, 99, 'Xuất xứ', 'Hương Ba Tri');
INSERT INTO `product_specifications` VALUES (503, 99, 'Quy cách', 'Đóng gói hút chân không');
INSERT INTO `product_specifications` VALUES (504, 99, 'Hạn sử dụng', '6 tháng');
INSERT INTO `product_specifications` VALUES (505, 99, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (506, 100, 'Nguyên liệu', 'Cá khoai Cà Mau');
INSERT INTO `product_specifications` VALUES (507, 100, 'Quy cách', 'Phơi khô');
INSERT INTO `product_specifications` VALUES (508, 100, 'Xuất xứ', 'Cà Mau');
INSERT INTO `product_specifications` VALUES (509, 100, 'Hạn sử dụng', '6 tháng');
INSERT INTO `product_specifications` VALUES (510, 100, 'Bảo quản', 'Nơi khô ráo thoáng mát');
INSERT INTO `product_specifications` VALUES (511, 100, 'Chất bảo quản', 'Không');
INSERT INTO `product_specifications` VALUES (512, 101, 'Nguyên liệu', 'Cá sặc đồng nhỏ (sặc bướm, sặc điệp)');
INSERT INTO `product_specifications` VALUES (513, 101, 'Quy cách', 'Hút chân không');
INSERT INTO `product_specifications` VALUES (514, 101, 'Khối lượng', 'Chọn size 500g / 1kg / 2kg / …');
INSERT INTO `product_specifications` VALUES (515, 101, 'Hạn sử dụng', '6 tháng kể từ ngày đóng gói');
INSERT INTO `product_specifications` VALUES (516, 101, 'Xuất xứ', 'Hương Ba Tri');
INSERT INTO `product_specifications` VALUES (517, 101, 'Chất bảo quản', 'Không');
INSERT INTO `product_specifications` VALUES (518, 102, 'Nguyên liệu', 'Cá bò da tươi');
INSERT INTO `product_specifications` VALUES (519, 102, 'Quy cách', 'Hút chân không / phơi khô');
INSERT INTO `product_specifications` VALUES (520, 102, 'Khối lượng', '500g / 1kg / … theo chọn');
INSERT INTO `product_specifications` VALUES (521, 102, 'Hạn sử dụng', '6 tháng');
INSERT INTO `product_specifications` VALUES (522, 102, 'Bảo quản', 'Nơi khô ráo thoáng mát');
INSERT INTO `product_specifications` VALUES (523, 102, 'Chất bảo quản', 'Không');
INSERT INTO `product_specifications` VALUES (524, 142, 'Xuất xứ', 'Ba Tri – Bến Tre');
INSERT INTO `product_specifications` VALUES (525, 142, 'Loại', 'Tôm đất khô hàng loại 1');
INSERT INTO `product_specifications` VALUES (526, 142, 'Quy cách', 'Hút chân không / phơi tự nhiên');
INSERT INTO `product_specifications` VALUES (527, 142, 'Khối lượng', '500g / 1KG / 2KG tùy chọn');
INSERT INTO `product_specifications` VALUES (528, 142, 'Hạn sử dụng', '12 tháng');
INSERT INTO `product_specifications` VALUES (529, 142, 'Bảo quản', 'Nơi khô ráo, thoáng mát');
INSERT INTO `product_specifications` VALUES (530, 104, 'Xuất xứ', 'Việt Nam');
INSERT INTO `product_specifications` VALUES (531, 104, 'Loại', 'Còi sò điệp khô – Gói 500g');
INSERT INTO `product_specifications` VALUES (532, 104, 'Quy cách', '500g / gói');
INSERT INTO `product_specifications` VALUES (533, 104, 'Hình thức', 'Khô sạch, không cát');
INSERT INTO `product_specifications` VALUES (534, 104, 'Hạn sử dụng', '6–12 tháng');
INSERT INTO `product_specifications` VALUES (535, 104, 'Bảo quản', 'Nơi khô ráo hoặc tủ mát/tủ đông');
INSERT INTO `product_specifications` VALUES (536, 105, 'Xuất xứ', 'Phan Thiết');
INSERT INTO `product_specifications` VALUES (537, 105, 'Loại', 'Khô cá bò da');
INSERT INTO `product_specifications` VALUES (538, 105, 'Quy cách', '500g / gói');
INSERT INTO `product_specifications` VALUES (539, 105, 'Hình thức', 'Khô – không chất bảo quản');
INSERT INTO `product_specifications` VALUES (540, 105, 'Cách dùng', 'Nướng, chiên, làm món nhắm');
INSERT INTO `product_specifications` VALUES (541, 105, 'Bảo quản', 'Nơi khô ráo hoặc tủ mát');
INSERT INTO `product_specifications` VALUES (542, 106, 'Xuất xứ', 'Phan Thiết');
INSERT INTO `product_specifications` VALUES (543, 106, 'Loại', 'Khô cá bóng lá trầu');
INSERT INTO `product_specifications` VALUES (544, 106, 'Quy cách', '500g / gói');
INSERT INTO `product_specifications` VALUES (545, 106, 'Hình thức', 'Khô – không chất bảo quản');
INSERT INTO `product_specifications` VALUES (546, 106, 'Cách dùng', 'Nướng, chiên, dùng kèm tương ớt');
INSERT INTO `product_specifications` VALUES (547, 106, 'Bảo quản', 'Nơi thoáng mát hoặc tủ lạnh');
INSERT INTO `product_specifications` VALUES (548, 107, 'Xuất xứ', 'Phan Thiết');
INSERT INTO `product_specifications` VALUES (549, 107, 'Loại', 'Khô cá chỉ vàng');
INSERT INTO `product_specifications` VALUES (550, 107, 'Quy cách', '500g / gói');
INSERT INTO `product_specifications` VALUES (551, 107, 'Hình thức', 'Khô tự nhiên – không chất bảo quản');
INSERT INTO `product_specifications` VALUES (552, 107, 'Cách dùng', 'Nướng, chiên, xé trộn gỏi');
INSERT INTO `product_specifications` VALUES (553, 107, 'Bảo quản', 'Nơi khô ráo hoặc tủ mát');
INSERT INTO `product_specifications` VALUES (554, 108, 'Xuất xứ', 'Phan Thiết');
INSERT INTO `product_specifications` VALUES (555, 108, 'Loại', 'Khô cá đù');
INSERT INTO `product_specifications` VALUES (556, 108, 'Quy cách', '500g / gói');
INSERT INTO `product_specifications` VALUES (557, 108, 'Hình thức', 'Khô tự nhiên');
INSERT INTO `product_specifications` VALUES (558, 108, 'Cách dùng', 'Nướng, chiên, trộn gỏi');
INSERT INTO `product_specifications` VALUES (559, 108, 'Bảo quản', 'Nơi khô ráo hoặc tủ lạnh');
INSERT INTO `product_specifications` VALUES (560, 178, 'Xuất xứ', 'Việt Nam');
INSERT INTO `product_specifications` VALUES (561, 178, 'Loại', 'Khô cá Sặc');
INSERT INTO `product_specifications` VALUES (562, 178, 'Quy cách', '500g / khay');
INSERT INTO `product_specifications` VALUES (563, 178, 'Hình thức', 'Khô – không mặn');
INSERT INTO `product_specifications` VALUES (564, 178, 'Cách dùng', 'Chiên, kho, nướng, hấp, nấu canh');
INSERT INTO `product_specifications` VALUES (565, 178, 'Bảo quản', 'Nơi khô ráo hoặc tủ lạnh');
INSERT INTO `product_specifications` VALUES (566, 109, 'Xuất xứ', 'Phan Thiết');
INSERT INTO `product_specifications` VALUES (567, 109, 'Loại', 'Khô cá lãi trứng');
INSERT INTO `product_specifications` VALUES (568, 109, 'Quy cách', '500g / gói');
INSERT INTO `product_specifications` VALUES (569, 109, 'Hình thức', 'Khô tự nhiên');
INSERT INTO `product_specifications` VALUES (570, 109, 'Cách dùng', 'Chiên, nướng');
INSERT INTO `product_specifications` VALUES (571, 109, 'Bảo quản', 'Nơi khô ráo hoặc tủ lạnh');
INSERT INTO `product_specifications` VALUES (572, 111, 'Xuất xứ', 'Việt Nam');
INSERT INTO `product_specifications` VALUES (573, 111, 'Loại', 'Khô cá cơm');
INSERT INTO `product_specifications` VALUES (574, 111, 'Quy cách', '500g / gói');
INSERT INTO `product_specifications` VALUES (575, 111, 'Hình thức', 'Khô tự nhiên');
INSERT INTO `product_specifications` VALUES (576, 111, 'Cách dùng', 'Nướng, chiên');
INSERT INTO `product_specifications` VALUES (577, 111, 'Bảo quản', 'Nơi khô ráo hoặc tủ lạnh');
INSERT INTO `product_specifications` VALUES (578, 112, 'Xuất xứ', 'Phan Thiết');
INSERT INTO `product_specifications` VALUES (579, 112, 'Loại', 'Khô mực câu size 12-14con');
INSERT INTO `product_specifications` VALUES (580, 112, 'Quy cách', '500g / gói');
INSERT INTO `product_specifications` VALUES (581, 112, 'Hình thức', 'Khô tự nhiên – không chất bảo quản');
INSERT INTO `product_specifications` VALUES (582, 112, 'Cách dùng', 'Nướng, chiên mắm, xào hành tây');
INSERT INTO `product_specifications` VALUES (583, 112, 'Bảo quản', 'Nơi khô ráo hoặc tủ lạnh');
INSERT INTO `product_specifications` VALUES (584, 113, 'Xuất xứ', 'Phan Thiết');
INSERT INTO `product_specifications` VALUES (585, 113, 'Loại', 'Khô cá đuối');
INSERT INTO `product_specifications` VALUES (586, 113, 'Quy cách', '1kg / gói');
INSERT INTO `product_specifications` VALUES (587, 113, 'Hình thức', 'Khô tự nhiên – không chất bảo quản');
INSERT INTO `product_specifications` VALUES (588, 113, 'Cách dùng', 'Nướng, chiên trứng');
INSERT INTO `product_specifications` VALUES (589, 113, 'Bảo quản', 'Nơi khô ráo hoặc tủ lạnh');
INSERT INTO `product_specifications` VALUES (590, 114, 'Xuất xứ', 'Phan Thiết');
INSERT INTO `product_specifications` VALUES (591, 114, 'Loại', 'Khô cá đường');
INSERT INTO `product_specifications` VALUES (592, 114, 'Quy cách', '1kg / gói');
INSERT INTO `product_specifications` VALUES (593, 114, 'Hình thức', 'Khô tự nhiên – không chất bảo quản');
INSERT INTO `product_specifications` VALUES (594, 114, 'Cách dùng', 'Nướng, chấm mắm me');
INSERT INTO `product_specifications` VALUES (595, 114, 'Bảo quản', 'Nơi khô ráo hoặc tủ lạnh');
INSERT INTO `product_specifications` VALUES (596, 115, 'Xuất xứ', 'Phan Thiết');
INSERT INTO `product_specifications` VALUES (597, 115, 'Loại', 'Khô mực câu');
INSERT INTO `product_specifications` VALUES (598, 115, 'Quy cách', '500g / gói');
INSERT INTO `product_specifications` VALUES (599, 115, 'Hình thức', 'Khô tự nhiên – không chất bảo quản');
INSERT INTO `product_specifications` VALUES (600, 115, 'Cách dùng', 'Nướng, chiên');
INSERT INTO `product_specifications` VALUES (601, 115, 'Bảo quản', 'Nơi khô ráo hoặc tủ lạnh');
INSERT INTO `product_specifications` VALUES (602, 116, 'Xuất xứ', 'Phan Thiết');
INSERT INTO `product_specifications` VALUES (603, 116, 'Loại', 'Tôm biển khô');
INSERT INTO `product_specifications` VALUES (604, 116, 'Quy cách', '500g / gói');
INSERT INTO `product_specifications` VALUES (605, 116, 'Hình thức', 'Khô tự nhiên – không chất bảo quản');
INSERT INTO `product_specifications` VALUES (606, 116, 'Cách dùng', 'Nấu canh, xào, ăn kèm củ kiệu');
INSERT INTO `product_specifications` VALUES (607, 116, 'Bảo quản', 'Nơi khô ráo hoặc tủ lạnh');
INSERT INTO `product_specifications` VALUES (608, 117, 'Xuất xứ', 'Phan Thiết');
INSERT INTO `product_specifications` VALUES (609, 117, 'Loại', 'Tôm khô nhỏ (Tôm canh)');
INSERT INTO `product_specifications` VALUES (610, 117, 'Quy cách', '500g / gói');
INSERT INTO `product_specifications` VALUES (611, 117, 'Hình thức', 'Khô tự nhiên – không chất bảo quản');
INSERT INTO `product_specifications` VALUES (612, 117, 'Cách dùng', 'Nấu canh, xào, ăn kèm củ kiệu');
INSERT INTO `product_specifications` VALUES (613, 117, 'Bảo quản', 'Nơi khô ráo hoặc tủ lạnh');
INSERT INTO `product_specifications` VALUES (614, 118, 'Xuất xứ', 'Phan Thiết');
INSERT INTO `product_specifications` VALUES (615, 118, 'Loại', 'Tép khô');
INSERT INTO `product_specifications` VALUES (616, 118, 'Quy cách', '500g / gói');
INSERT INTO `product_specifications` VALUES (617, 118, 'Hình thức', 'Khô tự nhiên – không chất bảo quản');
INSERT INTO `product_specifications` VALUES (618, 118, 'Cách dùng', 'Xào bắp, xào khế chua, rang tép');
INSERT INTO `product_specifications` VALUES (619, 118, 'Bảo quản', 'Nơi khô ráo hoặc tủ lạnh');
INSERT INTO `product_specifications` VALUES (620, 119, 'Chất liệu', 'Gốm sứ');
INSERT INTO `product_specifications` VALUES (621, 119, 'Hệ thống ánh sáng', 'Đèn LED');
INSERT INTO `product_specifications` VALUES (622, 119, 'Phong cách', 'Thủ công mỹ nghệ');
INSERT INTO `product_specifications` VALUES (623, 119, 'Màu sắc', 'Trắng');
INSERT INTO `product_specifications` VALUES (624, 119, 'Loại', 'Thác khói trầm hương');
INSERT INTO `product_specifications` VALUES (625, 120, 'Chất liệu', 'Gốm sứ');
INSERT INTO `product_specifications` VALUES (626, 120, 'Bộ gồm', '1 ấm, 4 chén, hủ trà');
INSERT INTO `product_specifications` VALUES (627, 120, 'Phong cách', 'Truyền thống');
INSERT INTO `product_specifications` VALUES (628, 120, 'Công dụng', 'Pha trà du lịch');
INSERT INTO `product_specifications` VALUES (629, 120, 'Loại', 'Bộ ấm trà');
INSERT INTO `product_specifications` VALUES (630, 121, 'Chất liệu', 'Gốm sứ');
INSERT INTO `product_specifications` VALUES (631, 121, 'Màu sắc', 'Xanh ngọc');
INSERT INTO `product_specifications` VALUES (632, 121, 'Bộ gồm', '1 ấm, 4 chén');
INSERT INTO `product_specifications` VALUES (633, 121, 'Công dụng', 'Dã ngoại / Du lịch');
INSERT INTO `product_specifications` VALUES (634, 121, 'Loại', 'Bộ ấm trà');
INSERT INTO `product_specifications` VALUES (635, 122, 'Chất liệu', 'Gốm sứ & Gỗ');
INSERT INTO `product_specifications` VALUES (636, 122, 'Ánh sáng', 'Đèn LED');
INSERT INTO `product_specifications` VALUES (637, 122, 'Chủ đề', 'Phật giáo');
INSERT INTO `product_specifications` VALUES (638, 122, 'Công dụng', 'Trang trí / Thiền');
INSERT INTO `product_specifications` VALUES (639, 122, 'Loại', 'Thác khói trầm hương');
INSERT INTO `product_specifications` VALUES (640, 123, 'Chất liệu', 'Gốm sứ');
INSERT INTO `product_specifications` VALUES (641, 123, 'Nhân vật', 'Di Lặc');
INSERT INTO `product_specifications` VALUES (642, 123, 'Ánh sáng', 'Đèn LED');
INSERT INTO `product_specifications` VALUES (643, 123, 'Ý nghĩa', 'Tài lộc – May mắn');
INSERT INTO `product_specifications` VALUES (644, 123, 'Công dụng', 'Trang trí');
INSERT INTO `product_specifications` VALUES (645, 124, 'Chất liệu', 'Gốm sứ cao cấp');
INSERT INTO `product_specifications` VALUES (646, 124, 'Số lượng', '3 món (2 bình + 1 đĩa)');
INSERT INTO `product_specifications` VALUES (647, 124, 'Phong cách', 'Thủ công mỹ nghệ');
INSERT INTO `product_specifications` VALUES (648, 124, 'Mục đích sử dụng', 'Trang trí phòng khách, phong thuỷ');
INSERT INTO `product_specifications` VALUES (649, 124, 'Kích thước', 'Bình lớn 24x15cm, Bình nhỏ 16.5x13.5cm');
INSERT INTO `product_specifications` VALUES (650, 124, 'Màu sắc', 'Xanh ngọc viền vàng');
INSERT INTO `product_specifications` VALUES (651, 125, 'Chất liệu', 'Gốm sứ');
INSERT INTO `product_specifications` VALUES (652, 125, 'Số lượng', '1 ấm, 4 chén, 1 khay');
INSERT INTO `product_specifications` VALUES (653, 125, 'Phong cách', 'Thủ công mỹ nghệ');
INSERT INTO `product_specifications` VALUES (654, 125, 'Mục đích sử dụng', 'Uống trà, quà tặng');
INSERT INTO `product_specifications` VALUES (655, 125, 'Tính năng', 'Kèm túi sách di động');
INSERT INTO `product_specifications` VALUES (656, 125, 'Màu sắc', 'Trắng');
INSERT INTO `product_specifications` VALUES (657, 126, 'Chất liệu', 'Gốm sứ');
INSERT INTO `product_specifications` VALUES (658, 126, 'Số lượng', '1 ấm, 4 chén, 1 khay');
INSERT INTO `product_specifications` VALUES (659, 126, 'Phong cách', 'Thủ công mỹ nghệ');
INSERT INTO `product_specifications` VALUES (660, 126, 'Mục đích sử dụng', 'Uống trà, quà tặng');
INSERT INTO `product_specifications` VALUES (661, 126, 'Tính năng', 'Kèm túi sách di động');
INSERT INTO `product_specifications` VALUES (662, 126, 'Màu sắc', 'Đen');
INSERT INTO `product_specifications` VALUES (663, 127, 'Chất liệu', 'Gốm sứ / cát tím');
INSERT INTO `product_specifications` VALUES (664, 127, 'Phong cách', 'Thủ công mỹ nghệ');
INSERT INTO `product_specifications` VALUES (665, 127, 'Loại', 'Thác khói trầm hương');
INSERT INTO `product_specifications` VALUES (666, 127, 'Tính năng', 'Kèm đế sen');
INSERT INTO `product_specifications` VALUES (667, 127, 'Mục đích sử dụng', 'Trang trí, thư giãn');
INSERT INTO `product_specifications` VALUES (668, 127, 'Kỹ thuật khói', 'Khói chảy ngược');
INSERT INTO `product_specifications` VALUES (669, 128, 'Chất liệu', 'Cát tím thủ công');
INSERT INTO `product_specifications` VALUES (670, 128, 'Phong cách', 'Thủ công mỹ nghệ');
INSERT INTO `product_specifications` VALUES (671, 128, 'Loại', 'Thác khói trầm hương');
INSERT INTO `product_specifications` VALUES (672, 128, 'Mục đích sử dụng', 'Trang trí, phong thuỷ');
INSERT INTO `product_specifications` VALUES (673, 128, 'Tính năng', 'Khói chảy ngược');
INSERT INTO `product_specifications` VALUES (674, 128, 'Biểu tượng', 'Bàn tay Phật');
INSERT INTO `product_specifications` VALUES (675, 129, 'Chất liệu', 'Gỗ + in kỹ thuật');
INSERT INTO `product_specifications` VALUES (676, 129, 'Phong cách', 'Thủ công mỹ nghệ');
INSERT INTO `product_specifications` VALUES (677, 129, 'Kích thước', '20x27cm / 30x40cm / 40x55cm');
INSERT INTO `product_specifications` VALUES (678, 129, 'Màu sắc', 'Vân gỗ');
INSERT INTO `product_specifications` VALUES (679, 129, 'Mục đích sử dụng', 'Trang trí, truyền động lực');
INSERT INTO `product_specifications` VALUES (680, 130, 'Chất liệu', 'Gốm sứ');
INSERT INTO `product_specifications` VALUES (681, 130, 'Số lượng', '2 ấm, 6 chén');
INSERT INTO `product_specifications` VALUES (682, 130, 'Phong cách', 'Thủ công mỹ nghệ');
INSERT INTO `product_specifications` VALUES (683, 130, 'Mục đích sử dụng', 'Pha trà, quà tặng');
INSERT INTO `product_specifications` VALUES (684, 130, 'Tính năng', 'Thiết kế hình cối xay');
INSERT INTO `product_specifications` VALUES (685, 130, 'Màu sắc', 'Trắng họa tiết');
INSERT INTO `product_specifications` VALUES (686, 131, 'Chất liệu', 'Gốm sứ');
INSERT INTO `product_specifications` VALUES (687, 131, 'Số lượng', '3 món (2 bình + 1 đĩa)');
INSERT INTO `product_specifications` VALUES (688, 131, 'Phong cách', 'Thủ công mỹ nghệ');
INSERT INTO `product_specifications` VALUES (689, 131, 'Mục đích sử dụng', 'Trang trí, phong thuỷ');
INSERT INTO `product_specifications` VALUES (690, 131, 'Họa tiết', 'Hoa sen');
INSERT INTO `product_specifications` VALUES (691, 131, 'Màu sắc', 'Trắng/Hoa văn');
INSERT INTO `product_specifications` VALUES (692, 132, 'Chất liệu', 'Bạch ngọc gốm sứ');
INSERT INTO `product_specifications` VALUES (693, 132, 'Số lượng', '1 ấm, nhiều chén');
INSERT INTO `product_specifications` VALUES (694, 132, 'Phong cách', 'Thủ công mỹ nghệ');
INSERT INTO `product_specifications` VALUES (695, 132, 'Mục đích sử dụng', 'Uống trà, quà tặng cao cấp');
INSERT INTO `product_specifications` VALUES (696, 132, 'Họa tiết', 'Hoa sen');
INSERT INTO `product_specifications` VALUES (697, 132, 'Màu sắc', 'Trắng bạch ngọc');
INSERT INTO `product_specifications` VALUES (698, 133, 'Chất liệu', 'Gốm sứ');
INSERT INTO `product_specifications` VALUES (699, 133, 'Số lượng', '2 bình, 2 đĩa');
INSERT INTO `product_specifications` VALUES (700, 133, 'Phong cách', 'Thủ công mỹ nghệ');
INSERT INTO `product_specifications` VALUES (701, 133, 'Mục đích sử dụng', 'Trang trí phòng khách, phong thuỷ');
INSERT INTO `product_specifications` VALUES (702, 133, 'Họa tiết', 'Nghệ thuật/Phong thuỷ');
INSERT INTO `product_specifications` VALUES (703, 133, 'Màu sắc', 'Đa sắc hoa văn');
INSERT INTO `product_specifications` VALUES (704, 134, 'Chất liệu', 'Gốm sứ');
INSERT INTO `product_specifications` VALUES (705, 134, 'Phong cách', 'Thủ công mỹ nghệ');
INSERT INTO `product_specifications` VALUES (706, 134, 'Mục đích sử dụng', 'Trang trí, phong thuỷ');
INSERT INTO `product_specifications` VALUES (707, 134, 'Họa tiết', 'Phong thuỷ');
INSERT INTO `product_specifications` VALUES (708, 134, 'Màu sắc', 'Hoa văn truyền thống');
INSERT INTO `product_specifications` VALUES (709, 135, 'Chất liệu', 'Gốm sứ');
INSERT INTO `product_specifications` VALUES (710, 135, 'Phong cách', 'Thủ công mỹ nghệ');
INSERT INTO `product_specifications` VALUES (711, 135, 'Mục đích sử dụng', 'Trang trí, tiểu cảnh');
INSERT INTO `product_specifications` VALUES (712, 135, 'Không gian phù hợp', 'Bàn làm việc, phòng khách');
INSERT INTO `product_specifications` VALUES (713, 135, 'Kích thước', 'Mini');
INSERT INTO `product_specifications` VALUES (714, 136, 'Chất liệu', 'Gỗ / Canvas');
INSERT INTO `product_specifications` VALUES (715, 136, 'Phong cách', 'Thủ công mỹ nghệ');
INSERT INTO `product_specifications` VALUES (716, 136, 'Nội dung', 'Chữ Nhân');
INSERT INTO `product_specifications` VALUES (717, 136, 'Mục đích sử dụng', 'Trang trí, phong thuỷ');
INSERT INTO `product_specifications` VALUES (718, 136, 'Màu sắc', 'Nền gỗ');
INSERT INTO `product_specifications` VALUES (719, 137, 'Chất liệu', 'Gỗ / Canvas');
INSERT INTO `product_specifications` VALUES (720, 137, 'Phong cách', 'Hiện đại thủ công');
INSERT INTO `product_specifications` VALUES (721, 137, 'Nội dung', 'Truyền cảm hứng');
INSERT INTO `product_specifications` VALUES (722, 137, 'Mục đích sử dụng', 'Trang trí, tạo động lực');
INSERT INTO `product_specifications` VALUES (723, 137, 'Không gian phù hợp', 'Văn phòng, phòng học');
INSERT INTO `product_specifications` VALUES (724, 138, 'Chất liệu', 'Gỗ / Canvas');
INSERT INTO `product_specifications` VALUES (725, 138, 'Phong cách', 'Thủ công mỹ nghệ');
INSERT INTO `product_specifications` VALUES (726, 138, 'Nội dung', 'Thông điệp động lực');
INSERT INTO `product_specifications` VALUES (727, 138, 'Mục đích sử dụng', 'Trang trí, truyền cảm hứng');
INSERT INTO `product_specifications` VALUES (728, 138, 'Màu sắc', 'Đa sắc nghệ thuật');
INSERT INTO `product_specifications` VALUES (729, 139, 'Chất liệu', 'Gốm sứ');
INSERT INTO `product_specifications` VALUES (730, 139, 'Phong cách', 'Thủ công mỹ nghệ');
INSERT INTO `product_specifications` VALUES (731, 139, 'Mục đích sử dụng', 'Trang trí, phong thuỷ');
INSERT INTO `product_specifications` VALUES (732, 139, 'Biểu tượng', 'Cá phong thuỷ');
INSERT INTO `product_specifications` VALUES (733, 139, 'Kích thước', 'Mini');
INSERT INTO `product_specifications` VALUES (734, 140, 'Chất liệu', 'Gốm sứ, đá');
INSERT INTO `product_specifications` VALUES (735, 140, 'Phong cách', 'Thủ công mỹ nghệ');
INSERT INTO `product_specifications` VALUES (736, 140, 'Mục đích sử dụng', 'Trang trí, phong thuỷ');
INSERT INTO `product_specifications` VALUES (737, 140, 'Không gian phù hợp', 'Bàn làm việc, kệ trang trí');
INSERT INTO `product_specifications` VALUES (738, 140, 'Kích thước', 'Mini');
INSERT INTO `product_specifications` VALUES (739, 141, 'Chất liệu', 'Gốm sứ, đá, cây trang trí');
INSERT INTO `product_specifications` VALUES (740, 141, 'Phong cách', 'Thủ công mỹ nghệ');
INSERT INTO `product_specifications` VALUES (741, 141, 'Mục đích sử dụng', 'Trang trí, tiểu cảnh');
INSERT INTO `product_specifications` VALUES (742, 141, 'Không gian phù hợp', 'Phòng khách, bàn làm việc');
INSERT INTO `product_specifications` VALUES (743, 141, 'Kích thước', 'Mini');
INSERT INTO `product_specifications` VALUES (744, 142, 'Thương hiệu', 'Twins Special');
INSERT INTO `product_specifications` VALUES (745, 142, 'Loại sản phẩm', 'Mũ bảo hộ võ thuật');
INSERT INTO `product_specifications` VALUES (746, 142, 'Chất liệu', 'Da tổng hợp cao cấp');
INSERT INTO `product_specifications` VALUES (747, 142, 'Mục đích sử dụng', 'Thi đấu, tập luyện');
INSERT INTO `product_specifications` VALUES (748, 142, 'Văn hóa', 'Võ thuật truyền thống');
INSERT INTO `product_specifications` VALUES (749, 143, 'Loại sản phẩm', 'Găng tay võ thuật');
INSERT INTO `product_specifications` VALUES (750, 143, 'Chất liệu', 'Da PU cao cấp');
INSERT INTO `product_specifications` VALUES (751, 143, 'Đối tượng sử dụng', 'Nam / Nữ');
INSERT INTO `product_specifications` VALUES (752, 143, 'Mục đích sử dụng', 'Tập luyện, thi đấu');
INSERT INTO `product_specifications` VALUES (753, 143, 'Văn hóa', 'Võ cổ truyền và võ hiện đại');
INSERT INTO `product_specifications` VALUES (754, 144, 'Thương hiệu', 'Fighter');
INSERT INTO `product_specifications` VALUES (755, 144, 'Loại sản phẩm', 'Găng tay thi đấu');
INSERT INTO `product_specifications` VALUES (756, 144, 'Màu sắc', 'Xanh / Đỏ');
INSERT INTO `product_specifications` VALUES (757, 144, 'Mục đích sử dụng', 'Thi đấu võ thuật');
INSERT INTO `product_specifications` VALUES (758, 144, 'Văn hóa', 'Võ cổ truyền Việt Nam');
INSERT INTO `product_specifications` VALUES (759, 145, 'Loại sản phẩm', 'Trang phục võ thuật');
INSERT INTO `product_specifications` VALUES (760, 145, 'Chất liệu', 'Vải cotton');
INSERT INTO `product_specifications` VALUES (761, 145, 'Đối tượng sử dụng', 'Nam / Nữ');
INSERT INTO `product_specifications` VALUES (762, 145, 'Kích thước', 'S, M, L, XL');
INSERT INTO `product_specifications` VALUES (763, 145, 'Văn hóa', 'Võ cổ truyền Việt Nam');
INSERT INTO `product_specifications` VALUES (764, 146, 'Loại sản phẩm', 'Găng tay võ thuật');
INSERT INTO `product_specifications` VALUES (765, 146, 'Kích thước', 'Size Gang 01');
INSERT INTO `product_specifications` VALUES (766, 146, 'Chất liệu', 'Da PU cao cấp');
INSERT INTO `product_specifications` VALUES (767, 146, 'Mục đích sử dụng', 'Tập luyện, thi đấu');
INSERT INTO `product_specifications` VALUES (768, 146, 'Văn hóa', 'Võ thuật truyền thống và hiện đại');
INSERT INTO `product_specifications` VALUES (769, 147, 'Loại sản phẩm', 'Găng tay võ thuật');
INSERT INTO `product_specifications` VALUES (770, 147, 'Chất liệu', 'Da PU');
INSERT INTO `product_specifications` VALUES (771, 147, 'Mục đích sử dụng', 'Tập luyện / Thi đấu');
INSERT INTO `product_specifications` VALUES (772, 147, 'Văn hóa', 'Võ thuật');
INSERT INTO `product_specifications` VALUES (773, 147, 'Đối tượng', 'Unisex');
INSERT INTO `product_specifications` VALUES (774, 148, 'Thương hiệu', 'Wesing');
INSERT INTO `product_specifications` VALUES (775, 148, 'Loại sản phẩm', 'Giáp bảo hộ');
INSERT INTO `product_specifications` VALUES (776, 148, 'Mục đích sử dụng', 'Tập luyện');
INSERT INTO `product_specifications` VALUES (777, 148, 'Văn hóa', 'Võ thuật');
INSERT INTO `product_specifications` VALUES (778, 148, 'Đối tượng', 'Tân thủ / Người mới bắt đầu');
INSERT INTO `product_specifications` VALUES (779, 149, 'Loại sản phẩm', 'Mũ thi đấu');
INSERT INTO `product_specifications` VALUES (780, 149, 'Chất liệu', 'Da tổng hợp cao cấp');
INSERT INTO `product_specifications` VALUES (781, 149, 'Mục đích sử dụng', 'Thi đấu / Tập luyện');
INSERT INTO `product_specifications` VALUES (782, 149, 'Văn hóa', 'Võ thuật');
INSERT INTO `product_specifications` VALUES (783, 149, 'Đối tượng', 'Unisex');
INSERT INTO `product_specifications` VALUES (784, 150, 'Thương hiệu', 'Twins');
INSERT INTO `product_specifications` VALUES (785, 150, 'Loại sản phẩm', 'Mũ thi đấu');
INSERT INTO `product_specifications` VALUES (786, 150, 'Môn võ phù hợp', 'Boxing / Muay / Kickboxing');
INSERT INTO `product_specifications` VALUES (787, 150, 'Mục đích sử dụng', 'Thi đấu');
INSERT INTO `product_specifications` VALUES (788, 150, 'Văn hóa', 'Võ thuật đa môn');
INSERT INTO `product_specifications` VALUES (789, 151, 'Loại sản phẩm', 'Găng tay võ thuật');
INSERT INTO `product_specifications` VALUES (790, 151, 'Chất liệu', 'Da PU cao cấp');
INSERT INTO `product_specifications` VALUES (791, 151, 'Mục đích sử dụng', 'Tập luyện / Thi đấu');
INSERT INTO `product_specifications` VALUES (792, 151, 'Văn hóa', 'Võ thuật');
INSERT INTO `product_specifications` VALUES (793, 151, 'Đối tượng', 'Unisex');
INSERT INTO `product_specifications` VALUES (794, 152, 'Loại sản phẩm', 'Găng bảo hộ');
INSERT INTO `product_specifications` VALUES (795, 152, 'Màu sắc', 'Xanh');
INSERT INTO `product_specifications` VALUES (796, 152, 'Kích thước', '0.8');
INSERT INTO `product_specifications` VALUES (797, 152, 'Mục đích sử dụng', 'Tập luyện');
INSERT INTO `product_specifications` VALUES (798, 152, 'Văn hóa', 'Võ thuật');
INSERT INTO `product_specifications` VALUES (799, 153, 'Loại sản phẩm', 'Găng tay võ thuật');
INSERT INTO `product_specifications` VALUES (800, 153, 'Chất liệu', 'Da PU');
INSERT INTO `product_specifications` VALUES (801, 153, 'Mục đích sử dụng', 'Tập luyện / Thi đấu');
INSERT INTO `product_specifications` VALUES (802, 153, 'Văn hóa', 'Võ thuật');
INSERT INTO `product_specifications` VALUES (803, 153, 'Đối tượng', 'Unisex');
INSERT INTO `product_specifications` VALUES (804, 154, 'Loại sản phẩm', 'Găng tay võ thuật');
INSERT INTO `product_specifications` VALUES (805, 154, 'Chất liệu', 'Da tổng hợp');
INSERT INTO `product_specifications` VALUES (806, 154, 'Mục đích sử dụng', 'Tập luyện');
INSERT INTO `product_specifications` VALUES (807, 154, 'Văn hóa', 'Võ thuật');
INSERT INTO `product_specifications` VALUES (808, 154, 'Đối tượng', 'Nam / Nữ');
INSERT INTO `product_specifications` VALUES (809, 155, 'Loại sản phẩm', 'Mũ võ thuật truyền thống');
INSERT INTO `product_specifications` VALUES (810, 155, 'Chất liệu', 'Vải + Đệm bảo hộ');
INSERT INTO `product_specifications` VALUES (811, 155, 'Mục đích sử dụng', 'Tập luyện / Biểu diễn');
INSERT INTO `product_specifications` VALUES (812, 155, 'Văn hóa', 'Võ cổ truyền');
INSERT INTO `product_specifications` VALUES (813, 155, 'Đối tượng', 'Unisex');
INSERT INTO `product_specifications` VALUES (814, 156, 'Loại sản phẩm', 'Mũ võ thuật truyền thống');
INSERT INTO `product_specifications` VALUES (815, 156, 'Chất liệu', 'Vải + Đệm');
INSERT INTO `product_specifications` VALUES (816, 156, 'Mục đích sử dụng', 'Tập luyện / Biểu diễn');
INSERT INTO `product_specifications` VALUES (817, 156, 'Văn hóa', 'Võ cổ truyền');
INSERT INTO `product_specifications` VALUES (818, 156, 'Đối tượng', 'Unisex');
INSERT INTO `product_specifications` VALUES (819, 157, 'Loại sản phẩm', 'Mũ võ thuật truyền thống');
INSERT INTO `product_specifications` VALUES (820, 157, 'Chất liệu', 'Vải + Đệm bảo hộ');
INSERT INTO `product_specifications` VALUES (821, 157, 'Mục đích sử dụng', 'Tập luyện / Biểu diễn');
INSERT INTO `product_specifications` VALUES (822, 157, 'Văn hóa', 'Võ cổ truyền');
INSERT INTO `product_specifications` VALUES (823, 157, 'Đối tượng', 'Unisex');
INSERT INTO `product_specifications` VALUES (824, 158, 'Loại sản phẩm', 'Mũ võ thuật truyền thống');
INSERT INTO `product_specifications` VALUES (825, 158, 'Chất liệu', 'Vải + Đệm bảo hộ');
INSERT INTO `product_specifications` VALUES (826, 158, 'Mục đích sử dụng', 'Tập luyện / Biểu diễn');
INSERT INTO `product_specifications` VALUES (827, 158, 'Văn hóa', 'Võ cổ truyền');
INSERT INTO `product_specifications` VALUES (828, 158, 'Đối tượng', 'Unisex');
INSERT INTO `product_specifications` VALUES (829, 159, 'Loại sản phẩm', 'Mũ võ thuật truyền thống');
INSERT INTO `product_specifications` VALUES (830, 159, 'Chất liệu', 'Vải + Đệm bảo hộ');
INSERT INTO `product_specifications` VALUES (831, 159, 'Mục đích sử dụng', 'Tập luyện / Biểu diễn');
INSERT INTO `product_specifications` VALUES (832, 159, 'Văn hóa', 'Võ cổ truyền');
INSERT INTO `product_specifications` VALUES (833, 159, 'Đối tượng', 'Unisex');
INSERT INTO `product_specifications` VALUES (834, 160, 'Loại sản phẩm', 'Vũ khí võ thuật');
INSERT INTO `product_specifications` VALUES (835, 160, 'Chất liệu', 'Nhôm hợp kim');
INSERT INTO `product_specifications` VALUES (836, 160, 'Mục đích sử dụng', 'Tập luyện / Biểu diễn');
INSERT INTO `product_specifications` VALUES (837, 160, 'Phong cách', 'Võ cổ truyền');
INSERT INTO `product_specifications` VALUES (838, 160, 'Đối tượng', 'Unisex');
INSERT INTO `product_specifications` VALUES (839, 161, 'Loại sản phẩm', 'Vũ khí võ thuật');
INSERT INTO `product_specifications` VALUES (840, 161, 'Chất liệu', 'Nhôm hợp kim');
INSERT INTO `product_specifications` VALUES (841, 161, 'Mục đích sử dụng', 'Tập luyện / Biểu diễn');
INSERT INTO `product_specifications` VALUES (842, 161, 'Phong cách', 'Võ cổ truyền');
INSERT INTO `product_specifications` VALUES (843, 161, 'Đối tượng', 'Unisex');
INSERT INTO `product_specifications` VALUES (844, 162, 'Loại sản phẩm', 'Vũ khí võ thuật');
INSERT INTO `product_specifications` VALUES (845, 162, 'Phong cách', 'Vovinam');
INSERT INTO `product_specifications` VALUES (846, 162, 'Chất liệu', 'Nhôm hợp kim');
INSERT INTO `product_specifications` VALUES (847, 162, 'Mục đích sử dụng', 'Biểu diễn / Tập luyện');
INSERT INTO `product_specifications` VALUES (848, 162, 'Đối tượng', 'Unisex');
INSERT INTO `product_specifications` VALUES (849, 163, 'Loại sản phẩm', 'Vũ khí võ thuật');
INSERT INTO `product_specifications` VALUES (850, 163, 'Chất liệu', 'Gỗ / Nhôm');
INSERT INTO `product_specifications` VALUES (851, 163, 'Mục đích sử dụng', 'Tập luyện / Biểu diễn');
INSERT INTO `product_specifications` VALUES (852, 163, 'Phong cách', 'Võ cổ truyền');
INSERT INTO `product_specifications` VALUES (853, 163, 'Đối tượng', 'Unisex');
INSERT INTO `product_specifications` VALUES (854, 164, 'Loại sản phẩm', 'Vũ khí võ thuật');
INSERT INTO `product_specifications` VALUES (855, 164, 'Chất liệu', 'Nhôm hợp kim');
INSERT INTO `product_specifications` VALUES (856, 164, 'Mục đích sử dụng', 'Tập luyện / Biểu diễn');
INSERT INTO `product_specifications` VALUES (857, 164, 'Phong cách', 'Võ cổ truyền');
INSERT INTO `product_specifications` VALUES (858, 164, 'Đối tượng', 'Unisex');

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
                             `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
                             `category_id` int UNSIGNED NULL DEFAULT NULL,
                             `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                             `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
                             `original_price` decimal(15, 2) NOT NULL,
                             `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                             `stock_quantity` int NULL DEFAULT 0,
                             `total_sold` int NULL DEFAULT 0,
                             `avg_rating` decimal(2, 1) NULL DEFAULT 0.0,
                             `review_count` int NULL DEFAULT 0,
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `fk_product_category`(`category_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 165 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES (1, 1, 'Tré Bó Rơm Bình Định (Cây gói lá)', 'Tré bó rơm là đặc sản truyền thống của Bình Định, được làm từ thịt heo, mè rang và gia vị tự nhiên, ủ lên men tạo vị chua nhẹ, thơm đặc trưng.', 33500.00, '/assets/images/products/nem-cha-tre/tre-bo-rom-cay.jpg', 50, 126, 0.0, 0);
INSERT INTO `products` VALUES (2, 1, 'Tré Hũ Nhựa PET 500g Bình Định', 'Tré Bình Định được đóng gói trong hũ nhựa PET tiện lợi, giữ trọn hương vị truyền thống, phù hợp sử dụng và bảo quản trong tủ lạnh.', 120000.00, '/assets/images/products/nem-cha-tre/tre-bo-rom-hu-500g.jpg', 40, 165, 0.0, 0);
INSERT INTO `products` VALUES (3, 1, 'Tré Hũ Nhựa PET 1kg Bình Định', 'Tré Bình Định hũ nhựa PET 1kg, phù hợp cho gia đình hoặc làm quà biếu, hương vị truyền thống, không chất bảo quản.', 220000.00, '/assets/images/products/nem-cha-tre/tre-bo-rom-hu-1kg.jpg', 30, 438, 0.0, 0);
INSERT INTO `products` VALUES (4, 1, 'Nem Chua Lá Ổi Chợ Huyện', 'Nem chua lá ổi Chợ Huyện là đặc sản nổi tiếng của Bình Định, được làm từ thịt heo tươi, bì heo và gia vị truyền thống, gói bằng lá ổi tạo mùi thơm đặc trưng.', 35000.00, '/assets/images/products/nem-cha-tre/nem-chua-la-oi-cho-huyen.jpg', 80, 214, 0.0, 0);
INSERT INTO `products` VALUES (5, 1, 'Chả lụa da heo ớt xiêm xanh Bình Định', 'Chả lụa da heo ớt xiêm xanh là đặc sản miền Trung, với vị ngọt của giò lụa kết hợp da heo giòn dẻo và cay nồng của ớt xiêm xanh, tạo hương vị hấp dẫn.', 102500.00, '/assets/images/products/nem-cha-tre/cha-lua-da-heo-ot-xiem-xanh.jpg', 50, 238, 0.0, 0);
INSERT INTO `products` VALUES (6, 1, 'Chả bò Bình Định', 'Chả bò Bình Định được làm từ thịt bò tươi xay nhuyễn, dai giòn tự nhiên, hương vị đậm đà đặc trưng của đặc sản miền Trung.', 115000.00, '/assets/images/products/nem-cha-tre/cha-bo-binh-dinh.jpg', 50, 48, 0.0, 0);
INSERT INTO `products` VALUES (7, 1, 'Cây nem chua lá ổi Chợ Huyện Bình Định (550g)', 'Nem chua Chợ Huyện Bình Định được gói lá ổi truyền thống, vị chua nhẹ, cay nồng, thơm đặc trưng, là đặc sản nổi tiếng của Bình Định.', 135000.00, '/assets/images/products/nem-cha-tre/cay-nem-chua-la-oi-cho-huyen.jpg', 40, 499, 0.0, 0);
INSERT INTO `products` VALUES (8, 1, 'Chả lụa 100% nạc heo', 'Chả lụa truyền thống Bình Định.', 102500.00, '/assets/images/products/nem-cha-tre/cha-lua-100-nac-heo.jpg', 50, 379, 0.0, 0);
INSERT INTO `products` VALUES (9, 1, 'Chả cá cuốn rau răm Quy Nhơn', 'Đặc sản chả cá cuốn rau răm Quy Nhơn.', 120000.00, '/assets/images/products/nem-cha-tre/cha-ca-cuon-rau-ram-quy-nhon.jpg', 50, 389, 0.0, 0);
INSERT INTO `products` VALUES (10, 1, 'Tré Bò Phi Lê', 'Tré bò phi lê là đặc sản truyền thống Bình Định, được làm từ thịt bò phi lê lên men tự nhiên, hương vị đậm đà, thơm ngon. Sản phẩm phù hợp làm món nhắm hoặc quà biếu đặc sản.', 250000.00, '/assets/images/products/nem-cha-tre/tre-bo-phi-le.jpg', 100, 308, 0.0, 0);
INSERT INTO `products` VALUES (11, 1, 'Nem Chua Chiếc Chợ Huyện', 'Nem chua Chiếc Chợ Huyện là đặc sản nổi tiếng Bình Định, có vị chua nhẹ tự nhiên, cay nồng, thơm mùi tỏi ớt và lá gói truyền thống. Phù hợp dùng trực tiếp hoặc làm quà biếu.', 200000.00, '/assets/images/products/nem-cha-tre/nem-chua-chiec-cho-huyen.jpg', 100, 363, 0.0, 0);
INSERT INTO `products` VALUES (12, 1, 'Chả Tôm Huế Ngon Chính Gốc', 'Chả tôm Huế chính gốc được làm từ tôm tươi xay nhuyễn kết hợp gia vị truyền thống xứ Huế, vị ngọt tự nhiên, thơm béo, thích hợp chiên hoặc nướng.', 230000.00, '/assets/images/products/nem-cha-tre/cha-tom-hue-ngon-chinh-goc-hue.jpg', 100, 393, 0.0, 0);
INSERT INTO `products` VALUES (13, 1, 'Chả Ram Tôm Đất Nguyên Con Nhỏ Bình Định (500gr)', 'Chả ram tôm đất Bình Định được làm từ tôm đất nguyên con nhỏ, cuốn bánh tráng mỏng, chiên giòn rụm. Món đặc sản nổi tiếng, thích hợp dùng trong bữa cơm gia đình hoặc làm quà.', 220000.00, '/assets/images/products/nem-cha-tre/cha-ram-tom-dat-nguyen-con-nho-binh-dinh-500gr.jpg', 100, 377, 0.0, 0);
INSERT INTO `products` VALUES (14, 1, 'Chả Giò Thủ Bình Định', 'Chả giò thủ Bình Định được làm từ thịt heo chọn lọc, tai heo và gia vị truyền thống, kết cấu giòn sần sật, hương vị đậm đà, thích hợp dùng kèm bánh tráng và rau sống.', 210000.00, '/assets/images/products/nem-cha-tre/cha-gio-thu-binh-dinh.jpg', 100, 205, 0.0, 0);
INSERT INTO `products` VALUES (15, 1, 'Chả Da Bao Ngon Chất Lượng Nhất', 'Chả da bao là đặc sản Bình Định, lớp da giòn dai bao bọc phần nhân thịt đậm đà, chế biến theo công thức truyền thống, thích hợp dùng trong bữa cơm gia đình hoặc làm quà.', 190000.00, '/assets/images/products/nem-cha-tre/cha-da-bao-ngon-chat-luong-nhat.jpg', 100, 377, 0.0, 0);
INSERT INTO `products` VALUES (16, 1, 'Chả Quế Bình Định Ngon TPHCM (500gr)', 'Chả quế Bình Định được làm từ thịt heo xay nhuyễn, ướp quế và gia vị truyền thống, có mùi thơm đặc trưng, vị đậm đà, thích hợp chiên hoặc dùng kèm bánh mì.', 200000.00, '/assets/images/products/nem-cha-tre/cha-que-binh-dinh-ngon-tphcm-500gr.jpg', 100, 279, 0.0, 0);
INSERT INTO `products` VALUES (17, 1, 'Chả Cá Quy Nhơn Hấp (500gr)', 'Chả cá Quy Nhơn hấp được làm từ cá tươi xay nhuyễn, nêm nếm theo công thức truyền thống, vị ngọt tự nhiên, dai mềm, thích hợp dùng trực tiếp hoặc chế biến món ăn.', 180000.00, '/assets/images/products/nem-cha-tre/cha-ca-quy-nhon-hap-500gr.jpg', 100, 256, 0.0, 0);
INSERT INTO `products` VALUES (18, 1, 'Chả Ram Tôm Đất Nguyên Con Lớn Bình Định (500gr)', 'Chả ram tôm đất Bình Định loại nguyên con lớn, cuốn bánh tráng mỏng, chiên giòn, nhân tôm ngọt chắc, là món đặc sản nổi tiếng dùng trong bữa cơm gia đình hoặc làm quà.', 240000.00, '/assets/images/products/nem-cha-tre/cha-ram-tom-dat-nguyen-con-lon-binh-dinh-500gr.jpg', 100, 434, 0.0, 0);
INSERT INTO `products` VALUES (19, 1, 'Chả Bò Gân Bình Định (500gr)', 'Chả bò gân Bình Định được làm từ thịt bò tươi kết hợp gân bò, tạo độ dai giòn tự nhiên, tẩm ướp gia vị truyền thống, thích hợp dùng trực tiếp hoặc chế biến món ăn.', 260000.00, '/assets/images/products/nem-cha-tre/cha-bo-gan-binh-dinh-500gr.jpg', 100, 412, 0.0, 0);
INSERT INTO `products` VALUES (20, 1, 'Chả Cá Đồng Tiền Quy Nhơn (500gr)', 'Chả cá đồng tiền Quy Nhơn được làm từ cá tươi xay nhuyễn, tạo hình miếng tròn dẹt, vị ngọt tự nhiên, dai mềm, thích hợp chiên hoặc hấp dùng trong bữa ăn gia đình.', 185000.00, '/assets/images/products/nem-cha-tre/cha-ca-dong-tien-quy-nhon-500gr.jpg', 100, 257, 0.0, 0);
INSERT INTO `products` VALUES (21, 1, 'Chả Cá Chiên Quy Nhơn (500gr)', 'Chả cá chiên Quy Nhơn được làm từ cá tươi xay nhuyễn, chiên vàng thơm, bên ngoài giòn nhẹ bên trong dai mềm, vị ngọt tự nhiên, thích hợp dùng trực tiếp hoặc ăn kèm bún bánh tráng.', 190000.00, '/assets/images/products/nem-cha-tre/cha-ca-chien-quy-nhon-500gr.jpg', 100, 42, 0.0, 0);
INSERT INTO `products` VALUES (22, 1, 'Chả Ram Tôm Đất Bình Định (500gr)', 'Chả ram tôm đất Bình Định được làm từ tôm đất tươi, cuốn bánh tráng mỏng, chiên giòn rụm, nhân ngọt tự nhiên, là món đặc sản quen thuộc trong bữa cơm gia đình.', 210000.00, '/assets/images/products/nem-cha-tre/cha-ram-tom-dat-binh-dinh-500gr.jpg', 100, 409, 0.0, 0);
INSERT INTO `products` VALUES (23, 1, 'Chả Lụa Bình Định (500gr)', 'Chả lụa Bình Định được làm từ thịt heo tươi xay nhuyễn, gói lá truyền thống, có vị ngọt tự nhiên, dai mịn, phù hợp dùng trực tiếp hoặc chế biến nhiều món ăn.', 180000.00, '/assets/images/products/nem-cha-tre/cha-lua-binh-dinh-500gr.jpg', 100, 439, 0.0, 0);
INSERT INTO `products` VALUES (24, 1, 'Chả Bò Bình Định Loại 1 (500gr)', 'Chả bò Bình Định loại 1 được làm từ thịt bò tươi chọn lọc, xay nhuyễn và nêm nếm theo công thức truyền thống, có độ dai giòn tự nhiên, mùi thơm đặc trưng, thích hợp dùng trực tiếp hoặc làm quà biếu.', 270000.00, '/assets/images/products/nem-cha-tre/cha-bo-binh-dinh-loai-1-500gr.jpg', 100, 468, 0.0, 0);
INSERT INTO `products` VALUES (25, 1, 'Tré Bình Định Đặc Sản Bà Tròn', 'Tré Bình Định Bà Tròn là đặc sản truyền thống nổi tiếng, được làm từ thịt heo, tai heo và gia vị lên men tự nhiên, hương vị chua cay mặn ngọt hài hòa, thường dùng làm món nhắm hoặc quà biếu.', 160000.00, '/assets/images/products/nem-cha-tre/tre-binh-dinh-dac-san-binh-dinh-ba-tron.jpg', 100, 32, 0.0, 0);
INSERT INTO `products` VALUES (26, 2, 'Bánh Ít Lá Gai Quy Nhơn', 'Bánh ít lá gai là đặc sản truyền thống Bình Định, lớp vỏ dẻo thơm từ lá gai, nhân đậu xanh dừa béo bùi, vị ngọt vừa phải, thường dùng làm quà biếu hoặc trong các dịp lễ.', 120000.00, '/assets/images/products/banh-keo-dac-san/banh-it-la-gai-ngon.jpg', 100, 217, 0.0, 0);
INSERT INTO `products` VALUES (27, 2, 'Bánh Ít Lá Gai Quy Nhơn', 'Bánh ít lá gai là đặc sản truyền thống Bình Định, lớp vỏ dẻo thơm từ lá gai, nhân đậu xanh dừa béo bùi, vị ngọt vừa phải, thường dùng làm quà biếu hoặc trong các dịp lễ.', 120000.00, '/assets/images/products/banh-keo-dac-san/banh-it-la-gai-ngon1.jpg', 100, 492, 0.0, 0);
INSERT INTO `products` VALUES (28, 2, 'Bánh Hồng Tam Quan', 'Bánh hồng Tam Quan là đặc sản truyền thống Bình Định, làm từ bột nếp, dừa và đường, bánh dẻo mềm, vị ngọt thanh, thường dùng trong cưới hỏi và làm quà biếu.', 150000.00, '/assets/images/products/banh-keo-dac-san/banh-hong-tam-quan.jpg', 100, 328, 0.0, 0);
INSERT INTO `products` VALUES (29, 2, 'Bánh Ít Lá Gai Giấy Vàng (Gold)', 'Bánh ít lá gai giấy vàng (Gold) là phiên bản cao cấp của đặc sản Bình Định, vỏ bánh dẻo thơm từ lá gai, nhân đậu xanh dừa béo bùi, gói giấy vàng sang trọng, phù hợp làm quà biếu.', 180000.00, '/assets/images/products/banh-keo-dac-san/banh-it-la-gai-giay-vang-gold.jpg', 100, 155, 0.0, 0);
INSERT INTO `products` VALUES (30, 2, 'Bánh Đậu Xanh Quy Nhơn', 'Bánh đậu xanh là đặc sản truyền thống, được làm từ đậu xanh xay nhuyễn, vị ngọt dịu, bùi thơm, tan nhẹ trong miệng, thích hợp dùng kèm trà hoặc làm quà biếu.', 130000.00, '/assets/images/products/banh-keo-dac-san/banh-dau-xanh.jpg', 100, 269, 0.0, 0);
INSERT INTO `products` VALUES (31, 2, 'Bánh Dừa Nướng Bình Định (Gói)', 'Bánh dừa nướng Bình Định được làm từ dừa tươi, bột nếp và đường, nướng giòn thơm, vị ngọt béo đặc trưng, thường dùng ăn vặt hoặc làm quà đặc sản.', 90000.00, '/assets/images/products/banh-keo-dac-san/banh-dua-nuong-binh-dinh-goi.jpg', 100, 384, 0.0, 0);
INSERT INTO `products` VALUES (32, 2, 'Bánh Tráng Dừa Thanh Liêm (Bột Gạo)', 'Bánh tráng dừa Thanh Liêm làm từ bột gạo, dừa tươi và mè, nướng giòn thơm, vị béo ngọt tự nhiên. Thích hợp ăn vặt hoặc làm quà đặc sản Bình Định.', 80000.00, '/assets/images/products/banh-keo-dac-san/banh-trang-dua-thanh-liem-bot-gao.jpg', 100, 121, 0.0, 0);
INSERT INTO `products` VALUES (33, 2, 'Bánh Ít Lá Gai Bình Định Thanh Liêm (Vỏ Lá)', 'Bánh ít lá gai Thanh Liêm vỏ lá được làm theo phương pháp truyền thống, vỏ bánh dẻo thơm từ lá gai, nhân đậu xanh dừa béo bùi, gói lá tự nhiên, giữ trọn hương vị đặc sản Bình Định.', 140000.00, '/assets/images/products/banh-keo-dac-san/banh-it-la-gai-binh-dinh-thanh-liem-vo-la.jpg', 100, 422, 0.0, 0);
INSERT INTO `products` VALUES (34, 2, 'Kẹo Lạc Bình Định', 'Kẹo lạc là món kẹo truyền thống làm từ đậu phộng rang, mạch nha và đường, có vị ngọt vừa, giòn bùi, thường dùng làm món ăn vặt hoặc ăn kèm trà nóng.', 70000.00, '/assets/images/products/banh-keo-dac-san/keo-lac.jpg', 100, 270, 0.0, 0);
INSERT INTO `products` VALUES (35, 2, 'Kẹo Mè Xửng', 'Kẹo mè xửng là loại kẹo truyền thống làm từ bột, đường mạch nha, đậu phộng và mè, có độ dẻo dai, vị ngọt bùi, thường dùng làm quà vặt hoặc ăn kèm trà.', 80000.00, '/assets/images/products/banh-keo-dac-san/keo-me-xung.jpg', 100, 74, 0.0, 0);
INSERT INTO `products` VALUES (36, 2, 'Kẹo Dừa Viên Bình Định', 'Kẹo dừa viên được làm từ dừa tươi nạo, đường và mạch nha, vo viên nhỏ gọn, vị ngọt béo tự nhiên, mềm dẻo, thích hợp làm món ăn vặt hoặc quà đặc sản.', 90000.00, '/assets/images/products/banh-keo-dac-san/keo-dua-vien.jpg', 100, 41, 0.0, 0);
INSERT INTO `products` VALUES (37, 2, 'Cơm Nếp Chiên Mắm Thanh Liêm', 'Cơm nếp chiên mắm Thanh Liêm là món ăn vặt truyền thống, làm từ nếp dẻo chiên giòn, áo lớp mắm ngọt mặn hài hòa, thơm ngon, thích hợp dùng ăn vặt hoặc làm quà.', 85000.00, '/assets/images/products/banh-keo-dac-san/com-nep-chien-mam-thanh-liem.jpg', 100, 464, 0.0, 0);
INSERT INTO `products` VALUES (38, 2, 'Bánh Tráng Cốt Dừa Thanh Liêm Mỹ', 'Bánh tráng cốt dừa Thanh Liêm Mỹ được làm từ bột gạo và cốt dừa tươi, nướng giòn thơm, vị béo ngậy đặc trưng, là món ăn vặt và quà đặc sản Bình Định được ưa chuộng.', 90000.00, '/assets/images/products/banh-keo-dac-san/banh-trang-cot-dua-thanh-liem-my.jpg', 100, 228, 0.0, 0);
INSERT INTO `products` VALUES (39, 2, 'Bánh Tráng Dừa Nướng Sẵn', 'Bánh tráng dừa nướng sẵn được làm từ bột gạo và dừa tươi, nướng giòn thơm, vị béo ngọt tự nhiên. Có thể dùng ăn liền, tiện lợi làm món ăn vặt hoặc quà đặc sản.', 85000.00, '/assets/images/products/banh-keo-dac-san/banh-trang-dua-nuong-san.jpg', 100, 227, 0.0, 0);
INSERT INTO `products` VALUES (40, 2, 'Bánh Khoai Lang Dẻo Thanh Liêm (730g)', 'Bánh khoai lang dẻo Thanh Liêm được làm từ khoai lang chọn lọc, sên dẻo vừa phải, vị ngọt tự nhiên, thơm bùi, thích hợp làm món ăn vặt hoặc quà đặc sản Bình Định.', 160000.00, '/assets/images/products/banh-keo-dac-san/banh-khoai-lang-deo-thanh-liem-730g.jpg', 100, 441, 0.0, 0);
INSERT INTO `products` VALUES (41, 2, 'Bánh Dừa Nướng Bình Định (Hộp)', 'Bánh dừa nướng Bình Định đóng hộp được làm từ dừa tươi, bột nếp và đường, nướng giòn thơm, vị béo ngọt đặc trưng. Phù hợp làm quà biếu đặc sản Bình Định.', 110000.00, '/assets/images/products/banh-keo-dac-san/banh-dua-nuong-binh-dinh-hop.jpg', 100, 45, 0.0, 0);
INSERT INTO `products` VALUES (42, 2, 'Bánh Tráng Nước Dừa Mắm Nhĩ Đặc Sản Bình Định', 'Bánh tráng nước dừa mắm nhĩ là đặc sản Bình Định, kết hợp bột gạo, nước dừa và mắm nhĩ tạo hương vị mặn ngọt đặc trưng, nướng lên thơm giòn, rất thích hợp ăn vặt hoặc làm quà.', 90000.00, '/assets/images/products/banh-keo-dac-san/banh-trang-nuoc-dua-mam-nhi-dac-san-binh-dinh.jpg', 100, 363, 0.0, 0);
INSERT INTO `products` VALUES (43, 2, 'Bánh Tráng Dừa Tam Quan Loại Đặc Biệt Chưa Nướng (Túi 10 Bánh)', 'Bánh tráng dừa Tam Quan loại đặc biệt chưa nướng, làm từ bột gạo và dừa tươi, bánh dày, nhiều dừa, mùi thơm tự nhiên. Dùng nướng than hoặc nướng điện đều ngon, thích hợp mua về dùng dần hoặc làm quà.', 95000.00, '/assets/images/products/banh-keo-dac-san/banh-trang-dua-tam-quan-loai-dac-biet-chua-nuong-tui-10-banh.jpg', 100, 202, 0.0, 0);
INSERT INTO `products` VALUES (44, 2, 'Bánh Tráng Dừa Tỏi Ớt (Set 10 Bánh Dùng Liền)', 'Bánh tráng dừa tỏi ớt dùng liền là phiên bản tiện lợi, đã tẩm sẵn tỏi ớt cay nhẹ, vị mặn ngọt hài hòa, bánh giòn thơm, thích hợp ăn vặt hoặc làm quà đặc sản Bình Định.', 120000.00, '/assets/images/products/banh-keo-dac-san/banh-trang-dua-toi-ot-set-10-banh-dung-lien.jpg', 100, 399, 0.0, 0);
INSERT INTO `products` VALUES (45, 2, 'Bánh Tráng Nhúng Mè Đen', 'Bánh tráng nhúng mè đen là đặc sản Bình Định, làm từ bột gạo pha mè đen rang thơm, khi nướng lên phồng giòn, mùi mè béo bùi đặc trưng, thích hợp ăn vặt hoặc làm quà.', 75000.00, '/assets/images/products/banh-keo-dac-san/banh-trang-nhung-me-den.jpg', 100, 402, 0.0, 0);
INSERT INTO `products` VALUES (46, 2, 'Bánh Ướt Bình Định (500gr)', 'Bánh ướt Bình Định được làm từ bột gạo xay mịn, tráng mỏng mềm dẻo, thường dùng ăn kèm chả, nem hoặc mắm nêm. Sản phẩm đóng gói tiện lợi, thích hợp mua về dùng hoặc làm quà.', 60000.00, '/assets/images/products/banh-keo-dac-san/banh-uot-binh-dinh-500gr.jpg', 100, 313, 0.0, 0);
INSERT INTO `products` VALUES (47, 2, 'Bánh Canh Gạo Bình Định (500gr)', 'Bánh canh gạo Bình Định được làm từ bột gạo nguyên chất, sợi bánh dẻo dai tự nhiên, thường dùng nấu bánh canh cá, bánh canh giò hoặc hải sản. Đóng gói tiện lợi, dễ bảo quản.', 70000.00, '/assets/images/products/banh-keo-dac-san/banh-canh-gao-binh-dinh-500gr.jpg', 100, 349, 0.0, 0);
INSERT INTO `products` VALUES (48, 2, 'Bánh Hỏi Bình Định (500gr)', 'Bánh hỏi Bình Định được làm từ bột gạo xay mịn, ép thành sợi nhỏ mềm mịn, thường ăn kèm lòng heo, thịt nướng hoặc mắm nêm. Sản phẩm đóng gói tiện lợi, thích hợp mua về sử dụng.', 65000.00, '/assets/images/products/banh-keo-dac-san/banh-hoi-binh-dinh-500gr.jpg', 100, 306, 0.0, 0);
INSERT INTO `products` VALUES (49, 2, 'Bánh Bèo Ly Bình Định Ngon Chuẩn Vị', 'Bánh bèo ly Bình Định được làm từ bột gạo mịn, hấp mềm, ăn kèm nhân tôm, hành phi và nước mắm đậm đà. Món ăn dân dã, chuẩn vị địa phương, đóng gói tiện lợi.', 70000.00, '/assets/images/products/banh-keo-dac-san/banh-beo-ly-binh-dinh-ngon-chuan-vi.jpg', 100, 472, 0.0, 0);
INSERT INTO `products` VALUES (50, 2, 'Bánh Xèo Vỏ Bình Định (500gr)', 'Bánh xèo vỏ Bình Định được làm từ bột gạo pha theo công thức truyền thống, dùng để đổ bánh xèo giòn rụm, thơm mùi gạo. Sản phẩm đóng gói tiện lợi, phù hợp sử dụng tại nhà.', 55000.00, '/assets/images/products/banh-keo-dac-san/banh-xeo-vo-binh-dinh-500gr.jpg', 100, 455, 0.0, 0);
INSERT INTO `products` VALUES (51, 2, 'Bánh Canh Mì Bình Định (500gr)', 'Bánh canh mì Bình Định được làm từ bột mì, sợi bánh dai mềm, thường dùng nấu bánh canh cá, bánh canh thịt hoặc hải sản. Sản phẩm đóng gói tiện lợi, dễ bảo quản và sử dụng.', 70000.00, '/assets/images/products/banh-keo-dac-san/banh-canh-mi-binh-dinh-500gr.jpg', 100, 361, 0.0, 0);
INSERT INTO `products` VALUES (52, 2, 'Bánh Tét Bình Định (1.6kg)', 'Bánh tét Bình Định được gói bằng lá chuối, nếp dẻo thơm, nhân đậu xanh và thịt heo đậm đà, mang hương vị truyền thống ngày Tết và các dịp lễ.', 220000.00, '/assets/images/products/banh-keo-dac-san/banh-tet-binh-dinh-1kg6.jpg', 100, 428, 0.0, 0);
INSERT INTO `products` VALUES (53, 3, 'Rượu Bàu Đá Long Phụng Màu Xanh Ngọc', 'Rượu Bàu Đá Long Phụng màu xanh ngọc là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương thơm nồng, vị êm sâu, chai sứ họa tiết long phụng sang trọng, thích hợp làm quà biếu.', 650000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-long-phung-mau-xanh-ngoc.jpg', 50, 67, 0.0, 0);
INSERT INTO `products` VALUES (54, 3, 'Rượu Bàu Đá Rồng Nhỏ Màu Xanh Rêu', 'Rượu Bàu Đá rồng nhỏ màu xanh rêu là dòng danh tửu Bình Định nấu theo phương pháp truyền thống từ gạo và men cổ truyền, hương thơm nồng, vị rượu êm sâu, chai sứ tạo hình rồng nhỏ sang trọng, thích hợp làm quà biếu.', 520000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-rong-nho-mau-xanh-reu.jpg', 50, 24, 0.0, 0);
INSERT INTO `products` VALUES (55, 3, 'Rượu Bàu Đá 2.5 Lít Màu Xanh Bút Bi', 'Rượu Bàu Đá dung tích 2.5 lít màu xanh bút bi là dòng rượu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương thơm nồng, vị rượu mạnh nhưng êm sâu, thích hợp sử dụng lâu dài hoặc làm quà biếu giá trị.', 1800000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-2-5lit-mau-xanh-but-bi.jpg', 20, 400, 0.0, 0);
INSERT INTO `products` VALUES (56, 3, 'Rượu Bàu Đá 2.5 Lít Màu Đen', 'Rượu Bàu Đá dung tích 2.5 lít màu đen là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương thơm nồng đặc trưng, vị rượu mạnh nhưng êm, phù hợp sử dụng lâu dài hoặc làm quà biếu sang trọng.', 1800000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-2-5lit-mau-den.jpg', 20, 446, 0.0, 0);
INSERT INTO `products` VALUES (57, 3, 'Rượu Bàu Đá Rồng Nhỏ Màu Xanh Ngọc', 'Rượu Bàu Đá rồng nhỏ màu xanh ngọc là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương thơm nồng, vị rượu êm sâu. Bình sứ tạo hình rồng nhỏ màu xanh ngọc sang trọng, phù hợp làm quà biếu.', 520000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-rong-nho-mau-xanh-ngoc.jpg', 50, 43, 0.0, 0);
INSERT INTO `products` VALUES (58, 3, 'Rượu Bàu Đá Long Phụng 650ml Màu Hồng', 'Rượu Bàu Đá Long Phụng dung tích 650ml màu hồng là dòng danh tửu Bình Định nấu từ gạo và men cổ truyền, hương thơm nồng, vị rượu êm sâu. Chai sứ họa tiết long phụng màu hồng sang trọng, thích hợp làm quà biếu.', 780000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-long-phung-650ml-mau-hong.jpg', 40, 338, 0.0, 0);
INSERT INTO `products` VALUES (59, 3, 'Rượu Bàu Đá Rồng Nhỏ Màu Xanh Bút Bi', 'Rượu Bàu Đá rồng nhỏ màu xanh bút bi là danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương thơm nồng đặc trưng, vị rượu mạnh nhưng êm. Bình sứ tạo hình rồng nhỏ màu xanh bút bi, phù hợp làm quà biếu.', 520000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-rong-nho-mau-xanh-but-bi.jpg', 50, 78, 0.0, 0);
INSERT INTO `products` VALUES (60, 3, 'Rượu Bàu Đá Thuyền Chim Màu Hồng', 'Rượu Bàu Đá thuyền chim màu hồng là dòng danh tửu truyền thống Bình Định, được nấu từ gạo và men cổ truyền. Bình sứ tạo hình thuyền chim màu hồng tinh xảo, hương rượu nồng, vị êm sâu, thích hợp làm quà biếu cao cấp.', 720000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-thuyen-chim-mau-hong.jpg', 40, 350, 0.0, 0);
INSERT INTO `products` VALUES (61, 3, 'Rượu Bàu Đá Thuyền Chim Màu Xanh Rêu', 'Rượu Bàu Đá thuyền chim màu xanh rêu là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền. Bình sứ tạo hình thuyền chim màu xanh rêu cổ điển, hương rượu nồng, vị êm sâu, thích hợp làm quà biếu sang trọng.', 720000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-thuyen-chim-mau-xanh-reu.jpg', 40, 34, 0.0, 0);
INSERT INTO `products` VALUES (62, 3, 'Rượu Bàu Đá 2.5 Lít Màu Xanh Rêu', 'Rượu Bàu Đá dung tích 2.5 lít màu xanh rêu là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương rượu nồng đậm, vị mạnh nhưng êm sâu. Bình sứ màu xanh rêu cổ điển, thích hợp làm quà biếu hoặc sử dụng lâu dài.', 1800000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-2-5lit-mau-xanh-reu.jpg', 20, 93, 0.0, 0);
INSERT INTO `products` VALUES (63, 3, 'Rượu Bàu Đá Rồng Nhỏ Màu Hồng', 'Rượu Bàu Đá rồng nhỏ màu hồng là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương rượu nồng, vị êm sâu. Bình sứ tạo hình rồng nhỏ màu hồng tinh tế, phù hợp làm quà biếu sang trọng.', 520000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-rong-nho-mau-hong.jpg', 50, 353, 0.0, 0);
INSERT INTO `products` VALUES (64, 3, 'Rượu Bàu Đá Chum 650ml Màu Nâu', 'Rượu Bàu Đá chum 650ml màu nâu là danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương rượu nồng đặc trưng, vị mạnh nhưng êm. Bình sứ dáng chum màu nâu mộc mạc, thích hợp làm quà biếu hoặc trưng bày.', 750000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-chum-650ml-mau-nau.jpg', 40, 498, 0.0, 0);
INSERT INTO `products` VALUES (65, 3, 'Rượu Bàu Đá Hồ Lô 350ml (Chai Nhỏ)', 'Rượu Bàu Đá hồ lô 350ml chai nhỏ là danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương rượu nồng, vị mạnh nhưng êm. Chai sứ dáng hồ lô nhỏ gọn, thích hợp làm quà biếu hoặc dùng thử.', 380000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-ho-lo-350ml-chai-nho.jpg', 60, 438, 0.0, 0);
INSERT INTO `products` VALUES (66, 3, 'Rượu Bàu Đá Ba Bầu Màu Xanh Ngọc', 'Rượu Bàu Đá ba bầu màu xanh ngọc là danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương rượu nồng đậm, vị êm sâu. Bình sứ tạo hình ba bầu màu xanh ngọc sang trọng, thích hợp làm quà biếu.', 680000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-ba-bau-mau-xanh-ngoc.jpg', 40, 200, 0.0, 0);
INSERT INTO `products` VALUES (67, 3, 'Rượu Bàu Đá Thuyền Chim Màu Đen', 'Rượu Bàu Đá thuyền chim màu đen là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương rượu nồng đậm, vị mạnh nhưng êm sâu. Bình sứ tạo hình thuyền chim màu đen sang trọng, thích hợp làm quà biếu cao cấp.', 720000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-thuyen-chim-mau-den.jpg', 40, 164, 0.0, 0);
INSERT INTO `products` VALUES (68, 3, 'Rượu Bàu Đá 2.5 Lít Màu Xanh Ngọc', 'Rượu Bàu Đá dung tích 2.5 lít màu xanh ngọc là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương rượu nồng đậm, vị mạnh nhưng êm sâu. Bình sứ màu xanh ngọc sang trọng, phù hợp trưng bày hoặc làm quà biếu.', 1800000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-2-5lit-mau-xanh-ngoc.jpg', 20, 210, 0.0, 0);
INSERT INTO `products` VALUES (69, 3, 'Rượu Bàu Đá Hồ Lô 350ml Màu Xanh Rêu', 'Rượu Bàu Đá hồ lô 350ml màu xanh rêu là danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương rượu nồng đậm, vị mạnh nhưng êm. Chai sứ dáng hồ lô màu xanh rêu cổ điển, thích hợp làm quà biếu hoặc dùng thử.', 380000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-ho-lo-350ml-mau-xanh-reu.jpg', 60, 61, 0.0, 0);
INSERT INTO `products` VALUES (70, 3, 'Rượu Bàu Đá 2.5 Lít Màu Nhớt', 'Rượu Bàu Đá dung tích 2.5 lít màu nhớt là danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương rượu nồng đậm, vị mạnh nhưng êm sâu. Bình sứ màu nhớt độc đáo, phù hợp trưng bày hoặc làm quà biếu giá trị.', 1800000.00, 'v/assets/images/products/danh-tuu-bau-da/ruou-bau-da-2-5lit-mau-nhot.jpg', 20, 155, 0.0, 0);
INSERT INTO `products` VALUES (71, 3, 'Rượu Bàu Đá Hồ Lô 2.5 Lít', 'Rượu Bàu Đá hồ lô dung tích 2.5 lít là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền. Rượu có hương thơm nồng, vị mạnh nhưng êm sâu. Bình sứ dáng hồ lô lớn, thích hợp trưng bày hoặc làm quà biếu cao cấp.', 1850000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-ho-lo-2-5-lit.jpg', 15, 94, 0.0, 0);
INSERT INTO `products` VALUES (72, 3, 'Rượu Bàu Đá Hồ Lô 350ml Màu Xanh Ngọc', 'Rượu Bàu Đá hồ lô 350ml màu xanh ngọc là danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương rượu nồng, vị mạnh nhưng êm. Chai sứ dáng hồ lô màu xanh ngọc sang trọng, thích hợp làm quà biếu hoặc dùng thử.', 380000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-ho-lo-350ml-mau-xanh-ngoc.jpg', 60, 483, 0.0, 0);
INSERT INTO `products` VALUES (73, 3, 'Rượu Bàu Đá Thuyền Lớn Màu Đen', 'Rượu Bàu Đá thuyền lớn màu đen là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền. Rượu có hương thơm nồng đậm, vị mạnh nhưng êm sâu. Bình sứ dáng thuyền lớn màu đen sang trọng, phù hợp trưng bày hoặc làm quà biếu cao cấp.', 1500000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-thuyen-lon-mau-den.jpg', 20, 165, 0.0, 0);
INSERT INTO `products` VALUES (74, 3, 'Rượu Bàu Đá Thuyền Lớn Màu Xanh Ngọc', 'Rượu Bàu Đá thuyền lớn màu xanh ngọc là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền. Rượu có hương thơm nồng đậm, vị mạnh nhưng êm sâu. Bình sứ dáng thuyền lớn màu xanh ngọc sang trọng, thích hợp trưng bày hoặc làm quà biếu cao cấp.', 1500000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-thuyen-lon-mau-xanh-ngoc.jpg', 20, 344, 0.0, 0);
INSERT INTO `products` VALUES (75, 3, 'Rượu Bàu Đá Ba Bầu Màu Đen', 'Rượu Bàu Đá ba bầu màu đen là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương rượu nồng đậm, vị mạnh nhưng êm sâu. Bình sứ tạo hình ba bầu màu đen sang trọng, phù hợp trưng bày hoặc làm quà biếu cao cấp.', 680000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-ba-bau-mau-den.jpg', 40, 237, 0.0, 0);
INSERT INTO `products` VALUES (76, 3, 'Rượu Bàu Đá Ba Bầu Màu Xanh Rêu', 'Rượu Bàu Đá ba bầu màu xanh rêu là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương rượu nồng đậm, vị mạnh nhưng êm sâu. Bình sứ tạo hình ba bầu màu xanh rêu cổ điển, thích hợp trưng bày hoặc làm quà biếu cao cấp.', 680000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-ba-bau-mau-xanh-reu.jpg', 40, 142, 0.0, 0);
INSERT INTO `products` VALUES (77, 3, 'Rượu Bàu Đá Long Phụng Màu Đen', 'Rượu Bàu Đá Long Phụng màu đen là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương rượu nồng đậm, vị mạnh nhưng êm sâu. Chai sứ họa tiết long phụng màu đen sang trọng, thích hợp làm quà biếu cao cấp.', 750000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-long-phung-mau-den.jpg', 40, 479, 0.0, 0);
INSERT INTO `products` VALUES (78, 3, 'Rượu Bàu Đá Ba Bầu Màu Xanh Bút Bi', 'Rượu Bàu Đá ba bầu màu xanh bút bi là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương rượu nồng đậm, vị mạnh nhưng êm sâu. Bình sứ tạo hình ba bầu màu xanh bút bi độc đáo, thích hợp trưng bày hoặc làm quà biếu cao cấp.', 680000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-ba-bau-mau-xanh-but-bi.jpg', 40, 493, 0.0, 0);
INSERT INTO `products` VALUES (79, 3, 'Rượu Bàu Đá Chum 650ml Màu Xanh Bút Bi', 'Rượu Bàu Đá chum 650ml màu xanh bút bi là danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương rượu nồng đậm, vị mạnh nhưng êm sâu. Bình sứ dáng chum màu xanh bút bi nổi bật, thích hợp trưng bày hoặc làm quà biếu.', 750000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-chum-650ml-mau-xanh-but-bi.jpg', 40, 35, 0.0, 0);
INSERT INTO `products` VALUES (80, 3, 'Rượu Bàu Đá Long Phụng Màu Xanh Rêu', 'Rượu Bàu Đá Long Phụng màu xanh rêu là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương rượu nồng đậm, vị mạnh nhưng êm sâu. Chai sứ họa tiết long phụng màu xanh rêu cổ điển, sang trọng, thích hợp làm quà biếu cao cấp.', 750000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-long-phung-mau-xanh-reu.jpg', 40, 159, 0.0, 0);
INSERT INTO `products` VALUES (81, 3, 'Rượu Bàu Đá Thuyền Lớn Màu Da Lươn', 'Rượu Bàu Đá thuyền lớn màu da lươn là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền. Rượu có hương thơm nồng đậm, vị mạnh nhưng êm sâu. Bình sứ dáng thuyền lớn màu da lươn độc đáo, thích hợp trưng bày hoặc làm quà biếu cao cấp.', 1500000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-thuyen-lon-mau-da-luon.jpg', 20, 192, 0.0, 0);
INSERT INTO `products` VALUES (82, 3, 'Rượu Bàu Đá Thuyền Chim Màu Da Lươn', 'Rượu Bàu Đá thuyền chim màu da lươn là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền. Rượu có hương thơm nồng đậm, vị mạnh nhưng êm sâu. Bình sứ tạo hình thuyền chim màu da lươn độc đáo, thích hợp trưng bày hoặc làm quà biếu.', 720000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-thuyen-chim-mau-da-luon.jpg', 40, 470, 0.0, 0);
INSERT INTO `products` VALUES (83, 3, 'Rượu Bàu Đá Hồ Lô Chai Trung', 'Rượu Bàu Đá hồ lô chai trung là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền. Rượu có hương thơm nồng đậm, vị mạnh nhưng êm sâu. Chai sứ dáng hồ lô kích thước trung, thích hợp làm quà biếu hoặc trưng bày.', 520000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-ho-lo-chai-trung.jpg', 50, 295, 0.0, 0);
INSERT INTO `products` VALUES (84, 3, 'Rượu Bàu Đá Thuyền Lớn Màu Xanh Bút Bi', 'Rượu Bàu Đá thuyền lớn màu xanh bút bi là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền. Rượu có hương thơm nồng đậm, vị mạnh nhưng êm sâu. Bình sứ dáng thuyền lớn màu xanh bút bi nổi bật, thích hợp trưng bày hoặc làm quà biếu cao cấp.', 1500000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-thuyen-lon-mau-xanh-but-bi.jpg', 20, 56, 0.0, 0);
INSERT INTO `products` VALUES (85, 3, 'Rượu Bàu Đá Rồng Nhỏ Màu Đen', 'Rượu Bàu Đá rồng nhỏ màu đen là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền, hương rượu nồng đậm, vị mạnh nhưng êm sâu. Bình sứ tạo hình rồng nhỏ màu đen sang trọng, thích hợp làm quà biếu cao cấp.', 520000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-rong-nho-mau-den.jpg', 50, 364, 0.0, 0);
INSERT INTO `products` VALUES (86, 3, 'Rượu Bàu Đá Thuyền Chim Màu Xanh Ngọc', 'Rượu Bàu Đá thuyền chim màu xanh ngọc là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền. Rượu có hương thơm nồng đậm, vị mạnh nhưng êm sâu. Bình sứ tạo hình thuyền chim màu xanh ngọc sang trọng, thích hợp trưng bày hoặc làm quà biếu cao cấp.', 720000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-thuyen-chim-mau-xanh-ngoc.jpg', 40, 174, 0.0, 0);
INSERT INTO `products` VALUES (87, 3, 'Rượu Bàu Đá Thuyền Chim Màu Xanh Bút Bi', 'Rượu Bàu Đá thuyền chim màu xanh bút bi là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền. Rượu có hương thơm nồng đậm, vị mạnh nhưng êm sâu. Bình sứ tạo hình thuyền chim màu xanh bút bi nổi bật, thích hợp trưng bày hoặc làm quà biếu.', 720000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-thuyen-chim-mau-xanh-but-bi.jpg', 40, 257, 0.0, 0);
INSERT INTO `products` VALUES (88, 3, 'Rượu Bàu Đá Hồ Lô 350ml Màu Hồng', 'Rượu Bàu Đá hồ lô 350ml màu hồng là danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền. Rượu có hương thơm nồng đậm, vị mạnh nhưng êm sâu. Chai sứ dáng hồ lô màu hồng tinh tế, phù hợp làm quà biếu hoặc dùng thử.', 380000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-ho-lo-350ml-mau-hong.jpg', 60, 262, 0.0, 0);
INSERT INTO `products` VALUES (89, 3, 'Rượu Bàu Đá Thuyền Lớn Màu Xanh Rêu', 'Rượu Bàu Đá thuyền lớn màu xanh rêu là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền. Rượu có hương thơm nồng đậm, vị mạnh nhưng êm sâu. Bình sứ dáng thuyền lớn màu xanh rêu cổ điển, thích hợp trưng bày hoặc làm quà biếu cao cấp.', 1500000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-thuyen-lon-mau-xanh-reu.jpg', 20, 42, 0.0, 0);
INSERT INTO `products` VALUES (90, 3, 'Rượu Bàu Đá Hồ Lô 350ml Màu Da Lươn', 'Rượu Bàu Đá hồ lô 350ml màu da lươn là danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền. Rượu có hương thơm nồng đậm, vị mạnh nhưng êm sâu. Chai sứ dáng hồ lô màu da lươn độc đáo, thích hợp làm quà biếu hoặc dùng thử.', 380000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-ho-lo-350ml-mau-da-luon.jpg', 60, 394, 0.0, 0);
INSERT INTO `products` VALUES (91, 3, 'Rượu Bàu Đá Hồ Lô 350ml Màu Đen', 'Rượu Bàu Đá hồ lô 350ml màu đen là danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền. Rượu có hương thơm nồng đậm, vị mạnh nhưng êm sâu. Chai sứ dáng hồ lô màu đen sang trọng, thích hợp làm quà biếu hoặc dùng thử.', 380000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-ho-lo-350ml-mau-den.jpg', 60, 366, 0.0, 0);
INSERT INTO `products` VALUES (92, 3, 'Rượu Bàu Đá Chum 650ml Màu Trắng', 'Rượu Bàu Đá chum 650ml màu trắng là danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền. Rượu có hương thơm nồng đậm, vị mạnh nhưng êm sâu. Bình sứ dáng chum màu trắng trang nhã, thích hợp trưng bày hoặc làm quà biếu.', 750000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-chum-650ml-mau-trang.jpg', 40, 148, 0.0, 0);
INSERT INTO `products` VALUES (93, 3, 'Rượu Bàu Đá Long Phụng 650ml Màu Xanh Bút Bi', 'Rượu Bàu Đá Long Phụng 650ml màu xanh bút bi là dòng danh tửu truyền thống Bình Định, nấu từ gạo và men cổ truyền. Rượu có hương thơm nồng đậm, vị mạnh nhưng êm sâu. Chai sứ họa tiết long phụng màu xanh bút bi sang trọng, thích hợp làm quà biếu cao cấp.', 780000.00, '/assets/images/products/danh-tuu-bau-da/ruou-bau-da-long-phung-650ml-mau-xanh-but-bi.jpg', 40, 124, 0.0, 0);
INSERT INTO `products` VALUES (94, 4, 'Tôm xẻ tẩm Song Phương 150g (Hộp)', 'Tôm xẻ tẩm Song Phương – được làm từ tôm tươi xẻ dọc sống lưng, tẩm ướp gia vị và sấy khô theo công nghệ hiện đại, giữ vị ngọt tự nhiên và phù hợp dùng ăn ngay hoặc chế biến nhanh.', 299000.00, '/assets/images/products/hai-san-kho/tom-xe-tam-song-phuong-150g-hop.jpg', 30, 166, 0.0, 0);
INSERT INTO `products` VALUES (95, 4, 'Cá thu 1 nắng Song Phương 500 g', 'Cá thu một nắng Song Phương 500 g là sản phẩm hải sản đặc sản được làm từ cá thu tươi phơi một nắng theo phương pháp truyền thống, giữ vị ngọt tự nhiên và thơm bùi của thịt cá.', 189000.00, '/assets/images/products/hai-san-kho/ca-thu-mot-nang-song-phuong-500g.jpg', 40, 448, 0.0, 0);
INSERT INTO `products` VALUES (96, 4, 'Mực thước đại dương thượng hạng (size 5-7 con)', 'Mực thước đại dương thượng hạng được tuyển chọn từ những con mực to, phơi khô theo phương pháp truyền thống, giữ hương vị thơm ngon và giàu dinh dưỡng, phù hợp dùng làm món nhậu hoặc chế biến đa dạng.', 1420000.00, '/assets/images/products/hai-san-kho/muc-thuoc-dai-duong-thuong-hang-size-5-7-con.jpg', 20, 263, 0.0, 0);
INSERT INTO `products` VALUES (97, 4, 'Tôm thẻ khô thiên nhiên cao cấp', 'Tôm thẻ khô thiên nhiên cao cấp được làm từ tôm tươi tuyển chọn, phơi khô theo quy trình chuẩn, giữ vị ngọt tự nhiên và thơm ngon, phù hợp dùng ăn ngay hoặc chế biến nhiều món đặc sản.', 1320000.00, '/assets/images/products/hai-san-kho/tom-the-thien-nhien.jpg', 30, 452, 0.0, 0);
INSERT INTO `products` VALUES (98, 4, 'Khô ức mỡ cá dứa loại ngon', 'Khô ức mỡ cá dứa loại ngon – được làm từ phần ức nhiều mỡ của cá dứa, phơi nắng đúng chuẩn 3 nắng, vị lạt vừa ăn, giòn béo thơm ngon, giàu dinh dưỡng và không chất bảo quản.', 80000.00, '/assets/images/products/hai-san-kho/kho-uc-mo-ca-dua-loai-ngon.jpg', 40, 481, 0.0, 0);
INSERT INTO `products` VALUES (99, 4, 'Khô ruột vịt loại ngon', 'Khô ruột vịt loại ngon được làm từ ruột vịt tươi, phơi khô theo phương pháp truyền thống, giòn thơm, phù hợp ăn nướng hoặc chiên.', 140000.00, '/assets/images/products/hai-san-kho/kho-ruot-vit-loai-ngon.jpg', 30, 60, 0.0, 0);
INSERT INTO `products` VALUES (100, 4, 'Khô Cá Khoai đặc sản Cà Mau', 'Khô Cá Khoai đặc sản Cà Mau – loại cá khô giòn, thịt ngọt thơm, dễ chế biến thành nhiều món ngon như chiên, rim hoặc nướng.', 260000.00, '/assets/images/products/hai-san-kho/kho-ca-khoai-ca-mau.jpg', 30, 315, 0.0, 0);
INSERT INTO `products` VALUES (101, 4, 'Khô Cá Sặc Bướm', 'Khô Cá Sặc Bướm được làm từ cá sặc đồng loại ngon, ướp gia vị vừa ăn, giữ vị ngọt đậm đà và giòn thơm phù hợp ăn ngay hoặc chế biến món ngon.', 140000.00, '/assets/images/products/hai-san-kho/kho-ca-sac-buom.jpg', 40, 407, 0.0, 0);
INSERT INTO `products` VALUES (102, 4, 'Khô Cá Bò Da Tẩm Vị', 'Khô cá bò da tẩm vị – cá được tẩm ướp gia vị và phơi khô, thịt mềm, thơm, thích hợp chiên hoặc nướng.', 160000.00, '/assets/images/products/hai-san-kho/kho-ca-bo-da-tam-vi.jpg', 30, 100, 0.0, 0);
INSERT INTO `products` VALUES (103, 4, 'Tôm Đất Ba Tri Hàng loại 1', 'Tôm Đất Ba Tri loại 1 – tôm khô thơm ngọt, hảo hạng với hương vị đặc trưng, dùng nấu canh, rim, nước dùng hay ăn trực tiếp.', 900000.00, '/assets/images/products/hai-san-kho/tom-dat-ba-tri.jpg', 30, 250, 0.0, 0);
INSERT INTO `products` VALUES (104, 4, 'Còi Sò Điệp Khô – Gói 500g', 'Còi sò điệp khô – gói 500g, hải sản khô giàu dinh dưỡng, dùng trực tiếp hoặc chế biến món ăn.', 375000.00, '/assets/images/products/hai-san-kho/coi-so-diep-kho-goi-500g.jpg', 40, 450, 0.0, 0);
INSERT INTO `products` VALUES (105, 4, 'Khô cá bò – Gói 500g', 'Khô cá bò da – gói 500g, sản phẩm hải sản khô thơm ngon, có thể nướng hoặc chiên.', 140000.00, '/assets/images/products/hai-san-kho/kho-ca-bo-goi-500g.jpg', 20, 21, 0.0, 0);
INSERT INTO `products` VALUES (106, 4, 'Khô cá bóng lá trầu – Gói 500g', 'Khô cá bóng lá trầu – gói 500g, hải sản khô Phan Thiết thơm ngon, nướng hoặc chiên ăn kèm tương ớt.', 175000.00, '/assets/images/products/hai-san-kho/kho-ca-bong-la-trau-goi-500g.jpg', 40, 216, 0.0, 0);
INSERT INTO `products` VALUES (107, 4, 'Khô cá chỉ vàng – Gói 500g', 'Khô cá chỉ vàng – gói 500g, sản phẩm hải sản khô Phan Thiết dễ chế biến: nướng, chiên hoặc xé trộn gỏi.', 105000.00, '/assets/images/products/hai-san-kho/kho-ca-chi-vang-phan-thiet-goi-500g.jpg', 43, 28, 0.0, 0);
INSERT INTO `products` VALUES (108, 4, 'Khô cá đù – Gói 500g', 'Khô cá đù – gói 500g, hải sản khô Phan Thiết thơm ngon, dùng nướng, chiên hoặc làm món gỏi.', 105000.00, '/assets/images/products/hai-san-kho/kho-ca-du-goi-500g.jpg', 30, 460, 0.0, 0);
INSERT INTO `products` VALUES (109, 4, 'Khô cá Sặc – Khay 500g', 'Khô cá Sặc bổi (không mặn) – khay 500g, hải sản khô đa công dụng: chiên, kho, nướng, hấp, nấu canh.', 125000.00, '/assets/images/products/hai-san-kho/kho-ca-sac-khay-500g.jpg', 50, 249, 0.0, 0);
INSERT INTO `products` VALUES (110, 4, 'Khô cá lãi trứng – Gói 500g', 'Khô cá lãi trứng – gói 500g, hải sản khô vị bùi đặc trưng, dùng chế biến nhiều món thơm ngon.', 105000.00, '/assets/images/products/hai-san-kho/kho-ca-lai-trung-goi-500g.jpg', 10, 345, 0.0, 0);
INSERT INTO `products` VALUES (111, 4, 'Khô cá cơm – Gói 500g', 'Khô cá cơm – gói 500g, hải sản khô dân dã, dễ chế biến thành nhiều món như nướng, chiên.', 75000.00, '/assets/images/products/hai-san-kho/kho-ca-com-goi-500g.jpg', 60, 479, 0.0, 0);
INSERT INTO `products` VALUES (112, 4, 'Khô mực câu size 12-14con – Gói 500g', 'Khô mực câu Phan Thiết size 12-14con – gói 500g, thích hợp nướng, chiên mắm hoặc xào.', 675000.00, '/assets/images/products/hai-san-kho/kho-muc-cau-size-12-14con-goi-500g.jpg', 49, 370, 0.0, 0);
INSERT INTO `products` VALUES (113, 4, 'Khô cá đuối – Gói 1kg', 'Khô cá đuối – gói 1kg, hải sản khô Phan Thiết thơm ngon, dùng nướng và chiên trứng.', 750000.00, '/assets/images/products/hai-san-kho/kho-ca-duoi-phan-thiet-goi-1kg.jpg', 50, 403, 0.0, 0);
INSERT INTO `products` VALUES (114, 4, 'Khô cá đường – Gói 1kg', 'Khô cá đường – gói 1kg, hải sản khô Phan Thiết, ngon khi nướng và chấm mắm me.', 320000.00, '/assets/images/products/hai-san-kho/kho-ca-duong-goi-1kg.jpg', 50, 404, 0.0, 0);
INSERT INTO `products` VALUES (115, 4, 'Khô mực câu Phan Thiết – Gói 500g', 'Khô mực câu Phan Thiết – gói 500g, hải sản khô dai và ngọt, dễ chế biến món nướng và chiên.', 675000.00, '/assets/images/products/hai-san-kho/kho-muc-cau-phan-thiet-goi-500g.jpg', 45, 313, 0.0, 0);
INSERT INTO `products` VALUES (116, 4, 'Tôm biển khô – Gói 500g', 'Tôm biển khô – gói 500g, hải sản khô Phan Thiết thơm ngon, dùng để nấu canh, xào hoặc ăn kèm củ kiệu.', 340000.00, '/assets/images/products/hai-san-kho/tom-bien-kho-goi-500g.jpg', 40, 341, 0.0, 0);
INSERT INTO `products` VALUES (117, 4, 'Tôm khô nhỏ (Tôm canh) – Gói 500g', 'Tôm khô nhỏ (Tôm canh) – gói 500g, hải sản khô Phan Thiết thơm ngon, dùng nấu canh, xào hoặc ăn kèm củ kiệu.', 275000.00, '/assets/images/products/hai-san-kho/tom-kho-nho-tom-canh-goi-500g.jpg', 30, 270, 0.0, 0);
INSERT INTO `products` VALUES (118, 4, 'Tép khô – Gói 500g', 'Tép khô – gói 500g, hải sản khô Phan Thiết thơm ngon, dùng để xào bắp, xào khế chua hoặc rang tép.', 75000.00, '/assets/images/products/hai-san-kho/tep-kho-goi-500g.jpg', 28, 314, 0.0, 0);
INSERT INTO `products` VALUES (119, 5, 'Thác Khói Trầm Hương Chảy Ngược Bàn Tay Phật Có Đèn Led và Đế Sen Trắng', 'Thác khói trầm hương thủ công mô phỏng bàn tay Phật cầm hoa sen, tạo khói trầm chảy ngược đẹp mắt; có vòng đèn LED chiếu sáng và đế sen trắng.', 1550000.00, '/assets/images/products/lang-nghe-thu-cong/thac-khoi-tram-huong-ban-tay-phat-led-de-sen.jpg', 30, 262, 0.0, 0);
INSERT INTO `products` VALUES (120, 5, 'Bộ Ấm Trà Du Lịch Zisha Cầm Tay 1 Ấm 4 Chén Kèm Hủ Chứa Trà', 'Bộ ấm trà du lịch Zisha bằng gốm sứ cao cấp gồm 1 ấm, 4 chén và hủ chứa trà; thiết kế nhỏ gọn, tiện lợi khi di chuyển.', 450000.00, '/assets/images/products/lang-nghe-thu-cong/bo-am-tra-du-lich-zisha-cam-tay.jpg', 25, 360, 0.0, 0);
INSERT INTO `products` VALUES (121, 5, 'Bộ Ấm Trà Du Lịch Ngoài Trời Gốm Sứ Màu 1 Ấm 4 Chén Xanh Ngọc', 'Bộ ấm trà du lịch ngoài trời bằng gốm sứ màu xanh ngọc gồm 1 ấm và 4 chén, thích hợp cho picnic và dã ngoại.', 520000.00, '/assets/images/products/lang-nghe-thu-cong/bo-am-tra-du-lich-ngoai-troi-xanh-ngoc.jpg', 25, 23, 0.0, 0);
INSERT INTO `products` VALUES (122, 5, 'Thác Khói Trầm Hương Tượng Phật Ngồi Đế Gỗ Có Đèn LED', 'Thác khói trầm hương thủ công với tượng Phật ngồi trên đế gỗ, kết hợp hiệu ứng khói chảy ngược và đèn LED.', 1350000.00, '/assets/images/products/lang-nghe-thu-cong/thac-khoi-tram-huong-tuong-phat-de-go-led.jpg', 20, 497, 0.0, 0);
INSERT INTO `products` VALUES (123, 5, 'Kệ Trang Trí Đèn LED Tháp Trầm Hương Vòng Tròn Hào Quang Kèm Tượng Di Lặc', 'Kệ trang trí thủ công kết hợp tháp trầm hương và tượng Di Lặc, tích hợp đèn LED mang ý nghĩa phong thuỷ.', 1680000.00, '/assets/images/products/lang-nghe-thu-cong/ke-trang-tri-thap-tram-huong-di-lac-led.jpg', 15, 447, 0.0, 0);
INSERT INTO `products` VALUES (124, 5, 'Bộ 3 Bình Gốm sứ Phong Thuỷ Trang Trí Phòng Khách', 'Bộ 3 bình gốm sứ phong thuỷ trang trí phòng khách, men sứ cao cấp, họa tiết nghệ thuật mang ý nghĩa phong thuỷ tốt lành cho gia chủ.', 1200000.00, '/assets/images/products/lang-nghe-thu-cong/bo-3-binh-gom-su-phong-thuy-trang-tri-phong-khach.jpg', 20, 242, 0.0, 0);
INSERT INTO `products` VALUES (125, 5, 'Bộ Ấm Chén Trà Gốm Sứ Có Túi Sách Màu Trắng 1 Ấm 4 Chén Kèm Khay', 'Bộ ấm chén trà gốm sứ màu trắng gồm 1 ấm, 4 chén và khay, kèm túi sách thời trang; phù hợp dùng uống trà và làm quà tặng.', 550000.00, '/assets/images/products/lang-nghe-thu-cong/bo-am-chen-tra-gom-su-co-tui-sach-mau-trang.jpg', 30, 350, 0.0, 0);
INSERT INTO `products` VALUES (126, 5, 'Bộ Ấm Chén Trà Gốm Sứ Có Túi Sách Màu Đen 1 Ấm 4 Chén Kèm Khay', 'Bộ ấm chén trà gốm sứ màu đen gồm 1 ấm, 4 chén và khay, đi kèm túi sách tiện lợi; phù hợp dùng uống trà, làm quà tặng.', 550000.00, '/assets/images/products/lang-nghe-thu-cong/bo-am-chen-tra-gom-su-co-tui-sach-mau-den.jpg', 30, 35, 0.0, 0);
INSERT INTO `products` VALUES (127, 5, 'Thác Khói Trầm Hương Chảy Ngược Kèm Đế Sen', 'Thác khói trầm hương chảy ngược kèm đế sen; thủ công mỹ nghệ, tạo dòng khói trầm đẹp mắt khi dùng nụ trầm.', 400000.00, '/assets/images/products/lang-nghe-thu-cong/thac-khoi-tram-huong-chay-nguoc-kem-de-sen.jpg', 20, 94, 0.0, 0);
INSERT INTO `products` VALUES (128, 5, 'Thác Khói Trầm Hương Chảy Ngược Trên Bàn Tay Phật', 'Thác khói trầm hương chảy ngược đặt trên bàn tay Phật bằng thủ công cát tím; mang phong thuỷ và tạo khói trầm đẹp mắt khi dùng trầm.', 550000.00, '/assets/images/products/lang-nghe-thu-cong/thac-khoi-tram-huong-chay-nguoc-tren-ban-tay-phat.jpg', 25, 357, 0.0, 0);
INSERT INTO `products` VALUES (129, 5, 'Tranh Slogan Hãy Là Số 1 Trong Lĩnh Vực Của Mình', 'Tranh slogan tạo động lực, họa tiết chữ “Hãy là số 1 trong lĩnh vực của mình” trên nền vân gỗ, dùng trang trí góc học tập hoặc văn phòng.', 400000.00, '/assets/images/products/lang-nghe-thu-cong/tranh-slogan-hay-la-so-1-trong-linh-vuc-cua-minh.jpg', 0, 22, 0.0, 0);
INSERT INTO `products` VALUES (130, 5, 'Bộ Ấm Chén Pha Trà Gốm Sứ Hình Cối Xay 2 Ấm 6 Chén', 'Bộ ấm chén pha trà bằng gốm sứ nghệ thuật hình cối xay gồm 2 ấm và 6 chén; thiết kế độc đáo, phù hợp dùng uống trà hoặc làm quà tặng.', 550000.00, '/assets/images/products/lang-nghe-thu-cong/bo-am-chen-pha-tra-gom-su-hinh-coi-xay-bao-gom-2-am-6-chen.jpg', 45, 499, 0.0, 0);
INSERT INTO `products` VALUES (131, 5, 'Bộ 3 Bình Đĩa Gốm Sứ Hoa Sen', 'Bộ 3 bình đĩa gốm sứ họa tiết hoa sen; đồ trang trí phòng khách phong thuỷ với họa tiết hoa sen tinh xảo.', 950000.00, '/assets/images/products/lang-nghe-thu-cong/bo-3-binh-dia-gom-su-hoa-sen.jpg', 15, 462, 0.0, 0);
INSERT INTO `products` VALUES (132, 5, 'Bộ Ấm Chén Bạch Ngọc Hoa Sen Quà Tặng Cao Cấp', 'Bộ ấm chén bạch ngọc gốm sứ họa tiết hoa sen; sản phẩm cao cấp phù hợp làm quà tặng sang trọng.', 5000000.00, '/assets/images/products/lang-nghe-thu-cong/bo-am-chen-bach-ngoc-hoa-sen-qua-tang-cao-cap.jpg', 10, 314, 0.0, 0);
INSERT INTO `products` VALUES (133, 5, 'Bình Gốm Sứ Phong Thuỷ Bộ 3 Món 2 Bình 2 Đĩa Trang Trí Phòng Khách', 'Bình gốm sứ phong thuỷ bộ 3 món gồm 2 bình và 2 đĩa; trang trí phòng khách với họa tiết nghệ thuật mang phong thuỷ tốt lành.', 1200000.00, '/assets/images/products/lang-nghe-thu-cong/binh-gom-su-phong-thuy-bo-3-mon-gom-2-binh-2-dia-trang-tri-phong-khach.jpg', 20, 174, 0.0, 0);
INSERT INTO `products` VALUES (134, 5, 'Bình Gốm Sứ Phong Thuỷ Trang Trí', 'Bình gốm sứ phong thuỷ dùng trang trí phòng khách hoặc không gian sống, mang ý nghĩa may mắn và tài lộc.', 900000.00, '/assets/images/products/lang-nghe-thu-cong/binh-gom-su-phong-thuy112.jpg', 20, 408, 0.0, 0);
INSERT INTO `products` VALUES (135, 5, 'Chậu Hòn Non Bộ Mini Trang Trí', 'Chậu hòn non bộ mini trang trí bàn làm việc hoặc phòng khách, mang không gian thiên nhiên thu nhỏ.', 450000.00, '/assets/images/products/lang-nghe-thu-cong/chau-hon-non-bo-mini_300x300.jpg', 25, 41, 0.0, 0);
INSERT INTO `products` VALUES (136, 5, 'Tranh Chữ Nhân Treo Tường', 'Tranh chữ Nhân mang ý nghĩa đạo đức và nhân văn, phù hợp trang trí phòng khách hoặc phòng làm việc.', 350000.00, '/assets/images/products/lang-nghe-thu-cong/tranh-chu-nhan12.jpg', 30, 438, 0.0, 0);
INSERT INTO `products` VALUES (137, 5, 'Tranh Động Lực Treo Tường', 'Tranh động lực với nội dung truyền cảm hứng, phù hợp treo phòng làm việc, học tập hoặc văn phòng.', 380000.00, '/assets/images/products/lang-nghe-thu-cong/tranh-dong-luc-12341.jpg', 35, 100, 0.0, 0);
INSERT INTO `products` VALUES (138, 5, 'Tranh Tạo Động Lực Treo Tường Nghệ Thuật', 'Tranh tạo động lực thiết kế nghệ thuật, mang thông điệp tích cực, phù hợp trang trí không gian sống và làm việc.', 420000.00, '/assets/images/products/lang-nghe-thu-cong/tranh-tao-dong-luc-treo-tuong.jpg', 30, 156, 0.0, 0);
INSERT INTO `products` VALUES (139, 5, 'Chậu Cá Phong Thuỷ Mini', 'Chậu cá phong thuỷ mini bằng gốm sứ, dùng trang trí bàn làm việc hoặc phòng khách, mang ý nghĩa tài lộc.', 520000.00, '/assets/images/products/lang-nghe-thu-cong/chau-ca-phong-thuy_300x300.jpg', 20, 469, 0.0, 0);
INSERT INTO `products` VALUES (140, 5, 'Hòn Non Bộ Mini Để Bàn Phong Thuỷ', 'Hòn non bộ mini phong thuỷ để bàn, thiết kế thủ công tinh xảo, phù hợp trang trí không gian nhỏ.', 580000.00, '/assets/images/products/lang-nghe-thu-cong/hon-non-bo-mini-de-ban-1231_300x300.jpg', 20, 398, 0.0, 0);
INSERT INTO `products` VALUES (141, 5, 'Hòn Non Bộ Mini Tiểu Cảnh Trang Trí', 'Hòn non bộ mini tiểu cảnh thủ công, kết hợp đá và cây trang trí, mang vẻ đẹp thiên nhiên thu nhỏ.', 620000.00, '/assets/images/products/lang-nghe-thu-cong/hon-non-bo-mini-tieu-canh-124_300x300.jpg', 15, 84, 0.0, 0);
INSERT INTO `products` VALUES (142, 6, 'Mũ Bảo Hộ Thi Đấu Twins HGL', 'Mũ bảo hộ thi đấu võ thuật Twins HGL, thiết kế ôm đầu, bảo vệ an toàn khi luyện tập và thi đấu.', 1650000.00, '/assets/images/products/van-hoa/competition-headguard-twins-hgl.jpg', 10, 194, 0.0, 0);
INSERT INTO `products` VALUES (143, 6, 'Găng Tay Tập Luyện Võ Thuật Fighter', 'Găng tay võ thuật dùng cho tập luyện và thi đấu, thiết kế chắc chắn, bảo vệ tay hiệu quả.', 720000.00, '/assets/images/products/van-hoa/gang-tay-vo-thuat-fighter.jpg', 18, 221, 0.0, 0);
INSERT INTO `products` VALUES (144, 6, 'Găng Fighter Thi Đấu Cao Cấp Xanh Đỏ', 'Găng tay Fighter dùng cho thi đấu võ cổ truyền, màu xanh đỏ theo chuẩn thi đấu.', 850000.00, '/assets/images/products/van-hoa/gang-fighter-thi-dau-xanh-do.jpg', 12, 24, 0.0, 0);
INSERT INTO `products` VALUES (145, 6, 'Quần Áo Thi Đấu Võ Cổ Truyền Fighter', 'Trang phục thi đấu võ cổ truyền Việt Nam, thiết kế truyền thống, phù hợp biểu diễn và thi đấu.', 480000.00, '/assets/images/products/van-hoa/quan-ao-thi-dau-vo-co-truyen-fighter.jpg', 20, 429, 0.0, 0);
INSERT INTO `products` VALUES (146, 6, 'Găng Tay Size Gang 01', 'Găng tay võ thuật kích thước Size Gang 01, phù hợp tập luyện và thi đấu võ thuật cơ bản.', 650000.00, '/assets/images/products/van-hoa/size_gang-01-01-01.jpg', 22, 101, 0.0, 0);
INSERT INTO `products` VALUES (147, 6, 'Găng Tay Võ Thuật 463886442247', 'Găng tay võ thuật chất lượng cao, thích hợp tập luyện đa môn võ.', 780000.00, '/assets/images/products/van-hoa/463886442247.jpg', 20, 188, 0.0, 0);
INSERT INTO `products` VALUES (148, 6, 'Giáp Tân Thủ Wesing', 'Giáp bảo hộ tân thủ Wesing, bảo vệ ngực và sườn khi bắt đầu học võ.', 1100000.00, '/assets/images/products/van-hoa/giap-tan-thu-wesing.jpg', 12, 140, 0.0, 0);
INSERT INTO `products` VALUES (149, 6, 'Mũ Thi Đấu Fighter', 'Mũ bảo hộ thi đấu Fighter, thiết kế ôm đầu, giảm lực va chạm.', 1250000.00, '/assets/images/products/van-hoa/mu-thi-dau-u-fighter.jpg', 14, 126, 0.0, 0);
INSERT INTO `products` VALUES (150, 6, 'Mũ Thi Đấu Twins HGZ-4 Boxing', 'Mũ thi đấu Twins HGZ-4 phù hợp Boxing, Muay và Kickboxing võ cổ truyền.', 1400000.00, '/assets/images/products/van-hoa/mu-thi-dau-twins-hgz-4.jpg', 16, 199, 0.0, 0);
INSERT INTO `products` VALUES (151, 6, 'Găng Tay Võ Thuật 16350842860', 'Găng tay võ thuật chất lượng, bảo vệ bàn tay khi tập luyện và thi đấu đa môn võ.', 880000.00, '/assets/images/products/van-hoa/16350842860.jpg', 20, 120, 0.0, 0);
INSERT INTO `products` VALUES (152, 6, 'Găng Bảo Hộ CH2 Blue 0.8', 'Găng bảo hộ CH2 màu xanh, kích thước 0.8, hỗ trợ bảo vệ tay tối ưu khi tập luyện.', 950000.00, '/assets/images/products/van-hoa/ch2-blue-0-8-1.jpg', 18, 483, 0.0, 0);
INSERT INTO `products` VALUES (153, 6, 'Găng Tay Võ Thuật 90172033882', 'Găng tay võ thuật đa năng, độ bền cao và ôm tay tốt cho mọi cấp độ võ sĩ.', 820000.00, '/assets/images/products/van-hoa/90172033882.jpg', 22, 84, 0.0, 0);
INSERT INTO `products` VALUES (154, 6, 'Găng Tay RX105453', 'Găng tay võ thuật RX105453 với thiết kế ôm sát và đệm bảo vệ cao cấp.', 910000.00, '/assets/images/products/van-hoa/rx105453.jpg', 16, 430, 0.0, 0);
INSERT INTO `products` VALUES (155, 6, 'Dịch Chuyển Mũ Võ Cổ Truyền', 'Mũ võ cổ truyền dùng cho tập luyện và biểu diễn, thiết kế truyền thống, bền đẹp.', 520000.00, '/assets/images/products/van-hoa/d_i_ch_y_nh_m_v__c__truy_n.jpg', 25, 421, 0.0, 0);
INSERT INTO `products` VALUES (156, 6, 'Đinh Bảnh Mũ Võ Cổ Truyền', 'Mũ võ cổ truyền có họa tiết đinh bảnh, phù hợp võ thuật biểu diễn và tập luyện.', 540000.00, '/assets/images/products/van-hoa/dinh_ba_nh_m_v__c__truy_n.png', 22, 316, 0.0, 0);
INSERT INTO `products` VALUES (158, 6, 'Ph Mũ Nh Mũ Võ Cổ Truyền', 'Mũ võ cổ truyền phong cách Ph, thích hợp luyện tập và biểu diễn võ thuật dân tộc.', 510000.00, '/assets/images/products/van-hoa/ph__nh_m_v__c__truy_n.jpg', 24, 77, 0.0, 0);
INSERT INTO `products` VALUES (160, 6, 'Liễu Đao Nhôm Võ Cổ Truyền PT Chấm Rồng Phương', 'Liễu đao nhôm thiết kế truyền thống, dùng cho võ cổ truyền và biểu diễn.', 2800000.00, '/assets/images/products/van-hoa/lieu-dao-nhom-vo-co-truyen-pt-cham-rong-phuong.png', 8, 497, 0.0, 0);
INSERT INTO `products` VALUES (161, 6, 'Kiếm Nhôm Võ Cổ Truyền PT', 'Kiếm nhôm võ thuật cổ truyền PT, chất liệu bền, dùng cho biểu diễn và luyện tập.', 2500000.00, '/assets/images/products/van-hoa/kiem-nhom-vo-co-truyen-pt.jpg', 10, 171, 0.0, 0);
INSERT INTO `products` VALUES (162, 6, 'Đại Đao Nhôm Võ Cổ Truyền Vovinam PT', 'Đại đao nhôm Vovinam PT, vũ khí truyền thống dùng cho luyện tập và biểu diễn.', 3200000.00, '/assets/images/products/van-hoa/dai-dao-nhom-vo-co-truyen-vovinam-pt.jpg', 7, 336, 0.0, 0);
INSERT INTO `products` VALUES (163, 6, 'Trưởng Côn Võ Cổ Truyền', 'Trưởng côn truyền thống dùng trong võ cổ truyền, thích hợp luyện tập và biểu diễn.', 2700000.00, '/assets/images/products/van-hoa/truong-con-vo-co-truyen.jpg', 9, 176, 0.0, 0);
INSERT INTO `products` VALUES (164, 6, 'Thương Nhôm Võ Cổ Truyền', 'Thương nhôm truyền thống, dùng cho tập luyện và biểu diễn võ thuật cổ truyền.', 2950000.00, '/assets/images/products/van-hoa/thuong-nhom-vo-co-truyen.jpg', 6, 352, 0.0, 0);

-- ----------------------------
-- Table structure for promotions
-- ----------------------------
DROP TABLE IF EXISTS `promotions`;
CREATE TABLE `promotions`  (
                               `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
                               `product_id` int UNSIGNED NOT NULL,
                               `discount_percent` int NOT NULL,
                               `start_date` datetime NULL DEFAULT NULL,
                               `end_date` datetime NULL DEFAULT NULL,
                               PRIMARY KEY (`id`) USING BTREE,
                               INDEX `fk_promotion_product`(`product_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 102 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = FIXED;

-- ----------------------------
-- Records of promotions
-- ----------------------------
INSERT INTO `promotions` VALUES (1, 1, 20, '2026-01-01 00:00:00', '2026-03-10 23:59:59');
INSERT INTO `promotions` VALUES (2, 110, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (3, 2, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (4, 3, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (5, 4, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (6, 5, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (7, 6, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (8, 7, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (9, 8, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (10, 9, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (11, 10, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (12, 11, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (13, 12, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (14, 13, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (15, 14, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (16, 15, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (17, 16, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (18, 17, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (19, 18, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (20, 19, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (21, 20, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (22, 21, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (23, 22, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (24, 23, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (25, 24, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (26, 25, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (27, 26, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (28, 27, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (29, 28, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (30, 29, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (31, 30, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (32, 31, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (33, 32, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (34, 33, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (35, 34, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (36, 35, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (37, 36, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (38, 37, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (39, 38, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (40, 39, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (41, 40, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (42, 41, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (43, 42, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (44, 43, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (45, 44, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (46, 45, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (47, 46, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (48, 47, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (49, 48, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (50, 49, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (51, 50, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (52, 51, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (53, 52, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (54, 53, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (55, 54, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (56, 55, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (57, 56, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (58, 57, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (59, 58, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (60, 59, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (61, 60, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (62, 61, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (63, 62, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (64, 63, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (65, 64, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (66, 65, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (67, 66, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (68, 67, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (69, 68, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (70, 69, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (71, 70, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (72, 71, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (73, 72, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (74, 73, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (75, 74, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (76, 75, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (77, 76, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (78, 77, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (79, 78, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (80, 79, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (81, 80, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (82, 81, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (83, 82, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (84, 83, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (85, 84, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (86, 85, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (87, 86, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (88, 87, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (89, 88, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (90, 89, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (91, 90, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (92, 91, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (93, 92, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (94, 93, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (95, 94, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (96, 95, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (97, 96, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (98, 97, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (99, 98, 20, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (100, 99, 10, '2026-01-28 00:00:00', '2026-02-28 23:59:59');
INSERT INTO `promotions` VALUES (101, 100, 15, '2026-01-28 00:00:00', '2026-02-28 23:59:59');

-- ----------------------------
-- Table structure for reviews
-- ----------------------------
DROP TABLE IF EXISTS `reviews`;
CREATE TABLE `reviews`  (
                            `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
                            `product_id` int UNSIGNED NOT NULL,
                            `user_id` int UNSIGNED NOT NULL,
                            `rating` tinyint NULL DEFAULT NULL,
                            `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
                            `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`) USING BTREE,
                            INDEX `fk_review_product`(`product_id`) USING BTREE,
                            INDEX `fk_review_user`(`user_id`) USING BTREE,
                            CONSTRAINT `reviews_chk_1` CHECK (`rating` between 1 and 5)
) ENGINE = MyISAM AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of reviews
-- ----------------------------
INSERT INTO `reviews` VALUES (1, 1, 1, 5, 'Sản phẩm rất ngon, đúng chuẩn đặc sản Bình Định, đóng gói cẩn thận.', '2026-01-25 15:12:44');
INSERT INTO `reviews` VALUES (2, 1, 1, 4, 'Chất lượng tốt, vị đậm đà, giao hàng nhanh.', '2026-01-25 15:12:44');
INSERT INTO `reviews` VALUES (3, 1, 1, 5, 'Rất hài lòng, sẽ tiếp tục ủng hộ lần sau.', '2026-01-25 15:12:44');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`
(
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,

    `full_name` VARCHAR(100) NOT NULL,

    `email` VARCHAR(191) NOT NULL,

    `password` VARCHAR(255) NOT NULL,

    `phone` VARCHAR(20) NOT NULL,

    `gender` ENUM('Male','Female','Other') NULL DEFAULT NULL,

    `dob` DATE NULL DEFAULT NULL,

    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    `updated_at` DATETIME NOT NULL
        DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    `avatar` VARCHAR(255) NULL DEFAULT NULL,

    `status` ENUM('Active','Inactive','Banned')
        NOT NULL DEFAULT 'Active',

    `reset_token` VARCHAR(255) NULL DEFAULT NULL,

    `token_expiry` DATETIME NULL DEFAULT NULL,

    PRIMARY KEY (`id`),

    UNIQUE KEY `uq_users_email` (`email`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of users
-- ----------------------------


CREATE TABLE IF NOT EXISTS user_credentials
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id            INT UNSIGNED NOT NULL UNIQUE,
    password_hash      VARCHAR(255) NOT NULL,
    email_verified     BOOLEAN      NOT NULL DEFAULT FALSE,
    verification_token VARCHAR(64),
    reset_token        VARCHAR(64),
    reset_expires_at   TIMESTAMP,
    created_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at         TIMESTAMP    NULL,
    CONSTRAINT fk_cred_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS oauth_accounts
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          INT UNSIGNED NOT NULL,
    provider         VARCHAR(30)  NOT NULL,
    provider_user_id VARCHAR(128) NOT NULL,
    provider_email   VARCHAR(255),
    token_expires_at TIMESTAMP,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at       TIMESTAMP    NULL,
    CONSTRAINT fk_oauth_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_provider_account UNIQUE (provider, provider_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sessions
(
    session_id VARCHAR(128) PRIMARY KEY,
    user_id    INT UNSIGNED    NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT fk_sess_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_sess_user (user_id),
    INDEX idx_sess_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS roles
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    is_system   BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP   NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS permissions
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    resource    VARCHAR(50) NOT NULL,
    action      VARCHAR(30) NOT NULL,
    description VARCHAR(255),
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP   NULL,
    CONSTRAINT uq_perm UNIQUE (resource, action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS role_permissions
(
    role_id       INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role
        FOREIGN KEY (role_id)
            REFERENCES roles (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_rp_perm
        FOREIGN KEY (permission_id)
            REFERENCES permissions (id)
            ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_roles
(
    user_id     INT UNSIGNED NOT NULL,
    role_id     INT NOT NULL,
    assigned_by INT UNSIGNED,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_ur_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_ur_role
        FOREIGN KEY (role_id)
            REFERENCES roles (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_ur_assigned_by
        FOREIGN KEY (assigned_by)
            REFERENCES users(id)
            ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- SEED PERMISSIONS
INSERT IGNORE INTO permissions (resource, action, description)
VALUES
    ('dashboard', 'read', 'View admin dashboard'),

    ('product', 'read', 'View products'),
    ('product', 'create', 'Create products'),
    ('product', 'update', 'Update products'),
    ('product', 'delete', 'Delete products'),

    ('category', 'read', 'View categories'),
    ('category', 'create', 'Create categories'),
    ('category', 'update', 'Update categories'),
    ('category', 'delete', 'Delete categories'),

    ('order', 'read', 'View orders'),
    ('order', 'update', 'Update orders'),

    ('customer', 'read', 'View customers'),
    ('customer', 'create', 'Create customers'),
    ('customer', 'update', 'Update customers'),
    ('customer', 'delete', 'Delete customers'),

    ('banner', 'read', 'View banners'),
    ('banner', 'create', 'Create banners'),
    ('banner', 'update', 'Update banners'),
    ('banner', 'delete', 'Delete banners'),

    ('settings', 'read', 'View settings'),
    ('settings', 'update', 'Update settings'),

    ('role', 'read', 'View permission groups'),
    ('role', 'create', 'Create permission groups'),
    ('role', 'update', 'Update permission groups'),
    ('role', 'delete', 'Delete permission groups');

-- SEED ROLES
INSERT IGNORE INTO roles (name, description, is_system)
VALUES
    ('Super Admin', 'Full system access including role management', TRUE),
    ('Admin', 'Administrative access to all features', TRUE),
    ('Sales', 'Sales management access (products, orders, customers)', TRUE),
    ('User', 'Basic user access', TRUE),
    ('Customer', 'Default customer account', TRUE);

-- SUPER ADMIN GETS ALL PERMISSIONS
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'Super Admin';

-- ADMIN GETS ALL PERMISSIONS EXCEPT ROLE MANAGEMENT
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON 1=1
WHERE r.name = 'Admin'
AND NOT (p.resource = 'role');

-- SALES ROLE - PRODUCT, ORDER, CUSTOMER, DASHBOARD
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON 1=1
WHERE r.name = 'Sales'
AND p.resource IN ('dashboard', 'product', 'order', 'customer')
AND (p.resource = 'dashboard' OR p.action IN ('read', 'update'));

-- USER ROLE - BASIC ACCESS
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON 1=1
WHERE r.name = 'User'
AND p.resource = 'dashboard'
AND p.action = 'read';

-- ASSIGN CUSTOMER ROLE TO USERS WITHOUT ROLE
INSERT IGNORE INTO user_roles (user_id, role_id, assigned_by)
SELECT
    u.id,
    r.id,
    NULL
FROM users u
JOIN roles r
    ON r.name = 'Customer'
LEFT JOIN user_roles ur
    ON ur.user_id = u.id
WHERE ur.user_id IS NULL;
SET FOREIGN_KEY_CHECKS = 1;