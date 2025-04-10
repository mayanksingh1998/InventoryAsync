// src/service/impl/OrderStateMachine.java
package service.Impl;

import exception.EcommerceException;
import model.ErrorCode;
import model.Order;
import model.OrderStatus;
import repository.OrderRepository;
import service.BuyerService;
import service.PincodeServiceabilityService;
import service.ProductService;
import utils.ErrorCodeMap;

public class OrderStateMachine {
    private OrderStatus currentState;
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
        this.currentState = OrderStatus.CHECK_PINCODE;
    }

    public String processOrder(Order order) throws EcommerceException {
        while (currentState != OrderStatus.ORDER_CREATED && currentState != OrderStatus.ORDER_CREATION_FAILED) {
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
        if (currentState == OrderStatus.ORDER_CREATED) {
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
            currentState = OrderStatus.ORDER_CREATION_FAILED;
            throw new EcommerceException(
                    model.ErrorCode.PIN_CODE_UNSERVICEABLE, ErrorCodeMap.errorCodeStringHashMap.get(model.ErrorCode.PIN_CODE_UNSERVICEABLE));
        } else {
            currentState = OrderStatus.CHECK_INVENTORY;
        }
    }

    private void checkInventory(Order order) throws EcommerceException {
        if (productService.checkInventory(order.getQuantity(), order.getProductId())) {
            currentState = OrderStatus.CREATE_ORDER;
        } else {
            currentState = OrderStatus.ORDER_CREATION_FAILED;
        }
    }

    private void createOrder(Order order) throws EcommerceException {
        Order createdOrder = orderRepository.createOrder(order);
        currentState = OrderStatus.ORDER_CREATED;
    }

    public OrderStatus getCurrentState() {
        return currentState;
    }

    public void setCurrentState(OrderStatus currentState) {
        this.currentState = currentState;
    }
}