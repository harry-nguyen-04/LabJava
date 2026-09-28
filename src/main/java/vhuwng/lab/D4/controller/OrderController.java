package vhuwng.lab.D4.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vhuwng.lab.D4.dto.request.CreateOrderRequestDto;
import vhuwng.lab.D4.dto.request.UpdateOrderStatusRequestDto;
import vhuwng.lab.D4.dto.response.CreateOrderResponseDto;
import vhuwng.lab.D4.dto.response.OrderResponseDto;
import vhuwng.lab.D4.dto.response.UpdateOrderStatusResponseDto;
import vhuwng.lab.D4.service.OrderService;

import java.util.List;

@RestController()
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("")
    public List<OrderResponseDto> getAllOrders(){
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public OrderResponseDto getOrderById(@PathVariable int id){
        return orderService.getOrderById(id);
    }

    @PostMapping("")
    public ResponseEntity<CreateOrderResponseDto> createOrder(@Valid @RequestBody CreateOrderRequestDto request){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(request));
    }

    @PatchMapping("/{id}")
    public UpdateOrderStatusResponseDto updateOrderStatus(@PathVariable int id, @Valid @RequestBody UpdateOrderStatusRequestDto request){
        return orderService.updateOrderStatus(id, request);
    }

    @DeleteMapping("/{id}")
    public OrderResponseDto cancelOrder(@PathVariable int id){
        return orderService.cancelOrder(id);
    }
}
