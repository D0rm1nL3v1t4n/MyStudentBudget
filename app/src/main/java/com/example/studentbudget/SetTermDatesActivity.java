package com.example.studentbudget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SetTermDatesActivity extends AppCompatActivity {

    String[] termStartHeadings = {"Autumn Term Start Date", "Spring Term Start Date", "Summer Term Start Date"};
    String[] termEndHeadings = {"Autumn Term End Date", "Spring Term End Date", "Summer Term End Date"};
    String[] termStartDates = {null, null, null};
    String[] termEndDates = {null, null, null};
    int termPointer = 0;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_term_dates);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        setTermHeadings(R.id.termStartDateTextView, termStartHeadings);
        setTermHeadings(R.id.termEndDateTextView, termEndHeadings);
        buttonVisibility();
        startDateChangeEvent();
        endDateChangedEvent();
        nextClickEvent();
        previousClickEvent();
        saveClickEvent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void nextClickEvent() {
        Button nextTermButton = findViewById(R.id.nextTermButton);
        nextTermButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationButtonClickEvent(1);
            }
        });
    }

    private void previousClickEvent() {
        Button previousTermButton = findViewById(R.id.previousTermButton);
        previousTermButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationButtonClickEvent(-1);
            }
        });
    }

    private void navigationButtonClickEvent(int delta) {
        CalendarView startDateCalendar = findViewById(R.id.startDateCalendarView);
        CalendarView endDateCalendar = findViewById(R.id.endDateCalendarView);

        termPointer += delta;

        setTermHeadings(R.id.termStartDateTextView, termStartHeadings);
        setTermHeadings(R.id.termEndDateTextView, termEndHeadings);
        buttonVisibility();
        setCalendarDates(startDateCalendar, termStartDates[termPointer]);
        setCalendarDates(endDateCalendar, termEndDates[termPointer]);
    }

    private void startDateChangeEvent() {
        CalendarView startDateCalendarView = findViewById(R.id.startDateCalendarView);
        startDateCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                termStartDates[termPointer] = dayOfMonth + "/" + (month + 1) + "/" + year;
            }
        });
    }

    private void endDateChangedEvent() {
        CalendarView endDateCalendarView = findViewById(R.id.endDateCalendarView);
        endDateCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                termEndDates[termPointer] = dayOfMonth + "/" + (month + 1) + "/" + year;
            }
        });
    }

    private void setCalendarDates(CalendarView calendarView, String termDate) {
        Date date = null;
        try {
            if (termDate == null) {
                date = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
            }
            else {
                date = simpleDateFormat.parse(termDate);
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendarView.setDate(calendar.getTimeInMillis());
    }

    private void setTermHeadings(int id, String[] headings) {
        TextView termTextView = findViewById(id);
        termTextView.setText(headings[termPointer]);
    }

    private void buttonVisibility() {
        Button previousTermButton = findViewById(R.id.previousTermButton);
        Button nextTermButton = findViewById(R.id.nextTermButton);

        switch (termPointer) {
            case 0:
                previousTermButton.setVisibility(View.INVISIBLE);
                nextTermButton.setVisibility(View.VISIBLE);
                break;
            case 1:
                previousTermButton.setVisibility(View.VISIBLE);
                nextTermButton.setVisibility(View.VISIBLE);
                break;
            case 2:
                previousTermButton.setVisibility(View.VISIBLE);
                nextTermButton.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void saveClickEvent() {
        Button saveButton = findViewById(R.id.saveTermDatesButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkDatesEntered()) // check all three sets of dates have been set
                    return;
                for (int i = 0; i < 3; ++i) {
                    String[] newData = { termStartDates[i], termEndDates[i], String.valueOf(i + 1) };
                    db.insertToTermDates(newData);
                }
                Toast.makeText(getBaseContext(), "Term dates saved", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private Boolean checkDatesEntered() {
        if (null == termStartDates[0] || null == termEndDates[0]) {
            Toast.makeText(this, "Dates not entered for AUTUMN term", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (null == termStartDates[1] || null == termEndDates[1]) {
            Toast.makeText(this, "Dates not entered for SPRING term", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (null == termStartDates[2] || null == termEndDates[2]) {
            Toast.makeText(this, "Dates not entered for SUMMER term", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
