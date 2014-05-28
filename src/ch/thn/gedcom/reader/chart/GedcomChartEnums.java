/**
 *    Copyright 2014 Thomas Naeff (github.com/thnaeff)
 *
 * Licensed under the Apache License
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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomChartEnums {

	/**
	 * 
	 * 
	 *
	 * @author Thomas Naeff (github.com/thnaeff)
	 *
	 */
	public enum Columns {
		/** *Mandatory column*/
		ID("id", true), 
		/** *Mandatory column*/
		FAMILY_NAME("family_name", true), 
		/** *Mandatory column*/
		FIRST_NAME("first_name", true), 
		/** Optional column*/
		MIDDLE_NAME("middle_name"), 
		/** Optional column*/
		FORMER_NAME("former_name"), 
		/** *Mandatory column*/
		GENDER("gender", true), 
		/** Optional column*/
		BIRTH_DATE("birth_date"), 
		/** Optional column*/
		DEATH_DATE("death_date"), 
		/** *Mandatory column*/
		PARENT1_ID("parent1_id", true), 
		/** *Mandatory column*/
		PARENT2_ID("parent2_id", true),
		/** *Mandatory column*/
		PARTNER_ID("partner_id", true), 
		/** Optional column*/
		MARRIAGE_DATE("marriage_date"), 
		/** *Mandatory column*/
		CIVIL_STATUS("civil_status", true), 
		/** Optional column*/
		EMAIL1("email1"), 
		/** Optional column*/
		EMAIL2("email2"),
		/** Optional column*/
		STREET1("street1"),
		/** Optional column*/
		STREET2("street2"), 
		/** Optional column*/
		TOWN("town"), 
		/** Optional column*/
		ZIP_CODE("zip_code"), 
		/** Optional column*/
		COUNTRY("country"), 
		/** Optional column*/
		PHONE1("phone1"), 
		/** Optional column*/
		PHONE2("phone2"), 
		/** Optional column*/
		OCCUPATION("occupation"), 
		/** Optional column*/
		EDUCATION("education"), 
		/** Optional column*/
		LAST_MODIFIED("last_modified");
		
		private static final Map<String, Columns> lookup = new HashMap<>();
		
		private String value = null;
		private boolean mandatory = false;
		private static boolean refreshNeeded = true;
		
		private Columns(String value, boolean mandatory) {
			this.value = value;
			this.mandatory = mandatory;
		}
		
		private Columns(String value) {
			this.value = value;
			this.mandatory = false;
		}
		
		public String getValue() {
			return value;
		}
		
		public boolean isMandatory() {
			return mandatory;
		}
		
		public void setColumnName(String columnName) {
			value = columnName;
			refreshNeeded = true;
		}
		
		/**
		 * Returns the Enum which has the given value associated
		 * 
		 * @param enumValue
		 * @return
		 */
		public static Columns getEnumForValue(String enumValue) {
			if (refreshNeeded) {
				//The first call refreshes the lookup map
				refreshLookup();
			}
			
			if (!lookup.containsKey(enumValue)) {
				throw new IllegalArgumentException(Enum.class.getSimpleName() + 
						" does not contain an Enum with the value " + enumValue);
			}
			return lookup.get(enumValue);
		}
		
		private static void refreshLookup() {
			for(Columns w : EnumSet.allOf(Columns.class))  {
				lookup.put(w.getValue(), w);
			}
			
			refreshNeeded = false;
		}
		
		@Override
		public String toString() {
			//Marks mandatory columns with an asterisk
			return (mandatory ? "*" : "") + value;
		}
		
	}
	
	
	/**
	 * 
	 * 
	 *
	 * @author Thomas Naeff (github.com/thnaeff)
	 *
	 */
	public enum CivilStatus {
		/**The civil status is not the same for both partners*/
		MIXED(-1), 
		/**The civil status is not set for both partners*/
		UNKNOWN(0), 
		SINGLE(1), 
		MARRIED_WIDOWED(2), 
		DIVORCED(3), 
		FIXED(4); //Lebenspartner
		
		private static final Map<Integer, CivilStatus> lookup = new HashMap<>();
		static {
			for(CivilStatus w : EnumSet.allOf(CivilStatus.class)) {
				lookup.put(w.getValue(), w);
			}
		}
		
		private int value = 0;
		
		private CivilStatus(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
		
		/**
		 * Returns the Enum which has the given value associated
		 * 
		 * @param enumValue
		 * @return
		 */
		public static CivilStatus valueOf(int enumValue) {
			if (!lookup.containsKey(enumValue)) {
				throw new IllegalArgumentException(Enum.class.getSimpleName() + 
						" does not contain an Enum with the value " + enumValue);
			}
			
			return lookup.get(enumValue);
		}
		
	}
	
}
