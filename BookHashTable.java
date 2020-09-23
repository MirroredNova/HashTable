
//Title: HashTable
//Files: Book.java, BookHashTable.java, BookHashTableTest.java
//Description: My implementation of a HashTable for books
//
//Author: Nate Wiltzius
//Email: nwiltzius@wisc.edu
//Lecturer's Name: Debra Deppeler
//Course: CS400 Fall 2019
//Lecture: 001

import java.util.ArrayList;

// my collision resolution scheme: uses ArrayList bucks that are of the 
// Node class to store the key and the value of the node. 
// The key corresponds to the key entered, and the value is the book. 
//
// my hashing algorithm: uses the ISBN13 ID and takes the digits
// that are significant, meaning all but the last 3, and mods them
// with the current table capacity to get a mostly unique array index.

/**
 * HashTable implementation that uses:
 * 
 * @author Nate Wiltzius
 * @param <K> unique comparable identifier for each <K,V> pair, may not be null
 * @param <V> associated value with a key, value may be null
 */
public class BookHashTable implements HashTableADT<String, Book> {

	/** The initial capacity that is used if none is specifed user */
	static final int DEFAULT_CAPACITY = 101;
	int capacity;

	/** The load factor that is used if none is specified by user */
	static final double DEFAULT_LOAD_FACTOR_THRESHOLD = 0.75;
	double loadFactorThreshold;

	int numKeys; // stores number of keys in table

	ArrayList<Node>[] table; // the table

	/**
	 * Class to store the key and value of each node
	 * 
	 * @author Nate Wiltzius
	 *
	 */
	private class Node {
		private String key;
		private Book value;

		/**
		 * constructor to create a Node object with specified key and value
		 * 
		 * @param key   is the key of this node
		 * @param value is the value of this node
		 */
		private Node(String key, Book value) {
			this.key = key;
			this.value = value;
		}

		/**
		 * gets the key of this node
		 * 
		 * @return the key
		 */
		private String getKey() {
			return this.key;
		}

		/**
		 * gets the value of this node
		 * 
		 * @return the node
		 */
		private Book getValue() {
			return this.value;
		}
	}

	/**
	 * default constructor that creates the table with the default capacity and
	 * load factor threshold
	 */
	public BookHashTable() {
		this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR_THRESHOLD);
	}

	/**
	 * Creates an empty hash table with the specified capacity and load factor.
	 * 
	 * @param initialCapacity     number of elements table should hold at start.
	 * @param loadFactorThreshold the ratio of items/capacity that causes table
	 *                            to resize and rehash
	 */
	@SuppressWarnings("unchecked")
	public BookHashTable(int initialCapacity, double loadFactorThreshold) {
		this.loadFactorThreshold = loadFactorThreshold;
		this.capacity = initialCapacity;
		this.numKeys = 0;
		this.table = new ArrayList[this.capacity];
	}

	/**
	 * the hash function that takes the key, and returns an index in the table
	 * 
	 * @param key is the key to be hashed
	 * @return an int index
	 */
	public int hashFunction(String key) {

		// returns the string hashcode absolute value moded with the capacity
		return Math.abs(key.hashCode()) % this.capacity;

//		key = key.substring(0, key.length()-3);
//		// turns the key into a long
//		long newKey = (long) Double.parseDouble(key);
//		long index = newKey % this.capacity; // mods the key with the capacity
//		return (int) index; // returns the index
	}

	/**
	 * Add the key,value pair to the data structure and increase the number of
	 * keys.
	 * 
	 * @throws IllegalNullKeyException if the key is null
	 * @throws DuplicateKeyException   if the key is already in the hashtable
	 */
	@Override
	public void insert(String key, Book value)
			throws IllegalNullKeyException, DuplicateKeyException {

		// checks if key is null
		if (key == null) {
			throw new IllegalNullKeyException();
		}

		// checks that the capacity isn't over the threshold
		this.checkCapacity();

		// computes the hash index
		int index;

		index = hashFunction(key);

		// creates the bucket
		ArrayList<Node> bucket;
		// if the bucket is null, creates and adds a new ArrayList and Node
		if (this.table[index] == null) {
			bucket = new ArrayList<Node>();
			Node bucketNode = new Node(key, value);
			bucket.add(bucketNode);
			table[index] = bucket;
			// if there are other Nodes in the bucket...
		} else {
			bucket = this.table[index];
			// loops through the bucket to make sure that there are no
			// duplicates
			for (int i = 0; i < bucket.size(); i++) {
				if (bucket.get(i).getKey().equalsIgnoreCase(key)) {
					throw new DuplicateKeyException();
				}
			}

			// creates a new Node and adds it to the bucket
			Node bucketNode = new Node(key, value);
			bucket.add(bucketNode);
			table[index] = bucket;
		}
		this.numKeys++; // increment count
	}

	/**
	 * If the key is found, remove the key/value pair from the hashtable and
	 * decrease the number of keys and return true, otherwise return false.
	 * 
	 * @throws IllegalNullKeyException if the key is null
	 */
	@Override
	public boolean remove(String key) throws IllegalNullKeyException {

		// checks if the key is null
		if (key == null) {
			throw new IllegalNullKeyException();
		}

		// computes the hash index
		int index;
		index = hashFunction(key);

		// gets the bucket of the index
		try {
			ArrayList<Node> bucket = this.table[index];
			// if the bucket is null, then the key doesn't exist
			if (bucket == null) {
				return false;
			}

			// loop through the bucket (ArrayList) and check if the key equals
			// the
			// key we are looking for
			for (int i = 0; i < bucket.size(); i++) {
				if (bucket.get(i).getKey().equalsIgnoreCase(key)) {
					bucket.remove(i);
					this.numKeys--;
					return true; // if so, return true and decrease numKeys
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false; // otherwise it doesn't exist
	}

	/**
	 * Returns the value associated with the specified key, but does not remove
	 * key or decrease number of keys
	 * 
	 * @throws IllegalNullKeyException if the key is null
	 * @throws KeyNotFoundException    if the key isn't found in the hashtable
	 */
	@Override
	public Book get(String key)
			throws IllegalNullKeyException, KeyNotFoundException {

		// checks if the key is null
		if (key == null) {
			throw new IllegalNullKeyException();
		}

		// computes the hash index
		int index;
		index = hashFunction(key);

		// gets the bucket of that index
		try {
			ArrayList<Node> bucket = table[index];

			// if the bucket is null, then the key doesn't exist
			if (bucket == null) {
				throw new KeyNotFoundException();
			}

			// loops through the bucket (ArrayList) and checks if the key equals
			// the
			// key we are looking for
			for (int i = 0; i < bucket.size(); i++) {
				if (bucket.get(i).getKey().equalsIgnoreCase(key)) {
					return bucket.get(i).getValue(); // return value
				}
			}
		} catch (Exception e) {
			throw new KeyNotFoundException();
		}
		throw new KeyNotFoundException(); // otherwise thrown exception
	}

	/**
	 * return the number of keys in the hashtable
	 * 
	 * @return the number of keys
	 */
	@Override
	public int numKeys() {
		return this.numKeys;
	}

	/**
	 * return the load factor of the hashtable
	 * 
	 * @return the loadFactorThreshold
	 */
	@Override
	public double getLoadFactorThreshold() {
		return this.loadFactorThreshold;
	}

	/**
	 * return the current capacity of the hashtable
	 * 
	 * @return the table length
	 */
	@Override
	public int getCapacity() {
		return table.length;
	}

	/**
	 * verifies the capacity and makes sure it isn't over the threshold
	 */
	@SuppressWarnings("unchecked")
	private void checkCapacity() {
		// checks to see if the ratio of keys to length is greater than the
		// threshold which would mean the table needs to be expanded
		if ((double) numKeys / table.length >= loadFactorThreshold) {
			// copies old table to temp table
			ArrayList<Node>[] tempTable = this.table;
			// sets old table to new table with new capacity
			this.table = new ArrayList[(this.capacity * 2) + 1];
			this.capacity = (this.capacity * 2) + 1; // sets the capacity
														// variable

			// loops through the old table and if the index isnt null, adds the
			// key and value back into the new table, which rehashes them to the
			// appropriate location
			for (int i = 0; i < tempTable.length; i++) {
				if (tempTable[i] != null) {
					for (int j = 0; j < tempTable[i].size(); j++) {
						Book temp = tempTable[i].get(j).getValue();
						try {
							this.insert(temp.getKey(), temp);
							this.numKeys--; // undoes the act of inserting to
											// keep correct num of keys
						} catch (IllegalNullKeyException e) {
							System.out.println("Error");
						} catch (DuplicateKeyException e) {
							System.out.println("Error");
						}
					}
				}
			}
		}
	}

	/**
	 * this method returns the collision resolution scheme used for this table.
	 * In this case, I used a Chained Bucket: Array of ArrayLists, which stores
	 * all collisions in the same bucket of ArrayLists.
	 */
	@Override
	public int getCollisionResolutionScheme() {
		return 4;
	}
}