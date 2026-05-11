import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class DiscountCalculatorTest {

    private final DiscountCalculator calculator = new DiscountCalculator();


    // SECTION 1


    @ParameterizedTest(name = "[{index}] type={0}, orders={1}, subscribed={2} => expected={3}%")
    @CsvSource({
            "NEW, 5, true, 7",
            "NEW, 3, false, 5",

            "REGULAR, 5, true, 10",
            "REGULAR, 3, false, 8",
            "REGULAR, 10, true, 15",
            "REGULAR, 12, false, 13",

            "PREMIUM, 5, true, 12",
            "PREMIUM, 3, false, 10",
            "PREMIUM, 10, true, 15",
            "PREMIUM, 15, false, 15"
    })
    @DisplayName("Pair-Wise Coverage: Valid Discount Calculations")
    void testValidDiscountCombinations(String customerType,
                                       int totalOrders,
                                       boolean subscribed,
                                       int expectedDiscount) {
        int actual = calculator.calculateDiscount(customerType, totalOrders, subscribed);
        assertEquals(expectedDiscount, actual,
                String.format("Failed for: type=%s, orders=%d, subscribed=%b",
                        customerType, totalOrders, subscribed));
    }


    // SECTION 2


    @ParameterizedTest(name = "[{index}] INFEASIBLE: NEW + orders={0} + subscribed={1}")
    @CsvSource({
            "10, true",
            "10, false",
            "20, true",
            "20, false"
    })
    @DisplayName("Infeasible Pair: NEW customer with totalOrdersInLastYear >= 10")
    void testInfeasibleNewCustomerWithHighOrders(int totalOrders, boolean subscribed) {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateDiscount("NEW", totalOrders, subscribed),
                "Expected IllegalArgumentException for NEW customer with orders >= 10");
    }


    // SECTION 3


    @ParameterizedTest(name = "[{index}] Invalid type={0}")
    @CsvSource({
            "GOLD,    5,  true",
            "SILVER,  3,  false",
            "UNKNOWN, 0,  true",
            "regular, 5,  false"    // case-sensitive check
    })
    @DisplayName("Invalid customerType throws IllegalArgumentException")
    void testInvalidCustomerTypeThrowsException(String customerType,
                                                int totalOrders,
                                                boolean subscribed) {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateDiscount(customerType, totalOrders, subscribed),
                "Expected IllegalArgumentException for invalid customerType: " + customerType);
    }


    // SECTION 4


    @Test
    @DisplayName("Boundary: Maximum discount cap is 15%")
    void testDiscountDoesNotExceedCap() {
        // PREMIUM + >=10 orders + subscribed => 5+5+5+2 = 17, should be capped at 15
        int result = calculator.calculateDiscount("PREMIUM", 10, true);
        assertTrue(result <= 15,
                "Discount exceeded the maximum cap of 15%. Got: " + result);
        assertEquals(15, result, "PREMIUM + >=10 orders + newsletter should be capped at 15%");
    }


    // SECTION 5

    @Test
    @DisplayName("Base: NEW customer, no orders, no newsletter => 5% base only")
    void testBaseDiscountOnly() {
        int result = calculator.calculateDiscount("NEW", 0, false);
        assertEquals(5, result, "Base discount should be 5% with no bonuses");
    }
}