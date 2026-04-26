import java.math.BigDecimal;

public class Customer {
    String name;
    BigDecimal oldBalance;
    BigDecimal newBalance;

    public Customer(String name, BigDecimal oldBalance, BigDecimal newBalance) {
        this.name = name;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getOldBalance() {
        return oldBalance;
    }


    public BigDecimal getNewBalance() {
        return newBalance;
    }

    public Customer() {
        super();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
