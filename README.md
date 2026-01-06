# 钢材信息管理（Spring Boot + MongoDB）

一个围绕钢材条目的全栈示例：提供分页检索、条件过滤、CRUD、Excel/CSV 批量导入、Excel 导出，并内置静态页面用于快速体验。

## 技术栈
- Java 21，Spring Boot 4.0.1
- Spring Web、Spring Data MongoDB、Validation
- Apache POI（Excel）、Commons CSV（CSV）
- 可选内存 Mongo（mongo-java-server）用于无安装体验
- 构建：Maven（附带 mvnw/mvnw.cmd）

## 项目结构
- 后端入口：com.example.demo.DemoApplication
- 控制器：/api/steel-items（CRUD、搜索、导入、导出）
- 服务：查询构建、导入/导出逻辑
- 模型：钢材实体、查询 DTO
- 配置：
  - 默认激活 external profile，使用本地 Mongo
  - 内存 Mongo 见 [demo/src/main/java/com/example/demo/config/EmbeddedMongoConfig.java](demo/src/main/java/com/example/demo/config/EmbeddedMongoConfig.java)
  - 属性文件：默认 [demo/src/main/resources/application.properties](demo/src/main/resources/application.properties)；外部 Mongo [demo/src/main/resources/application-external.properties](demo/src/main/resources/application-external.properties)；内存 Mongo [demo/src/main/resources/application-embedded.properties](demo/src/main/resources/application-embedded.properties)
- 前端：打包在 JAR 的 static/index.html

## 运行要求
- JDK 21
- Windows 上可直接使用 mvnw.cmd（无需全局 Maven）
- MongoDB：默认使用本地实例；若未安装可启用 embedded profile 使用内存 Mongo

## 快速开始
### 1) 构建
```powershell
cd d:\steel\demo
.\u006d.cmd clean package
```
产物：target/demo-0.0.1-SNAPSHOT.jar

### 2) 运行（默认 external，本地 Mongo）
```powershell
java -jar target\demo-0.0.1-SNAPSHOT.jar
```
- 页面：<http://localhost:8080/>
- API：<http://localhost:8080/api/steel-items>

### 3) 运行（embedded，内存 Mongo）
无需安装 MongoDB：
```powershell
java -Dspring.profiles.active=embedded -jar target\demo-0.0.1-SNAPSHOT.jar
```

## 配置要点
- 激活 profile：spring.profiles.active（默认 external）
- 外部 Mongo 连接在 [demo/src/main/resources/application-external.properties](demo/src/main/resources/application-external.properties)
  - spring.data.mongodb.uri=mongodb://localhost:27017/steel
  - 上传大小：spring.servlet.multipart.max-file-size / spring.servlet.multipart.max-request-size
- 内存 Mongo 属性在 [demo/src/main/resources/application-embedded.properties](demo/src/main/resources/application-embedded.properties)

## API 速览（基路径 /api/steel-items）
- 分页查询：GET /api/steel-items?page=0&size=10&sortBy=updatedAt&direction=DESC&category=型材&productName=工字钢...
- 导出 Excel：GET /api/steel-items/export（同查询参数）
- 获取详情：GET /api/steel-items/{id}
- 创建：POST /api/steel-items（JSON）
- 更新：PUT /api/steel-items/{id}（JSON）
- 删除：DELETE /api/steel-items/{id}
- 批量导入：POST /api/steel-items/upload（multipart/form-data，字段名 file，支持 .xlsx/.xls/.csv）

示例创建：
```powershell
curl.exe -H "Content-Type: application/json" ^
  -d "{\"category\":\"型材\",\"productName\":\"工字钢\",\"model\":\"10#\"}" ^
  http://localhost:8080/api/steel-items
```

示例上传 CSV：
```powershell
curl.exe -X POST -F "file=@d:\\steel\\sample.csv;type=text/csv" ^
  http://localhost:8080/api/steel-items/upload
```

## 导入文件格式
- 首行必须为表头；未知列自动忽略
- 推荐 UTF-8 编码的 CSV
- 常用表头映射（节选）：类别→category，品名→productName，型号→model，规格1~5→spec1~spec5，材质→material，标准→standard，品牌→brand，省/市/区→province/city/district，价格1→price1（价格2~5 同理），计算方式→calcMode，库存→inventory，供货价→supplyPrice，差价→diffPrice，备注→remark，是否显示→visible

最小 CSV 示例：
```csv
类别,品名,型号,规格1,单位,材质,标准,品牌,产地,省,市,区,价格1,计算方式,库存,联系人,供货价,差价,备注,是否显示
型材,工字钢,10#,10x100,吨,Q235B,GB/T700,宝钢,上海,上海市,上海市,浦东新区,4500,过磅,100,张三,4300,200,测试导入,true
```

## 前端页面
构建后随 JAR 提供，浏览器访问 <http://localhost:8080/> 可进行查询、编辑与导入。

## 开发与调试
- 开发模式（热加载）：
```powershell
..cmd spring-boot:run
# 内存 Mongo
.cmd spring-boot:run -Dspring-boot.run.profiles=embedded
```
- 运行测试：
```powershell
.cmd test
```

## 常见问题
- 上传 413/超限：调整 [demo/src/main/resources/application-external.properties](demo/src/main/resources/application-external.properties) 中的上传大小或拆分文件
- 解析错误 500：确保首行表头、UTF-8 编码、数值列格式正确
- 构建时报 JAR 被占用：先停止正在运行的旧进程
- 无 Mongo 环境：使用 embedded profile 运行

---

