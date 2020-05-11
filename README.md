# Bill Sorter

## Build

Build using Gradle Wrapper.

```sh
./gradlew shadowJar
```

The output will be located in `./build/libs/BillSorter-x.x.x-all.jar`

## Install

To run create a Windows batch (`.bat`) file with the following contents:

```bat
@echo off

java -cp BillSorter-0.1.0-all.jar endeavor85.billsorter.BillSorter %*

pause
```

In the same directory, place the [`BillSorter-x.x.x-all.jar`](#build), and the [`settings.json`](#settings-file) file.

The directory should contain the following:

```sh
some-dir:
+---BillSorter-x.x.x-all.jar
+---rename.bat
\---settings.json
```

## Run

Drag the file(s) to be sorted onto the batch file's icon in Windows Explorer and drop to run the script.

## Settings File

Bill Sorter reads the `settings.json` file to configure identification and renaming rules.

The `settings.json` file has the following format:

```json
[
    {
        "label": "Internet Bill",
        "idPattern": ".*account_num.*some_ISP.*",
        "dateMatcher": ".* Billing Date (\\d+)\\/(\\d+)\\/(\\d+) .*",
        "parsedDateFormat": "MM dd yy",
        "filenamePrefix": "",
        "filenameDateFormat": "yyyy_MM_dd",
        "filenameSuffix": " Internet Bill.pdf",
        "destinationFolder": "C:/Bills/Internet"
    },
    ...
]
```
