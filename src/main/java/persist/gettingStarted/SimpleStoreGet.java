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

public class SimpleStoreGet {

//    private static File envHome = new File("./JEDB");
    private static final File envHome = new File("./tmp");

    private Environment envmnt;
    private EntityStore store;
    private SimpleDA sda; 

   // The setup() method opens the environment and store
    // for us.
    public void setup()
        throws DatabaseException {

        EnvironmentConfig envConfig = new EnvironmentConfig();
        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setAllowCreate(true);

        // Open the environment and entity store
        envmnt = new Environment(envHome, envConfig);
//            store = new EntityStore(envmnt, "EntityStore", storeConfig);
        store = new EntityStore(envmnt, "filelist.n2z3", storeConfig);
    }

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

        System.out.println(store.getPrimaryIndex(
            String.class, SimpleEntityClass.class).count());
        // Instantiate and store some entity classes
//        SimpleEntityClass sec1 = sda.pIdx.get("keyone");
//        SimpleEntityClass sec2 = sda.pIdx.get("keytwo");
//
//        SimpleEntityClass sec4 = sda.sIdx.get("skeythree");
//
//        System.out.println("sec1: " + sec1.getpKey());
//        System.out.println("sec2: " + sec2.getpKey());
//        System.out.println("sec4: " + sec4.getpKey());
//
//        System.out.println("############ Doing pcursor ##########");
        for (SimpleEntityClass seci : sda.sec_pcursor ) {
                System.out.println("sec from pcursor : " + seci.getpKey() );
        }
//
//        sda.pIdx.delete("keyone");
//        System.out.println("############ Doing pcursor ##########");
//        System.out.println("sec from pcursor : " + sda.sec_pcursor.first().getpKey());
        for (SimpleEntityClass seci : sda.sec_pcursor ) {
                System.out.println("sec from pcursor : " + seci.getpKey() );
        }
//
//        System.out.println("############ Doing scursor ##########");
        for (SimpleEntityClass seci : sda.sec_scursor ) {
                System.out.println("sec from scursor : " + seci.getpKey() );
        }

        sda.close();
        shutdown();
    } 

    public static void main(String[] args) {
        SimpleStoreGet ssg = new SimpleStoreGet();
        try {
            ssg.run();
        } catch (DatabaseException dbe) {
            System.err.println("SimpleStoreGet: " + dbe);
            dbe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        } 
        System.out.println("All done.");
    } 

}
