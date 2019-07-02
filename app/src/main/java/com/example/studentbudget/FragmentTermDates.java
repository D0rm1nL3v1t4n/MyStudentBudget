package com.example.studentbudget;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTermDates extends Fragment {

    View view;
    DatabaseHelper db;
    double currentTerm = 0;
    public static final String INTENT_MESSAGE = "com.example.studentbudget.EDIT_TERM_MESSAGE";
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_term_dates, container, false);
        operations();
        return view;
    }

    private void operations() {
        db = new DatabaseHelper(getActivity());
        onResumeOperations();
        nextYearDatesClickEvent();
        autumnEditClickEvent();
        springEditClickEvent();
        summerEditClickEvent();
    }

    private void onResumeOperations() {
        setTermData(R.id.autumnStartDateTextView, R.id.autumnEndDateTextView, R.id.autumnMessageTextView,"1");
        setTermData(R.id.springStartDateTextView, R.id.springEndDateTextView, R.id.springMessageTextView,"2");
        setTermData(R.id.summerStartDateTextView, R.id.summerEndDateTextView, R.id.summerMessageTextView,"3");
        setAutumnMessage();
        setSpringMessage();
        setSummerMessage();
        toggleNextYearDatesVisibility();
    }

    @Override
    public void onResume() {
        super.onResume();
        onResumeOperations();
    }

    private void setTermData(int startDateId, int endDateId, int termMessageId, String termNumber) {
        TextView termStartDateTextView = view.findViewById(startDateId);
        TextView termEndDateTextView = view.findViewById(endDateId);
        TextView termMessageTextView = view.findViewById(termMessageId);

        Cursor startData = db.searchData(DatabaseHelper.TABLE_NAMES[3], DatabaseHelper.COL_TERM_DATES[1], DatabaseHelper.COL_TERM_DATES[3], termNumber);
        Cursor endData = db.searchData(DatabaseHelper.TABLE_NAMES[3], DatabaseHelper.COL_TERM_DATES[2], DatabaseHelper.COL_TERM_DATES[3], termNumber);
        if (startData.getCount() == 0) {
            invalidDataText(termMessageTextView, termStartDateTextView, termEndDateTextView, "NO DATES FOUND");
            return;
        }

        startData.moveToLast();
        endData.moveToLast();

        Date start = null;
        Date end = null;

        try {
            start = dateFormat.parse(startData.getString(0));
            end = dateFormat.parse(endData.getString(0));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (null == start || null == end) {
            invalidDataText(termMessageTextView, termStartDateTextView, termEndDateTextView, "INVALID DATA");
        }
        else {
            termStartDateTextView.setText(dateFormat.format(start));
            termEndDateTextView.setText(dateFormat.format(end));
            isCurrentTerm(start, end, Integer.parseInt(termNumber));
        }
    }

    private void isCurrentTerm(Date start, Date end, int termNum) {
        Date today = new Date();
        if (start.compareTo(today) < 0 && end.compareTo(today) > 0) {
            currentTerm = termNum;
        }
        else if (end.compareTo(today) < 0) {
            currentTerm = termNum + 0.5;
        }
    }

    private void setAutumnMessage() {
        TextView autumnMessage = view.findViewById(R.id.autumnMessageTextView);
        TextView autumnStartDate = view.findViewById(R.id.autumnStartDateTextView);
        if (autumnStartDate.getText().toString() != "dd/MM/yyyy") {
            if (currentTerm == 0)
                autumnMessage.setText("Next Term");
            else if (currentTerm == 1)
                autumnMessage.setText("Current Term");
            else if (currentTerm == 1.5 || currentTerm == 2)
                autumnMessage.setText("Previous Term");
            else if (currentTerm == 3.5)
                autumnMessage.setText("Year Finished");
            else
                autumnMessage.setText("");
        }
    }

    private void setSpringMessage() {
        TextView springMessage = view.findViewById(R.id.springMessageTextView);
        TextView springStartDate = view.findViewById(R.id.springStartDateTextView);
        if (springStartDate.getText().toString() != "dd/MM/yyyy") {
            if (currentTerm == 1.5)
                springMessage.setText("Next Term");
            else if (currentTerm == 2)
                springMessage.setText("Current Term");
            else if (currentTerm == 2.5 || currentTerm == 3)
                springMessage.setText("Previous Term");
            else if (currentTerm == 3.5)
                springMessage.setText("Year Finished");
            else
                springMessage.setText("");
        }
    }

    private void setSummerMessage() {
        TextView summerMessage = view.findViewById(R.id.summerMessageTextView);
        TextView summerStartDate = view.findViewById(R.id.summerStartDateTextView);
        if (summerStartDate.getText().toString() != "dd/MM/yyyy") {
            if (currentTerm == 2.5)
                summerMessage.setText("Next Term");
            else if (currentTerm == 3)
                summerMessage.setText("Current Term");
            else if (currentTerm == 3.5)
                summerMessage.setText("Year Finished");
            else
                summerMessage.setText("");
        }
    }

    private void invalidDataText(TextView termMessageTextView, TextView termStartDateTextView, TextView termEndDateTextView, String message) {
        termMessageTextView.setText(message);
        termStartDateTextView.setText("dd/MM/yyyy");
        termEndDateTextView.setText("dd/MM/yyyy");
    }

    private void toggleNextYearDatesVisibility() {
        Button nextYearDatesButton = view.findViewById(R.id.nextYearDatesButton);
        View dividerView = view.findViewById(R.id.dividerView3);
        if (currentTerm == 3.5 || db.searchData(DatabaseHelper.TABLE_NAMES[3], DatabaseHelper.COL_TERM_DATES[0]).getCount() == 0) {
            nextYearDatesButton.setVisibility(View.VISIBLE);
            dividerView.setVisibility(View.VISIBLE);
        }
        else {
            nextYearDatesButton.setVisibility(View.INVISIBLE);
            dividerView.setVisibility(View.INVISIBLE);
        }
    }

    private void nextYearDatesClickEvent() {
        Button nextYearDatesButton = view.findViewById(R.id.nextYearDatesButton);
        nextYearDatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SetTermDatesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void autumnEditClickEvent() {
        Button autumnEditButton = view.findViewById(R.id.editAutumnTermDatesButton);
        autumnEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTermDateEvent(R.id.autumnTermTextView, R.id.autumnStartDateTextView, R.id.autumnEndDateTextView);
            }
        });
    }

    private void springEditClickEvent() {
        Button springEditButton = view.findViewById(R.id.editSpringTermDatesButton);
        springEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTermDateEvent(R.id.springTermTextView, R.id.springStartDateTextView, R.id.springEndDateTextView);
            }
        });
    }

    private void summerEditClickEvent() {
        Button summerEditButton = view.findViewById(R.id.editSummerTermDatesButton);
        summerEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTermDateEvent(R.id.summerTermTextView, R.id.summerStartDateTextView, R.id.summerEndDateTextView);
            }
        });
    }

    private void editTermDateEvent(int termHeadingId, int termStartDateId, int termEndDateId) {
        TextView termHeadingTextView = view.findViewById(termHeadingId);
        TextView termStartDateTextView = view.findViewById(termStartDateId);
        TextView termEndDateTextView = view.findViewById(termEndDateId);
        String[] message = {termHeadingTextView.getText().toString(), termStartDateTextView.getText().toString(), termEndDateTextView.getText().toString()};

        Intent intent = new Intent(getActivity(), EditTermDateActivity.class);
        intent.putExtra(INTENT_MESSAGE, message);
        startActivity(intent);
    }


}
