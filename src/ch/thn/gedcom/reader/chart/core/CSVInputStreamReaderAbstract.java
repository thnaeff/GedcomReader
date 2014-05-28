/**
 *    Copyright 2014 Thomas Naeff (github.com/thnaeff)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.thn.gedcom.reader.chart.core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import ch.thn.gedcom.reader.GedcomReaderError;

/**
 * A input stream reader with a method to clean/modify the CSV data between the 
 * reading and the processing.
 * 
 * 
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public abstract class CSVInputStreamReaderAbstract extends BufferedReader {	
	
	/**
	 * 
	 * 
	 * @param sourceFile The source (.csv) file to read from
	 * @param charset The charset of the source file (usually UTF-8)
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	public CSVInputStreamReaderAbstract(String sourceFile, String charset) 
			throws UnsupportedEncodingException, FileNotFoundException {
		super(new InputStreamReader(new FileInputStream(sourceFile), charset));
		
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		String line = super.readLine();
		
		if (line == null) {
			//End of stream reached
			return -1;
		}
		
		//Make sure there is a line terminator because readLine() reads without it 
		//but read() would include it
		line = processCsvData(line, len) + "\n";
		
		if (line.length() > len) {
			throw new GedcomReaderError("Error while reading. Processed " +
					"CSV data is longer than the maximum length of " + len);
		}
		
		System.arraycopy(line.toCharArray(), 0, cbuf, 0, line.length());
		
		return line.length();
	}

	/**
	 * This method is called while reading the CSV data. The parameter <code>s</code> 
	 * contains one row of CSV data which has been read.
	 * 
	 * @param line The CSV data
	 * @param maxLength The maximum length of the processed data
	 * @return The CSV data to be passed on to SuperCSV
	 */
	protected abstract String processCsvData(String line, int maxLength);
	
	

	
	
	

}
