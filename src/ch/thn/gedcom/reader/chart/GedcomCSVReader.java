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
package ch.thn.gedcom.reader.chart;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import org.supercsv.prefs.CsvPreference;
import ch.thn.gedcom.reader.GedcomReader;
import ch.thn.gedcom.reader.GedcomReaderException;
import ch.thn.gedcom.reader.chart.GedcomChartEnums.Columns;
import ch.thn.gedcom.reader.chart.core.AbstractTableReader;
import ch.thn.gedcom.reader.chart.core.CSVReader;
import ch.thn.gedcom.reader.chart.core.ChartLinePostProcessor;
import ch.thn.gedcom.reader.chart.core.ChartValuePreProcessor;
import ch.thn.gedcom.store.GedcomStore;

/**
 * Reads the data of a CSV file and creates the table structure which then 
 * can be converted to a gedcom structure using the {@link GedcomChartConverter}.
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomCSVReader extends GedcomReader {
	
	public static final int CSV_STANDARD = 0;
	public static final int CSV_EXCEL = 1;
	public static final int CSV_EXCEL_NORTH_EUROPE = 2;
	public static final int CSV_TAB = 3;
	
	private GedcomChartConverter converter = null;
		
	
	/**
	 * 
	 * 
	 * @param store
	 */
	public GedcomCSVReader(GedcomStore store) {
		super(store, "csv");
		
		converter = new GedcomChartConverter(store);
	}
	
	/**
	 * Reads the data of the whole CSV file
	 * 
	 * @param csvFileName
	 * @param columnSeparator
	 * @param valuePreProcessor
	 * @param linePostProcessor
	 * @return
	 * @throws GedcomReaderException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public boolean read(String csvFileName, int columnSeparator, 
			ChartValuePreProcessor valuePreProcessor, ChartLinePostProcessor linePostProcessor) 
			throws GedcomReaderException, FileNotFoundException, IOException {
		CSVReader reader = new CSVReader(getCsvPreference(columnSeparator), valuePreProcessor, linePostProcessor);
		reader.open(csvFileName);
		Map<String, Map<Columns, Object>> data = doReading(reader);
		converter.convert(data, getStructureStorage());
		return true;
	}
	
	/**
	 * Reads the data of the whole CSV file by using the given input reader
	 * 
	 * @param inputReader
	 * @param columnSeparator
	 * @param valuePreProcessor
	 * @param linePostProcessor
	 * @return
	 * @throws GedcomReaderException
	 * @throws IOException
	 */
	public boolean read(Reader inputReader, int columnSeparator, 
			ChartValuePreProcessor valuePreProcessor, ChartLinePostProcessor linePostProcessor) 
			throws GedcomReaderException, IOException {
		CSVReader reader = new CSVReader(getCsvPreference(columnSeparator), valuePreProcessor, linePostProcessor);
		reader.open(inputReader);
		Map<String, Map<Columns, Object>> data = doReading(reader);
		converter.convert(data, getStructureStorage());
		return true;
	}
	
	/**
	 * 
	 * 
	 * @param separator
	 * @return
	 */
	private CsvPreference getCsvPreference(int separator) {
		switch (separator) {
		case CSV_STANDARD:
			return CsvPreference.STANDARD_PREFERENCE;
		case CSV_EXCEL:
			return CsvPreference.EXCEL_PREFERENCE;
		case CSV_EXCEL_NORTH_EUROPE:
			return CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE;
		case CSV_TAB:
			return CsvPreference.TAB_PREFERENCE;
		default:
			return CsvPreference.STANDARD_PREFERENCE;
		}
	}
	
	/**
	 * 
	 * 
	 * @param reader
	 * @return
	 * @throws GedcomReaderException
	 */
	private Map<String, Map<Columns, Object>> doReading(AbstractTableReader reader) throws GedcomReaderException {
		Map<String, Map<Columns, Object>> chartData = null;
		
		//Read all CSV data at once
		try {
			chartData = reader.readAll();
			
			System.out.println("CSV data loaded with " + chartData.size() + " lines.");	
		} finally {
			reader.close();
		}
		
		return chartData;
	}
	

}
