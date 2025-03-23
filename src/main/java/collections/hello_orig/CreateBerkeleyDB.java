package collections.hello_orig;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import java.io.File;
import java.io.UnsupportedEncodingException;

// ... [Other necessary imports] ...

public class CreateBerkeleyDB {
    public static void main(String[] args) throws UnsupportedEncodingException {
        // Set up the environment and database configuration
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
//        dbConfig.setType(DatabaseType.HASH); // Set the database type to HASH

        // Initialize the environment and database
        Environment myDbEnvironment = new Environment(new File("./tmp"), envConfig);
        Database myDatabase = myDbEnvironment.openDatabase(null, "myDatabase", dbConfig);

        // Insert some data
        String key = "exampleKey";
        String value = "exampleValuezz";
        byte[] output = new byte[100];
        DatabaseEntry theKey = new DatabaseEntry(key.getBytes("UTF-8"));
        DatabaseEntry theData = new DatabaseEntry(value.getBytes("UTF-8"));
        myDatabase.put(null, theKey, theData);

        theData = new DatabaseEntry(output);
        var status = myDatabase.get(null, theKey, theData, null);
        System.out.println(status);
        System.out.println(new String(theData.getData(), "UTF-8"));

        // Close the database and environment
        myDatabase.close();
        myDbEnvironment.close();
    }
}
