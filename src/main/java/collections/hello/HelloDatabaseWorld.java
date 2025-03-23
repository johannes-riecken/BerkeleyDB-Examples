// extracts values of database key-value pairs as new files into tmp

package collections.hello;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import com.sleepycat.bind.ByteArrayBinding;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.collections.TransactionRunner;
import com.sleepycat.collections.TransactionWorker;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class HelloDatabaseWorld implements TransactionWorker {
    private static boolean ready = true;
    private final Environment env;
    private ClassCatalog catalog;
    private Database db;
    private SortedMap<byte[], byte[]> map;

    public static synchronized void main(String[] argv) throws Exception {
        String dir = argv[0];
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setTransactional(true);
        Environment env = new Environment(new File(dir), envConfig);

        ArrayList<HelloDatabaseWorld> workers = new ArrayList<>();
        // add all .pak files to workers
        File[] files = new File(dir).listFiles();
        assert files != null;
        for (File file : files) {
            if (file.getName().endsWith(".pak")) {
                var fileName = file.getName();
                if (fileName.equals("weapon3.pak") || fileName.equals("ogre6.pak")
                        || fileName.equals("image29.pak")
                        || fileName.equals("image39.pak")
                        || fileName.equals("image38.pak")
                        || fileName.equals("image36.pak")
                        || fileName.equals("image37.pak")
                        || fileName.equals("image35.pak")
                        || fileName.equals("patchlist.pak")
                        || fileName.equals("image34.pak")
                        || fileName.equals("image30.pak")
                        || fileName.equals("image33.pak")
                        || fileName.equals("image32.pak")
                        || fileName.equals("image41.pak")
                        || fileName.equals("image40.pak")
                        || fileName.equals("ogre9.pak")
                ) {
                    continue;
                }
                workers.add(new HelloDatabaseWorld(env, file.getName()));
            }
        }
//        String[] filenames = {"image1.pak" };
//        for (String dbName : filenames) {
//            workers.add(new HelloDatabaseWorld(env, dbName));
//        }
        TransactionRunner runner = new TransactionRunner(env);
        for (HelloDatabaseWorld worker : workers) {
            try {
                 if (!ready) {worker.wait();}
                runner.run(worker);
            } finally {
                System.out.println("Closing worker");
				worker.close();
                System.out.println("closed");
            }

        }
    }

    private HelloDatabaseWorld(Environment env, String dbName) {
        this.env = env;
        open(dbName);
    }

    @Override
    public synchronized void doWork() throws Exception {
        writeAndRead();
        ready = true;
        System.out.println("thread finished");
        notify();
    }

    private void open(String dbName) {
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setTransactional(true);
        EntryBinding<byte[]> keyBinding = new ByteArrayBinding();
        EntryBinding<byte[]> dataBinding = new ByteArrayBinding();

        this.db = env.openDatabase(null, dbName, dbConfig);

        this.map = new StoredSortedMap<>(db, keyBinding, dataBinding, true);
    }

    private void close() {

        if (catalog != null) {
            catalog.close();
            catalog = null;
        }
        if (db != null) {
            db.close();
            db = null;
        }
//        if (env != null) {
//            env.close();
//            env = null;
//        }
    }

    private void writeAndRead() throws IOException, DatabaseException {
        Iterator<Map.Entry<byte[], byte[]>> iter = map.entrySet().iterator();
        if (map.isEmpty()) {
            System.out.println("Empty");
        } else {
            System.out.println("Reading data");
        }
		String filename = "./tmp/" + db.getDatabaseName() + ".txt";
        // object for writing lines to a file
        BufferedWriter bos = new BufferedWriter(new FileWriter(filename));
        // map.get("\\conf\\vnm\\help\\11.1.6.3.\xec\x8e\x88\xec\x8e\x8e\xec\x8e\x8e\xec\x8e\xb1.txt\0".getBytes("UTF-16LE")),
        // "UTF-16LE"));
        while (iter.hasNext()) {
            Map.Entry<byte[], byte[]> entry = iter.next();
            byte[] key = entry.getKey();
            byte[] val = entry.getValue();
            bos.write(Arrays.toString(key) + " : " + Arrays.toString(val) + "\n");
        }
        bos.close();

    }
}
