package com.thomas_bayer.www.product.bean;

/**
 * Created by Dzmitry_Sankouski on 31-Mar-17.
 */
public class Product {

    private String price;
    private String name;
    private String ID;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (price != null ? !price.equals(product.price) : product.price != null) return false;
        if (name != null ? !name.equals(product.name) : product.name != null) return false;
        return ID != null ? ID.equals(product.ID) : product.ID == null;

    }

    @Override
    public int hashCode() {
        int result = price != null ? price.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (ID != null ? ID.hashCode() : 0);
        return result;
    }

    //todo toString()
}
