package model;

public class OrderDetail {
    private int orderDetailId;
    private int orderId;
    private int productId;
    private int quantity;
    private double unitPrice;
    private String productName;
    private String storage;
    private String color;

    private Integer flashSaleId;
    private int flashSaleQuantity;
    private int normalQuantity;

    public OrderDetail() {
    }

    public int getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getFlashSaleId() {
        return flashSaleId;
    }

    public void setFlashSaleId(Integer flashSaleId) {
        this.flashSaleId = flashSaleId;
    }

    public int getFlashSaleQuantity() {
        return flashSaleQuantity;
    }

    public void setFlashSaleQuantity(int flashSaleQuantity) {
        this.flashSaleQuantity = flashSaleQuantity;
    }

    public int getNormalQuantity() {
        return normalQuantity;
    }

    public void setNormalQuantity(int normalQuantity) {
        this.normalQuantity = normalQuantity;
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }
}