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
package ch.thn.gedcom.reader;

import ch.thn.gedcom.creator.GedcomCreatorStructureStorage;
import ch.thn.gedcom.creator.structures.AbstractGedcomStructure;

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomReaderUtil {
	
	/**
	 * 
	 * 
	 * @param structureStorage
	 * @return
	 */
	public static StringBuilder printStorageStatistics(GedcomCreatorStructureStorage structureStorage) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Loading family data done. " + 
				structureStorage.getTotalStructureCount() + " different structures loaded:\n");
		sb.append(AbstractGedcomStructure.END_OF_FILE + ": " + 
				structureStorage.getEOFCount() + "\n");
		sb.append(AbstractGedcomStructure.HEADER + ": " + 
				structureStorage.getHeaderCount() + "\n");
		sb.append(AbstractGedcomStructure.SUBMITTER_RECORD + ": " + 
				structureStorage.getSubmitterCount() + "\n");
		sb.append(AbstractGedcomStructure.FAM_RECORD + ": " + 
				structureStorage.getFamilyCount() + "\n");
		sb.append(AbstractGedcomStructure.INDIVIDUAL_RECORD + ": " + 
				structureStorage.getIndividualCount() + "\n");
		
		return sb;
	}

}
