package toy.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.project.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}