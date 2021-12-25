package kitchenpos.menu.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

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

import kitchenpos.menu.dao.MenuProductRepository;
import kitchenpos.menu.dao.MenuRepository;
import kitchenpos.menu.dao.ProductRepository;
import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuGroup;
import kitchenpos.menu.domain.MenuProduct;
import kitchenpos.menu.domain.Price;
import kitchenpos.menu.domain.Product;
import kitchenpos.menu.dto.MenuRequest;
import kitchenpos.menu.dto.MenuResponse;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuGroupService menuGroupService;

    @Mock
    private MenuProductRepository menuProductRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private MenuService menuService;

    @DisplayName("메뉴를 등록할 수 있다")
    @Test
    void 메뉴_등록() {
        // given
        Menu 메뉴 = Menu.of("짜장면", 6000, MenuGroup.from("중식"));
        Product 상품 = Product.of("짜장면", 6000);
        MenuProduct 메뉴상품 = MenuProduct.of(메뉴, 상품, 1L);
        메뉴.addMenuProducts(Arrays.asList(메뉴상품));
        
        given(menuGroupService.findById(nullable(Long.class))).willReturn(메뉴.getMenuGroup());
        given(productRepository.findById(nullable(Long.class))).willReturn(Optional.of(상품));
        given(menuRepository.save(any())).willReturn(메뉴);
        given(menuProductRepository.save(메뉴상품)).willReturn(메뉴상품);

        // when
        MenuResponse 저장된_메뉴 = menuService.create(MenuRequest.from(메뉴));

        // then
        assertThat(저장된_메뉴).isEqualTo(MenuResponse.from(메뉴));

    }
    
    @DisplayName("메뉴 등록시 메뉴그룹이 지정되어 있어야한다 - 예외처리")
    @Test
    void 메뉴_등록_메뉴그룹_필수() {
        // given
        Menu 메뉴그룹_미지정_메뉴 = Menu.of("짜장면", 6000, null);
        MenuProduct 메뉴상품 = MenuProduct.of(메뉴그룹_미지정_메뉴, Product.of("짜장면", 6000), 1L);
        메뉴그룹_미지정_메뉴.addMenuProducts(Arrays.asList(메뉴상품));
        given(menuGroupService.findById(nullable(Long.class))).willReturn(메뉴그룹_미지정_메뉴.getMenuGroup());
        
        // when, then
        assertThatThrownBy(() -> {
            menuService.create(MenuRequest.from(메뉴그룹_미지정_메뉴));
        }).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("해당하는 메뉴그룹이 없습니다");
    }
    @DisplayName("메뉴에 등록된 메뉴그룹은 등록된 메뉴그룹이어야한다 - 예외처리")
    @Test
    void 메뉴_등록_등록된_메뉴그룹만() {
        // given
        Menu 메뉴 = Menu.of("짜장면", 6000, MenuGroup.from("미등록_메뉴그룹"));
        Product 상품 = Product.of("짜장면", 6000);
        메뉴.addMenuProducts(Arrays.asList(MenuProduct.of(메뉴, 상품, 1L)));
        
        // when, then
        assertThatThrownBy(() -> {
            menuService.create(MenuRequest.from(메뉴));
        }).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("해당하는 메뉴그룹이 없습니다");
    
    }
    
    @DisplayName("메뉴에 포함된 상품은 등록된 상품이어야한다 - 예외처리")
    @Test
    void 메뉴_등록_등록된_상품만() {
        // given
        Menu 메뉴 = Menu.of("짜장면", 6000, MenuGroup.from("메뉴그룹"));
        
        Product 상품 = Product.of("짜장면", 6000);
        메뉴.addMenuProducts(Arrays.asList(MenuProduct.of(메뉴, 상품, 1L)));
        
        given(menuGroupService.findById(nullable(Long.class))).willReturn(메뉴.getMenuGroup());
        
        // when
        when(productRepository.findById(nullable(Long.class))).thenReturn(Optional.empty());
    
        // then
        assertThatThrownBy(() -> {
            menuService.create(MenuRequest.from(메뉴));
        }).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("등록된 상품이 아닙니다");
    
    }
    
    @DisplayName("메뉴 목록을 조회할 수 있다")
    @Test
    void 메뉴_목록_조회() {
        // given
        Menu 첫번째_메뉴 = Menu.of("짜장면", 6000, MenuGroup.from("짜장면_메뉴그룹"));
        
        Menu 두번째_메뉴 = Menu.of("짬뽕", 7000, MenuGroup.from("짬뽕_메뉴그룹"));
    
        given(menuRepository.findAll()).willReturn(Arrays.asList(첫번째_메뉴, 두번째_메뉴));
    
        // when
        List<MenuResponse> 메뉴_목록 = menuService.list();
    
        // then
        assertThat(메뉴_목록).containsExactly(MenuResponse.from(첫번째_메뉴), MenuResponse.from(두번째_메뉴));
    }
}
