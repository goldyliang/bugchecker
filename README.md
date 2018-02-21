
An experimental basic Java static analysis tool.

Now only support three basic Exception bug checker, bit it can be easily extended.

## Bugs Supported

  * Empty catch clause
  
  * Over-catch Exceptions with exit or abort
  
  * TODO or FIXME in atch clause
  
## Report

Generate basic bug-check reports in CSV format

  * Per java-class report of all bugs found
  
  * Per project report with the statistics of the check result

## Command Line Tool

**Usage**:
    
To check for a single java file:

    bin/BugChecker <path_to_src_file> <path_to_csv_report_file>
    
To check for all java files under a project folder:

    bin/BugChecker <path_to_src_folder> <path_to_csv_report_folder>

For windows, replace 'BugChecker' with 'BugChecker.bat'

