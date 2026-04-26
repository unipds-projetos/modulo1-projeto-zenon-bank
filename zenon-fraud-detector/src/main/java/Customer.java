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

//    public void setName(String name) {
//        this.name = name;
//    }

    public BigDecimal getOldBalance() {
        return oldBalance;
    }

//    public void setOldBalance(BigDecimal oldBalance) {
//        this.oldBalance = oldBalance;
//    }

    public BigDecimal getNewBalance() {
        return newBalance;
    }

//    public void setNewBalance(BigDecimal newBalance) {
//        this.newBalance = newBalance;
//    }


}
