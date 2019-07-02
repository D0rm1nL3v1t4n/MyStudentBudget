package com.example.studentbudget;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.lang.*;

import static java.lang.Float.isNaN;

public class EditBudgetActivity extends AppCompatActivity {

    DatabaseHelper db;
    MySharedPreferences weekSp;
    MySharedPreferences monthSp;
    MySharedPreferences termSp;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    long termLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_budget);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        weekSp = new MySharedPreferences(this, MySharedPreferences.PREFERENCE_WEEK_KEY);
        monthSp = new MySharedPreferences(this, MySharedPreferences.PREFERENCE_MONTH_KEY);
        termSp = new MySharedPreferences(this, MySharedPreferences.PREFERENCE_TERM_KEY);

        termLength = getTermLength();
        weekOperations();
        monthOperations();
        termOperations();
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

    private void weekOperations() {
        EditText savingsValueEditText = findViewById(R.id.weekSavingsValueEditText);
        EditText budgetEditText = findViewById(R.id.weekBudgetEditText);
        TextView savingsPercentageTextView = findViewById(R.id.weekSavingsPercentageTextView);

        getWeekData();
        weekSyncClickEvent();
        savingsTextChange(savingsValueEditText, budgetEditText, savingsPercentageTextView);
    }

    private void monthOperations() {
        EditText savingsValueEditText = findViewById(R.id.monthSavingsValueEditText);
        EditText budgetEditText = findViewById(R.id.monthBudgetEditText);
        TextView savingsPercentageTextView = findViewById(R.id.monthSavingsPercentageTextView);

        getMonthData();
        monthSyncEvent();
        savingsTextChange(savingsValueEditText, budgetEditText, savingsPercentageTextView);
    }

    private void termOperations() {
        EditText savingsValueEditText = findViewById(R.id.termSavingsValueEditText);
        EditText budgetEditText = findViewById(R.id.termBudgetEditText);
        TextView savingsPercentageTextView = findViewById(R.id.termSavingsPercentageTextView);

        getTermData();
        termSyncEvent();
        savingsTextChange(savingsValueEditText, budgetEditText, savingsPercentageTextView);
    }

    private void getWeekData() {
        MySharedPreferences spWeek = new MySharedPreferences(this, MySharedPreferences.PREFERENCE_WEEK_KEY);
        float[] weekData = spWeek.getAllData(); // 0: budget, 1: target savings, 2: savings, 3: spending

        EditText weekBudgetEditText = findViewById(R.id.weekBudgetEditText);
        EditText weekSavingsValueEditText = findViewById(R.id.weekSavingsValueEditText);
        TextView weekSavingsPercentageTextView = findViewById(R.id.weekSavingsPercentageTextView);

        Log.d("WEEK DATA", "Budget: " + weekData[0] + ", Target Savings: " + weekData[1]);

        weekBudgetEditText.setText(roundMe(weekData[0],2) + "");
        weekSavingsValueEditText.setText(roundMe(weekData[1], 2) + "");
        float savingsPercentage = weekData[1] * 100 / weekData[0];
        weekSavingsPercentageTextView.setText(roundMe(savingsPercentage, 1) + "%");
    }

    private void getMonthData() {
        MySharedPreferences spMonth= new MySharedPreferences(this, MySharedPreferences.PREFERENCE_MONTH_KEY);
        float[] monthData = spMonth.getAllData();   // 0: budget, 1: target savings, 2: savings, 3: spending

        EditText monthBudgetEditText = findViewById(R.id.monthBudgetEditText);
        EditText monthSavingsValueEditText = findViewById(R.id.monthSavingsValueEditText);
        TextView monthSavingsPercentageTextView = findViewById(R.id.monthSavingsPercentageTextView);

        Log.d("MONTH DATA", "Budget: " + monthData[0] + ", Target Savings: " + monthData[1]);

        monthBudgetEditText.setText(roundMe(monthData[0], 2) + "");
        monthSavingsValueEditText.setText(roundMe(monthData[1], 2) + "");
        float savingsPercentage = monthData[1] * 100 / monthData[0];
        monthSavingsPercentageTextView.setText(roundMe(savingsPercentage, 1) + "%");
    }

    private void getTermData() {
        MySharedPreferences spTerm = new MySharedPreferences(this, MySharedPreferences.PREFERENCE_TERM_KEY);
        float[] termData = spTerm.getAllData(); // 0: budget, 1: target savings, 2: savings, 3: spending

        EditText termBudgetEditText = findViewById(R.id.termBudgetEditText);
        EditText termSavingsValueEditText = findViewById(R.id.termSavingsValueEditText);
        TextView termSavingsPercentageTextView = findViewById(R.id.termSavingsPercentageTextView);

        Log.d("TERM DATA", "Budget: " + termData[0] + ", Target Savings: " + termData[1]);

        termBudgetEditText.setText(roundMe(termData[0], 2) + "");
        termSavingsValueEditText.setText(roundMe(termData[1], 2) + "");
        float savingsPercentage = termData[1] * 100 / termData[0];
        termSavingsPercentageTextView.setText(roundMe(savingsPercentage, 1) + "%");
    }

    private void savingsTextChange(final EditText savingsEditText, final EditText budgetValueEditText, final TextView percentageTextView) {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                updateSavingsPercentage(savingsEditText, budgetValueEditText, percentageTextView);
            }
        };
        savingsEditText.addTextChangedListener(textWatcher);
        budgetValueEditText.addTextChangedListener(textWatcher);
    }

    private void updateSavingsPercentage(EditText savingsEditText, EditText budgetValueEditText, TextView percentageTextView) {
        if (budgetValueEditText.getText().length() > 0) {
            float percentage;
            try {
                percentage = Float.parseFloat(savingsEditText.getText().toString()) * 100 / Integer.parseInt(budgetValueEditText.getText().toString());
                if (!isNaN(percentage))
                    percentageTextView.setText(roundMe(percentage, 1) + "%");
                else percentageTextView.setText("");
            }
            catch (java.lang.NumberFormatException e) {
                percentageTextView.setText("");
                e.printStackTrace();
            }
            catch (java.lang.ArithmeticException e){
                percentageTextView.setText("");
                e.printStackTrace();
            }
        }
        else {
            percentageTextView.setText("");
        }
    }

    private void updateOtherPercentages(String syncType) {
        if (!syncType.equals("Week")) {
            EditText savingsWeek = findViewById(R.id.weekSavingsValueEditText);
            EditText budgetWeek = findViewById(R.id.weekBudgetEditText);
            TextView percentageWeek = findViewById(R.id.weekSavingsPercentageTextView);
            setSavingsPercentage(savingsWeek, budgetWeek, percentageWeek);
        }

        if (!syncType.equals("Month")) {
            EditText savingsMonth = findViewById(R.id.monthSavingsValueEditText);
            EditText budgetMonth = findViewById(R.id.monthBudgetEditText);
            TextView percentageMonth = findViewById(R.id.monthSavingsPercentageTextView);
            setSavingsPercentage(savingsMonth, budgetMonth, percentageMonth);
        }

        if (!syncType.equals("Term")) {
            EditText savingsTerm = findViewById(R.id.termSavingsValueEditText);
            EditText budgetTerm = findViewById(R.id.termBudgetEditText);
            TextView percentageTerm = findViewById(R.id.termSavingsPercentageTextView);
            setSavingsPercentage(savingsTerm, budgetTerm, percentageTerm);
        }
    }

    private void setSavingsPercentage(EditText savingsEditText, EditText budgetValueEditText, TextView percentageTextView) {
        percentageTextView.setText(roundMe(Float.parseFloat(savingsEditText.getText().toString()) * 100 / Float.parseFloat(budgetValueEditText.getText().toString()), 1) + "%");
    }

    private void weekSyncClickEvent() {
        Button weekSyncButton = findViewById(R.id.weekSyncButton);
        weekSyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText weekBudgetEditText = findViewById(R.id.weekBudgetEditText);
                EditText weekSavingsEditText = findViewById(R.id.weekSavingsValueEditText);
                float dailyBudget = Float.parseFloat(weekBudgetEditText.getText().toString()) / 7;  //get daily budget value
                float dailySavings = Float.parseFloat(weekSavingsEditText.getText().toString()) / 7;    //get daily savings value

                float monthlyBudget = dailyBudget * 365;   //get monthly & termly budget value
                monthlyBudget /= 12;
                float termlyBudget = dailyBudget * termLength;

                float monthlySavings = dailySavings * 365; //get monthly & termly savings value
                monthlySavings /= 12;
                float termlySavings = dailySavings * termLength;

                //get editText for month & term
                EditText monthBudgetEditText = findViewById(R.id.monthBudgetEditText);
                EditText monthSavingsEditText = findViewById(R.id.monthSavingsValueEditText);
                EditText termBudgetEditText = findViewById(R.id.termBudgetEditText);
                EditText termSavingsEditText = findViewById(R.id.termSavingsValueEditText);

                //set text for month & term for budget and savings value
                monthBudgetEditText.setText(roundMe(monthlyBudget, 2) + "");
                monthSavingsEditText.setText(roundMe(monthlySavings,2) + "");
                termBudgetEditText.setText(roundMe(termlyBudget,2) + "");
                termSavingsEditText.setText(roundMe(termlySavings,2) + "");

                updateOtherPercentages("Week");
            }
        });
    }

    private void monthSyncEvent() {
        Button monthSyncButton = findViewById(R.id.monthSyncButton);
        monthSyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText monthBudgetEditText = findViewById(R.id.monthBudgetEditText);
                EditText monthSavingsEditText = findViewById(R.id.monthSavingsValueEditText);
                float dailyBudget = Float.parseFloat(monthBudgetEditText.getText().toString()) / (365/12);  //get daily budget value
                float dailySavings = Float.parseFloat(monthSavingsEditText.getText().toString()) / (365/12);    //get daily savings value

                float weeklyBudget = dailyBudget * 7;   //get weekly & termly budget value
                float termlyBudget = dailyBudget * termLength;

                float weeklySavings = dailySavings * 7; //get weekly & termly savings value
                float termlySavings = dailySavings * termLength;

                //get editText for week & term
                EditText weekBudgetEditText = findViewById(R.id.weekBudgetEditText);
                EditText weekSavingsEditText = findViewById(R.id.weekSavingsValueEditText);
                EditText termBudgetEditText = findViewById(R.id.termBudgetEditText);
                EditText termSavingsEditText = findViewById(R.id.termSavingsValueEditText);

                //set text for week & term for budget and savings value
                weekBudgetEditText.setText(roundMe(weeklyBudget,2) + "");
                weekSavingsEditText.setText(roundMe(weeklySavings, 2) + "");
                termBudgetEditText.setText(roundMe(termlyBudget, 2) + "");
                termSavingsEditText.setText(roundMe(termlySavings, 2) + "");

                updateOtherPercentages("Month");
            }
        });
    }

    private void termSyncEvent() {
        Button termSyncButton = findViewById(R.id.termSyncButton);
        termSyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText termBudgetEditText = findViewById(R.id.termBudgetEditText);
                EditText termSavingsEditText = findViewById(R.id.termSavingsValueEditText);
                float dailyBudget = Float.parseFloat(termBudgetEditText.getText().toString()) / termLength;  //get daily budget value
                float dailySavings = Float.parseFloat(termSavingsEditText.getText().toString()) / termLength;    //get daily savings value

                float weeklyBudget = dailyBudget * 7;   //get weekly & monthly budget value
                float monthlyBudget = dailyBudget * 365;
                monthlyBudget /= 12;

                float weeklySavings = dailySavings * 7; //get weekly & monthly savings value
                float monthlySavings = dailySavings * 365;
                monthlySavings /= 12;


                //get editText for week & month
                EditText weekBudgetEditText = findViewById(R.id.weekBudgetEditText);
                EditText weekSavingsEditText = findViewById(R.id.weekSavingsValueEditText);
                EditText monthBudgetEditText = findViewById(R.id.monthBudgetEditText);
                EditText monthSavingsEditText = findViewById(R.id.monthSavingsValueEditText);

                //set text for week & month for budget and savings value
                weekBudgetEditText.setText(roundMe(weeklyBudget,2) + "");
                weekSavingsEditText.setText(roundMe(weeklySavings,2) + "");
                monthBudgetEditText.setText(roundMe(monthlyBudget,2) + "");
                monthSavingsEditText.setText(roundMe(monthlySavings, 2) + "");

                updateOtherPercentages("Term");
            }
        });
    }

    private long getTermLength() {
        Cursor data = db.searchData(DatabaseHelper.TABLE_NAMES[3], DatabaseHelper.COL_TERM_DATES[1] + ", " + DatabaseHelper.COL_TERM_DATES[2]);
        Date termStartDate = null;
        Date termEndDate = null;

        data.moveToFirst();
        for (int i = 0; i < data.getCount(); ++i) {
            Date start = null;
            Date end = null;
            Date today = new Date();
            try {
                start = simpleDateFormat.parse(data.getString(0));
                end = simpleDateFormat.parse(data.getString(1));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (start.compareTo(today) < 0 && end.compareTo(today) > 0) {
                termStartDate = start;
                termEndDate = end;
            }
        }

        long daysBetween = 0;
        if (termStartDate != null) {
            daysBetween = termEndDate.getTime() - termStartDate.getTime();
        }
        daysBetween /= (1000 * 60 * 60 * 24);
        return daysBetween;
    }

    private static double roundMe(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    private void saveClickEvent() {
        Button saveButton = findViewById(R.id.saveBudgetButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText weekBudget = findViewById(R.id.weekBudgetEditText);
                EditText monthBudget = findViewById(R.id.monthBudgetEditText);
                EditText termBudget = findViewById(R.id.termBudgetEditText);
                EditText weekSavings = findViewById(R.id.weekSavingsValueEditText);
                EditText monthSavings = findViewById(R.id.monthSavingsValueEditText);
                EditText termSavings = findViewById(R.id.termSavingsValueEditText);

                float[] weekValues = {Float.parseFloat(weekBudget.getText().toString()), Float.parseFloat(weekSavings.getText().toString())};
                float[] monthValues = {Float.parseFloat(monthBudget.getText().toString()), Float.parseFloat(monthSavings.getText().toString())};
                float[] termValues = {Float.parseFloat(termBudget.getText().toString()), Float.parseFloat(termSavings.getText().toString())};
                int[] indexValues = {0, 1};
                weekSp.writePreferenceData(weekValues, indexValues);
                monthSp.writePreferenceData(monthValues, indexValues);
                termSp.writePreferenceData(termValues, indexValues);

                Toast.makeText(getBaseContext(), "Budget Saved", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

}
