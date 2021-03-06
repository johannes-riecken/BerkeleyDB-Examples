/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.  All rights reserved.
 *
 * $Id$ 
 */

package persist.gettingStarted;

import java.io.File;

import com.sleepycat.je.DatabaseException; 
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

import java.io.FileNotFoundException;

public class SimpleStorePut {

    private static final File envHome = new File("./JEDB");

    private Environment envmnt;
    private EntityStore store;
    private SimpleDA sda; 

   // The setup() method opens the environment and store
    // for us.
    public void setup()
        throws DatabaseException {

        EnvironmentConfig envConfig = new EnvironmentConfig();
        StoreConfig storeConfig = new StoreConfig();

        envConfig.setAllowCreate(true);
//        envConfig.setInitializeCache(true);
        storeConfig.setAllowCreate(true);

        // Open the environment and entity store
        envmnt = new Environment(envHome, envConfig);
        store = new EntityStore(envmnt, "EntityStore", storeConfig);
    }

    // Close our environment and store.
    public void shutdown()
        throws DatabaseException {

        store.close();
        envmnt.close();
    } 

    private void run()
        throws DatabaseException {

        setup();

        // Open the data accessor. This is used to store
        // persistent objects.
        sda = new SimpleDA(store);

        // Instantiate and store some entity classes
        SimpleEntityClass sec1 = new SimpleEntityClass();
        SimpleEntityClass sec2 = new SimpleEntityClass();
        SimpleEntityClass sec3 = new SimpleEntityClass();
        SimpleEntityClass sec4 = new SimpleEntityClass();
        SimpleEntityClass sec5 = new SimpleEntityClass();

        sec1.setpKey("keyone");
        sec1.setsKey("skeyone");

        sec2.setpKey("keytwo");
        sec2.setsKey("skeyone");

        sec3.setpKey("keythree");
        sec3.setsKey("skeytwo");

        sec4.setpKey("keyfour");
        sec4.setsKey("skeythree");

        sec5.setpKey("keyfive");
        sec5.setsKey("skeyfour");

        sda.pIdx.put(sec1);
        sda.pIdx.put(sec2);
        sda.pIdx.put(sec3);
        sda.pIdx.put(sec4);
        sda.pIdx.put(sec5);

        sda.close();

        shutdown();
    } 

    public static void main(String[] args) {
        SimpleStorePut ssp = new SimpleStorePut();
        try {
            ssp.run();
        } catch (DatabaseException dbe) {
            System.err.println("SimpleStorePut: " + dbe);
            dbe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        } 
        System.out.println("All done.");
    } 

}
