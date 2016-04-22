package com.hh.stringmatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

/**
 * Loader for text files that represent a list of words.
 */
public class WordlistLoader {

	/**
	 * Loads a text file associated with a given class (See
	 * {@link Class#getResourceAsStream(String)}) and adds every line as an
	 * entry to a {@link Set} (omitting leading and trailing whitespace). Every
	 * line of the file should contain only one word. The words need to be in
	 * lower-case if you make use of an Analyzer which uses LowerCaseFilter
	 * (like StandardAnalyzer).
	 * 
	 * @param aClass
	 *            a class that is associated with the given stopwordResource
	 * @param stopwordResource
	 *            name of the resource file associated with the given class
	 * @return a {@link Set} with the file's words
	 */
	public static Set<String> getWordSet(Class<?> aClass, String stopwordResource) throws IOException {
		final Reader reader = new BufferedReader(new InputStreamReader(aClass.getResourceAsStream(stopwordResource), "UTF-8"));
		try {
			return getWordSet(reader);
		} finally {
			reader.close();
		}
	}

	/**
	 * Loads a text file associated with a given class (See
	 * {@link Class#getResourceAsStream(String)}) and adds every line as an
	 * entry to a {@link Set} (omitting leading and trailing whitespace). Every
	 * line of the file should contain only one word. The words need to be in
	 * lower-case if you make use of an Analyzer which uses LowerCaseFilter
	 * (like StandardAnalyzer).
	 * 
	 * @param aClass
	 *            a class that is associated with the given stopwordResource
	 * @param stopwordResource
	 *            name of the resource file associated with the given class
	 * @param comment
	 *            the comment string to ignore
	 * @return a {@link Set} with the file's words
	 */
	public static Set<String> getWordSet(Class<?> aClass, String stopwordResource, String comment) throws IOException {
		final Reader reader = new BufferedReader(new InputStreamReader(aClass.getResourceAsStream(stopwordResource), "UTF-8"));
		try {
			return getWordSet(reader, comment);
		} finally {
			reader.close();
		}
	}

	/**
	 * Loads a text file and adds every line as an entry to a HashSet (omitting
	 * leading and trailing whitespace). Every line of the file should contain
	 * only one word. The words need to be in lowercase if you make use of an
	 * Analyzer which uses LowerCaseFilter (like StandardAnalyzer).
	 * 
	 * @param wordfile
	 *            File containing the wordlist
	 * @return A HashSet with the file's words
	 */
	public static HashSet<String> getWordSet(File wordfile) throws IOException {
		FileReader reader = null;
		try {
			reader = new FileReader(wordfile);
			return getWordSet(reader);
		} finally {
			if (reader != null)
				reader.close();
		}
	}

	/**
	 * Loads a text file and adds every non-comment line as an entry to a
	 * HashSet (omitting leading and trailing whitespace). Every line of the
	 * file should contain only one word. The words need to be in lowercase if
	 * you make use of an Analyzer which uses LowerCaseFilter (like
	 * StandardAnalyzer).
	 * 
	 * @param wordfile
	 *            File containing the wordlist
	 * @param comment
	 *            The comment string to ignore
	 * @return A HashSet with the file's words
	 */
	public static HashSet<String> getWordSet(File wordfile, String comment) throws IOException {
		FileReader reader = null;
		try {
			reader = new FileReader(wordfile);
			return getWordSet(reader, comment);
		} finally {
			if (reader != null)
				reader.close();
		}
	}

	/**
	 * Reads lines from a Reader and adds every line as an entry to a HashSet
	 * (omitting leading and trailing whitespace). Every line of the Reader
	 * should contain only one word. The words need to be in lowercase if you
	 * make use of an Analyzer which uses LowerCaseFilter (like
	 * StandardAnalyzer).
	 * 
	 * @param reader
	 *            Reader containing the wordlist
	 * @return A HashSet with the reader's words
	 */
	public static HashSet<String> getWordSet(Reader reader) throws IOException {
		final HashSet<String> result = new HashSet<String>();
		BufferedReader br = null;
		try {
			if (reader instanceof BufferedReader) {
				br = (BufferedReader) reader;
			} else {
				br = new BufferedReader(reader);
			}
			String word = null;
			while ((word = br.readLine()) != null) {
				result.add(word.trim());
			}
		} finally {
			if (br != null)
				br.close();
		}
		return result;
	}

	/**
	 * Reads lines from a Reader and adds every non-comment line as an entry to
	 * a HashSet (omitting leading and trailing whitespace). Every line of the
	 * Reader should contain only one word. The words need to be in lowercase if
	 * you make use of an Analyzer which uses LowerCaseFilter (like
	 * StandardAnalyzer).
	 * 
	 * @param reader
	 *            Reader containing the wordlist
	 * @param comment
	 *            The string representing a comment.
	 * @return A HashSet with the reader's words
	 */
	public static HashSet<String> getWordSet(Reader reader, String comment) throws IOException {
		final HashSet<String> result = new HashSet<String>();
		BufferedReader br = null;
		try {
			if (reader instanceof BufferedReader) {
				br = (BufferedReader) reader;
			} else {
				br = new BufferedReader(reader);
			}
			String word = null;
			while ((word = br.readLine()) != null) {
				if (word.startsWith(comment) == false) {
					result.add(word.trim());
				}
			}
		} finally {
			if (br != null)
				br.close();
		}
		return result;
	}

}
