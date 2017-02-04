/**
 *    Copyright 2013 Thomas Naeff (github.com/thnaeff)
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import ch.thn.gedcom.reader.GedcomReaderError;
import ch.thn.gedcom.reader.GedcomReaderException;
import ch.thn.gedcom.reader.chart.GedcomChartEnums.Columns;

/**
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class CSVReader extends AbstractTableReader {

	private ICsvMapReader mapReader = null;

	private CsvPreference columnSeparator = null;

	private CellProcessor[] processors = null;

	private String[] header = null;

	/**
	 * 
	 * 
	 * @param columnSeparator
	 * @param valuePreProcessor
	 * @param linePostProcessor
	 */
	public CSVReader(CsvPreference columnSeparator,
			ChartValuePreProcessor valuePreProcessor,
			ChartLinePostProcessor linePostProcessor) {
		super(valuePreProcessor, linePostProcessor);
		this.columnSeparator = columnSeparator;

	}

	/**
	 * Opens the given file and checks if the header and the mandatory
	 * columns are correct.<br>
	 * <br>
	 * When opening a file, make sure all the mappings are already set with
	 * {@link #addHeaderMapping(String, String)} since they are needed to verify
	 * mandatory columns.
	 * 
	 * @param csvFileName
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws FamilyChartConverterError
	 */
	public void open(String csvFileName) throws IOException, FileNotFoundException {
		open(new InputStreamReader(new FileInputStream(csvFileName), "UTF-8"));
	}

	/**
	 * Read from a file with the given input reader. Also checks if the header and the mandatory
	 * columns are correct.<br>
	 * <br>
	 * When opening a file, make sure all the mappings are already set with
	 * {@link #addHeaderMapping(String, String)} since they are needed to verify
	 * mandatory columns.
	 * 
	 * @param inputReader
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void open(Reader inputReader) throws IOException {
		mapReader = new CsvMapReader(inputReader, columnSeparator);

		header = mapReader.getHeader(true);
		processors = createProcessors(header);

	}

	@Override
	protected Map<Columns, Object> readLine() throws GedcomReaderException {
		if (mapReader == null || header == null || processors == null) {
			throw new GedcomReaderError("File not open and initialized");
		}

		try {
			Map<String, Object> read = mapReader.read(header, processors);
			if (read == null) {
				//EOF
				return null;
			}

			Map<Columns, Object> ret = new HashMap<>();
			for (Entry<String, Object> entry : read.entrySet()) {
				try {
					ret.put(Columns.getEnumForValue(entry.getKey()), entry.getValue());
				} catch (IllegalArgumentException e) {
					//Enum constant not found because column label is not in the
					//standard columns
					//					System.out.println("Column " + entry.getKey() + " not used because it is not a known column.");
				}
			}
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
			throw new GedcomReaderException(e.getMessage());
		}
	}


	/**
	 * 
	 * 
	 * @throws FamilyChartConverterException
	 */
	@Override
	public void close() throws GedcomReaderException {
		try {
			mapReader.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new GedcomReaderException(e.getMessage());
		}

		mapReader = null;
		header = null;
		processors = null;
	}


	/**
	 * Creates a {@link CSVCellProcessor} for each column
	 * 
	 * @param header
	 * @return
	 */
	private CellProcessor[] createProcessors(String[] header) {

		ArrayList<CellProcessor> processors = new ArrayList<CellProcessor>();


		for (int i = 0; i < header.length; i++) {
			processors.add(new CSVCellProcessor(header[i], getValuePreProcessor()));
		}

		return processors.toArray(new CellProcessor[processors.size()]);
	}



}
