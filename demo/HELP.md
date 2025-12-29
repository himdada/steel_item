# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.1/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.0.1/maven-plugin/build-image.html)
* [MongoDB](https://docs.spring.io/spring-boot/4.0.1/reference/data/nosql.html#data.nosql.mongodb)
* [Spring Web](https://docs.spring.io/spring-boot/4.0.1/reference/web/servlet.html)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the
parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.


## Excel 导入 API 使用说明

- 路径: `/api/steel-items/upload`
- 方法: `POST`
- Content-Type: `multipart/form-data`
- 参数: `file` (Excel 文件，支持 `.xlsx` 和 `.xls`)
- 返回: `{ "importedCount": <成功导入条数> }`

### 表头约定
Excel 第一行需要作为表头，列名可使用如下中文或英文字段名（大小写不敏感）：

- 类别 / `category`
- 品名 / `productName`
- 型号 / `model`
- 每米重量 / `weightPerMeter`
- 长度mm / `lengthMm`
- 规格1 / `spec1`
- 规格2 / `spec2`
- 规格3 / `spec3`
- 规格4 / `spec4`
- 规格5 / `spec5`
- 单位 / `unit`
- 材质 / `material`
- 标准 / `standard`
- 品牌 / `brand`
- 产地 / `origin`
- 价格1 / `price1`
- 价格2 / `price2`
- 价格3 / `price3`
- 价格4 / `price4`
- 价格5 / `price5`
- 计算方式 / `calcMode`
- 库存 / `inventory`
- 预测变化 / `forecastChange`
- 联系人 / `contact`
- 供货价 / `supplyPrice`
- 差价 / `diffPrice`

未识别的表头会被忽略。数值列支持数字或可解析的字符串。

### 示例 `curl`

```bash
curl -X POST "http://localhost:8080/api/steel-items/upload" \
	-H "Content-Type: multipart/form-data" \
	-F "file=@D:/path/to/steel-items.xlsx"
```

