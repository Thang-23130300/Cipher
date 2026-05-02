package nlu.fit.web.souvenirecommerce.cart;

import nlu.fit.web.souvenirecommerce.model.entity.Product;

import java.io.Serializable;

public class CartItem implements Serializable {

    private Product product;
    private double price;
    private int quantity;

    public CartItem() {}

    public CartItem(Product product, double price, int quantity) {
        this.product = product;
        this.price = price;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void upQuantity(int quantity) {
        this.quantity += quantity;
    }

    public double getSubTotal() {
        return price * quantity;
    }
}