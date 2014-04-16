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
package ch.thn.gedcom.reader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import ch.thn.gedcom.GedcomHelper;
import ch.thn.gedcom.data.GedcomLine;
import ch.thn.gedcom.data.GedcomNode;
import ch.thn.gedcom.data.GedcomTree;
import ch.thn.gedcom.store.GedcomStore;
import ch.thn.util.StringUtil;

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomReader {
	
	public static final String gedcomFileExtension = "ged";
			
	private GedcomStore store = null;
	
	private GedcomTree currentTree = null;
	private GedcomNode currentNode = null;
	
	private LinkedList<GedcomNode> path = null;
	
	/**
	 * 
	 * &lt;Structure name &lt;Parsed trees&gt;&gt;
	 */
	private LinkedHashMap<String, LinkedList<GedcomTree>> parsedTrees = null;
	
	
	/**
	 * 
	 * 
	 * @param store
	 */
	public GedcomReader(GedcomStore store) {
		this.store = store;
		
		path = new LinkedList<GedcomNode>();
		parsedTrees = new LinkedHashMap<>();
	}
	
	/**
	 * Returns all the structures which have been parsed. The structures are grouped 
	 * by structure name.<br />
	 * <br />
	 * &lt;Structure name &lt;Parsed trees&gt;&gt;
	 * 
	 * @return
	 */
	public LinkedHashMap<String, LinkedList<GedcomTree>> getParsedStructures() {
		return parsedTrees;
	}
	
	/**
	 * Reads the file given with the path parameter and adds all parsed structures 
	 * to the list of parsed structures
	 * 
	 * @param gedcomFile
	 * @return
	 * @throws IOException
	 * @throws GedcomReaderError
	 */
	public boolean read(String gedcomFile) throws IOException {
		System.out.println(getClass().getSimpleName() + ": Load family data from a " +
				"GEDCOM file.");
		
		if (!gedcomFile.endsWith(gedcomFileExtension)) {
			throw new GedcomReaderError("Invalid file format. A GEDCOM file (" + 
					gedcomFileExtension + ") is needed.");
		}
		
		BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(gedcomFile), "UTF-8"));
		
		String line = null;
		boolean headerFound = false;
		int lineCount = 0;
				
		while((line = input.readLine()) != null) {
			
			//Remove all leading and trailing extra stuff (spaces, tags, newlines, linefeeds)
			line = StringUtil.removeAll(GedcomHelper.leadingTrailingPatternWhole, line);
			//Remove any excessive spaces
			line = StringUtil.replaceAll(GedcomHelper.spacesPattern, line, " ");
			
			//Also count empty lines because the error output shows the line number
			lineCount++;
			
			//Skip empty lines
			if (line.length() == 0) {
				continue;
			}
			
			if (!headerFound) {
				if (line.equals("0 HEAD")) {
					headerFound = true;
				} else {
					//Don't do anything until the header record is found
					continue;
				}
			}
			
			parseLine(line, lineCount);
			
		}
		
		input.close();
		
		if (!headerFound) {
			throw new GedcomReaderError("Failed to read GEDCOM data from file " + 
					gedcomFile + ". " + "HEADER structure (HEAD tag) not found.");
		}
		
		//Add the last tree to the list
		addCurrentTree();
		
		System.out.println("Loading family data done. " + parsedTrees.size() + " different structures loaded:");
		for (String structureName : parsedTrees.keySet()) {
			System.out.println(structureName + ": " + parsedTrees.get(structureName).size());
		}
			
		
		return true;
	}
	
	/**
	 * Adds the current tree to the list of all parsed trees
	 */
	private void addCurrentTree() {
		if (currentTree != null) {
			String structureName = currentTree.getStructureName();
			if (!parsedTrees.containsKey(structureName)) {
				parsedTrees.put(structureName, new LinkedList<GedcomTree>());
			}
			
//			if (currentTree != null) {
//				GedcomStructureTreePrinter printer = new GedcomStructureTreePrinter(true);
//				System.out.println(printer.print(currentTree));
//			}
			
			parsedTrees.get(structureName).add(currentTree);
			currentTree = null;
		}
	}
	
	/**
	 * Parse one line of the gedcom data file
	 * 
	 * @param line
	 * @param lineCount
	 * @return
	 */
	private boolean parseLine(String line, int lineCount) {
		int level = 0;
		int valueIndex = 0;
		String tag = null;
		String xref = null;
		boolean xrefBeforeTag = false;
		
		String[] lineParts = line.split(GedcomLine.DELIM);
		
		if (lineParts.length < 2) {
			throw new GedcomReaderError("Line '" + line + "' can not be parsed. Line needs at least a level number and a tag name.");
		}
		
//		System.out.print(line);
		
		//1. The first index is always the level number
		try {
			level = Integer.parseInt(lineParts[0]);
		} catch (NumberFormatException e) {
			System.out.println("Failed to parse line " + lineCount + ". " + 
					"Level number not found (" + e.getMessage() + ")");
			return false;
		}
		valueIndex++;
		
		//2. XRef before tag?
		if (isXRef(lineParts[1])) {
			xrefBeforeTag = true;
			xref = getXRef(lineParts[1]);
		} else {
			tag = lineParts[1];
		}
		valueIndex++;
		
		if (lineParts.length == 2) {
			//Nothing more to to
			return processLine(level, tag, xref, null, xrefBeforeTag, lineCount);
		}
		
		//3. XRef after tag?
		if (tag != null && isXRef(lineParts[2])) {
			xref = getXRef(lineParts[2]);
			valueIndex++;
		} else if (tag == null) {
			tag = lineParts[2];
			valueIndex++;
		}
		
		
		if (lineParts.length == valueIndex) {
			//Nothing more to to
			return processLine(level, tag, xref, null, xrefBeforeTag, lineCount);
		}
		
		//4. With value
		return processLine(level, tag, xref, getValue(valueIndex, lineParts), xrefBeforeTag, lineCount);
	}
	
	/**
	 * Create the value by putting the line parts back together, starting at 
	 * the given index
	 * 
	 * @param start
	 * @param lineParts
	 * @return
	 */
	private String getValue(int start, String[] lineParts) {
		StringBuilder sb = new StringBuilder(lineParts.length);
		
		for (int i = start; i < lineParts.length; i++) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			
			sb.append(lineParts[i]);
		}
		
		return sb.toString();
	}
	
	/**
	 * Process the parsed line parts
	 * 
	 * @param level
	 * @param tag
	 * @param xref
	 * @param value
	 * @param xrefBeforeTag
	 * @param lineCount
	 * @return
	 */
	private boolean processLine(int level, String tag, String xref, String value, 
			boolean xrefBeforeTag, int lineCount) {
				
		boolean hasXRef = false;
		boolean hasValue = false;
		
		if (xref != null && xref.length() > 0) {
			hasXRef = true;
		}
		
		if (value != null && value.length() > 0) {
			hasValue = true;
		}
		
//		System.out.println(" -> level=" + level + ", tag=" + tag + ", xref=" + xref + ", value=" + value);
		
		if (level == 0) {
			//A new tree
			
			//Add the previously parsed tree to the list
			addCurrentTree();
			
			path.clear();
			
			String structureName = store.getStructureNameForTag(tag);
			
			if (structureName == null) {
				throw new GedcomReaderError("Could not find structure name for tag " + tag + 
						" (source file line " + lineCount + ")");
			}
			
			currentTree = store.getGedcomTree(structureName, tag, hasXRef, hasValue);
			
			currentNode = currentTree.addChildLine(tag);
//			System.out.println("New structure: " + structureName);
			
			if (currentNode == null) {
				throw new GedcomReaderError("Failed to add child line " + tag + " to structure " + 
							structureName + " (source file line " + lineCount + ")");
			}
		} else {
			//Continue on the current tree
			
			if (level < path.size()) {
				//Go back to a lower level
				while (level < path.size()) {
					path.removeLast();
				}
				
				currentNode = path.getLast();
			}
			
			LinkedList<String> path = currentNode.getStoreBlock().getPathToStoreLine(tag, hasXRef, hasValue);
			
			if (path == null || path.size() == 0) {
				throw new GedcomReaderError("Failed to get path from " + currentNode.getStoreLine().getId() + 
							" to " + tag + " in structure " + 
							currentTree.getStructureName() + " (source file line " + lineCount + ")");
			}
			
			currentNode = currentNode.followPathCreate(path.toArray(new String[path.size()]));
		}
		
		
		path.add(currentNode);
		
		if (hasXRef) {
			currentNode.setTagLineXRef(xref);
		}
		
		if (hasValue) {
			currentNode.setTagLineValue(value);
		}
		
		return true;
	}
	
	/**
	 * 
	 * 
	 * @param linePart
	 * @return
	 */
	private boolean isXRef(String linePart) {
		return linePart.startsWith("@") && linePart.endsWith("@");
	}
	
	/**
	 * 
	 * 
	 * @param linePart
	 * @return
	 */
	private String getXRef(String linePart) {
		return linePart.substring(1, linePart.length() - 1);
	}

}
