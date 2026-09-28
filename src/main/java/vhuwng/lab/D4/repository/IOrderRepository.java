package vhuwng.lab.D4.repository;

import vhuwng.lab.D4.dto.response.OrderResponseDto;

import java.util.List;

public interface IOrderRepository {
    List<OrderResponseDto> getAllOrders();

    OrderResponseDto getOrderById(int id);


}
