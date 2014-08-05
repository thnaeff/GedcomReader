# GedcomReader

**This library allows the loading of family tree data from various sources into memory (as GedcomNodes), which can then be further used with all my GEDCOM libraries**

Currently, the following readers are available:

* **GedcomGEDReader:** To read existing GEDCOM (*.ged) files with individuals and families etc. The GEDCOM format is a "standard" format for exchanging genealogy data.
* **GedcomCSVReader:** Reads individuals from a chart (with their partners, parents, ...) and creates all the needed individual and family structures
* **GedcomResultSetReader:** Reads individuals from a `ResultSet` (with their partners, parents, ...) and creates all the needed individual and family structures



##GedcomGEDReader

```java
GedcomStore store = new GedcomStore();

store.showParsingOutput(false);
		
try {
	store.parse(store.getClass().getResource("/gedcomobjects_5.5.1.gedg").getPath());
} catch (GedcomParseException e) {
	e.printStackTrace();
}


GedcomGEDReader gedReader = new GedcomGEDReader(store);

try {
	gedReader.read("PATH_TO_GEDCOM_FILE/familienfest/test.ged");
} catch (IOException e) {
	e.printStackTrace();
}

//Access all loaded structures
LinkedHashMap<String, LinkedList<GedcomTree>> s = reader.getParsedStructures();

```



##GedcomCSVReader/GedcomResultSetReader

The `Columns` enum in the `GedcomChartEnums` class contains all the available standard column names. However, if your chart has different column names, you can define the column names as shown in the example below.

```java
GedcomCSVReader csvReader = new GedcomCSVReader(store);

Columns.ID.setColumnName("member_id");
Columns.FAMILY_NAME.setColumnName("member_name");
Columns.FIRST_NAME.setColumnName("member_firstname");
Columns.MIDDLE_NAME.setColumnName("member_middlename");
Columns.FORMER_NAME.setColumnName("member_formername");
Columns.GENDER.setColumnName("member_gender");
Columns.BIRTH_DATE.setColumnName("member_birthdate");
Columns.DEATH_DATE.setColumnName("member_deathdate");
Columns.PARENT1_ID.setColumnName("member_father_id");
Columns.PARENT2_ID.setColumnName("member_mother_id");
Columns.PARTNER_ID.setColumnName("member_partner_id");
Columns.MARRIAGE_DATE.setColumnName("member_marriagedate");
Columns.CIVIL_STATUS.setColumnName("member_civilstatus");
Columns.EMAIL1.setColumnName("member_email1");
Columns.EMAIL2.setColumnName("member_email2");
Columns.STREET1.setColumnName("member_street1");
Columns.STREET2.setColumnName("member_street2");
Columns.TOWN.setColumnName("member_town");
Columns.ZIP_CODE.setColumnName("member_zipcode");
Columns.COUNTRY.setColumnName("member_country");
Columns.PHONE1.setColumnName("member_phone1");
Columns.PHONE2.setColumnName("member_phone2");
Columns.OCCUPATION.setColumnName("member_occupation");
Columns.EDUCATION.setColumnName("member_education");
Columns.LAST_MODIFIED.setColumnName("member_lastmodified");

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
```



********************************************************************************************************


# Dependencies
* [GedcomStore](http://github.com/thnaeff/GedcomStore)
* My own utility library: [Util](http://github.com/thnaeff/Util)

