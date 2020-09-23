
//Title: HashTable
//Files: Book.java, BookHashTable.java, BookHashTableTest.java
//Description: My implementation of a HashTable for books
//
//Author: Nate Wiltzius
//Email: nwiltzius@wisc.edu
//Lecturer's Name: Debra Deppeler
//Course: CS400 Fall 2019
//Lecture: 001

import org.junit.After;
import java.io.FileNotFoundException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test HashTable class implementation to ensure that required functionality
 * works for all cases.
 */
public class BookHashTableTest {

	// Default name of books data file
	public static final String BOOKS = "books.csv";

	// Empty hash tables that can be used by tests
	static BookHashTable bookObject;
	static ArrayList<Book> bookTable;

	static final int INIT_CAPACITY = 2;
	static final double LOAD_FACTOR_THRESHOLD = 0.49;

	static Random RNG = new Random(0); // seeded to make results repeatable
										// (deterministic)

	/** Create a large array of keys and matching values for use in any test */
	@BeforeAll
	public static void beforeClass() throws Exception {
		bookTable = BookParser.parse(BOOKS);
	}

	/** Initialize empty hash table to be used in each test */
	@BeforeEach
	public void setUp() throws Exception {
		bookObject = new BookHashTable(INIT_CAPACITY, LOAD_FACTOR_THRESHOLD);
	}

	/** Not much to do, just make sure that variables are reset */
	@AfterEach
	public void tearDown() throws Exception {
		bookObject = null;
	}

	private void insertMany(ArrayList<Book> bookTable)
			throws IllegalNullKeyException, DuplicateKeyException {
		for (int i = 0; i < bookTable.size(); i++) {
			bookObject.insert(bookTable.get(i).getKey(), bookTable.get(i));
		}
	}

	/**
	 * IMPLEMENTED AS EXAMPLE FOR YOU Tests that a HashTable is empty upon
	 * initialization
	 */
	@Test
	public void test000_collision_scheme() {
		if (bookObject == null)
			fail("Gg");
		int scheme = bookObject.getCollisionResolutionScheme();
		if (scheme < 1 || scheme > 9)
			fail("collision resolution must be indicated with 1-9");
	}

	/**
	 * IMPLEMENTED AS EXAMPLE FOR YOU Tests that a HashTable is empty upon
	 * initialization
	 */
	@Test
	public void test001_IsEmpty() {
		// "size with 0 entries:"
		assertEquals(0, bookObject.numKeys());
	}

	/**
	 * IMPLEMENTED AS EXAMPLE FOR YOU Tests that a HashTable is not empty after
	 * adding one (key,book) pair
	 * 
	 * @throws DuplicateKeyException
	 * @throws IllegalNullKeyException
	 */
	@Test
	public void test002_IsNotEmpty()
			throws IllegalNullKeyException, DuplicateKeyException {
		bookObject.insert(bookTable.get(0).getKey(), bookTable.get(0));
		String expected = "" + 1;
		// "size with one entry:"
		assertEquals(expected, "" + bookObject.numKeys());
	}

	/**
	 * IMPLEMENTED AS EXAMPLE FOR YOU Test if the hash table will be resized
	 * after adding two (key,book) pairs given the load factor is 0.49 and
	 * initial capacity to be 2.
	 */
	@Test
	public void test003_Resize()
			throws IllegalNullKeyException, DuplicateKeyException {
		bookObject.insert(bookTable.get(0).getKey(), bookTable.get(0));
		int cap1 = bookObject.getCapacity();
		bookObject.insert(bookTable.get(1).getKey(), bookTable.get(1));
		int cap2 = bookObject.getCapacity();

		// "size with one entry:"
		assertTrue(cap2 > cap1 & cap1 == 2);
	}

	/**
	 * Test to make sure that the hash function returns the correct number when
	 * given a key
	 */
	@Test
	public void test004_HashFunction() {
		assertEquals(bookObject.hashFunction("409801"),
				"409801".hashCode() % bookObject.capacity);
	}

	/**
	 * test to make sure that when inserting one value, it can be retrieved
	 * using its key
	 * 
	 * @throws IllegalNullKeyException
	 * @throws DuplicateKeyException
	 * @throws KeyNotFoundException
	 */
	@Test
	public void test005_InsertOne() throws IllegalNullKeyException,
			DuplicateKeyException, KeyNotFoundException {
		bookObject.insert(bookTable.get(0).getKey(), bookTable.get(0));
		assertEquals(bookObject.get(bookTable.get(0).getKey()),
				bookTable.get(0));
	}

	/**
	 * test to make sure that when inserting many values, the correct amount of
	 * pairs were added to the table
	 * 
	 * @throws IllegalNullKeyException
	 * @throws DuplicateKeyException
	 */
	@Test
	public void test006_InsertMany()
			throws IllegalNullKeyException, DuplicateKeyException {
		insertMany(bookTable);
		assertEquals(bookObject.numKeys, bookTable.size());
	}

	/**
	 * test to make sure that an IllegalNullKeyException was thrown when one
	 * should have been thrown
	 */
	@Test
	public void test007_InsertNull() {
		try {
			bookObject.insert(null, bookTable.get(0));
			fail("No Exception was thrown when IllegalNullKeyException should have been.");
		} catch (IllegalNullKeyException e) {
			assertEquals(0, bookObject.numKeys);
		} catch (DuplicateKeyException e) {
			fail("DuplicateKeyException thrown when one should not have been thrown.");
		}
	}

	/**
	 * test to make sure that a DuplicateKeyException was thrown when one should
	 * have been thrown
	 */
	@Test
	public void test008_InsertDuplicate() {
		try {
			bookObject.insert(bookTable.get(0).getKey(), bookTable.get(0));
			bookObject.insert(bookTable.get(0).getKey(), bookTable.get(1));
			fail("No Exception was thrown when IllegalNullKeyException should have been.");
		} catch (IllegalNullKeyException e) {
			fail("DuplicateKeyException thrown when one should not have been thrown.");
		} catch (DuplicateKeyException e) {
			assertEquals(1, bookObject.numKeys);
		}
	}

	/**
	 * test to make sure that getting a key that exists in the table returns the
	 * correct value
	 */
	@Test
	public void test009_GetKeyThatExists() {
		try {
			bookObject.insert(bookTable.get(0).getKey(), bookTable.get(0));

			try {
				bookObject.insert(bookTable.get(0).getKey(), bookTable.get(1));
			} catch (DuplicateKeyException e) {

			}

			assertEquals(bookObject.get(bookTable.get(0).getKey()),
					bookTable.get(0));
		} catch (IllegalNullKeyException e) {
			e.printStackTrace();
		} catch (DuplicateKeyException e) {
			e.printStackTrace();
		} catch (KeyNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * test to make sure that getting a key that doesn't exist throws the
	 * correct exception
	 */
	@Test
	public void test010_GetKeyThatDoesNotExist() {
		try {
			bookObject.insert(bookTable.get(0).getKey(), bookTable.get(0));

			bookObject.get("nothing");
			fail("No Exception was thrown when a KeyNotFoundException should have been thrown");
		} catch (IllegalNullKeyException e) {
			fail("IllegalNullKeyException was thrown when a KeyNotFoundException should have been thrown");
		} catch (DuplicateKeyException e) {
			fail("DuplicateKeyException was thrown when a KeyNotFoundException should have been thrown");
		} catch (KeyNotFoundException e) {

		}
	}

	/**
	 * test to make sure that getting a null key throws the right exception
	 */
	@Test
	public void test011_GetKeyThatIsNull() {
		try {
			bookObject.get(null);
			fail("No Exception was thrown when a KeyNotFoundException should have been thrown");
		} catch (IllegalNullKeyException e) {

		} catch (KeyNotFoundException e) {
			fail("KeyNotFoundException was thrown when a KeyNotFoundException should have been thrown");
		}
	}

	/**
	 * test to make sure that removing a key that exists in the table is
	 * actually removed and cannot be retrived later
	 */
	@Test
	public void test012_RemoveKeyThatExists() {
		try {
			bookObject.insert(bookTable.get(0).getKey(), bookTable.get(0));
			bookObject.insert(bookTable.get(1).getKey(), bookTable.get(1));

			bookObject.remove(bookTable.get(0).getKey());

			bookObject.get(bookTable.get(0).getKey());
			fail("No Exception was thrown when a KeyNotFoundException should have been thrown");
		} catch (IllegalNullKeyException e) {
			fail("IllegalNullKeyException was thrown when a KeyNotFoundException should have been thrown");
		} catch (DuplicateKeyException e) {
			fail("DuplicateKeyException was thrown when a KeyNotFoundException should have been thrown");
		} catch (KeyNotFoundException e) {

		}
	}

	/**
	 * test to make sure that false is retruned when a key that doesn't exist is
	 * returned
	 */
	@Test
	public void test013_RemoveKeyThatDoesNotExist() {
		try {
			assertEquals(bookObject.remove("nothing"), false);
		} catch (IllegalNullKeyException e) {
			fail("IllegalNullKeyException was thrown when no exception should have been thrown");
		}
	}

	/**
	 * test to make sure that an IllegalNullKeyException is thrown when a null
	 * key is removed
	 */
	@Test
	public void test014_RemoveNullKey() {
		try {
			bookObject.remove(null);
			fail("No Exception was thrown when a IllegalNullKeyException should have been thrown");
		} catch (IllegalNullKeyException e) {

		}

	}
}
