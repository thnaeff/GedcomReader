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

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import ch.thn.gedcom.data.GedcomTree;
import ch.thn.gedcom.reader.GedcomReader;
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
			store.parse("/home/thomas/Projects/java/GedcomStore/gedcomobjects_5.5.1.gedg");
		} catch (GedcomParseException e) {
			e.printStackTrace();
		}
		
		
		GedcomReader reader = new GedcomReader(store);
		
		try {
			reader.read("/home/thomas/Desktop/familienfest/test.ged");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Access all loaded structures
		LinkedHashMap<String, LinkedList<GedcomTree>> s = reader.getParsedStructures();
		
		
		
	}

}
