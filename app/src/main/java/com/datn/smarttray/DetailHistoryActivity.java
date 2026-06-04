package com.datn.smarttray;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.datn.smarttray.model.History;
import com.datn.smarttray.repository.HistoryRepository;
import com.datn.smarttray.utils.GetLocalTime;

import java.io.File;

public class DetailHistoryActivity extends AppCompatActivity {

    ImageView imgPredict;

    TextView txtTime;
    InvoiceFragment invoiceFragment;
    History history;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history);
        imgPredict = findViewById(R.id.imageView);
        txtTime = findViewById(R.id.txtTime);

        initInvoiceFragment();
        /*
        History history =
                (History) getIntent()
                        .getSerializableExtra("history");

         */
        String historyId =
                getIntent().getStringExtra("history_id");
        HistoryRepository.getHistoryById(historyId, new HistoryRepository.SingleHistoryCallback() {
            @Override
            public void onSuccess(History history1) {
                history = history1;
                initDataToUI();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(DetailHistoryActivity.this, "Không thể tải dữ liệu: " + error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });




    }
    private void initDataToUI(){
        invoiceFragment.updateInvoice(history.getListInvoice());

        txtTime.setText(GetLocalTime.getLocalTime(history.getTimestamp()));
        Glide.with(this)
                .load(history.getImagePredict())
                .placeholder(R.drawable.ic_baseline_fastfood_24)
                .error(R.drawable.ic_baseline_fastfood_24)
                .into(imgPredict);
    }
    private void initInvoiceFragment() {

        invoiceFragment = new InvoiceFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.invoiceContainer,
                        invoiceFragment
                )
                .commitNow();
    }
}
