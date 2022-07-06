/*
 Navicat Premium Data Transfer

 Source Server         : MYSQL
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : qiangke

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 30/06/2022 00:06:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for courses
-- ----------------------------
DROP TABLE IF EXISTS `courses`;
CREATE TABLE `courses`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '课程id',
  `courses_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '课程名称',
  `courses_title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '课程标题',
  `courses_img` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '课程封面',
  `courses_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '课程详情',
  `courses_module` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '课程模块',
  `courses_teacher` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任课老师',
  `courses_stock` int(0) NULL DEFAULT 0 COMMENT '课程余量',
  `courses_capacity` int(0) NULL DEFAULT NULL COMMENT '课程容量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '选课id',
  `student_id` bigint(0) NULL DEFAULT NULL COMMENT '学生id',
  `courses_id` bigint(0) NULL DEFAULT NULL COMMENT '课程id',
  `courses_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '课程名称',
  `create_date` datetime(0) NULL DEFAULT NULL COMMENT '选课时间',
  `status` tinyint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33327 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qiangke_courses
-- ----------------------------
DROP TABLE IF EXISTS `qiangke_courses`;
CREATE TABLE `qiangke_courses`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '抢课课程id',
  `courses_id` bigint(0) NULL DEFAULT NULL COMMENT '课程id',
  `stock_count` int(0) NULL DEFAULT NULL COMMENT '课程容量',
  `start_date` datetime(0) NULL DEFAULT NULL COMMENT '抢课开始时间',
  `end_date` datetime(0) NULL DEFAULT NULL COMMENT '抢课结束时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qiangke_order
-- ----------------------------
DROP TABLE IF EXISTS `qiangke_order`;
CREATE TABLE `qiangke_order`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '抢课课程id',
  `student_id` bigint(0) NULL DEFAULT NULL COMMENT '学生id',
  `order_id` bigint(0) NULL DEFAULT NULL COMMENT '记录id',
  `courses_id` bigint(0) NULL DEFAULT NULL COMMENT '课程id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33327 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qiangke_student
-- ----------------------------
DROP TABLE IF EXISTS `qiangke_student`;
CREATE TABLE `qiangke_student`  (
  `id` bigint(0) NOT NULL COMMENT '学生ID，学号',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '昵称',
  `password` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '暗文存储：MD5(MD5(pass明文, 固定SALT), 随机SALT)',
  `salt` char(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '盐，生成规则为salt+学号',
  `register_date` datetime(0) NULL DEFAULT NULL COMMENT '注册时间',
  `last_login_date` datetime(0) NULL DEFAULT NULL COMMENT '上一次登录时间',
  `login_count` int(0) NULL DEFAULT 0 COMMENT '登录次数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
