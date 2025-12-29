# 钢材市场分析平台（Spring Boot + MongoDB）

一个包含前后端一体的简单钢材信息管理与导入示例项目：
- 后端：Spring Boot 4（Java 21）、MongoDB、分页检索、条件过滤、CRUD、Excel/CSV 导入
- 前端：内置单页页面（静态资源）用于查询、编辑与文件上传

## 功能特性
- 钢材条目管理：创建、修改、删除、按条件分页查询
- 多条件筛选：类型、品名、型号、规格、地区、价格区间、是否显示等
- 文件导入：支持 `.xlsx/.xls/.csv` 批量导入
- 数据初始化：首次运行自动注入少量演示数据

## 运行环境
- Java 21（建议安装 JDK 21）
- Maven（本项目已提供 `mvnw/mvnw.cmd`，可不安装全局 Maven）
- MongoDB（两种模式）
  - external：默认使用本地 MongoDB（`mongodb://localhost:27017/steel`）
  - embedded：使用内存 Mongo（适合快速体验与本地开发，无需安装 MongoDB）

## 快速开始
### 1) 构建
Windows PowerShell：
```powershell
cd d:\steel\demo
.\u006d
```

或使用更详细输出：
```powershell
.\u006d -U clean package
```

构建成功会生成 `target/demo-0.0.1-SNAPSHOT.jar`。

### 2) 启动（默认 external 模式）
```powershell
java -jar target\demo-0.0.1-SNAPSHOT.jar
```
启动后访问：
- 页面：<http://localhost:8080/>
- API：<http://localhost:8080/api/steel-items>

### 3) 启动（embedded 内存 Mongo 模式）
无需安装 MongoDB，适合快速体验：
```powershell
.\u006d spring-boot:run -Dspring-boot.run.profiles=embedded
```
或使用可执行 JAR：
```powershell
java -Dspring.profiles.active=embedded -jar target\demo-0.0.1-SNAPSHOT.jar
```

> 当前默认激活配置为 external（见 `src/main/resources/application.properties`）。

## 配置
主要配置文件：
- 运行时激活：`spring.profiles.active`
  - 默认：external（见 [demo/src/main/resources/application.properties](demo/src/main/resources/application.properties)）
- external 模式（本地 MongoDB）：
  - [demo/src/main/resources/application-external.properties](demo/src/main/resources/application-external.properties)
  - 关键项：
    - `spring.data.mongodb.uri=mongodb://localhost:27017/steel`
    - `spring.servlet.multipart.max-file-size=100MB`
    - `spring.servlet.multipart.max-request-size=120MB`
- embedded 模式（内存 Mongo）：
  - [demo/src/main/resources/application-embedded.properties](demo/src/main/resources/application-embedded.properties)
  - Java 配置类：[demo/src/main/java/com/example/demo/config/EmbeddedMongoConfig.java](demo/src/main/java/com/example/demo/config/EmbeddedMongoConfig.java)

## API 概览
基路径：`/api/steel-items`

- 分页查询
  - `GET /api/steel-items?page=0&size=10&sortBy=updatedAt&direction=DESC&category=型材&productName=工字钢...`
- 获取详情
  - `GET /api/steel-items/{id}`
- 创建
  - `POST /api/steel-items`（JSON）
- 更新
  - `PUT /api/steel-items/{id}`（JSON）
- 删除
  - `DELETE /api/steel-items/{id}`
- 导入文件
  - `POST /api/steel-items/upload`（`multipart/form-data`，字段名 `file`，支持 `xlsx/xls/csv`）

示例：创建一条数据
```powershell
curl.exe -s -i -H "Content-Type: application/json" \
  -d "{\"category\":\"型材\",\"productName\":\"工字钢\",\"model\":\"10#\"}" \
  http://localhost:8080/api/steel-items
```

示例：上传 CSV
```powershell
curl.exe -s -i -X POST -F "file=@d:\\steel\\sample.csv;type=text/csv" \
  http://localhost:8080/api/steel-items/upload
```

## 数据导入说明（Excel/CSV）
- 第一行必须为表头（用于字段映射）；忽略未知列
- CSV 建议 UTF-8 编码
- 常用表头与字段映射（部分）：
  - 类别→`category`，品名→`productName`，型号→`model`
  - 规格1→`spec1`，规格2→`spec2` … 规格5→`spec5`
  - 单位→`unit`，材质→`material`，执行标准→`standard`，品牌/厂家→`brand`
  - 省→`province`，市→`city`，区→`district`
  - 默认价格/元/吨→`price1`（`price2`~`price5` 同理）
  - 过磅/理计→`calcMode`，库存→`inventory`
  - 供货价→`supplyPrice`，差价→`diffPrice`，备注→`remark`，是否显示→`visible`

最小 CSV 示例：
```csv
类别,品名,型号,规格1,单位,材质,标准,品牌,产地,省,市,区,价格1,计算方式,库存,联系人,供货价,差价,备注,是否显示
型材,工字钢,10#,10x100,吨,Q235B,GB/T700,宝钢,上海,上海市,上海市,浦东新区,4500,过磅,100,张三,4300,200,测试导入,true
```

## 前端页面
- 构建后内置于 JAR，访问 <http://localhost:8080/>
- 「导入 Excel」按钮支持 `.xlsx/.xls/.csv`，字段名 `file`

## 常见问题（FAQ）
- 413 或上传失败（文件过大）
  - 增大上传限制：[demo/src/main/resources/application-external.properties](demo/src/main/resources/application-external.properties)
    - `spring.servlet.multipart.max-file-size`
    - `spring.servlet.multipart.max-request-size`
  - 或按批拆分上传
- 500 且提示解析失败
  - 确保首行是表头、CSV 编码为 UTF-8、数值列格式正确
- 构建时报目标 JAR 无法删除/重命名
  - 请先停止正在运行的旧进程（占用 `target/demo-0.0.1-SNAPSHOT.jar` 的 Java 进程）
- 没有安装 MongoDB
  - 使用 embedded 模式启动（内存 Mongo）

## 开发辅助
- 运行单元测试
```powershell
.\u006d -q test
```
- 开发模式运行（自动加载变更）
```powershell
.\u006d spring-boot:run
# 或指定内存 Mongo
. spring-boot:run -Dspring-boot.run.profiles=embedded
```

---
如需我帮助准备示例数据文件或 API 调试脚本，请告诉我你的具体需求与环境（external/embedded）。
