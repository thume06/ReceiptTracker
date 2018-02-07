package tracker;

import java.io.Serializable;

public class Receipt implements Serializable{
    private String category;
    private String store;
    private String payer;
    private double price;

    public Receipt(String c, String pa, double pr, String s){
        category = c;
        payer = pa;
        price = pr;
        store = s;
    }

    @Override
    public String toString(){
        return (category + ", " + store + ", paid by " + payer + ": " + price);
    }

    public String getPayer(){
        return payer;
    }

    public Double getPrice(){
        return price;
    }
}
