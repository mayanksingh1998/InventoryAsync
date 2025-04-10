package service.Impl.state;

import exception.EcommerceException;
import model.Order;

public class CreateOrderState implements OrderState {
    @Override
    public void processOrder(OrderContext context, Order order) throws EcommerceException {
        context.getOrderRepository().createOrder(order);
        context.setState(null); // End of state transitions
    }
}
