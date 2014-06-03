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

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import ch.thn.gedcom.GedcomFormatter;
import ch.thn.gedcom.GedcomHelper;
import ch.thn.gedcom.creator.GedcomCreatorStructureStorage;
import ch.thn.gedcom.creator.GedcomEnums.*;
import ch.thn.gedcom.creator.structures.GedcomEOF;
import ch.thn.gedcom.creator.structures.GedcomFamily;
import ch.thn.gedcom.creator.structures.GedcomHeader;
import ch.thn.gedcom.creator.structures.GedcomIndividual;
import ch.thn.gedcom.creator.structures.GedcomSubmitter;
import ch.thn.gedcom.data.GedcomError;
import ch.thn.gedcom.reader.GedcomReaderError;
import ch.thn.gedcom.reader.chart.GedcomChartEnums.CivilStatus;
import ch.thn.gedcom.reader.chart.GedcomChartEnums.Columns;
import ch.thn.gedcom.store.GedcomStore;

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomChartConverterUtil {
	
	/**
	 * 
	 * 
	 * @param store
	 * @return
	 */
	public static GedcomEOF createEOF(GedcomStore store) {
		return new GedcomEOF(store);
	}
	
	/**
	 * 
	 * 
	 * @param store
	 * @param submitterRecordId
	 * @param gedcomVersion
	 * @param characterSet
	 * @param language
	 * @return
	 */
	public static GedcomHeader createHeader(GedcomStore store, 
			String submitterRecordId, String gedcomVersion, String characterSet, String language) {
		GedcomHeader header = new GedcomHeader(store);
		
		header.setSource("ch.thn.gedcom.reader", GedcomCSVReader.class.getSimpleName(), null);
		header.setTransmissionDate(GedcomFormatter.getGedcomDate(new Date(), true, true), GedcomFormatter.getGedcomTime(new Date()));
		header.setSubmitterRecordLink(submitterRecordId);
		header.setGedcomInfo(gedcomVersion, "LINEAGE-LINKED");
		header.setCharacterSet(characterSet);
		header.setLanguage(language);
		
		return header;
	}
	
	/**
	 * 
	 * 
	 * @param store
	 * @param submitterRecordId
	 * @param submitterName
	 * @return
	 */
	public static GedcomSubmitter createSubmitter(GedcomStore store, String submitterRecordId, 
			String submitterName) {
		GedcomSubmitter submitter = new GedcomSubmitter(store, submitterRecordId);
		
		submitter.setSubmitterName(submitterName);
		
		return submitter;
	}
	
	/**
	 * 
	 * 
	 * @param store
	 * @param familyId
	 * @param parent1CsvData
	 * @param parent2CsvData
	 * @return
	 */
	public static GedcomFamily createFamily(GedcomStore store, String familyId, 
			Map<Columns, Object> parent1CsvData, Map<Columns, Object> parent2CsvData) {
		
		GedcomFamily family = new GedcomFamily(store, familyId);
		
		String parent1Id = null;
		String parent2Id = null;
		String parent1Gender = null;
		String parent2Gender = null;
		String marriageDateString = null;
		String changeDate = null;
		CivilStatus civilStatus = null;
		
		if (parent1CsvData != null) {
			parent1Id = getString(parent1CsvData, Columns.ID);
			parent1Gender = getString(parent1CsvData, Columns.GENDER);
			
			if (parent1Gender != null) {
				parent1Gender = parent1Gender.toUpperCase();
			}
		}
		
		if (parent2CsvData != null) {
			parent2Id = getString(parent2CsvData, Columns.ID);
			parent2Gender = getString(parent2CsvData, Columns.GENDER);
			
			if (parent2Gender != null) {
				parent2Gender = parent2Gender.toUpperCase();
			}
		}
		
		if (parent1CsvData != null && parent2CsvData != null) {
			civilStatus = getCivilStatus(parent1CsvData, parent2CsvData);
			marriageDateString = getMarriageDateString(parent1CsvData, parent2CsvData);
			changeDate = getChangeDateTime(parent1CsvData, parent2CsvData);;
		}
		
		//TODO adjust gender if both parents have the same gender

		//Set HUSB and WIFE
		if (parent1Id != null && parent2Id != null) {
			if (Sex.MALE.getValue().equals(parent1Gender) 
					&& Sex.FEMALE.getValue().equals(parent2Gender)) {
				//Parent1 is husband
				family.setHusbandLink(parent1Id);
				family.setWifeLink(parent2Id);
			} else if (Sex.MALE.getValue().equals(parent2Gender) 
					&& Sex.FEMALE.getValue().equals(parent1Gender)) {
				//Parent2 is husband
				family.setHusbandLink(parent2Id);
				family.setWifeLink(parent1Id);
			} else if (Sex.MALE.getValue().equals(parent1Gender) 
					&& Sex.MALE.getValue().equals(parent2Gender)
					|| Sex.FEMALE.getValue().equals(parent1Gender) 
					&& Sex.FEMALE.getValue().equals(parent2Gender)) {
				//Both parents have the same gender
				family.setHusbandLink(parent1Id);
				family.setWifeLink(parent2Id);
				System.out.println("Genders of parent " + parent1Id + 
						" (" + parent1Gender + ")" + " and " + parent2Id + 
						" (" + parent2Gender + ") are the same. Parent " + parent1Id + 
						" has been assigned to the family as wife.");
			} else {
				throw new GedcomReaderError("Genders of parent " + parent1Id + 
						" (" + parent1Gender + ")" + " and " + parent2Id + 
						" (" + parent2Gender + ") are not valid. Valid genders: " + 
						Arrays.toString(Sex.values()));
			}
		} else if (parent1Id != null) {
			if (Sex.MALE.getValue().equals(parent1Gender)) {
				//Parent1 is husband
				family.setHusbandLink(parent1Id);
			} else if (Sex.FEMALE.getValue().equals(parent1Gender)) {
				//Parent1 is wife
				family.setWifeLink(parent1Id);
			} else {
				throw new GedcomReaderError("Gender of parent " + parent1Id + 
						" (" + parent1Gender + ") is not valid. Valid genders: " + 
						Arrays.toString(Sex.values()));
			}
		} else if (parent2Id != null) {
			if (Sex.MALE.getValue().equals(parent2Gender)) {
				//Parent2 is husband
				family.setHusbandLink(parent2Id);
			} else if (Sex.FEMALE.getValue().equals(parent2Gender)) {
				//Parent2 is wife
				family.setWifeLink(parent2Id);
			} else {
				throw new GedcomReaderError("Gender of parent " + parent2Id + 
						" (" + parent2Gender + ") is not valid. Valid genders: " + 
						Arrays.toString(Sex.values()));
			}
		}
		
		if (changeDate != null) {
			String[] dateTime = splitDateTime(changeDate);
			family.setChangeDate(dateTime[0], dateTime[1]);
		}
		
//		if (parentIdGenderChange != 0) {	
//			family.addNote("Gender of individual " + parentIdGenderChange + " in this family " + 
//							familyId + " has been changed to the opposite sex in order to create the " +
//							"family (the gedcom definition does not support same-sex marriage). " +
//							"The change only affects the reference of HUSB or WIFE in this family " +
//							"and no changes have been done on any individual record.");
//
//		}
		
		if (civilStatus != null) {
			switch (civilStatus) {
			case UNKNOWN:
				//nothing
				break;
			case SINGLE:
				//nothing
				break;
			case MARRIED_WIDOWED:
				family.setMarried(true, marriageDateString);
				break;
			case MIXED:
				family.addNote("Civil status: Unknown. Parents do not have the same civil status.");
				break;
			case DIVORCED:
				family.setDivorced(true, null);
				break;
			case FIXED:
				family.addNote("Civil status: Fixed relationship.");
				break;
			default:
				break;
			}
		}
		
		
		return family;
	}
	
	
	/**
	 * 
	 * 
	 * @param parent1CsvData
	 * @param parent2CsvData
	 * @return
	 */
	private static CivilStatus getCivilStatus(Map<Columns, Object> parent1CsvData, Map<Columns, Object> parent2CsvData) {
		CivilStatus civilStatus = CivilStatus.UNKNOWN;
		
		CivilStatus parent1CivilStatus = CivilStatus.valueOf(getInteger(parent1CsvData, Columns.CIVIL_STATUS));
		CivilStatus parent2CivilStatus = CivilStatus.valueOf(getInteger(parent2CsvData, Columns.CIVIL_STATUS));
		String parent1Partner = getString(parent1CsvData, Columns.PARTNER_ID);
		String parent2Partner = getString(parent2CsvData, Columns.PARTNER_ID);
		String parent1Id = getString(parent1CsvData, Columns.ID);
		String parent2Id = getString(parent2CsvData, Columns.ID);
		
		if (parent1Partner != null && parent2Partner != null 
				&& parent1Partner.equals(parent2Id) && parent2Partner.equals(parent1Id)) {
			//The two individuals are a family
			
			if (parent1CivilStatus != null || parent2CivilStatus != null) {
				//At least one has a civil status
				
				if (parent1CivilStatus == parent2CivilStatus) {
					//They are the same so just take one
					civilStatus = parent1CivilStatus;
				} else {
					//They are not the same
					civilStatus = CivilStatus.MIXED;
				}
			} else {
				//Not set for both partners
				
				civilStatus = CivilStatus.UNKNOWN;
			}
			
		} else {
			//The two individuals are not a family any more.
			//They could both be married to new people, or divorced again, ...
			
			civilStatus = CivilStatus.UNKNOWN;
		}
		
		
		return civilStatus;
	}
	
	/**
	 * 
	 * 
	 * @param parent1CsvData
	 * @param parent2CsvData
	 * @return
	 */
	private static String getMarriageDateString(Map<Columns, Object> parent1CsvData, Map<Columns, Object> parent2CsvData) {
		
		String parent1MarriageDateString = getString(parent1CsvData, Columns.MARRIAGE_DATE);
		String parent2MarriageDateString = getString(parent2CsvData, Columns.MARRIAGE_DATE);
		
		if (parent1MarriageDateString != null && parent2MarriageDateString != null) {
			//Both marriage dates given
			
			Date parent1MarriageDate = GedcomFormatter.getDateFromGedcom(parent1MarriageDateString);
			Date parent2MarriageDate = GedcomFormatter.getDateFromGedcom(parent2MarriageDateString);
			
			//TODO Better comparison: for one only the year might be given and for the 
			//other one the day and year -> take only the year
			
			if (GedcomHelper.isBeforeOrAfter(parent1MarriageDate, parent2MarriageDate) == GedcomHelper.SAME) {
				//They are the same so just take one
				return parent1MarriageDateString;
			} else {
				//They are not the same -> Not usable
				return null;
			}
		} else {
			//Both are null or one is null
			return null;
		}
			
	}
	
	/**
	 * 
	 * 
	 * @param parent1CsvData
	 * @param parent2CsvData
	 * @return
	 */
	private static String getChangeDateTime(Map<Columns, Object> parent1CsvData, Map<Columns, Object> parent2CsvData) {
		String parent1LastModified = getString(parent1CsvData, Columns.LAST_MODIFIED);
		String parent2LastModified = getString(parent2CsvData, Columns.LAST_MODIFIED);
		
		if (parent1LastModified != null && parent2LastModified != null) {
			Date d1 = GedcomFormatter.getDateTimeFromGedcom(parent1LastModified);
			Date d2 = GedcomFormatter.getDateTimeFromGedcom(parent2LastModified);
			
			//Take the date of the individual which has been changed later
			if (GedcomHelper.isBeforeOrAfter(d1, d2) == GedcomHelper.AFTER) {
				return parent2LastModified;
			} else {
				return parent1LastModified;
			}
		} else if (parent1LastModified != null) {
			return parent1LastModified;
		} else if (parent2LastModified != null) {
			return parent2LastModified;
		}
		
		return null;
	}
	
	/**
	 * 
	 * 
	 * @param store
	 * @param individualData
	 * @return
	 */
	public static GedcomIndividual createIndividual(GedcomStore store, 
			Map<Columns, Object> individualData) {
		
		String individualId = getString(individualData, Columns.ID);
		
		if (individualId == null || individualId.length() == 0) {
			throw new GedcomError("Invalid individual ID '" + individualId + "'");
		}
		
		GedcomIndividual individual = new GedcomIndividual(store, individualId);
		
		//Mandatory
		String sex = getString(individualData, Columns.GENDER).toUpperCase();
		//Optional
		String birthDate = getString(individualData, Columns.BIRTH_DATE);
		String deathDate = getString(individualData, Columns.DEATH_DATE);
		String occupation = getString(individualData, Columns.OCCUPATION);
		String education = getString(individualData, Columns.EDUCATION);
		String formerName = getString(individualData, Columns.FORMER_NAME);
		String firstName = getString(individualData, Columns.FIRST_NAME);
		String middleName = getString(individualData, Columns.MIDDLE_NAME);
		String familyName = getString(individualData, Columns.FAMILY_NAME);
		
		
		
		if (Sex.MALE.getValue().equals(sex)) {
			individual.setSex(Sex.MALE);
		} else if (Sex.FEMALE.getValue().equals(sex)) {
			individual.setSex(Sex.FEMALE);
		} else {
			throw new GedcomError("The value " + sex + " in the " + Columns.GENDER + 
					" column is invalid. Only " + Arrays.toString(Sex.values()) + " allowed.");
		}
		
		if (birthDate != null) {individual.setBirth(true, birthDate);}
		if (deathDate != null) {individual.setDeath(true, deathDate);}
		if (occupation != null) {individual.setOccupation(occupation);}
		if (education != null) {individual.setEducation(education);}
		
		if (formerName != null && formerName.length() > 0) {individual.addName(formerName, firstName, middleName);}
	
		if (familyName != null && familyName.length() > 0 && !familyName.equals(formerName)) {
			individual.addName(familyName, NameType.MARRIED, firstName, middleName);
		}
		
		individual.addAddress(
				getString(individualData, Columns.STREET1), 
				getString(individualData, Columns.STREET2), 
				getString(individualData, Columns.TOWN), 
				getString(individualData, Columns.ZIP_CODE), 
				getString(individualData, Columns.COUNTRY), 
				new String[] {
					getString(individualData, Columns.PHONE1), 
					getString(individualData, Columns.PHONE2)
					}, 
				new String[] {
					getString(individualData, Columns.EMAIL1), 
					getString(individualData, Columns.EMAIL2)
					}, 
				null, 
				null);
		
		//Optional
		if (getString(individualData, Columns.LAST_MODIFIED) != null) {
			String[] dateTime = splitDateTime(getString(individualData, Columns.LAST_MODIFIED));
			individual.setChangeDate(dateTime[0], dateTime[1]);
		}
		
		return individual;
	}
	
	/**
	 * Creates the links to the individuals family where the individual is a child 
	 * or a parent of.
	 * 
	 * @param structureStorage
	 * @param individualData
	 */
	public static void createFamilyIndividualLinks(GedcomCreatorStructureStorage structureStorage, 
			Map<Columns, Object> individualData) {
		
		String individualId = getString(individualData, Columns.ID);
		String parent1Id = getString(individualData, Columns.PARENT1_ID);
		String parent2Id = getString(individualData, Columns.PARENT2_ID);
		
		
		GedcomFamily familyOfParents = structureStorage.getFamilyOfParents(parent1Id, parent2Id);
		Set<GedcomFamily> families = structureStorage.getFamiliesOfParent(individualId);
		
		GedcomIndividual individual = structureStorage.getIndividual(individualId);
		
		//Link individual as child to its parents family
		if (familyOfParents != null) {
			familyOfParents.addChildLink(individualId);
			individual.addChildFamilyLink(familyOfParents.getId());
		}
		
		//Link individual as spouse to the families
		if (families != null) {
			for (GedcomFamily family : families) {
				individual.addSpouseFamilyLink(family.getId());
			}
		}
	}
	
	
	/**
	 * Returns the string representation of the value stored for the given key. 
	 * If there is a value stored for that key, the value is returned. If the 
	 * value is <code>null</code>, an empty string or <code>0</code>, 
	 * <code>null</code> is returned. 
	 * 
	 * @param data
	 * @param column
	 * @return
	 * @throws FamilyChartConverterError If the key does not exist for the data
	 */
	public static String getString(Map<Columns, Object> data, Columns column) {
		if (!data.containsKey(column)) {
			throw new GedcomReaderError("No such column " + column);
		}
		
		Object o = data.get(column);
		
		if (o == null) {
			return null;
		}
		
		String s = o.toString();
		
		if (s.equals("0") || s.length() == 0) {
			return null;
		}
		
		return s;
	}
	
	/**
	 * Returns the string representation of the value stored for the given key. 
	 * If there is a value stored for that key, the value is returned. If the 
	 * value is <code>null</code> and empty string is returned. 
	 * 
	 * @param data
	 * @param column
	 * @return
	 * @throws FamilyChartConverterError If the key does not exist for the data
	 */
	public static int getInteger(Map<Columns, Object> data, Columns column) {
		if (!data.containsKey(column)) {
			throw new GedcomReaderError("No such column " + column);
		}
		
		Object o = data.get(column);
		
		if (o == null) {
			return 0;
		}
		
		return Integer.valueOf(o.toString());
	}
	
	/**
	 * 
	 * 
	 * @param dateTimeString A date-time string in the gedcom format "dd MMM yyyy HH:mm:ss"
	 * @return The date [0] and time [1]
	 */
	public static String[] splitDateTime(String dateTimeString) {
		String[] s = new String[2];
		
		if (!dateTimeString.contains(":") || !dateTimeString.contains(" ")) {
			//The last modified date-time does not contain a time string or a date string
			throw new GedcomReaderError("The date-time-string does " +
					"not contain a date and time part: " + dateTimeString);
		}
		
		int timeIndex = dateTimeString.lastIndexOf(" ");
		
		//The date part
		s[0] = dateTimeString.substring(0, timeIndex);
		//The time part
		s[1] = dateTimeString.substring(timeIndex + 1, dateTimeString.length());
		
		return s;
	}
	

}
