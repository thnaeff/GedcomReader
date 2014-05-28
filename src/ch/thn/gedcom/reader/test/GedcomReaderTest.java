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
package ch.thn.gedcom.reader.test;

import java.io.FileNotFoundException;
import java.io.IOException;

import ch.thn.gedcom.reader.GedcomReaderException;
import ch.thn.gedcom.reader.chart.GedcomCSVReader;
import ch.thn.gedcom.reader.chart.GedcomChartEnums.Columns;
import ch.thn.gedcom.reader.ged.GedcomGEDReader;
import ch.thn.gedcom.store.GedcomParseException;
import ch.thn.gedcom.store.GedcomStore;

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomReaderTest {
	
	
	public static void main(String[] args) {
		
		GedcomStore store = new GedcomStore();
		
		store.showParsingOutput(false);
				
		try {
			store.parse(store.getClass().getResource("/gedcomobjects_5.5.1.gedg").getPath());
		} catch (GedcomParseException e) {
			e.printStackTrace();
		}
		
		System.out.println("---");
		
		GedcomGEDReader gedReader = new GedcomGEDReader(store);
		
		try {
			gedReader.read("/home/thomas/Desktop/familienfest/test.ged");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("---");
		
		GedcomCSVReader csvReader = new GedcomCSVReader(store);
		
		Columns.ID.setColumnName("mitglied_id");
		Columns.FAMILY_NAME.setColumnName("mitglied_name");
		Columns.FIRST_NAME.setColumnName("mitglied_vorname");
		Columns.MIDDLE_NAME.setColumnName("mitglied_vorname2");
		Columns.FORMER_NAME.setColumnName("mitglied_ledigname");
		Columns.GENDER.setColumnName("mitglied_geschlecht");
		Columns.BIRTH_DATE.setColumnName("mitglied_gebdatum");
		Columns.DEATH_DATE.setColumnName("mitglied_toddatum");
		Columns.PARENT1_ID.setColumnName("mitglied_vater_mitglied_id");
		Columns.PARENT2_ID.setColumnName("mitglied_mutter_mitglied_id");
		Columns.PARTNER_ID.setColumnName("mitglied_partner_mitglied_id");
		Columns.MARRIAGE_DATE.setColumnName("mitglied_hochzeitdatum");
		Columns.CIVIL_STATUS.setColumnName("mitglied_zivilstand");
		Columns.EMAIL1.setColumnName("mitglied_email1");
		Columns.EMAIL2.setColumnName("mitglied_email2");
		Columns.STREET1.setColumnName("mitglied_strasse1");
		Columns.STREET2.setColumnName("mitglied_strasse2");
		Columns.TOWN.setColumnName("mitglied_ort");
		Columns.ZIP_CODE.setColumnName("mitglied_plz");
		Columns.COUNTRY.setColumnName("mitglied_land");
		Columns.PHONE1.setColumnName("mitglied_tel_p");
		Columns.PHONE2.setColumnName("mitglied_tel_g");
		Columns.OCCUPATION.setColumnName("mitglied_beruf");
		Columns.EDUCATION.setColumnName("mitglied_ausbildung");
		Columns.LAST_MODIFIED.setColumnName("mitglied_lastmodified");
		
		try {
			csvReader.read("/home/thomas/Desktop/familienfest/naeffen_familytree.csv", 
					GedcomCSVReader.CSV_EXCEL_NORTH_EUROPE, null, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (GedcomReaderException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
	}

}
