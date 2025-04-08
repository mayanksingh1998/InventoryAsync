// src/service/impl/OrderStateMachine.java
package service.Impl;

import exception.EcommerceException;
import model.ErrorCode;
import model.Order;
import model.OrderState;
import repository.OrderRepository;
import service.BuyerService;
import service.PincodeServiceabilityService;
import service.ProductService;
import utils.ErrorCodeMap;

public class OrderStateMachine {
    private OrderState currentState;
    private OrderRepository orderRepository;
    private ProductService productService;
    private PincodeServiceabilityService pincodeServiceabilityService;
    private BuyerService buyerService;

    public OrderStateMachine(OrderRepository orderRepository, ProductService productService,
                             PincodeServiceabilityService pincodeServiceabilityService, BuyerService buyerService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.pincodeServiceabilityService = pincodeServiceabilityService;
        this.buyerService = buyerService;
        this.currentState = OrderState.CHECK_PINCODE;
    }

    public String processOrder(Order order) throws EcommerceException {
        while (currentState != OrderState.ORDER_CREATED && currentState != OrderState.ORDER_CREATION_FAILED) {
            switch (currentState) {
                case CHECK_PINCODE:
                    checkPincode(order);
                    break;
                case CHECK_INVENTORY:
                    checkInventory(order);
                    break;
                case CREATE_ORDER:
                    createOrder(order);
                    break;
                default:
                    throw new IllegalStateException("Unexpected state: " + currentState);
            }
        }
        if (currentState == OrderState.ORDER_CREATED) {
            return order.getOrderId();
        } else {
            throw new EcommerceException(
                    ErrorCode.ORDER_CREATION_FAILED, ErrorCodeMap.errorCodeStringHashMap.get(ErrorCode.ORDER_CREATION_FAILED)
            );
        }
    }

    private void checkPincode(Order order) throws EcommerceException {
        final String sourcePinCode = productService.getProduct(order.getProductId()).getAddress().getPinCode();
        final String destinationPinCode = buyerService.getBuyer(order.getBuyerId()).getAddress().getPinCode();

        if (!pincodeServiceabilityService.checkIsSourceAndDestPinCodeMatchesForPaymentType(
                sourcePinCode,
                destinationPinCode, order.getPaymentMode()
        )) {
            currentState = OrderState.ORDER_CREATION_FAILED;
        } else {
            currentState = OrderState.CHECK_INVENTORY;
        }
    }

    private void checkInventory(Order order) throws EcommerceException {
        if (productService.checkInventory(order.getQuantity(), order.getProductId())) {
            currentState = OrderState.CREATE_ORDER;
        } else {
            currentState = OrderState.ORDER_CREATION_FAILED;
        }
    }

    private void createOrder(Order order) throws EcommerceException {
        Order createdOrder = orderRepository.createOrder(order);
        currentState = OrderState.ORDER_CREATED;
    }

    public OrderState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(OrderState currentState) {
        this.currentState = currentState;
    }
}