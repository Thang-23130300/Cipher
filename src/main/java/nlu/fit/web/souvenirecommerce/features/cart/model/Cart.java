package nlu.fit.web.souvenirecommerce.features.cart.model;

import nlu.fit.web.souvenirecommerce.model.entity.Product;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<Long, CartItem> data = new HashMap<>();
    private User user;

    public Cart() {
        data = new HashMap<>();
    }

    public void addItem(Product product, int quantity) {
        if (quantity <= 0) {
            quantity = 1;
        }

        CartItem existingItem = data.get(product.getId());
        if (existingItem != null) {
            existingItem.upQuantity(quantity);
            return;
        }

        data.put(product.getId(), new CartItem(product, resolveUnitPrice(product), quantity));
    }

    public boolean updateItem(Long productId, int quantity) {
        CartItem item = data.get(productId);
        if (item == null) {
            return false;
        }

        if (quantity <= 0) {
            quantity = 1;
        }
        item.setQuantity(quantity);
        return true;
    }

    public CartItem removeItem(Long productId) {
        if (data.get(productId) == null) {
            return null;
        }
        return data.remove(productId);
    }

    public List<CartItem> removeAllItems() {
        ArrayList<CartItem> cartItems = new ArrayList<>(data.values());
        data.clear();
        return cartItems;
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(data.values());
    }

    public CartItem getItem(Long productId) {
        return data.get(productId);
    }

    public int totalQuantity() {
        return data.values()
                .stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public double total() {
        return data.values()
                .stream()
                .mapToDouble(CartItem::getSubTotal)
                .sum();
    }

    public void updateCustomerInfor(User user) {
        this.user = user;
    }

    private double resolveUnitPrice(Product product) {
        if (product.getSalePrice() != null && product.getSalePrice() > 0) {
            return product.getSalePrice();
        }
        return product.getOriginalPrice();
    }
}
