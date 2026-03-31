package model;

public class Product {
    private int productId;
    private String productName;
    private String storage;
    private String color;
    private double price;
    private int stock;
    private String description;
    private int categoryId;
    private String status;
    private String createdAt;

    private boolean flashSaleActive;
    private double discountPercent;
    private int flashSaleRemainingQuantity;
    private double finalPrice;

    public Product() {
    }

    public Product(int productId, String productName, String storage, String color,
            double price, int stock, String description, int categoryId,
            String status, String createdAt) {
        this.productId = productId;
        this.productName = productName;
        this.storage = storage;
        this.color = color;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.categoryId = categoryId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        recalculateFinalPrice();
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isFlashSaleActive() {
        return flashSaleActive;
    }

    public void setFlashSaleActive(boolean flashSaleActive) {
        this.flashSaleActive = flashSaleActive;
        recalculateFinalPrice();
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
        recalculateFinalPrice();
    }

    public int getFlashSaleRemainingQuantity() {
        return flashSaleRemainingQuantity;
    }

    public void setFlashSaleRemainingQuantity(int flashSaleRemainingQuantity) {
        this.flashSaleRemainingQuantity = flashSaleRemainingQuantity;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    private void recalculateFinalPrice() {
        if (flashSaleActive && discountPercent > 0) {
            this.finalPrice = price * (100 - discountPercent) / 100.0;
        } else {
            this.finalPrice = price;
        }
    }

    public int getMaxPurchasableQuantity() {
        if (flashSaleActive) {
            return Math.min(stock, flashSaleRemainingQuantity);
        }
        return stock;
    }
}