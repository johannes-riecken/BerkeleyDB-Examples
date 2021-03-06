/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2005, 2012 Oracle and/or its affiliates.  All rights reserved.
 *
 * $Id$ 
 */

// File TxnGuide.java

package db.txn;

import com.sleepycat.bind.serial.StoredClassCatalog;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
//import com.sleepycat.je.DatabaseType;
//import com.sleepycat.je.LockDetectMode;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import java.io.File;
import java.io.FileNotFoundException;

public class TxnGuide {

    private static String myEnvPath = "./";
    private static final String dbName = "mydb.db";
    private static final String cdbName = "myclassdb.db";

    // DB handles
    private static Database myDb;
    private static Database myClassDb;
    private static Environment myEnv;

    private static final int NUMTHREADS = 5;

    private static void usage() {
        System.out.println("TxnGuide [-h <env directory>]");
        System.exit(-1);
    }

    public static void main(String[] args) {
        try {
            // Parse the arguments list
            parseArgs(args);
            // Open the environment and databases
            openEnv();
            // Get our class catalog (used to serialize objects)
            StoredClassCatalog classCatalog =
                new StoredClassCatalog(myClassDb);

            // Start the threads
            DBWriter[] threadArray;
            threadArray = new DBWriter[NUMTHREADS];
            for (int i = 0; i < NUMTHREADS; i++) {
                threadArray[i] = new DBWriter(myEnv, myDb, classCatalog);
                threadArray[i].start();
            }

            for (int i = 0; i < NUMTHREADS; i++) {
                threadArray[i].join();
            }
        } catch (Exception e) {
            System.err.println("TxnGuide: " + e);
            e.printStackTrace();
        } finally {
            closeEnv();
        }
        System.out.println("All done.");
    }

    private static void openEnv() throws DatabaseException {
        System.out.println("opening env");

        // Set up the environment.
        EnvironmentConfig myEnvConfig = new EnvironmentConfig();
        myEnvConfig.setAllowCreate(true);
//        myEnvConfig.setInitializeCache(true);
//        myEnvConfig.setInitializeLocking(true);
//        myEnvConfig.setInitializeLogging(true);
//        myEnvConfig.setRunRecovery(true);
        myEnvConfig.setTransactional(true);
        // EnvironmentConfig.setThreaded(true) is the default behavior
        // in Java, so we do not have to do anything to cause the
        // environment handle to be free-threaded.

        // Indicate that we want db to internally perform deadlock
        // detection. Also indicate that the transaction that has
        // performed the least amount of write activity to
        // receive the deadlock notification, if any.
//        myEnvConfig.setLockDetectMode(LockDetectMode.MINWRITE);

        // Set up the database
        DatabaseConfig myDbConfig = new DatabaseConfig();
//        myDbConfig.setType(DatabaseType.BTREE);
        myDbConfig.setAllowCreate(true);
        myDbConfig.setTransactional(true);
        myDbConfig.setSortedDuplicates(true);
//        myDbConfig.setReadUncommitted(true);
        // no DatabaseConfig.setThreaded() method available.
        // db handles in java are free-threaded so long as the
        // env is also free-threaded.

        // Open the environment
        myEnv = new Environment(new File(myEnvPath),    // Env home
                                myEnvConfig);

        // Open the database. Do not provide a txn handle. This open
        // is autocommitted because DatabaseConfig.setTransactional()
        // is true.
        myDb = myEnv.openDatabase(null,dbName, myDbConfig);

        // Used by the bind API for serializing objects
        // Class database must not support duplicates
        myDbConfig.setSortedDuplicates(false);
        myClassDb = myEnv.openDatabase(null,cdbName, myDbConfig);
    }

    private static void closeEnv() {
        System.out.println("Closing env and databases");
        if (myDb != null ) {
            try {
                myDb.close();
            } catch (DatabaseException e) {
                System.err.println("closeEnv: myDb: " +
                        e);
                e.printStackTrace();
            }
        }

        if (myClassDb != null ) {
            try {
                myClassDb.close();
            } catch (DatabaseException e) {
                System.err.println("closeEnv: myClassDb: " +
                        e);
                e.printStackTrace();
            }
        }

        if (myEnv != null ) {
            try {
                myEnv.close();
            } catch (DatabaseException e) {
                System.err.println("closeEnv: " + e);
                e.printStackTrace();
            }
        }
    }

    private TxnGuide() {}

    private static void parseArgs(String[] args) {
        for(int i = 0; i < args.length; ++i) {
            if (args[i].startsWith("-")) {
                switch(args[i].charAt(1)) {
                    case 'h':
                        myEnvPath = args[++i];
                        break;
                    default:
                        usage();
                }
            }
        }
    }
}
