package kitchenpos.menu.application;

import java.util.List;
import java.util.stream.Collectors;

import kitchenpos.menu.domain.Menu;
import org.springframework.stereotype.Component;

import kitchenpos.menu.domain.MenuProduct;
import kitchenpos.product.application.ProductService;
import kitchenpos.product.domain.Product;

@Component
public class MenuValidator {
    private ProductService productService;

    public MenuValidator(ProductService productService) {
        this.productService = productService;
    }

    public void checkTotalPrice(Menu menu) {
        List<MenuProduct> menuProducts = menu.getMenuProducts();
        List<Long> productIds = menuProducts.stream()
                .map(MenuProduct::getProductId)
                .collect(Collectors.toList());
        List<Product> products = productService.findAllByIds(productIds);
        
        long sumProductPrice = 0;
        for (int i = 0; i < products.size(); i++) {
            sumProductPrice += getMenuProductPrice(products.get(i), menuProducts.get(i));
        }
        
        if (menu.getPrice().isGreaterThan(sumProductPrice)) {
            throw new IllegalArgumentException("메뉴 가격이 상품 가격의 합보다 큽니다");
        }
    }
    
    public void checkProducts(List<Long> productIds) {
        List<Product> products = productService.findAllByIds(productIds);

        if (products.size() != productIds.size()) {
            new IllegalArgumentException("메뉴에는 저장된 상품만 등록할 수 있습니다");
        }
    }
    
    private long getMenuProductPrice(Product product, MenuProduct menuProduct) {
        return product.getPrice().multiply(menuProduct.getQuantity());
    }
}
