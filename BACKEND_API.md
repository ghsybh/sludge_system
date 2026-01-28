# 管网污泥近红外光谱系统｜后端设计与接口说明

本文档用于说明当前后端的分层设计、数据模型与 REST API 接口规范，便于前端或联调使用。

## 1. 项目概览

- 技术栈：Java 17 / Spring Boot 3.2 / Spring Data JPA / MySQL 8
- 目标：为微信小程序提供采样点、光谱样本、理化标签、预测历史等服务
- 统一响应：所有接口返回统一结构

成功：

```json
{ "code": 0, "message": "OK", "data": { } }
```

失败：

```json
{ "code": 40001, "message": "BANDS_LENGTH_INVALID", "data": { "expected": 228, "actual": 200 } }
```

## 2. 分层架构

- Controller：参数校验、路由、响应封装
- Service：业务逻辑、事务
- Repository：JPA 数据访问
- Validation：光谱 QC 规则与输出
- DTO：请求/响应结构
- Exception：统一错误码与异常处理
- (预留) ModelClient：模型服务调用（当前为 mock）

## 3. 数据模型说明

### 3.1 SamplingSite（采样点）

- siteCode：唯一编码
- siteName：名称
- lat/lng：经纬度
- remark：备注

### 3.2 SpectrumSample（光谱样本）

- bands：228 波段数组（JSON）
- bandCount：波段数
- source：来源（如 import）
- capturedAt：采集时间
- qcStatus：PASS/WARN/FAIL
- qcMessage：QC 结果合并文本

### 3.3 ChemLabel（理化标签）

- sampleId：唯一关联 SpectrumSample
- organic/tn/tp：理化指标
- unit：单位

### 3.4 PredictionHistory（预测历史）

- taskType：classification/regression/both
- modelType：模型类型（如 svm）
- inputSampleId / inputBands
- predSiteCode / predOrganic / predTn / predTp
- metrics：指标 JSON

## 4. 关键业务规则

- 光谱 bands 长度必须为 228，否则 FAIL
- 不允许 NaN/Infinity
- 低方差、负值、极端值视为 WARN
- 删除采样点时会清理关联光谱、理化标签与预测历史
- 理化标签 sampleId 唯一

## 5. 错误码

- 40001 BANDS_LENGTH_INVALID
- 40002 BANDS_CONTAINS_NAN
- 40003 SITE_NOT_FOUND
- 40004 SPECTRUM_NOT_FOUND
- 40005 IMPORT_FORMAT_INVALID
- 40006 PREDICTION_NOT_FOUND
- 40901 SITE_HAS_SPECTRA_CANNOT_DELETE
- 50001 INTERNAL_ERROR

## 6. REST API

统一前缀：`/api`

### 6.1 采样点

新增采样点

```
POST /api/sites
```

```json
{
  "siteCode": "S001",
  "siteName": "一号井",
  "lat": 31.2304,
  "lng": 121.4737,
  "remark": "备注"
}
```

查询采样点（分页 + 关键词）

```
GET /api/sites?page=0&size=20&keyword=S0
```

采样点详情

```
GET /api/sites/{id}
```

修改采样点

```
PUT /api/sites/{id}
```

删除采样点

```
DELETE /api/sites/{id}
```

删除行为：自动清理该采样点关联的光谱样本、理化标签与预测历史。

### 6.2 光谱样本

新增光谱样本

```
POST /api/spectra
```

```json
{
  "siteId": 1,
  "bands": [0.1, 0.2, 0.3, "... 共 228 个数值"],
  "source": "import",
  "capturedAt": "2026-01-28T10:00:00",
  "label": { "organic": 12.3, "tn": 1.02, "tp": 0.33, "unit": "g/kg" }
}
```

返回示例（包含 QC）

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "id": 1001,
    "qcStatus": "PASS",
    "qcMessage": ""
  }
}
```

查询光谱（分页 + 条件）

```
GET /api/spectra?page=0&size=20&siteId=1&qcStatus=PASS
```

光谱详情

```
GET /api/spectra/{id}
```

修改光谱

```
PUT /api/spectra/{id}
```

删除光谱

```
DELETE /api/spectra/{id}
```

### 6.3 理化标签

新增/修改标签

```
PUT /api/spectra/{id}/label
```

```json
{ "organic": 12.3, "tn": 1.02, "tp": 0.33, "unit": "g/kg" }
```

获取标签

```
GET /api/spectra/{id}/label
```

### 6.4 光谱校验

```
POST /api/spectra/validate
```

```json
{ "bands": [0.1, 0.2, 0.3, "... 共 228 个数值"] }
```

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "valid": true,
    "qcStatus": "PASS",
    "errors": [],
    "warnings": []
  }
}
```

### 6.5 文件导入（CSV/JSON）

```
POST /api/spectra/import
```

表单字段：
- file：CSV 或 JSON
- siteId（可选）

返回示例

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "successCount": 56,
    "failCount": 4,
    "failures": [
      { "row": 12, "reason": "BANDS_LENGTH_INVALID" }
    ]
  }
}
```

CSV 推荐格式

```
siteCode,capturedAt,band_1,band_2,...,band_228,organic,tn,tp
```

### 6.6 预测与预测历史

预测（mock）

```
POST /api/predict
```

```json
{
  "taskType": "both",
  "model": "svm",
  "sampleId": 1001,
  "bands": null
}
```

或直接传 bands：

```json
{
  "taskType": "regression",
  "model": "svm",
  "bands": [0.1, 0.2, 0.3, "... 共 228 个数值"]
}
```

返回示例

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "classification": { "siteCode": "S012", "confidence": 0.82 },
    "regression": { "organic": 12.3, "tn": 1.02, "tp": 0.33, "unit": "g/kg" },
    "metrics": {}
  }
}
```

历史列表

```
GET /api/predictions?page=0&size=20&taskType=both
```

历史详情

```
GET /api/predictions/{id}
```

删除历史

```
DELETE /api/predictions/{id}
```

## 7. 运行与配置

配置文件：`src/main/resources/application.yml`

默认 MySQL 配置（可改）

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sludge_nir?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: root
```

## 8. 备注

- 当前预测接口为 mock，后续可替换为 ModelClient 调用 Python 服务
- `ddl-auto` 目前为 `validate`，需先创建表结构
