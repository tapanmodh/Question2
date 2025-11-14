package com.example.question2.model;

import android.content.Context;

import com.example.question2.R;

public enum PaymentType {
    CASH,
    BANK_TRANSFER,
    CREDIT_CARD;

    public static PaymentType fromDisplayName(Context context, String name) {
        for (PaymentType type : values()) {
            if (type.getDisplayName(context).equals(name))
                return type;
        }
        return null;
    }

    public String getDisplayName(Context context) {
        switch (this) {
            case CASH:
                return context.getString(R.string.cash);
            case BANK_TRANSFER:
                return context.getString(R.string.bank_transfer);
            case CREDIT_CARD:
                return context.getString(R.string.credit_card);
        }
        return "";
    }
}
