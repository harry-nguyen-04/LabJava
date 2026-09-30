# Controller flow

## 1. Controller gọi tới server khác

`Controller` nên gọi `Service`, sau đó `Service` gọi client để giao tiếp với server bên ngoài.

```mermaid
sequenceDiagram
    actor User
    participant C as OrderController
    participant S as OrderService
    participant RC as ResilientPricingClient
    participant HC as HttpPricingClient
    participant P as Pricing Server

    User->>C: GET /orders/{id}
    C->>S: getOrder(id)
    S->>RC: quote(pricingRequest)
    RC->>HC: quote(request)
    HC->>P: POST /api/pricing/quote
    P-->>HC: PricingQuoteResponse
    HC-->>RC: PricingQuoteResponse
    RC-->>S: PricingQuoteResponse
    S-->>C: OrderResponse
    C-->>User: HTTP 200 JSON
```

### Khi Pricing Server bị lỗi

```mermaid
sequenceDiagram
    actor User
    participant C as OrderController
    participant S as OrderService
    participant R as ResilientPricingClient
    participant P as Pricing Server

    User->>C: GET /orders/{id}
    C->>S: getOrder(id)
    S->>R: quote(request)
    R->>P: Gọi Pricing Server
    P-->>R: Timeout hoặc Error
    R->>R: Retry hoặc Circuit Breaker
    R-->>S: PricingQuoteResponse.pending()
    S-->>C: OrderResponse với pricingStatus=PENDING
    C-->>User: HTTP 200 JSON
```

## 2. Controller không gọi tới server khác

Controller chỉ gọi service nội bộ. Service lấy dữ liệu từ repository hoặc database trong cùng ứng dụng.

```mermaid
sequenceDiagram
    actor User
    participant C as OrderController
    participant S as OrderService
    participant R as OrderRepository
    participant DB as Database

    User->>C: GET /orders/{id}
    C->>S: getOrder(id)
    S->>R: findById(id)
    R->>DB: SELECT order
    DB-->>R: OrderEntity
    R-->>S: OrderEntity
    S->>S: Mapping Entity -> Response DTO
    S-->>C: OrderResponse
    C-->>User: HTTP 200 JSON
```

## Vai trò của từng thành phần

```text
Controller = nhận request và trả response
Service    = xử lý business flow
Client     = gọi server khác
Repository = truy cập database
DTO        = dữ liệu request/response
```

Controller không nên tự gọi `RestClient` trực tiếp. Flow nên là:

```text
Controller -> Service -> PricingClient -> Server bên ngoài
```
