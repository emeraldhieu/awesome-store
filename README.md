# Awesome store

## API-first approach

Awesome store chooses [API First approach](https://swagger.io/resources/articles/adopting-an-api-first-approach/) using [Open API 3.0](https://swagger.io/specification/) and [Open API Maven Generator](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-maven-plugin) to boost API development and allow foreseeing how the product looks like. The generated code can be overriden via [Mustache templates](https://mustache.github.io/mustache.5.html) such as [data transfer object](https://github.com/emeraldhieu/vinci/blob/master/order/src/main/resources/templates/pojo.mustache). The REST API can be viewed via [Swagger UI](http://localhost:50001/swagger-ui/index.html).

## Message queue

Whenever a product is created, a message is sent to Kafka for other services to consume. This way other services will be notified of the newly created product.

## Schema registry

As Product's Kafka messages tend to evolve by development's needs, [Confluent Avro](https://docs.confluent.io/2.0.0/schema-registry/docs/intro.html) is used to version schemas of Kafka messages. Schemas are stored in Kafka's log files and indices are stored in Avro's in-memory storage. For example, OrderMessage's schema:
```
{
    "type": "record",
    "name": "ProductMessage",
    "namespace": "com.emeraldhieu.awesomestore.product",
    "fields":
    [
        {
            "name": "productId",
            "type": "string"
        }
    ]
}
```

## Database schema change management

[Liquibase](https://docs.liquibase.com/tools-integrations/springboot/springboot.html) supports revisioning, deploying and rolling back database changes. On top of that, it allows [initializing data from CSV](https://docs.liquibase.com/change-types/load-data.html) for demonstrative purpose.

## Product API

### 1) List products

```
GET /products
```

#### Request parameters (optional)

| Parameters   | Description   | Format                                   |
|--------------|---------------|------------------------------------------|
| `sortOrders` | Sort products | column1,direction&#124;column2,direction |

Some examples of `sortOrders`:
+ `createdAt,desc`
+ `updatedAt,desc|createdBy,asc`

#### Example

##### List products

```sh
curl --location 'http://localhost:50001/products?sortOrders=updatedAt%2Cdesc%7CcreatedBy%2Casc'
```

##### Response

```json
[
  {
    "id": "0a5eb04756f54776ac7752d3c8fae45b",
    "name": "spaghetti",
    "price": 3.14,
    "createdBy": "20825389f950461b8766c051b9182dd4",
    "createdAt": "2022-11-27T00:00:00",
    "updatedBy": "cca4806536fe4b218c12cdcde4d173df",
    "updatedAt": "2022-11-28T00:00:00"
  }
]
```

### 2) Create a product

```
POST /products
```

#### Request body

Required parameters

| Parameters | Type   | Description |
|------------|--------|-------------|
| `name`     | String | Name        |
| `price`    | Double | Price       |

#### Example

##### Create a product

```sh
curl --location 'http://localhost:50001/products' \
--header 'Content-Type: application/json' \
--data '{
    "name": "coke",
    "price": 123
}'
```

##### Response

```json
{
  "id": "a65944903bd94b1dabee196323542ed9",
  "name": "coke",
  "price": 123.0,
  "createdBy": "4b93f05a150d489d949abb71ec0d3c58",
  "createdAt": "2023-04-27T00:35:59.378757",
  "updatedBy": "87d708c4766f461d8fa718ce50249081",
  "updatedAt": "2023-04-27T00:35:59.378757"
}
```

### 3) Get a product

```
GET /products/<id>
```

#### Path parameters

| Parameters | Description | Type   |
|------------|-------------|--------|
| `id`       | Product ID  | String |

#### Example

##### Get a product

```sh
curl --location 'http://localhost:50001/products/a65944903bd94b1dabee196323542ed9'
```

##### Response

```json
{
  "id": "a65944903bd94b1dabee196323542ed9",
  "name": "coke",
  "price": 123.0,
  "createdBy": "4b93f05a150d489d949abb71ec0d3c58",
  "createdAt": "2023-04-27T00:35:59.378757",
  "updatedBy": "87d708c4766f461d8fa718ce50249081",
  "updatedAt": "2023-04-27T00:35:59.378757"
}
```

### 4) Delete a product

```
DELETE /products/<id>
```

#### Path parameters

| Parameters | Description | Type   |
|------------|-------------|--------|
| `id`       | Product ID  | String |

#### Example

##### Delete a product

```sh
curl --location --request DELETE 'http://localhost:50001/products/a65944903bd94b1dabee196323542ed9'
```

Response status is 204 with no content

## Resources

+ Postman collections: 