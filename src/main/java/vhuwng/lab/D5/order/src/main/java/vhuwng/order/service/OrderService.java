package vhuwng.order.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vhuwng.order.client.PricingClient;
import vhuwng.order.dto.request.CreateOrderLineRequest;
import vhuwng.order.dto.request.CreateOrderRequest;
import vhuwng.order.dto.request.UpdateOrderStatusRequest;
import vhuwng.order.dto.response.OrderResponse;
import vhuwng.order.entity.OrderEntity;
import vhuwng.order.entity.OrderLineEntity;
import vhuwng.order.entity.OrderStatus;
import vhuwng.order.repository.OrderRepository;
import vhuwng.order.util.OrderUtil;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PricingClient pricingClient;

    public OrderService(OrderRepository orderRepository, PricingClient pricingClient) {
        this.orderRepository = orderRepository;
        this.pricingClient = pricingClient;
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> OrderUtil.toResponse(order, true, pricingClient))
                .toList();
    }

    public OrderResponse getOrderById(Long id) {
        return OrderUtil.toResponse(OrderUtil.findOrder(orderRepository, id), true, pricingClient);
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        var now = OrderUtil.now();
        OrderEntity order = new OrderEntity(
                null,
                request.code(),
                request.customerId(),
                request.customerName(),
                request.createdBy(),
                request.status(),
                BigDecimal.valueOf(request.discountPercent()),
                BigDecimal.valueOf(request.taxPercent()),
                request.currency(),
                now,
                now,
                null,
                null,
                null
        );

        for (CreateOrderLineRequest line : request.lines()) {
            order.addLine(new OrderLineEntity(
                    null,
                    line.lineNo(),
                    line.productId(),
                    line.sku(),
                    line.productName(),
                    Math.toIntExact(line.quantity()),
                    line.unitPrice()
            ));
        }

        return OrderUtil.toResponse(orderRepository.save(order), false, pricingClient);
    }

    public OrderResponse updateStatus(Long id, UpdateOrderStatusRequest request) {
        OrderEntity order = OrderUtil.findOrder(orderRepository, id);
        order.changeStatus(request.status(), OrderUtil.now());
        return OrderUtil.toResponse(orderRepository.save(order), false, pricingClient);
    }

    public OrderResponse cancelOrder(Long id) {
        OrderEntity order = OrderUtil.findOrder(orderRepository, id);
        order.changeStatus(OrderStatus.CANCELLED, OrderUtil.now());
        return OrderUtil.toResponse(orderRepository.save(order), false, pricingClient);
    }
}
