package model;

public class CartItem {
    private int cartItemId;
    private Product product;
    private int quantity;

    private Integer flashSaleId;
    private int reservedFlashQuantity;
    private int reservedNormalQuantity;

    private double flashUnitPrice;
    private double normalUnitPrice;

    public CartItem() {
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Integer getFlashSaleId() {
        return flashSaleId;
    }

    public void setFlashSaleId(Integer flashSaleId) {
        this.flashSaleId = flashSaleId;
    }

    public int getReservedFlashQuantity() {
        return reservedFlashQuantity;
    }

    public void setReservedFlashQuantity(int reservedFlashQuantity) {
        this.reservedFlashQuantity = reservedFlashQuantity;
    }

    public int getReservedNormalQuantity() {
        return reservedNormalQuantity;
    }

    public void setReservedNormalQuantity(int reservedNormalQuantity) {
        this.reservedNormalQuantity = reservedNormalQuantity;
    }

    public double getFlashUnitPrice() {
        return flashUnitPrice;
    }

    public void setFlashUnitPrice(double flashUnitPrice) {
        this.flashUnitPrice = flashUnitPrice;
    }

    public double getNormalUnitPrice() {
        return normalUnitPrice;
    }

    public void setNormalUnitPrice(double normalUnitPrice) {
        this.normalUnitPrice = normalUnitPrice;
    }

    public double getSubtotal() {
        return reservedFlashQuantity * flashUnitPrice
                + reservedNormalQuantity * normalUnitPrice;
    }
}