package kitchenpos.table.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kitchenpos.table.domain.OrderTable;

public interface OrderTableRepository extends JpaRepository<OrderTable, Long> {
    
    List<OrderTable> findAllByIdIn(List<Long> ids);

}
