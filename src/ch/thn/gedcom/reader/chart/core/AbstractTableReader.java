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

import java.util.LinkedHashMap;
import java.util.Map;

import ch.thn.gedcom.reader.GedcomReaderException;
import ch.thn.gedcom.reader.chart.GedcomChartConverterUtil;
import ch.thn.gedcom.reader.chart.GedcomChartEnums.Columns;

/**
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public abstract class AbstractTableReader {
	
	
	/** Holds the whole csv file data */
	private  Map<String, Map<Columns, Object>> chartData = null;
		
	private ChartValuePreProcessor valuePreProcessor = null;
	private ChartLinePostProcessor linePostProcessor = null;
	
	/**
	 * 
	 * 
	 * @param columnSeparator
	 * @param valuePreProcessor
	 * @param linePostProcessor
	 */
	public AbstractTableReader(ChartValuePreProcessor valuePreProcessor, 
			ChartLinePostProcessor linePostProcessor) {
		this.valuePreProcessor = valuePreProcessor;
		this.linePostProcessor = linePostProcessor;
				
		chartData = new LinkedHashMap<>();
		
	}
	
	/**
	 * 
	 * @return
	 */
	protected ChartValuePreProcessor getValuePreProcessor() {
		return valuePreProcessor;
	}
	
	/**
	 * 
	 * @return
	 */
	protected ChartLinePostProcessor getLinePostProcessor() {
		return linePostProcessor;
	}
	
	/**
	 * 
	 * 
	 * @return
	 * @throws GedcomReaderException
	 */
	protected abstract Map<Columns, Object> readLine() 
			throws GedcomReaderException;
	
	/**
	 * 
	 * 
	 * @return
	 * @throws GedcomReaderException
	 */
	public Map<String, Map<Columns, Object>> readAll() 
			throws GedcomReaderException {
		
		Map<Columns, Object> readLine = null;
		
		chartData.clear();
		
		while ((readLine = readLine()) != null) {
			if (chartData.containsKey(readLine.get(Columns.ID))) {
				System.err.println("Line with ID " + readLine.get(Columns.ID) + 
						" already exists. Overwriting previous entry.");
			}
			
			if (getLinePostProcessor() != null) {
				getLinePostProcessor().process(readLine);
			}
			
			chartData.put(GedcomChartConverterUtil.getString(readLine, Columns.ID), readLine);
		}
		
		return chartData;
	}
	
	/**
	 * 
	 * 
	 * @throws FamilyChartConverterException
	 */
	public abstract void close() throws GedcomReaderException;

}
