package model;

public enum OrderState {
    CHECK_PINCODE,
    CHECK_INVENTORY,
    CREATE_ORDER,
    ORDER_CREATED,
    ORDER_CREATION_FAILED
}