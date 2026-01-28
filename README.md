# sludge_system

管网污泥近红外光谱系统后端（Spring Boot）。

## 功能概览
- 采样点管理
- 光谱样本管理（含 QC 校验）
- 理化标签
- 文件导入（CSV/JSON）
- 预测与预测历史（当前为 mock）

## 环境要求
- JDK 17
- MySQL 8

## 本地运行
1) 初始化数据库

```
db/init.sql
```

2) 配置数据库连接

`src/main/resources/application.yml`

3) 编译与启动

```
mvnw.cmd clean compile
mvnw.cmd spring-boot:run
```

## 接口文档
详见 `BACKEND_API.md`。

## 测试报告
详见 `API_TEST_REPORT_ZH.md`（中文测试报告）。
