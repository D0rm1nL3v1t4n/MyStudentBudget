package com.example.studentbudget;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLData;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "StudentBudget.db";

    public static final String[] TABLE_NAMES = {"BudgetWeek", "BudgetMonth", "BudgetTerm", "TermDates", "Categories", "Expenses", "RepeatingExpenses"};

    public static final String[] COL_BUDGET_WEEK = {"Id", "Budget", "TargetSavings", "TotalSpending", "WeekNumber"};
    public static final String[] COL_BUDGET_MONTH = {"Id", "Budget", "TargetSavings", "TotalSpending", "MonthNumber"};
    public static final String[] COL_BUDGET_TERM = {"Id", "Budget", "TargetSavings", "TotalSpending", "TermId"};
    public static final String[] COL_TERM_DATES = {"Id", "StartDate", "EndDate", "Number"};
    public static final String[] COL_CATEGORIES = {"Id", "Name", "Colour"};
    public static final String[] COL_EXPENSES = {"Id", "Name", "Date", "Price", "CategoryId", "RepeatingExpenseId"};
    public static final String[] COL_REPEATING_EXPENSES = {"Id", "Frequency", "Price"};
    Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
        mContext = context;
    }

    /*
        tableName - the name of the table that the data being searched for exists in
        returnCol - the data from this column will be returned once the data is found
            --> this can be more than one by listing the columns "col1, col2, col3" or all *
        searchCol - the column in the table that will have data compared to
        searchData - the data which is being searched for
    */

    @Override
    public void onCreate(SQLiteDatabase db) {
        createBudgetWeek(db);
        createBudgetMonth(db);
        createBudgetTerm(db);
        createTermDates(db);
        createCategories(db);
        createExpenses(db);
        createRepeatingExpenses(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAMES[3]);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAMES[5]);
        createTermDates(db);
        createExpenses(db);
    }

    public int getMaxId(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor maxIdResult = db.rawQuery("select max(Id) from " + tableName, null);
        maxIdResult.moveToFirst();
        if (maxIdResult.getString(0) == null)
            return 0;
        return Integer.parseInt(maxIdResult.getString(0));
    }

    public Cursor searchData(String tableName, String returnCol, String searchCol, String searchData) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select " + returnCol + " from " + tableName + " where " + searchCol + " = " + searchData, null);
    }

    public Cursor searchData(String tableName, String returnCol, String searchCol, int searchData) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select " + returnCol + " from " + tableName + " where " + searchCol + " = " + searchData, null);
    }

    public Cursor searchData(String tableName, String returnCol) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select " + returnCol + " from " + tableName, null);
    }

    public void clearTable(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + tableName);
    }

    public void updateData(String tableName, String updateCol, String newData, String searchCol, Object searchData) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + tableName + " SET " + updateCol + " = '" + newData + "' WHERE " + searchCol + " = " + searchData);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////// Functions to insert data ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void insertToBudgetWeek(String[] newData) {
        SQLiteDatabase db = this.getWritableDatabase();
        int maxId = getMaxId(TABLE_NAMES[0]) + 1;
        db.execSQL("INSERT INTO " + TABLE_NAMES[0] + " VALUES (" + maxId + ", ?, ?, ?, ? )", newData);
    }

    public void insertToBudgetMonth(String[] newData) {
        SQLiteDatabase db = this.getWritableDatabase();
        int maxId = getMaxId(TABLE_NAMES[1]) + 1;
        db.execSQL("INSERT INTO " + TABLE_NAMES[1] + " VALUES (" + maxId + ", ?, ?, ?, ? )", newData);
    }

    public void insertToBudgetTerm(String[] newData) {
        SQLiteDatabase db = this.getWritableDatabase();
        int maxId = getMaxId(TABLE_NAMES[2]) + 1;
        db.execSQL("INSERT INTO " + TABLE_NAMES[2] + " VALUES (" + maxId + ", ?, ?, ?, ? )", newData);
    }

    public void insertToTermDates(String[] newData) {
        SQLiteDatabase db = this.getWritableDatabase();
        int maxId = getMaxId(TABLE_NAMES[3]) + 1;
        db.execSQL("INSERT INTO " + TABLE_NAMES[3] + " VALUES (" + maxId + ", ?, ?, ? )", newData);
    }

    public void insertToCategories(String[] newData) {
        SQLiteDatabase db = this.getWritableDatabase();
        int maxId = getMaxId(TABLE_NAMES[4]) + 1;
        db.execSQL("INSERT INTO " + TABLE_NAMES[4] + " VALUES (" + maxId + ", ?, ? )", newData);
    }

    public void insertToExpenses(String[] newData) {
        SQLiteDatabase db = this.getWritableDatabase();
        int maxId = getMaxId(TABLE_NAMES[5]) + 1;
        db.execSQL("INSERT INTO " + TABLE_NAMES[5] + " VALUES (" + maxId + ", ?, ?, ?, ?, ? )", newData);
    }

    public void insertToRepeatingExpenses(String[] newData) {
        SQLiteDatabase db = this.getWritableDatabase();
        int maxId = getMaxId(TABLE_NAMES[6]) + 1;
        db.execSQL("INSERT INTO " + TABLE_NAMES[6] + " VALUES (" + maxId + ", ?, ? )", newData);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// Functions to create tables //////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void createBudgetWeek(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAMES[0] + " ( " +
                COL_BUDGET_WEEK[0] + " integer primary key, " +
                COL_BUDGET_WEEK[1] + " float, " +
                COL_BUDGET_WEEK[2] + " float, " +
                COL_BUDGET_WEEK[3] + " float, " +
                COL_BUDGET_WEEK[4] + " integer );");
    }

    private void createBudgetMonth(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAMES[1] + " ( " +
                COL_BUDGET_MONTH[0] + " integer primary key, " +
                COL_BUDGET_MONTH[1] + " float, " +
                COL_BUDGET_MONTH[2] + " float, " +
                COL_BUDGET_MONTH[3] + " float, " +
                COL_BUDGET_MONTH[4] + " integer);");
    }

    private void createBudgetTerm(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAMES[2] + " ( " +
                COL_BUDGET_TERM[0] + " integer primary key, " +
                COL_BUDGET_TERM[1] + " float, " +
                COL_BUDGET_TERM[2] + " float, " +
                COL_BUDGET_TERM[3] + " float, " +
                COL_BUDGET_TERM[4] + " integer references " + TABLE_NAMES[3] + "(" + COL_TERM_DATES[0] + "));");
    }

    private void createTermDates(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAMES[3] + " ( " +
                COL_TERM_DATES[0] + " integer primary key, " +
                COL_TERM_DATES[1] + " text, " +
                COL_TERM_DATES[2] + " text, " +
                COL_TERM_DATES[3] + " integer);");
    }

    private void createCategories(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAMES[4] + " ( " +
                COL_CATEGORIES[0] + " integer primary key, " +
                COL_CATEGORIES[1] + " text, " +
                COL_CATEGORIES[2] + " text);");
    }

    private void createExpenses(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAMES[5] + " ( " +
                COL_EXPENSES[0] + "  integer primary key, " +
                COL_EXPENSES[1] + " text, " +
                COL_EXPENSES[2] + " text, " +
                COL_EXPENSES[3] + " float, " +
                COL_EXPENSES[4] + " integer references " + TABLE_NAMES[4] + "(" + COL_CATEGORIES[0] + "), " +
                COL_EXPENSES[5] + " integer references " + TABLE_NAMES[6] + "(" + COL_REPEATING_EXPENSES[0] + "));");
    }

    private void createRepeatingExpenses(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAMES[6] + " (" +
                COL_REPEATING_EXPENSES[0] + " integer primary key, " +
                COL_REPEATING_EXPENSES[1] + " text, " +
                COL_REPEATING_EXPENSES[2] + " float);");
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////


}
