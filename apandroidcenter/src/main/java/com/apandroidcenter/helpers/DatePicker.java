package com.apandroidcenter.helpers;

import com.apandroidcenter.BaseActivity;
import com.apandroidcenter.interfaces.DatePickerListener;
import com.apandroidcenter.type.DateDisableType;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatePicker {

    private BaseActivity BaseActivity;
    private String title = null;
    private int themeResId = 0;
    private DateDisableType dateDisableType = null;

    private long fromSpecificDate = 0;
    private long toSpecificDate = 0;
    private long specificDate = 0;

    private MaterialDatePicker.Builder<Long> builder;
    private DatePickerListener callBack;


    public DatePicker(BaseActivity BaseActivity, DatePickerListener callBack) {
        this.BaseActivity = BaseActivity;
        this.callBack = callBack;
    }

    public DatePicker(BaseActivity BaseActivity, String title, DatePickerListener callBack) {
        this.BaseActivity = BaseActivity;
        this.title = title;
        this.callBack = callBack;
    }

    public DatePicker(BaseActivity BaseActivity,
                      String title,
                      int themeResId,
                      DateDisableType dateDisableType,
                      long specificDate,
                      DatePickerListener callBack) {
        this.BaseActivity = BaseActivity;
        this.title = title;
        this.themeResId = themeResId;
        this.dateDisableType = dateDisableType;
        this.specificDate = specificDate;
        this.callBack = callBack;
    }

    public void callDatePicker() {
        builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText(title);
        builder.setTheme(themeResId);

        if (dateDisableType != null && specificDate != 0) {
            disableDates(dateDisableType);
        }


        MaterialDatePicker<Long> picker = builder.build();
        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                //String dateString = dateFormat.format(new Date(selection));
                callBack.onDate(selection, dateFormat.format(new Date(selection)));
            }
        });
        picker.show(BaseActivity.getSupportFragmentManager(), picker.toString());
    }

    private void disableDates(DateDisableType dateDisableType) {
        CalendarConstraints calendarConstraints = null;
        switch (dateDisableType) {
            case PastDateFromSpecific:
                CalendarConstraints.DateValidator dateValidator = DateValidatorPointForward.from(specificDate);
                calendarConstraints = new CalendarConstraints.Builder()
                        .setValidator(dateValidator)
                        .build();
                break;
            case FutureDateFromSpecific:
                CalendarConstraints.DateValidator dateValidator1 = DateValidatorPointBackward.before(specificDate);
                calendarConstraints = new CalendarConstraints.Builder()
                        .setValidator(dateValidator1)
                        .build();
                break;
        }

        if (calendarConstraints != null) {
            builder.setCalendarConstraints(calendarConstraints);
        }
    }
}

