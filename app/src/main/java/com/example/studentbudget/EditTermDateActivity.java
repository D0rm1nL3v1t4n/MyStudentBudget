package com.example.studentbudget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditTermDateActivity extends AppCompatActivity {

    DatabaseHelper db;
    String[] message;   // 0: term name, 1: start date, 2: end date
    String startDate;
    String endDate;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_term_date);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        getIntentMessage();
        setTermName(R.id.termNameStartTextView, " start date:");
        setTermName(R.id.termNameEndTextView, " end date:");
        setCalendarDate(R.id.startDateCalendarView, message[1]);
        setCalendarDate(R.id.endDateCalendarView, message[2]);

        startDateChangeEvent();
        endDateChangeEvent();

        saveButtonClickEvent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getIntentMessage() {
        Intent intent = getIntent();
        message = intent.getStringArrayExtra(FragmentTermDates.INTENT_MESSAGE);
    }

    private void setTermName(int textViewId, String info) {    //
        TextView termNameStartTextView = findViewById(textViewId);
        termNameStartTextView.setText(message[0] + info);
    }

    private void setCalendarDate(int calendarViewId, String receivedDate) {
        CalendarView calendarView = findViewById(calendarViewId);
        Calendar date = Calendar.getInstance();

        int[] dateToSet = getDateToSet(receivedDate);

        date.set(Calendar.DAY_OF_MONTH, dateToSet[0]);
        date.set(Calendar.MONTH, dateToSet[1]);
        date.set(Calendar.YEAR, dateToSet[2]);

        calendarView.setDate(date.getTimeInMillis());
    }

    private int[] getDateToSet(String receivedDate) {
        String[] myDateSegments = receivedDate.split("/");
        String[] todaySegment = simpleDateFormat.format(new Date()).split("/");

        int day = Integer.parseInt(todaySegment[0]);
        int month = Integer.parseInt(todaySegment[1]) - 1;
        int year = Integer.parseInt(todaySegment[2]);

        if (!receivedDate.equals("dd/MM/yyyy")) {
            day = Integer.parseInt(myDateSegments[0]);
            month = Integer.parseInt(myDateSegments[1]) - 1;
            year = Integer.parseInt(myDateSegments[2]);
        }

        return new int[] {day, month, year};
    }

    private void saveButtonClickEvent() {
        Button saveButton = findViewById(R.id.saveTermDatesButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor data = db.searchData(DatabaseHelper.TABLE_NAMES[3], DatabaseHelper.COL_TERM_DATES[0], DatabaseHelper.COL_TERM_DATES[3], getTermNum());
                data.moveToLast();
                int id = data.getInt(0);

                db.updateData(DatabaseHelper.TABLE_NAMES[3], DatabaseHelper.COL_TERM_DATES[1], startDate, DatabaseHelper.COL_TERM_DATES[0], id);
                db.updateData(DatabaseHelper.TABLE_NAMES[3], DatabaseHelper.COL_TERM_DATES[2], endDate, DatabaseHelper.COL_TERM_DATES[0], id);
                Toast.makeText(getBaseContext(), "Changes saved", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void startDateChangeEvent() {
        CalendarView calendarView = findViewById(R.id.startDateCalendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                startDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            }
        });
    }

    private void endDateChangeEvent() {
        CalendarView calendarView = findViewById(R.id.endDateCalendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                endDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            }
        });
    }

    private int getTermNum() {
        int termNum = 0;
        switch (message[0]) {
            case "Autumn term":
                termNum = 1;
                break;
            case "Spring term":
                termNum = 2;
                break;
            case "Summer term":
                termNum = 3;
                break;
        }
        return termNum;
    }

}
