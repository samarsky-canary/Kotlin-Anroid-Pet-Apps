import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast

class DbManager {
    val dbName = "MyNotes"
    val dbTable = "notes"
    val colId = "id"
    val colTitle = "title"
    val colContent = "content"
    val dbVersion = 1
    val sqlCreateTable =
        "CREATE TABLE IF NOT EXISTS $dbTable ($colId INTEGER PRIMARY KEY, $colTitle TEXT, $colContent TEXT);"
    var sqlDB: SQLiteDatabase;

    constructor(context: Context) {
        val db = dbNotesHelper(context);
        sqlDB = db.writableDatabase;
    }

    fun Insert(values: ContentValues): Long {
        val id = sqlDB.insert(dbTable, "", values);
        return id;
    }
    fun Delete(selection:String, selectionArgs: Array<String>): Int{
        val count = sqlDB.delete(dbTable,selection,selectionArgs);
        return count; // count of deleted arrows
    }

    fun Update(values:ContentValues, selection: String, selectionArgs: Array<String>): Int{
        val status = sqlDB.update(dbTable,values,selection, selectionArgs);
        return status;
    }

    fun Query(
        projection: Array<String>,
        selection: String,
        selectionArgs: Array<String>,
        sortOrder: String
    ): Cursor {
        val qb = SQLiteQueryBuilder();
        qb.tables = dbTable;
        val cursor = qb.query(sqlDB, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    // helper implement SQlite methods
    inner class dbNotesHelper : SQLiteOpenHelper {
        var activityContext: Context;

        constructor(activityContext: Context) : super(activityContext, dbName, null, dbVersion) {
            this.activityContext = activityContext;
        }

        override fun onCreate(p0: SQLiteDatabase?) {
            p0!!.execSQL(sqlCreateTable);
            Toast.makeText(this.activityContext, "Database is created", Toast.LENGTH_LONG).show();
        }

        override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
            p0!!.execSQL("DROP TABLE IF EXISTS $dbTable");
        }
    }
}