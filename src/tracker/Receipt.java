package tracker;

import java.io.Serializable;

public class Receipt implements Serializable{
    private String category;
    private String payer;
    private String period;
    private double price;

    public Receipt(String c, String pa, double pr, String per){
        category = c;
        payer = pa;
        price = pr;
        period = per;
    }

    @Override
    public String toString(){
        return (category + " - " + payer + ": $" + price);
    }

    public String getCategory(){
        return category;
    }


    public String getPayer(){
        return payer;
    }

    public Double getPrice(){
        return price;
    }

    public String getPeriod(){
        return period;
    }
}
