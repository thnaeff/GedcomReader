# GedcomReader

**This library allows the loading of family tree data from various sources into memory (as GedcomNodes), which can then be further used with all my GEDCOM libraries**

Currently, the following readers are available:

* **GedcomGEDReader:** To read existing GEDCOM (*.ged) files with individuals and families etc. The GEDCOM format is a "standard" format for exchanging genealogy data.
* **GedcomCSVReader:** Reads individuals from a chart (with their partners, parents, ...) and creates all the needed individual and family structures
* **GedcomResultSetReader:** Reads individuals from a `ResultSet` (with their partners, parents, ...) and creates all the needed individual and family structures


All the readers need a `GedcomStore` to work. The gedcom grammar file which is loaded with the `GedcomStore` defines how the family tree data is read and stored in memory. 



##GedcomGEDReader

Example file which can be read:

```
0 HEAD
1 SOUR source
2 NAME source name
2 CORP corporation
1 SUBM @0@
1 GEDC
2 VERS 5.5.1
2 FORM LINEAGE-LINKED
1 LANG German

0 @0@ SUBM
1 NAME submission name

0 @1@ INDI
1 NAME Johann, Mat /Frank/
2 TYPE married
2 GIVN Johann, Mat
2 SURN Frank
1 SEX M
1 BIRT Y
2 DATE 1775
1 DEAT Y
2 DATE 1858
1 RESI
1 FAMS @4454@
1 CHAN
2 DATE 13 MAR 2009
3 TIME 14:13:50
0 @10@ INDI
1 NAME Ann, Cathy /Snider/
2 GIVN Ann, Cathy
2 SURN Lutz
1 NAME Ann, Cathy /Doe-Snider/
2 TYPE married
2 GIVN Ann, Cathy
2 SURN Doe-Snider
1 SEX F
1 BIRT Y
2 DATE 1810
1 DEAT Y
2 DATE 1895
1 RESI
1 FAMS @4470@
1 CHAN
2 DATE 13 MAR 2009
3 TIME 14:13:53
```


The following code creates a gedcom store and loads the content of the gedcom file.

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
	gedReader.read("PATH_TO_GEDCOM_FILE/test.ged");
} catch (IOException e) {
	e.printStackTrace();
}

//Access all loaded structures
LinkedHashMap<String, LinkedList<GedcomTree>> s = reader.getParsedStructures();

```



##GedcomCSVReader/GedcomResultSetReader

Example file which can be read:

```
| member_id | member_name   | member_firstname | member_middlename | member_formername  | member_gender |
|-----------|---------------|------------------|-------------------|--------------------|---------------|
| 1         | Frank         | Johann           | Mat               |                    | m             |
| 10        | Doe-Snider    | Ann              | Cathy             | Snider             | f             |
```

The table data could be data from a CSV file or data from a result set of a database table.



The `Columns` enum in the `GedcomChartEnums` class contains all the available standard column names. However, if your chart has different column names, you can define the column names as shown in the example below.

```java
GedcomCSVReader csvReader = new GedcomCSVReader(store);

Columns.ID.setColumnName("member_id");			//Assigns the table name "member_id" to the internally used "id"
Columns.FAMILY_NAME.setColumnName("member_name");	//Assigns the table name "member_name" to the internally used "family_name"
Columns.FIRST_NAME.setColumnName("member_firstname");	//...
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

