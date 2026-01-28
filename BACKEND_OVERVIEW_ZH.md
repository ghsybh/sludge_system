# 后端项目说明

## 项目概览
- 技术栈: Java 17 / Spring Boot 3.2 / JPA / MySQL 8
- 功能: 采样点、光谱样本、理化标签、校验、导入、预测、历史

## 目录结构
- controller: 接口层
- service: 业务逻辑
- repository: 数据库访问
- domain: 实体模型
- dto: 请求/响应结构
- validation: 光谱校验
- application.yml: 配置
- db/init.sql: 数据库结构

## 核心流程
- 采样点与光谱样本关联
- 删除采样点会自动清理关联数据
- 预测目前为 mock，结果写入预测历史

## 运行与配置
- 配置文件: src/main/resources/application.yml
- 运行命令: mvnw.cmd spring-boot:run

## 老师接口下发后的修改位置
1) PredictRequest: src/main/java/com/sludge_system/dto/PredictRequest.java
2) PredictResponse: src/main/java/com/sludge_system/dto/PredictResponse.java
3) PredictionService: src/main/java/com/sludge_system/service/PredictionService.java
4) PredictionController: src/main/java/com/sludge_system/controller/PredictionController.java
5) BACKEND_API.md 更新文档
6) db/init.sql 和相关实体类如 PredictionHistory
