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

import java.util.regex.Pattern;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.util.CsvContext;

import ch.thn.util.StringUtil;

/**
 * This cell processor just does some cleanup of the cell content
 * 
 * 
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class CSVCellProcessor extends CellProcessorAdaptor {
	
	private Pattern multipleSpaces = Pattern.compile("[ ]+");
	
	private ChartValuePreProcessor valuePreProcessor = null;
	
	private String columnName = null;
	
	/**
	 * 
	 * 
	 * @param next
	 */
	public CSVCellProcessor(CellProcessor next) {
		super(next);
	}
	
	/**
	 * 
	 * 
	 */
	public CSVCellProcessor() {
		super();
	}
	
	/**
	 * 
	 * 
	 * @param columnName
	 * @param valuePreProcessor
	 */
	protected CSVCellProcessor(String columnName, 
			ChartValuePreProcessor valuePreProcessor) {
		this();
		this.columnName = columnName;
		this.valuePreProcessor = valuePreProcessor;
	}
	
	/**
	 * 
	 * 
	 * @param valuePreProcessor
	 */
	protected void setValuePreProcessor(ChartValuePreProcessor valuePreProcessor) {
		this.valuePreProcessor = valuePreProcessor;
	}
	
	/**
	 * 
	 * 
	 * @param columnName
	 */
	protected void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public String getColumnName() {
		return columnName;
	}

	@Override
	public Object execute(Object value, CsvContext context) {
		if (valuePreProcessor != null) {
			value = valuePreProcessor.process(value, columnName);
		}
		
		if (value == null) {
			return next.execute(value, context);
		}
		
		if (value instanceof String) {
			String s = (String)value;
			
			//String cleanup
			s = StringUtil.replaceAll(multipleSpaces, s, " ");
			s = s.trim();
			
//			if (s.length() == 0) {
//				return next.execute(null, context);
//			}
			
			return next.execute(s, context);
		}
		
		
		return next.execute(value, context);
	}

}
