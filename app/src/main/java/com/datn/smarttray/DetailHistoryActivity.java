package com.datn.smarttray;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.datn.smarttray.manager.HistoryManager;
import com.datn.smarttray.model.Food;
import com.datn.smarttray.model.History;
import com.datn.smarttray.utils.InvoiceItem;

import java.io.File;
import java.util.List;

public class DetailHistoryActivity extends AppCompatActivity {

    ImageView imgPredict;

    TextView txtTime;
    InvoiceFragment invoiceFragment;




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
        History history =
                HistoryManager.findHistoryById(historyId);

        if(history == null) {

            finish();

            return;
        }
        invoiceFragment.updateInvoice(history.getListInvoice());

        txtTime.setText(history.getLocalTime());
        Glide.with(this)
                .load(new File(history.getImagePredict()))
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
