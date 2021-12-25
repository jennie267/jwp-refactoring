package kitchenpos.menu.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class MenuProducts {

    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<MenuProduct> menuProducts;

    protected MenuProducts() {
        this.menuProducts = new ArrayList<MenuProduct>();
    }

    public MenuProducts(List<MenuProduct> menuProducts) {
        this.menuProducts = menuProducts;
    }

    public static MenuProducts from(List<MenuProduct> menuProducts) {
        return new MenuProducts(menuProducts);
    }

    public List<MenuProduct> getMenuProducts() {
        return Collections.unmodifiableList(menuProducts);
    }

    public Price calculatorTotalPrice() {
        Price sum = Price.from(0);
        for (MenuProduct menuProduct : this.menuProducts) {
            sum = sum.add(menuProduct.calculatePrice());
        }
        return sum;
    }

    public void add(MenuProduct menuProduct) {
        this.menuProducts.add(menuProduct);
    }
}
