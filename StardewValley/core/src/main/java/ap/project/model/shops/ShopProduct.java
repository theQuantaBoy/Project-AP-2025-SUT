package ap.project.model.shops;

import ap.project.model.enums.GameObjectType;

public class ShopProduct {
    private String name;
    private int price;
    private int stock; // -1 for unlimited, 0 for out of stock
    private GameObjectType gameObjectType;
    private Object originalItem; // For special handling during purchase
    public boolean isAvailable;
    public boolean isSeasonal;

    public ShopProduct(String name, int price, int stock, GameObjectType type, Object originalItem) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.gameObjectType = type;
        this.originalItem = originalItem;
        this.isAvailable = stock == -1 || stock > 0;
        this.isSeasonal = false;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public GameObjectType getGameObjectType() {
        return gameObjectType;
    }

    public Object getOriginalItem() {
        return originalItem;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public boolean isSeasonal() {
        return isSeasonal;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
