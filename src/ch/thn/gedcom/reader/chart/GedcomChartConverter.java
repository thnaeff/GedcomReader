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

import java.util.Map;

import ch.thn.gedcom.creator.GedcomCreatorStructureStorage;
import ch.thn.gedcom.creator.structures.GedcomFamily;
import ch.thn.gedcom.creator.structures.GedcomIndividual;
import ch.thn.gedcom.reader.GedcomReaderError;
import ch.thn.gedcom.reader.GedcomReaderUtil;
import ch.thn.gedcom.reader.chart.GedcomChartEnums.Columns;
import ch.thn.gedcom.store.GedcomStore;

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomChartConverter {
	
	private GedcomStore store = null;
	
	private GedcomCreatorStructureStorage structureStorage = null;
		
	private static int familyIdCount = 1;
	
	
	
	/**
	 * 
	 * 
	 * @param store
	 */
	public GedcomChartConverter(GedcomStore store) {
		this.store = store;
		
		structureStorage = new GedcomCreatorStructureStorage();
		
	}
	
	
	/**
	 * 
	 * 
	 * @return
	 */
	public GedcomCreatorStructureStorage getStructureStorage() {
		return structureStorage;
	}
	
	/**
	 * Converts the given chart data into gedcom individual and family records.
	 * 
	 * @param chartData A map which contains all the chart lines and their columns 
	 * with values. The inner map contains the columns with values, and the outer 
	 * map contains the individual ID as string and the inner map.<br />
	 * &lt;Individual ID &lt;Column, Cell value&gt;&gt;
	 */
	public void convert(Map<String, Map<Columns, Object>> chartData) {
		convert(chartData);
	}
	
	/**
	 * Converts the given chart data into gedcom individual and family records.
	 * 
	 * @param chartData A map which contains all the chart lines and their columns 
	 * with values. The inner map contains the columns with values, and the outer 
	 * map contains the individual ID as string and the inner map.<br />
	 * &lt;Individual ID &lt;Column, Cell value&gt;&gt;
	 * @param structureStorage Add the converted structures to this storage
	 */
	public void convert(Map<String, Map<Columns, Object>> chartData, 
			GedcomCreatorStructureStorage structureStorage) {
		this.structureStorage = structureStorage;
		
		if (chartData == null || chartData.size() == 0) {
			return;
		}
		
		//Check if all mandatory columns are present
		Columns[] allColumns = Columns.values();
		for (int i = 0; i < allColumns.length; i++) {
			if (allColumns[i].isMandatory()) {
				//Checks ALL the rows if the mandatory column is present
				//TODO use a table object -> for example google guavas Table
				for (Map<Columns, Object> row : chartData.values()) {
					if (!row.containsKey(allColumns[i])) {
						throw new GedcomReaderError("Mandatory column " + allColumns[i].getValue() + 
								" is missing from row " + row);
					}
				}
			}
		}
		
		System.out.println("Creating individuals and families...");
		for (Map<Columns, Object> individualData : chartData.values()) {
			//Creates an individual record for each line
			GedcomIndividual individual = GedcomChartConverterUtil.createIndividual(store, individualData);
			structureStorage.addIndividual(individual);
			
			//Create family from individual and partner
			String partnerId = GedcomChartConverterUtil.getString(individualData, Columns.PARTNER_ID);
			Map<Columns, Object> partnerData = chartData.get(partnerId);
			String family1Id = String.valueOf(++familyIdCount);
			GedcomFamily family1 = GedcomChartConverterUtil.createFamily(store, family1Id, individualData, partnerData);
			structureStorage.addFamily(family1Id, family1);
			
			//Create family from parents
			String parent1Id = GedcomChartConverterUtil.getString(individualData, Columns.PARENT1_ID);
			String parent2Id = GedcomChartConverterUtil.getString(individualData, Columns.PARENT2_ID);
			Map<Columns, Object> parent1Data = chartData.get(parent1Id);
			Map<Columns, Object> parent2Data = chartData.get(parent2Id);
			String family2Id = String.valueOf(++familyIdCount);
			GedcomFamily family2 = GedcomChartConverterUtil.createFamily(store, family2Id, parent1Data, parent2Data);
			structureStorage.addFamily(family2Id, family2);
		}
		
		System.out.println("Linking individuals and families...");
		//Now since all the family ID's are known, the families can be linked 
		//to their children and parents
		for (Map<Columns, Object> individualData : chartData.values()) {
			GedcomChartConverterUtil.createFamilyIndividualLinks(structureStorage, individualData);
		}
		
		System.out.println("Cleanup...");
		structureStorage.cleanup();
		
		System.out.println("Building family relations...");
		structureStorage.buildFamilyRelations();
		
		System.out.println(GedcomReaderUtil.printStorageStatistics(structureStorage));

		
	}

}
