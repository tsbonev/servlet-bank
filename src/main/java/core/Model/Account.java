package core.Model;

public class Account {

    private int id;
    private double amount;

    public Account(){}

    public Account(double amount) {
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAmountFormatted() {

        return String.format("%.2f", getAmount());

    }
}
