# Data-gen - data generation tool
Tool to generate json or csv files based on generic definitions
Example of a definition:
```json
{
	"number": ["number", 100],
	"string": ["string", 3],
	"string-with-range": ["string-range", 3, 8],
	"string-with-placeholder": ["placeholder", "hello-*-test", 100],
	"random-from-collection": ["take-random", ["first", "second", "third", "fourth"]]
}
```
Example of one of the items generated from this definition:
in json:
```json
{
  "number": 47,
  "string": "cVU",
  "string-with-range": "Bq2jO4t",
  "string-with-placeholder": "hello-96-test",
  "random-from-collection": "second"
}
```
in csv (order of columns is lexicographical order of keys in a definition hashmap)
```
81 third AXq hello-69-test Z05jhpup
```

## Rationale
This tool was developed during an evaluation of different storage options for a completely unrelated project. I needed to generate a big amount of data to compare query times on big data sets.

## How to run
If you have `lein`, you can run the app without building a `jar` file. Use `./bin/generate` script with parameters.
Otherwise, you can use an already built `jar` file in the `build` folder.
`java -jar data-gen` and pass supported params.

## Options
* -f -- path to a file with definition (see section "Definitions" for more details)
* -d -- name of a definition from the definitions file based on which data should be generated
* -n -- into how many output files random output data should be split (useful when generating GBs of data)
* -s -- size of a single output file in MBs (useful when generating GBs of data)
* -o -- csv or json output format
* -u -- path to an output folder
* -h -- show help

## Example
Generate 10 files, each of size ~5 GBs based on an "employee" definition from "some-definitions.json" file and put results into `./output` folder.
`java -jar data-gen.jar -f some-definitions.json -d employee -n 10 -s 5000 -o json -u ./output`

## Definitions
Generator value types and parameters are specified as tuples in a definition.
Example of a definition file that contains one `main` definition:
```json
{
  "main": {
    "number": ["number", 100],
    "string": ["string", 3],
    "string-with-range": ["string-range", 3, 8],
    "string-with-placeholder": ["placeholder", "hello-*-test", 100],
    "random-from-collection": ["take-random", ["first", "second", "third", "fourth"]]
  }
}
```

Types:

* number - `["number", exclusive-upper-boundary]`
* string - `["string", length-of-random-string]`
* string-range - `["string-range", min-chars-in-string, max-chars-in-string(inclusive)]`
* placeholder - `["placeholder", string-with-single-start-placholder, max-placeholder(inclusive)]`
* take-random - `["take-random", array-with-items-from-which-to-pick]`
