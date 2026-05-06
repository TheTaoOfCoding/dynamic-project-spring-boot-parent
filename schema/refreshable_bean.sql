SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for refreshable_bean
-- ----------------------------
DROP TABLE IF EXISTS `refreshable_bean`;
CREATE TABLE `refreshable_bean` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `bean_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'spring bean 名字',
    `script` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'groovy 脚本',
    `description` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '描述信息',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_bean_name` (`bean_name`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of refresh_bean
-- ----------------------------
INSERT INTO `refreshable_bean` VALUES (1, 'runnable-task', 'return { param -> println "Runnable running ..." } as SAM', '任务型接口示例');
INSERT INTO `refreshable_bean` VALUES (2, 'consumer-task', 'return { param -> println "Hello, $param" } as SAM', '消费型接口示例');
INSERT INTO `refreshable_bean` VALUES (3, 'supplier-task', 'return { param -> "TheTaoOfCoding"} as SAM', '供给型接口示例');
INSERT INTO `refreshable_bean` VALUES (4, 'function-task', 'return { str -> str.length() } as SAM', '函数型接口示例');
INSERT INTO `refreshable_bean` VALUES (5, 'predicate-task', 'return { name -> name == "TheTaoOfCoding" } as SAM', '断言型接口示例');
INSERT INTO `refreshable_bean` VALUES (6, 'run-4-ioc', 'import javax.sql.DataSource; return { param -> println ioc.getBean(DataSource.class) } as SAM', '使用内置对象 ioc 查找依赖示例');
INSERT INTO `refreshable_bean` VALUES (7, 'run-4-locals', 'return { param -> println "locals.get() = ${locals.get()}, in groovy."; locals.remove(); } as SAM', '使用内置对象 locals 传递线程变量示例');

INSERT INTO `refreshable_bean` VALUES (101, 'report-task', 'return { param -> println "Generating monthly report..."} as SAM', '统计报表任务');
INSERT INTO `refreshable_bean` VALUES (102, 'cleanup-task', 'return { param -> println "Cleaning expired data..."} as SAM', '数据清理任务');
INSERT INTO `refreshable_bean` VALUES (103, 'print-a', 'return { param -> println "aaaaaa ..."} as SAM', '测试任务:打印a');
INSERT INTO `refreshable_bean` VALUES (104, 'print-b', 'return { param -> println "bbbbbb ..."} as SAM', '测试任务:打印b');

SET FOREIGN_KEY_CHECKS = 1;