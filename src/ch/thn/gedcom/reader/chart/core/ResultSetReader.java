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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import ch.thn.gedcom.reader.GedcomReaderError;
import ch.thn.gedcom.reader.GedcomReaderException;
import ch.thn.gedcom.reader.chart.GedcomChartEnums.Columns;

/**
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class ResultSetReader extends AbstractTableReader {
		
	private String[] header = null;
	
	private ResultSet resultSet = null;
	
	/**
	 * 
	 * 
	 * @param valuePreProcessor
	 * @param linePostProcessor
	 */
	public ResultSetReader(ChartValuePreProcessor valuePreProcessor, 
			ChartLinePostProcessor linePostProcessor) {
		super(valuePreProcessor, linePostProcessor);
		
	}
	
	/**
	 * 
	 * 
	 * @param resultSet
	 * @throws GedcomReaderError
	 */
	public void init(ResultSet resultSet) {
		this.resultSet = resultSet;
		
		try {
			ResultSetMetaData rsmd = resultSet.getMetaData();
			int columnCount = rsmd.getColumnCount();
			header = new String[columnCount];
			
			for (int i = 1; i <= columnCount; i++) {
				header[i - 1] = rsmd.getColumnName(i);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GedcomReaderError(e.getMessage());
		}
		
	}
	
	@Override
	protected Map<Columns, Object> readLine() throws GedcomReaderException {
				
		LinkedHashMap<Columns, Object> line = new LinkedHashMap<>();
		
		try {
			if (!resultSet.next()) {
				return null;
			}
			
			for (int i = 0; i < header.length; i++) {
				Object value = resultSet.getObject(header[i]);
				
				if (getValuePreProcessor() != null) {
					value = getValuePreProcessor().process(value, header[i]);
				}
				
				try {
					line.put(Columns.getEnumForValue(header[i]), value);
				} catch (IllegalArgumentException e) {
					//Enum constant not found because column label is not in the 
					//standard columns
//					System.out.println("Column " + header[i] + " not used because it is not a known column.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GedcomReaderException(e.getMessage());
		}
		
		return line;
	}
	
	
	@Override
	public void close() throws GedcomReaderException {
		//Nothing to do
	}
	

}
