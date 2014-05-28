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

import java.sql.ResultSet;
import java.util.Map;

import ch.thn.gedcom.reader.GedcomReader;
import ch.thn.gedcom.reader.GedcomReaderException;
import ch.thn.gedcom.reader.chart.GedcomChartEnums.Columns;
import ch.thn.gedcom.reader.chart.core.AbstractTableReader;
import ch.thn.gedcom.reader.chart.core.ChartLinePostProcessor;
import ch.thn.gedcom.reader.chart.core.ResultSetReader;
import ch.thn.gedcom.reader.chart.core.ChartValuePreProcessor;
import ch.thn.gedcom.store.GedcomStore;

/**
 * Reads the data of a result set and creates the table structure which then 
 * can be converted to a gedcom structure using the {@link GedcomChartConverter}.
 * 
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomResultSetReader extends GedcomReader {
	
	
	private GedcomChartConverter converter = null;
	
	
	/**
	 * 
	 * 
	 * @param store
	 */
	public GedcomResultSetReader(GedcomStore store) {
		super(store, null);
		
		converter = new GedcomChartConverter(store);
		
	}
	
	/**
	 * 
	 * 
	 * @param resultSet
	 * @param valuePreProcessor
	 * @param linePostProcessor
	 * @return
	 * @throws GedcomReaderException
	 */
	public boolean read(ResultSet resultSet, 
			ChartValuePreProcessor valuePreProcessor, ChartLinePostProcessor linePostProcessor) 
			throws GedcomReaderException {
		
		ResultSetReader reader = new ResultSetReader(valuePreProcessor, linePostProcessor);
		reader.init(resultSet);
		Map<String, Map<Columns, Object>> data = doReading(reader);
		converter.convert(data, getStructureStorage());
		return true;
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
		
		//Read all data at once
		try {
			chartData = reader.readAll();
			
			System.out.println("Result set loaded with " + chartData.size() + " lines.");	
		} finally {
			reader.close();
		}
		
		return chartData;
	}
	

}
