# GedcomReader

**This library allows the loading of GEDCOM files into memory by reading *.ged files and creating the tree structures which can further be used with all my GEDCOM libraries**

Since the structure of the input file is read and it tries to follow that structure matching the loaded GEDCOM grammar file, invalid data in the source file (which does not match the GEDCOM grammar) is pointed out as parsing error.


********************************************************************************************************

# A simple example

```java
GedcomStore store = new GedcomStore();

store.showParsingOutput(false);

try {
	store.parse("PATH_TO_GRAMMAR_FILE/gedcomobjects_5.5.1.gedg");
} catch (GedcomParseException e) {
	e.printStackTrace();
}


GedcomReader reader = new GedcomReader(store);

try {
	reader.read("PATH_TO_GEDCOM_FILE/gedcomobjects_5.5.1.gedg");
} catch (IOException e) {
	e.printStackTrace();
}

//Access all loaded structures
LinkedHashMap<String, LinkedList<GedcomTree>> s = reader.getParsedStructures();

```


********************************************************************************************************


# Dependencies
* [GedcomStore](http://github.com/thnaeff/GedcomStore)
* My own utility library: [Util](http://github.com/thnaeff/Util)

