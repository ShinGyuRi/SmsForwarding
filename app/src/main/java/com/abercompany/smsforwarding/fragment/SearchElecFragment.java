package com.abercompany.smsforwarding.fragment;


import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.activity.ElecStatusActivity;
import com.abercompany.smsforwarding.adapter.SmsRecyclerAdapter;
import com.abercompany.smsforwarding.databinding.FragmentSearchElecBinding;
import com.abercompany.smsforwarding.dialog.ElecDialog;
import com.abercompany.smsforwarding.model.Defaulter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchElecFragment extends Fragment {

    private FragmentSearchElecBinding binding;

    private List<Defaulter> elec;
    private List<Defaulter> filterElec;
    private SmsRecyclerAdapter adapter;

    public SearchElecFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public SearchElecFragment(List<Defaulter> elec) {
        this.elec = elec;
    }

    public static SearchElecFragment newInstance(List<Defaulter> elec) {
        SearchElecFragment fragment = new SearchElecFragment(elec);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_search_elec, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_elec, container, false);
        View view = binding.getRoot();

        binding.setSearchElec(this);
        return view;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                filterElec = new ArrayList<>();
                getFilterElec(elec, binding.spMonth.getSelectedItem().toString().replace("월", ""));
                break;

        }
    }

    private void getFilterElec(List<Defaulter> elec, String month) {
        for (int i = 0; i < elec.size(); i++) {
            String dateMonth = elec.get(i).getEndDate().split("-")[1];

            if (dateMonth.equals(month)) {
                filterElec.add(elec.get(i));
            }
        }

        setElecDefaulterAdapter(filterElec);
    }

    private void setElecDefaulterAdapter(final List<Defaulter> elecStatus) {
        adapter = new SmsRecyclerAdapter(getContext(), elecStatus);
        binding.rvElecStats.setAdapter(adapter);
        binding.rvElecStats.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
    }

}
