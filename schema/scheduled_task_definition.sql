SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for scheduled_task_definition
-- ----------------------------
DROP TABLE IF EXISTS `scheduled_task_definition`;
CREATE TABLE `scheduled_task_definition`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `bean_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `registry_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_registry_key` (`registry_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of scheduled_task_definition
-- ----------------------------
INSERT INTO `scheduled_task_definition` VALUES (1, 'report-task', '0/5 * * * * ? ', 'dynamic-schedule-1', '动态定时任务：统计报表');
INSERT INTO `scheduled_task_definition` VALUES (2, 'cleanup-task', '0/10 * * * * ? ', 'dynamic-schedule-2', '动态定时任务：数据清理');
SET FOREIGN_KEY_CHECKS = 1;