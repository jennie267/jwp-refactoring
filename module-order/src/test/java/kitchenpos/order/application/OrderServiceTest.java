package kitchenpos.order.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.order.dao.OrderRepository;
import kitchenpos.order.domain.Order;
import kitchenpos.order.domain.OrderLineItem;
import kitchenpos.order.domain.OrderStatus;
import kitchenpos.order.dto.OrderLineItemRequest;
import kitchenpos.order.dto.OrderRequest;
import kitchenpos.order.dto.OrderResponse;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private OrderValidator orderValidator;

    @InjectMocks
    private OrderService orderService;

    @DisplayName("주문 등록시 주문 상태는 조리 상태이다")
    @Test
    void 주문_등록() {
        // given
        Long 메뉴_Id = 1L;
        Long 테이블_Id = 1L;
        
        List<OrderLineItem> 주문_항목_목록 = new ArrayList<OrderLineItem>();
        주문_항목_목록.add(OrderLineItem.of(메뉴_Id, 1L));
        
        Order 주문 = Order.createOrder(테이블_Id, 주문_항목_목록);
        
        given(orderRepository.save(any())).willReturn(주문);
        
        OrderRequest 주문_생성_요청 = OrderRequest.of(1L, 주문.getOrderStatus(), Arrays.asList(OrderLineItemRequest.of(1L, 1L)));
        
        // when
        OrderResponse 등록된_주문 = orderService.create(주문_생성_요청);

        // then
        assertThat(등록된_주문.getOrderStatus()).isEqualTo(OrderStatus.COOKING);
    }
    
    @DisplayName("메뉴없이 주문할 수 없다")
    @Test
    void 주문_등록_메뉴_필수() {
        // given
        
        // when, then
        assertThatThrownBy(() -> {
            orderService.create(OrderRequest.of(1L, new ArrayList<OrderLineItemRequest>()));
        }).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("주문에 메뉴가 없습니다");
    
    }
    
    
    @DisplayName("주문 목록을 조회할 수 있다")
    @Test
    void 주문_목록_조회() {
        // given
        Long 테이블_Id = 1L;
        
        Long 첫번째_메뉴_Id = 1L;
        List<OrderLineItem> 첫번째_주문_항목 = new ArrayList<OrderLineItem>();
        첫번째_주문_항목.add(OrderLineItem.of(첫번째_메뉴_Id, 1L));

        Long 두번째_메뉴_Id = 2L;
        List<OrderLineItem> 두번째_주문_항목 = new ArrayList<OrderLineItem>();
        두번째_주문_항목.add(OrderLineItem.of(두번째_메뉴_Id, 1L));
        
        Order 첫번째_주문 = Order.of(테이블_Id, OrderStatus.COOKING, 첫번째_주문_항목);
        Order 두번째_주문 = Order.of(테이블_Id, OrderStatus.MEAL, 두번째_주문_항목);
        
        given(orderRepository.findAll()).willReturn(Arrays.asList(첫번째_주문, 두번째_주문));
    
        // when
        List<OrderResponse> 주문_목록 = orderService.list();
    
        // then
        assertThat(주문_목록.size()).isEqualTo(2);
    }
    
    @DisplayName("식사중으로 상태를 변경할 수 있다")
    @Test
    void 식사중_상태_변경() {
        // given
        Long 메뉴_Id = 1L;
        Long 테이블_Id = 1L;
        OrderLineItem 주문_항목 = OrderLineItem.of(메뉴_Id, 2L);
        Order 조리중_주문 = Order.of(테이블_Id, OrderStatus.COOKING, Arrays.asList(주문_항목));
        
        given(orderRepository.findById(anyLong())).willReturn(Optional.of(조리중_주문));
        
        // when
        OrderResponse 변경후_주문 = orderService.onMealing(1L);
    
        // then
        assertThat(변경후_주문.getOrderStatus()).isEqualTo(OrderStatus.MEAL);
    }
    
    @DisplayName("계산완료로 상태를 변경할 수 있다")
    @Test
    void 계산완료_상태_변경() {
        // given
        Long 메뉴_Id = 1L;
        Long 테이블_Id = 1L;
        OrderLineItem 주문_항목 = OrderLineItem.of(메뉴_Id, 2L);
        Order 식사중_주문 = Order.of(테이블_Id, OrderStatus.MEAL, Arrays.asList(주문_항목));
        
        given(orderRepository.findById(anyLong())).willReturn(Optional.of(식사중_주문));
        
        // when
        OrderResponse 변경후_주문 = orderService.completed(1L);
    
        // then
        assertThat(변경후_주문.getOrderStatus()).isEqualTo(OrderStatus.COMPLETION);
    }
    
    @DisplayName("계산이 완료된 주문은 상태를 변경 할 수 없다")
    @Test
    void 계산_완료_주문_변경_불가() {
        // given
        Long 메뉴_Id = 1L;
        Long 테이블_Id = 1L;
        OrderLineItem 주문_항목 = OrderLineItem.of(메뉴_Id, 2L);
        Order 계산_완료_주문 = Order.of(테이블_Id, OrderStatus.COMPLETION, Arrays.asList(주문_항목));
        
        given(orderRepository.findById(anyLong())).willReturn(Optional.of(계산_완료_주문));
    
        // when, then
        assertThatThrownBy(() -> {
            orderService.onMealing(1L);
        }).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("계산이 완료된 주문은 상태를 변경 할 수 없습니다");
    }

}
