package kitchenpos.order.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kitchenpos.order.domain.OrderLineItem;

public interface OrderLineItemRepository extends JpaRepository<OrderLineItem, Long> {

    List<OrderLineItem> findAllByOrderId(Long orderId);

}
