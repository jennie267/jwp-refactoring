package kitchenpos.product.dto;

import java.util.Objects;

import kitchenpos.product.domain.Product;

public class ProductResponse {
    private Long id;
    private String name;
    private long price;
    
    private ProductResponse() {
    }

    private ProductResponse(Long id, String name, long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
    
    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getPrice().getValue());
    }
    
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductResponse that = (ProductResponse) o;
        return Objects.equals(name, that.name) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }
}
