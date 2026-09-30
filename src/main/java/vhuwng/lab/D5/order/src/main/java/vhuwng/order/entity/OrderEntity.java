package vhuwng.order.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32, unique = true)
    private String code;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OrderStatus status;

    @Column(name = "discount_percent", precision = 5, scale = 2, nullable = false)
    private BigDecimal discountPercent;

    @Column(name = "tax_percent", precision = 5, scale = 2, nullable = false)
    private BigDecimal taxPercent;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    @Column(name = "fulfilled_at")
    private OffsetDateTime fulfilledAt;

    @Column(name = "cancelled_at")
    private OffsetDateTime cancelledAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("lineNo ASC")
    private List<OrderLineEntity> lines = new ArrayList<>();

    protected OrderEntity() {
    }

    public OrderEntity(
            Long id,
            String code,
            Long customerId,
            String customerName,
            Long createdBy,
            OrderStatus status,
            BigDecimal discountPercent,
            BigDecimal taxPercent,
            String currency,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            OffsetDateTime paidAt,
            OffsetDateTime fulfilledAt,
            OffsetDateTime cancelledAt
    ) {
        this.id = id;
        this.code = code;
        this.customerId = customerId;
        this.customerName = customerName;
        this.createdBy = createdBy;
        this.status = status;
        this.discountPercent = discountPercent;
        this.taxPercent = taxPercent;
        this.currency = currency;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.paidAt = paidAt;
        this.fulfilledAt = fulfilledAt;
        this.cancelledAt = cancelledAt;
    }

    public void addLine(OrderLineEntity line) {
        lines.add(line);
        line.attachTo(this);
    }

    public void changeStatus(OrderStatus newStatus, OffsetDateTime changedAt) {
        this.status = newStatus;
        this.updatedAt = changedAt;
        switch (newStatus) {
            case PAID -> this.paidAt = changedAt;
            case FULFILLED -> {
                this.paidAt = changedAt;
                this.fulfilledAt = changedAt;
            }
            case CANCELLED -> this.cancelledAt = changedAt;
            case DRAFT, PENDING_PAYMENT -> {
                // No status timestamp is recorded for these states.
            }
        }
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    public String getCurrency() {
        return currency;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public OffsetDateTime getPaidAt() {
        return paidAt;
    }

    public OffsetDateTime getFulfilledAt() {
        return fulfilledAt;
    }

    public OffsetDateTime getCancelledAt() {
        return cancelledAt;
    }

    public List<OrderLineEntity> getLines() {
        return lines;
    }
}
