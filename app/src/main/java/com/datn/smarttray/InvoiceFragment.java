package com.datn.smarttray;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.datn.smarttray.utils.InvoiceItem;

import java.util.List;
import java.util.Locale;


public class InvoiceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView txtTongTien;
    private TableLayout tableInvoice;
    private List<InvoiceItem> cachedInvoiceList;
/*
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InvoiceFragment() {
        // Required empty public constructor
    }
    */

    // TODO: Rename and change types and number of parameters
    /*
    public static InvoiceFragment newInstance(List<InvoiceItem> invoiceItemList) {
        InvoiceFragment fragment = new InvoiceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        updateInvoice(invoiceItemList);
        return fragment;
    }*/
    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invoice, container, false);
    }
    */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invoice, container, false);
        tableInvoice = view.findViewById(R.id.tableInvoice);
        txtTongTien = view.findViewById(R.id.txtTongTien);

        return view;
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        if(cachedInvoiceList != null){

            updateInvoice(cachedInvoiceList);
        }
    }

    // Hàm public để MainActivity đẩy dữ liệu món ăn vào bảng bất cứ lúc nào
    public void updateInvoice (List<InvoiceItem> invoiceItemList) {
        cachedInvoiceList = invoiceItemList;
        if (tableInvoice == null) return;

        // Xóa các dòng cũ (chỉ giữ lại dòng tiêu đề Index 0)
        int childCount = tableInvoice.getChildCount();
        if (childCount > 1) {
            tableInvoice.removeViews(1, childCount - 1);
        }

        int tongtien = 0;

        // Duyệt qua danh sách để add dòng mới vào Table
        for (InvoiceItem item : invoiceItemList) {
            TableRow row = new TableRow(getContext());
            row.setPadding(8, 12, 8, 12);

            // Đổi màu nền xen kẽ cho đẹp giống hóa đơn thật
            if (tableInvoice.getChildCount() % 2 == 0) {
                row.setBackgroundColor(Color.parseColor("#F9F9F9"));
            }

            // 1. Cột Tên món
            TextView tvName = new TextView(getContext());
            tvName.setText(item.getName());
            tvName.setTextColor(Color.parseColor("#333333"));

            tvName.setMaxLines(1);

            tvName.setEllipsize(
                    TextUtils.TruncateAt.END
            );

            tvName.setWidth(405);
            tvName.setOnClickListener(v->{
                //go to detail food;

                openFoodDetailActivity(item.getIdFood());
            });

            // 2. Cột Số lượng
            TextView tvQty = new TextView(getContext());
            tvQty.setText(String.valueOf(item.getQuantity()));
            tvQty.setGravity(android.view.Gravity.CENTER);
            tvQty.setTextColor(Color.parseColor("#333333"));
            tvQty.setWidth(190);

            // 3. Cột Đơn giá
            TextView tvPrice = new TextView(getContext());
            tvPrice.setText(String.format(Locale.US, "%,d", item.getPrice()));
            tvPrice.setGravity(Gravity.CENTER);
            tvPrice.setTextColor(Color.parseColor("#333333"));
            tvPrice.setWidth(190);

            // 4. Cột Thành tiền
            TextView tvTotal = new TextView(getContext());
            tvTotal.setText(String.format(Locale.US, "%,d", item.getTotalPrice()));
            tvTotal.setGravity(Gravity.CENTER);
            tvTotal.setTextColor(Color.parseColor("#333333"));
            tvTotal.setTypeface(null, android.graphics.Typeface.BOLD);
            tvTotal.setWidth(220);

            // Add các ô vào dòng
            row.addView(tvName);
            row.addView(tvQty);
            row.addView(tvPrice);
            row.addView(tvTotal);

            // Add dòng vào bảng
            tableInvoice.addView(row);
            tongtien+=item.getTotalPrice();


        }

        // Cập nhật text tổng tiền hiển thị công khai
        txtTongTien.setText(String.format(Locale.US, "%,d VNĐ", tongtien));
    }
    public void openFoodDetailActivity(String id){
        Intent intent =
                new Intent(
                        requireContext(),
                        DetailFoodActivity.class
                );
        intent.putExtra(
                "food_id",
                id
        );
        startActivity(intent);
    }
}