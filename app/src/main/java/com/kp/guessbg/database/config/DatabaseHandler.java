package com.kp.guessbg.database.config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kp.guessbg.models.Guess;
import com.kp.guessbg.models.GuessDto;

/**
 * Created by KO on 25-Aug-19.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "guessManager";
    private static final String TABLE_GUESSES = "guesses";
    private static final String KEY_ID = "id";
    private static final String KEY_WORD = "word";
    private static final String KEY_ACTIVITY = "activity";
    private static final String KEY_COUNT_OF_USAGES = "countOfUsages";
    private static Guess previousGuess = null;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        onCreate(this.getWritableDatabase());
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GUESSES);
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_GUESSES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_WORD + " TEXT,"
                + KEY_COUNT_OF_USAGES + " INTEGER," +  KEY_ACTIVITY + " INTEGER)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GUESSES);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    public void addGuess(GuessDto guessDto) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_WORD, guessDto.getWord());
        values.put(KEY_ACTIVITY,guessDto.getActivityEnum().ordinal());
        values.put(KEY_COUNT_OF_USAGES, 0); // Contact Phone

        // Inserting Row
        db.insert(TABLE_GUESSES, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
    public Guess getRandomGuess() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KEY_ID + "," + KEY_WORD + ","  + KEY_ACTIVITY
                + ","+ KEY_COUNT_OF_USAGES + " FROM " + TABLE_GUESSES + " WHERE id = (SELECT " + KEY_ID + " FROM "
                + TABLE_GUESSES + " ORDER BY RANDOM () LIMIT 1)", null);

        if (cursor != null)
            cursor.moveToFirst();

        Guess guess = null;
        if (cursor != null) {
            guess = new Guess(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), Integer.parseInt(cursor.getString(2)),
                    Integer.parseInt(cursor.getString(3)));
            guess.setCountOfUsages(guess.getCountOfUsages()+1);
            updateContact(guess);
            if(previousGuess != null && guess.getId().equals(previousGuess.getId())){
                return getRandomGuess();
            } else {
                previousGuess = guess;
            }
        }
        return guess;
    }



    // code to update the single contact
    private void updateContact(Guess guess) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_WORD, guess.getWord());
        values.put(KEY_COUNT_OF_USAGES, guess.getCountOfUsages());

        // updating row
        db.update(TABLE_GUESSES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(guess.getId())});
    }

    // Getting contacts Count
    private int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_GUESSES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }


}
