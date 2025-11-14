package com.example.question2;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.question2.data.PaymentStorage;
import com.example.question2.model.Payment;
import com.example.question2.model.PaymentType;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AddPaymentDialog.Listener {

    private static final String KEY_PAYMENTS = "payments_json";

    private final List<Payment> paymentsList = new ArrayList<>();
    private final Gson gson = new Gson();
    private ChipGroup chipGroup;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chipGroup = findViewById(R.id.chipGroup);
        tvTotal = findViewById(R.id.tvTotal);

        if (savedInstanceState != null) {
            restoreFromBundle(savedInstanceState);
        } else {
            PaymentStorage.load(this, loaded -> runOnUiThread(() -> {
                paymentsList.clear();
                paymentsList.addAll(loaded);
                refreshChips();
            }));
        }

        TextView tvAdd = findViewById(R.id.tvAddPayment);
        tvAdd.setPaintFlags(tvAdd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvAdd.setOnClickListener(view -> new AddPaymentDialog().show(getSupportFragmentManager(), "add"));
        findViewById(R.id.btnSave).setOnClickListener(view -> PaymentStorage.save(this, paymentsList));
    }

    private void refreshChips() {
        chipGroup.removeAllViews();
        ColorStateList chipBgColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.chip_bg));

        for (Payment payment : paymentsList) {
            Chip chip = new Chip(this);
            chip.setText(getString(R.string.payment_chip, payment.type.getDisplayName(this), payment.amount));
            chip.setCloseIconVisible(true);
            chip.setChipBackgroundColor(chipBgColor);
            chip.setOnCloseIconClickListener(view -> {
                paymentsList.remove(payment);
                refreshChips();
            });
            chipGroup.addView(chip);
        }

        updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (Payment payment : paymentsList) total += payment.amount;

        setColoredTotal(total);
    }

    private void setColoredTotal(double total) {
        String amount = String.valueOf(total);
        String base = getString(R.string.total_amount, amount);

        SpannableString span = new SpannableString(base);

        int start = base.indexOf(amount);
        int end = start + amount.length();

        span.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(this, R.color.text_color)),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        tvTotal.setText(span);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        String json = gson.toJson(paymentsList);
        outState.putString(KEY_PAYMENTS, json);
    }

    private void restoreFromBundle(Bundle state) {
        String json = state.getString(KEY_PAYMENTS);
        if (json != null) {
            Payment[] arr = gson.fromJson(json, Payment[].class);
            paymentsList.clear();
            paymentsList.addAll(Arrays.asList(arr));
            refreshChips();
        }
    }

    @Override
    public void onPaymentAdded(Payment payment) {
        paymentsList.add(payment);
        refreshChips();
    }

    @Override
    public List<PaymentType> getExistingTypes() {
        List<PaymentType> types = new ArrayList<>();
        for (Payment payment : paymentsList) types.add(payment.type);
        return types;
    }
}
