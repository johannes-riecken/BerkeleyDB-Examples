// extracts values of database key-value pairs as new files into tmp

package collections.hello;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

//import sun.io.ByteToCharUTF16;

import com.sleepycat.bind.ByteArrayBinding;
import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.collections.TransactionRunner;
import com.sleepycat.collections.TransactionWorker;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;

/**
 * @author Mark Hayes
 */
public class HelloDatabaseWorld implements TransactionWorker {

	private static final String[] INT_NAMES = { "Hello", "Database", "World", };
	private static boolean create = false;
	private static boolean ready = true;

	private Environment env;
	private ClassCatalog catalog;
	private Database db;
	private SortedMap map;

	/** Creates the environment and runs a transaction */
	public static synchronized void main(String[] argv) throws Exception {
//		 String dir = "./tmp";
//		String dir = "D:\\Users\\workspace\\BerkeleyDB-Examples\\JEDB";    // incompatible with env flags
		String dir = "D:\\Users\\Downloads\\games\\xyjdb";
		new File(dir);
		// environment is transactional
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setTransactional(true);
		envConfig.setInitializeCache(true);
		envConfig.setInitializeLocking(true);
		envConfig.setMaxMutexes(Integer.MAX_VALUE);
		envConfig.setMutexIncrement(Integer.MAX_VALUE);
		if (create) {
			envConfig.setAllowCreate(true);
		}
		Environment env = new Environment(new File(dir), envConfig);

		// create the application and run a transaction
		ArrayList<HelloDatabaseWorld> workers = new ArrayList<HelloDatabaseWorld>();
		String[] filenames = new String[] { "conf.pak", "cursor.pak",
				"data.pak", "effect1.pak", "effect2.pak", "effect3.pak",
				"effect4.pak", "effect5.pak", "effect6.pak", "effect7.pak",
				"effect8.pak", "face.pak", "fonts.pak", "game.pak",
				"image1.pak", "image11.pak", "image12.pak", "image13.pak",
				"image14.pak", "image15.pak", "image16.pak", "image17.pak",
				"image18.pak", "image19.pak", "image2.pak", "image20.pak",
				"image21.pak", "image22.pak", "image23.pak", "image24.pak",
				"image25.pak", "image26.pak", "image27.pak", "image28.pak",
				"image3.pak", "image4.pak", "image5.pak", "image6.pak",
				"image7.pak", "map.pak", "npc1.pak", "npc2.pak", "npc3.pak",
				"npc4.pak", "npc5.pak", "npc6.pak", "ogre1.pak", "ogre10.pak",
				"ogre11.pak", "ogre12.pak", "ogre13.pak", "ogre14.pak",
				"ogre15.pak", "ogre16.pak", "ogre2.pak", "ogre3.pak",
				"ogre4.pak", "ogre5.pak", "ogre7.pak", "ogre8.pak",
				"ride1.pak", "ride2.pak", "ride3.pak", "sound.pak", "spc1.pak",
				"spc2.pak", "spc3.pak", "spc4.pak", "spc5.pak", "spc6.pak",
				"spc7.pak", "spc8.pak", "spc9.pak", "task.pak", "weapon1.pak",
				"weapon2.pak" };
//		String[] filenames = new String[]{"filelist.n2z3"};
		for (String dbName : filenames) {
			workers.add(new HelloDatabaseWorld(env, dbName));
		}
		TransactionRunner runner = new TransactionRunner(env);
		for (Iterator iterator = workers.iterator(); iterator.hasNext();) {
			HelloDatabaseWorld worker = (HelloDatabaseWorld) iterator
					.next();
			try {
				// open and access the database within a transaction
				 if (!ready) {worker.wait();};
				runner.run(worker);
			} finally {
				// close the database outside the transaction
				System.out.println("Closing worker");
//				worker.close();
				System.out.println("closed");
			}
			
		}
	}

	/** Creates the database for this application */
	private HelloDatabaseWorld(Environment env, String dbName) throws Exception {

		this.env = env;
		open(dbName);
	}

	/** Performs work within a transaction. */
	public synchronized void doWork() throws Exception {

		writeAndRead();
		ready = true;
		System.out.println("thread finished");
		notify();
	}

	/** Opens the database and creates the Map. */
	private void open(String dbName) throws Exception {

		// use a generic database configuration
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setTransactional(true);
		if (create) {
			dbConfig.setAllowCreate(true);
			dbConfig.setType(DatabaseType.HASH);
			dbConfig.setByteOrder(1234);
		}

		// catalog is needed for serial bindings (java serialization)
		// changed two lines
		// Database catalogDb = env.openDatabase(null, "catalog", null,
		// dbConfig);
		// catalog = new StoredClassCatalog(catalogDb);

		// use Integer tuple binding for key entries
		// TupleBinding<Integer> keyBinding =
		// TupleBinding.getPrimitiveBinding(Integer.class);
		ByteArrayBinding keyBinding = new ByteArrayBinding();
		ByteArrayBinding dataBinding = new ByteArrayBinding();
//		TupleBinding<String> keyBinding = TupleBinding.getPrimitiveBinding(String.class);
//		TupleBinding<String> dataBinding = TupleBinding.getPrimitiveBinding(String.class);
		
		// use String serial binding for data entries
		// changed one line
		// SerialBinding<String> dataBinding = new
		// SerialBinding<String>(catalog, String.class);

		// this.db = env.openDatabase(null, "helloworld", null, dbConfig);
		this.db = env.openDatabase(null, dbName, null, dbConfig);
		// this.db = env.openDatabase(null, "task.pak", null, dbConfig);

		// create a map view of the database
		// changed one line
		this.map = new StoredSortedMap(db, keyBinding, dataBinding, true);
	}

	/** Closes the database. */
	private void close() throws Exception {

		if (catalog != null) {
			catalog.close();
			catalog = null;
		}
		if (db != null) {
			db.close();
			db = null;
		}
		if (env != null) {
			env.close();
			env = null;
		}
	}

	/**
	 * Writes and reads the database via the Map.
	 * 
	 * @throws IOException
	 * @throws DatabaseException
	 */
	private void writeAndRead() throws IOException, DatabaseException {

		// check for existing data
		// Integer key = new Integer(0);
		// String val = (String) map.get(key);
		// Object val = map.get(key);
		// if (val == null) {
		// System.out.println("Writing data");
		// // write in reverse order to show that keys are sorted
		// for (int i = INT_NAMES.length - 1; i >= 0; i -= 1) {
		// map.put(new Integer(i), INT_NAMES[i]);
		// }
		// }
		// get iterator over map entries
		Iterator<?> iter = map.entrySet().iterator();
		if (map.isEmpty()) {
			System.out.println("Empty");
		} else {
			System.out.println("Reading data");
		}
		int i = 0;
		String filename = "./tmp/" + db.getDatabaseFile() + ".txt";
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filename));
//		 BufferedWriter bos = new BufferedWriter(new FileWriter(filename));
//		bos.write(new byte[] { (byte) 0xFF, (byte) 0xFE });
		// bos.write(new byte[]{(byte)0xFF,(byte)0xFE,(byte)0x00,(byte)0x00});
		// System.out.println(new String((byte[])
		// map.get("\\conf\\vnm\\help\\11.1.6.3.ÈÎÎñ.txt\0".getBytes("UTF-16LE")),
		// "UTF-16LE"));
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			byte[] key = (byte[]) entry.getKey();
			byte[] val = (byte[]) entry.getValue();
//			String key = (String) entry.getKey();
//			String val = (String) entry.getValue();
//			bos.write(key + "\n");
//			bos.write(val + "\n");
			// val = Arrays.copyOfRange(val, 160, val.length - 1);

			// bos.write(new String((byte[]) entry.getKey(),
			// Charset.forName("UTF-16LE")) + '\t' + new String((byte[])
			// entry.getValue(),Charset.forName("UTF-16LE")) + '\n');
			// bos.write(new String((byte[]) entry.getValue(),"UTF-16LE"));
//			bos.write(key);
//			bos.write("\n".getBytes("UTF-16LE"));
			bos.write(val);
//			bos.write("\n".getBytes("UTF-16LE"));
			// bos.write(Arrays.copyOfRange(val, j, val.length - 1));
			// bos.write(val);
			// int j = 0;
			// while(j < val.length - 1) {
			// if (!(val[j] == 0 && val[j + 1] == 0)) {
			// bos.write(new byte[]{val[j], val[j+1]});
			// }
			// j+=2;
			// }
//			bos.write("\n".getBytes("UTF-16LE"));
			// bos.write("\n".getBytes("UTF-32LE"));
			if (++i > 0)
				break;
		}
		bos.close();

	}
}
