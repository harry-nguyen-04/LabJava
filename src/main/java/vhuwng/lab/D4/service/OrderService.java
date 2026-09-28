package vhuwng.lab.D4.service;

import org.springframework.stereotype.Service;
import vhuwng.lab.D4.common.OrderStatus;
import vhuwng.lab.D4.dto.request.CreateOrderRequestDto;
import vhuwng.lab.D4.dto.request.UpdateOrderStatusRequestDto;
import vhuwng.lab.D4.dto.response.CreateOrderResponseDto;
import vhuwng.lab.D4.dto.response.OrderResponseDto;
import vhuwng.lab.D4.dto.response.UpdateOrderStatusResponseDto;
import vhuwng.lab.D4.middleware.exception.InvalidStatusTransitionException;
import vhuwng.lab.D4.repository.IOrderRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderService {
    private final IOrderRepository orderRepository;

    public OrderService(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderResponseDto> getAllOrders(){
        return orderRepository.getAllOrders();
    }

    public OrderResponseDto getOrderById(int id){
        return orderRepository.getOrderById(id);
    }

    public CreateOrderResponseDto createOrder(CreateOrderRequestDto request){
        int id = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);

        return new CreateOrderResponseDto(
                id,
                request.code(),
                request.customerId(),
                request.customerName(),
                request.createdBy(),
                request.status(),
                request.lines(),
                request.subtotal(),
                request.discountPercent(),
                request.discountAmount(),
                request.taxPercent(),
                request.taxAmount(),
                request.total(),
                request.currency(),
                request.createdAt(),
                request.updatedAt(),
                request.paidAt(),
                request.fulfilledAt(),
                request.cancelledAt()
        );
    }

    public UpdateOrderStatusResponseDto updateOrderStatus(int id, UpdateOrderStatusRequestDto request){
        OrderResponseDto order = orderRepository.getOrderById(id);
        OrderStatus newStatus = request.status();
        if(order.status().equals(OrderStatus.PAID) && newStatus.equals(OrderStatus.DRAFT)){
            throw new InvalidStatusTransitionException(
                    "Cannot change status from "
                            + order.status()
                            + " to "
                            + newStatus
            );
        }

        String now = OffsetDateTime.now(ZoneOffset.ofHours(7)).toString();
        String paidAt = order.paidAt();
        String fulfilledAt = order.fulfilledAt();
        String cancelledAt = order.cancelledAt();

        switch (newStatus) {
            case PAID -> paidAt = now;
            case FULFILLED -> {
                paidAt = now;
                fulfilledAt = now;
            }
            case CANCELLED -> cancelledAt = now;
            case DRAFT, PENDING_PAYMENT -> {
                // These statuses do not set a status timestamp.
            }
        }

        return new UpdateOrderStatusResponseDto(
                order.id(),
                order.code(),
                order.customerId(),
                order.customerName(),
                order.createdBy(),
                newStatus,
                order.lines(),
                order.subtotal(),
                order.discountPercent(),
                order.discountAmount(),
                order.taxPercent(),
                order.taxAmount(),
                order.total(),
                order.currency(),
                order.createdAt(),
                now,
                paidAt,
                fulfilledAt,
                cancelledAt
        );
    }

    public OrderResponseDto cancelOrder(int id){
        OrderResponseDto order = orderRepository.getOrderById(id);
        return order.cancelOrder();
    }

}
