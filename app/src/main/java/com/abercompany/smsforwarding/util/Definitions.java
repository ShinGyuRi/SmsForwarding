package com.abercompany.smsforwarding.util;

public class Definitions {

    public static int REQUEST_REALTY = 0;

    public interface UPDATE_TRIM_DATA   {
        String DEPOSIT_DOWN_PAYMENT = "deposit_down_payment";
        String DEPOSIT_SECURITY = "deposit_security";
        String DEPOSIT_RENT = "deposit_rent";
        String DEPOSIT_SECURITY_RENT = "deposit_security_rent";
        String DEPOSIT_MANAGEMENT_EXPENSES = "deposit_management_expenses";
        String WITHDRAW_BROKERAGE = "withdraw_brokerage";
        String WITHDRAW_ADJUSTMENT = "withdraw_adjustment";
        String WITHDRAW_MAINTENANCE = "withdraw_maintenance";
    }

    public interface TRIM_DATA  {
        String NEW_DATA = "new";
        String EXISTING_DATA = "existing";
    }

    public interface CONTACT_TYPE   {
        String RESIDENT = "resident";
        String BROKER = "broker";
    }
}
