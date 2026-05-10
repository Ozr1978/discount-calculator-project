
public class DiscountCalculator {

    public int calculateDiscount(String customerType,
                                 int totalOrdersInLastYear,
                                 boolean isSubscribedToNewsletter) {

        int discount = 5;

        if (isSubscribedToNewsletter == true) {
            discount = discount + 2;
        }

        switch (customerType) {
            case "REGULAR" : discount = discount + 3;
            break;

            case "PREMIUM" : discount = discount + 5;
            break;

            case "NEW" : {
                if (totalOrdersInLastYear >= 10) {
                    throw new IllegalArgumentException("Invalid NEW customer orders");
                }
            }
            break;
            
            default : throw new IllegalArgumentException("Customer type is wrong");
            
        }

        if (totalOrdersInLastYear >= 10) {
            discount = discount + 5;
        }

        return discount;
    }
}