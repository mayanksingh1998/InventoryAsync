package service.Impl.state;
// src/service/impl/state/OrderContext.java

import exception.EcommerceException;
import model.Order;
import model.OrderStatus;
import service.BuyerService;
import service.PincodeServiceabilityService;
import service.ProductService;
import repository.OrderRepository;

public class OrderContext {
    private OrderState currentState;

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final PincodeServiceabilityService pincodeServiceabilityService;
    private final BuyerService buyerService;

    public OrderContext( OrderRepository orderRepository, ProductService productService,
                        PincodeServiceabilityService pincodeServiceabilityService, BuyerService buyerService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.pincodeServiceabilityService = pincodeServiceabilityService;
        this.buyerService = buyerService;
    }

    public void setState(OrderState state) {
        this.currentState = state;
    }

    public void processOrder(Order order) throws EcommerceException {
        while (currentState != null) {
            currentState.processOrder(this, order);
        }
    }

    public OrderRepository getOrderRepository() {
        return orderRepository;
    }

    public ProductService getProductService() {
        return productService;
    }

    public PincodeServiceabilityService getPincodeServiceabilityService() {
        return pincodeServiceabilityService;
    }

    public BuyerService getBuyerService() {
        return buyerService;
    }
}
