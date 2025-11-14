package com.example.question2;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.question2.model.Payment;
import com.example.question2.model.PaymentType;

import java.util.ArrayList;
import java.util.List;

public class AddPaymentDialog extends DialogFragment {

    private Listener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (Listener) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_add_payment, null);

        Spinner paymentTypeSpinner = view.findViewById(R.id.spinnerType);
        EditText etAmount = view.findViewById(R.id.etAmount);
        EditText etProvider = view.findViewById(R.id.etProvider);
        EditText etReference = view.findViewById(R.id.etReference);

        List<String> spinnerTypes = new ArrayList<>();
        for (PaymentType type : PaymentType.values()) {
            if (!listener.getExistingTypes().contains(type)) {
                spinnerTypes.add(type.getDisplayName(getContext()));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                spinnerTypes
        );

        paymentTypeSpinner.setAdapter(adapter);

        paymentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String option = spinnerTypes.get(position);

                boolean isCash = option.equals(getResources().getString(R.string.cash));
                etProvider.setVisibility(isCash ? View.GONE : View.VISIBLE);
                etReference.setVisibility(isCash ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return new AlertDialog.Builder(requireContext())
                .setTitle(getResources().getString(R.string.add_payment))
                .setView(view)
                .setPositiveButton(getResources().getString(android.R.string.ok), (dialog, which) -> {
                    String amountText = etAmount.getText().toString().trim();
                    if (!amountText.isEmpty()) {
                        String selectedPaymentType = paymentTypeSpinner.getSelectedItem().toString();
                        PaymentType type = PaymentType.fromDisplayName(getContext(), selectedPaymentType);

                        double amount = Double.parseDouble(amountText);
                        String provider = etProvider.getVisibility() == View.VISIBLE ? etProvider.getText().toString() : null;
                        String reference = etReference.getVisibility() == View.VISIBLE ? etReference.getText().toString() : null;

                        listener.onPaymentAdded(new Payment(type, amount, provider, reference));
                    }
                })
                .setNegativeButton(getResources().getString(android.R.string.cancel), null)
                .create();
    }

    public interface Listener {
        void onPaymentAdded(Payment payment);
        List<PaymentType> getExistingTypes();
    }
}
