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

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public interface ChartValuePreProcessor {
	
	/**
	 * Is called for each cell before the cell is processed.
	 * 
	 * @param value The value to process
	 * @param columnName The column name of the CSV file. This is not the internally 
	 * used column name, it is the column name of the actual CSV file.
	 * @return
	 */
	public Object process(Object value, String columnName); 

}
