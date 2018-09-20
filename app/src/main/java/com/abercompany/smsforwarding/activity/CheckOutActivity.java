package com.abercompany.smsforwarding.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.abercompany.smsforwarding.R;
import com.abercompany.smsforwarding.databinding.ActivityCheckOutBinding;
import com.abercompany.smsforwarding.model.Contract;
import com.abercompany.smsforwarding.model.Deposit;
import com.abercompany.smsforwarding.model.Resident;
import com.abercompany.smsforwarding.util.JSLog;
import com.abercompany.smsforwarding.util.PrefUtil;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class CheckOutActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private ActivityCheckOutBinding binding;

    private List<Contract> contracts;
    private List<Resident> residents;
    private List<String> residentName = new ArrayList<>();
    private List<Deposit> trimmedData;
    private List<String> endDates = new ArrayList<>();
    private Contract selectedContract;

    private String checkoutDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_out);
        binding.setCheckout(this);

        Intent intent = getIntent();
        contracts = (List<Contract>) intent.getSerializableExtra("contract");
        residents = (List<Resident>) intent.getSerializableExtra("resident");
        trimmedData = (List<Deposit>) intent.getSerializableExtra("trimmedData");

        initViews();
    }

    private void initViews() {
        residentName.add("입주자");
        for (int i = 0; i < residents.size(); i++) {
            if (!residentName.contains(getString(R.string.str_deposit_realty, residents.get(i).getName(), residents.get(i).getHo()))) {
                residentName.add(getString(R.string.str_deposit_realty, residents.get(i).getName(), residents.get(i).getHo()));
            }
        }

        binding.etCheckoutFee.setText("70000");

        binding.spResident.setAdapter(new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, residentName));
        binding.spResident.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    selectedContract = getContract(contracts);

                    binding.etDownPayment.setText(selectedContract.getDeposit());
                    binding.etRent.setText(selectedContract.getRent());
                    binding.etManageFee.setText(selectedContract.getManageFee());
                    binding.etElecAmount.setText("0");
                    binding.etGasAmount.setText("0");


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private Contract getContract(List<Contract> contracts) {
        String dstName = binding.spResident.getSelectedItem().toString();
        String name = dstName.substring(0, dstName.indexOf("("));
        String roomNum = dstName.substring(dstName.indexOf("(") + 1, dstName.indexOf(")"));
        Contract contract = new Contract();

        for (int i = 0; i < contracts.size(); i++) {
            if (roomNum.equals(contracts.get(i).getRoomNum()) &&
                    name.equals(contracts.get(i).getName())) {
                contract = contracts.get(i);
            }
        }

        return contract;
    }

    private void calculateAmount(Contract contract) {
        endDates.clear();
        binding.etUsageFee.setText("0");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date endDate = null;
        Date checkoutDt = null;
        Date endDateWithinAMonth = null;

        int deposit = Integer.parseInt(contract.getDeposit());
        int rent = Integer.parseInt(contract.getRent());
        int manageFee = Integer.parseInt(contract.getManageFee());
        int elecAmount = Integer.parseInt(binding.etElecAmount.getText().toString());
        int gasAmount = Integer.parseInt(binding.etGasAmount.getText().toString());
        int checkoutFee = Integer.parseInt(binding.etCheckoutFee.getText().toString());
        int usageDays = 0;
        int usageFee = 0;
        boolean payment = false;



        for (int i = 0; i < trimmedData.size(); i++) {
            if (trimmedData.get(i).getEndDate() != null &&
                    trimmedData.get(i).getType().contains("월세") &&
                    binding.spResident.getSelectedItem().toString().equals(trimmedData.get(i).getDestinationName())) {
                JSLog.D("endDates               :::     " + trimmedData.get(i).getEndDate(), null);
                endDates.add(trimmedData.get(i).getEndDate());
            }
        }

        try {
            endDate = dateFormat.parse(contract.getEndDate());
            checkoutDt = dateFormat.parse(checkoutDate);
            endDateWithinAMonth = dateFormat.parse(getEndDateWithinAMonth(contract.getStartDate()));

            if (endDates.size() > 1) {
                if (dateFormat.parse(endDates.get(0)).after(dateFormat.parse(checkoutDate))) {
                    usageDays = (int) getDifference(dateFormat.parse(endDates.get(1)),
                            dateFormat.parse(checkoutDate));

                    payment = true;
                } else {
                    usageDays = (int) getDifference(dateFormat.parse(endDates.get(0)),
                            dateFormat.parse(checkoutDate));

                    payment = false;
                }
            }
            usageFee = usageDays * ((rent + manageFee) / 30);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (endDate.before(checkoutDt) ||
                contract.getEndDate().equals(checkoutDate)) {

            binding.etRealtyFees.setText(0+"");
            binding.etPenalty.setText(0+"");
            binding.etDiscount.setText(0+"");
            binding.etUsageFee.setText(usageFee+"");

            binding.tvTotal.setText("정산금액: " + deposit + "-" +
                    usageFee + "-" +
                    elecAmount + "-" +
                    gasAmount + "-" +
                    checkoutFee + "= " +
                    (deposit - usageDays - elecAmount - gasAmount - checkoutFee));
        } else {
            int stayMonth = Integer.parseInt(checkoutDate.split("-")[1]) - Integer.parseInt(contract.getStartDate().split("-")[1]);
            int penalty = (Integer.parseInt(contract.getRent()) + Integer.parseInt(contract.getManageFee())) / 30 * 10;
            int discount = 0;
            int realtyFees = 0;

            if (contract.getDiscount() != null) {
                discount = Integer.parseInt(contract.getDiscount()) * stayMonth;
            }

            if (!contract.getRoomNum().substring(0, 1).equals("1")) {
                realtyFees = (Integer.parseInt(contract.getTerm()) - stayMonth) *
                        PrefUtil.getInstance().getIntPreference("upperRealtyFees");
            } else {
                realtyFees = (Integer.parseInt(contract.getTerm()) - stayMonth) *
                        PrefUtil.getInstance().getIntPreference("underRealtyFees");
            }

            binding.etRealtyFees.setText(realtyFees+"");
            binding.etPenalty.setText(penalty+"");
            binding.etDiscount.setText(discount * stayMonth + "");

            if (endDateWithinAMonth.after(checkoutDt) ||
                    getEndDateWithinAMonth(contract.getStartDate()).equals(checkoutDate)) {


                binding.tvTotal.setText("정산금액: " + deposit + "-" +
                        elecAmount + "-" +
                        gasAmount + "-" +
                        checkoutFee + "-" +
                        realtyFees + "-" +
                        penalty + "=" +
                        (deposit - elecAmount - gasAmount - checkoutFee - realtyFees - penalty));
            } else {

                binding.etUsageFee.setText(usageFee + "");

                if (!payment) {
                    binding.tvTotal.setText("정산금액: " + deposit + "+" +
                            usageFee + "-" +
                            elecAmount + "-" +
                            gasAmount + "-" +
                            checkoutFee + "-" +
                            realtyFees + "-" +
                            penalty + "-" +
                            discount + "=" +
                            (deposit - usageFee - elecAmount - gasAmount - checkoutFee - realtyFees - penalty - discount));
                } else {
                    binding.tvTotal.setText("정산금액: " + deposit + "+" +
                            rent + "+" +
                            manageFee + "-" +
                            usageFee + "-" +
                            elecAmount + "-" +
                            gasAmount + "-" +
                            checkoutFee + "-" +
                            realtyFees + "-" +
                            penalty + "-" +
                            discount + "=" +
                            (deposit + rent + manageFee - usageFee - elecAmount - gasAmount - checkoutFee - realtyFees - penalty - discount));

                }
            }
        }

    }

    public long getDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        return elapsedDays;
    }


    private String getEndDateWithinAMonth(String startDate) {
        int year = Integer.parseInt(startDate.split("-")[0]);
        int month = Integer.parseInt(startDate.split("-")[1]);

        int monthValue = 0;
        String result = "";

        switch (month) {
            case 1:
                monthValue = Calendar.JANUARY;
                break;
            case 2:
                monthValue = Calendar.FEBRUARY;
                break;
            case 3:
                monthValue = Calendar.MARCH;
                break;
            case 4:
                monthValue = Calendar.APRIL;
                break;
            case 5:
                monthValue = Calendar.MAY;
                break;
            case 6:
                monthValue = Calendar.JUNE;
                break;
            case 7:
                monthValue = Calendar.JULY;
                break;
            case 8:
                monthValue = Calendar.AUGUST;
                break;
            case 9:
                monthValue = Calendar.SEPTEMBER;
                break;
            case 10:
                monthValue = Calendar.OCTOBER;
                break;
            case 11:
                monthValue = Calendar.NOVEMBER;
                break;
            case 12:
                monthValue = Calendar.DECEMBER;
                break;
        }
        Calendar mycal = new GregorianCalendar(year, monthValue, 1);
        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int addDay = daysInMonth - 1;

        try {

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(startDate);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            JSLog.D("addDay        :::    " + addDay, null);

            cal.add(Calendar.DATE, addDay);
            result = sdf.format(cal.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                break;

            case R.id.et_checkout_date:
                Calendar startDate = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        this,
                        startDate.get(Calendar.YEAR),
                        startDate.get(Calendar.MONTH),
                        startDate.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setTitle("퇴실일");
                dpd.show(getFragmentManager(), "checkoutDate");
                break;

            case R.id.btn_calculate:
                calculateAmount(selectedContract);
                break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String monthResult = "";
        String dayResult = "";
        if (String.valueOf(monthOfYear + 1).length() == 1) {
            monthResult = "0" + (monthOfYear + 1);
        } else {
            monthResult = String.valueOf(monthOfYear + 1);
        }
        if (String.valueOf(dayOfMonth).length() == 1) {
            dayResult = "0" + dayOfMonth;
        } else {
            dayResult = String.valueOf(dayOfMonth);
        }

        checkoutDate = year + "-" + monthResult + "-" + dayResult;

        binding.etCheckoutDate.setText(checkoutDate);
    }
}
