package vhuwng.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vhuwng.order.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
