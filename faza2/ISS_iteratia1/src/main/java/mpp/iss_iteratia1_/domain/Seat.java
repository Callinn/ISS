package mpp.iss_iteratia1_.domain;

public class Seat extends Entity<Long>{
    private String row;
    private int number;
    private int price;
    private boolean isAvailable;

    public Seat(String row, int number,int price, boolean isAvailable) {
        this.row = row;
        this.number = number;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    public String getRow() {
        return row;
    }

    public int getNumber() {
        return number;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
    public int getPrice() {
        return price;
    }
    public void setAvailable(boolean available) {
        isAvailable = available;
    }



    @Override
    public String toString() {
        return "Seat{" +
                "row='" + row + '\'' +
                ", number=" + number +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
