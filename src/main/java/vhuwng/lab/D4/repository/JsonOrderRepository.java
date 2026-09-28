package vhuwng.lab.D4.repository;

import org.springframework.stereotype.Repository;
import vhuwng.lab.D4.dto.response.OrderResponseDto;
import vhuwng.lab.D4.util.OrderUtil;

import java.util.List;

@Repository
public class JsonOrderRepository implements IOrderRepository{

    private final OrderUtil orderUtil;
    public JsonOrderRepository(OrderUtil orderUtil) {
        this.orderUtil = orderUtil;
    }

    @Override
    public List<OrderResponseDto> getAllOrders() {
        return orderUtil.getAllOrdersWithJsonSource();
    }

    @Override
    public OrderResponseDto getOrderById(int id){
        return orderUtil.getAllOrdersWithJsonSource().stream()
                .filter(order -> order.id() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
