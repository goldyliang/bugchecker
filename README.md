
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

Refer to the source folder sampleReports/ for the examples of the reports for three open sourced projects.

## Command Line Tool

**Usage**:
    
To check a single java file:

    bin/BugChecker <path_to_src_file> <path_to_csv_report_file>
    
To check all java files under a project folder:

    bin/BugChecker <path_to_src_folder> <path_to_csv_report_folder>
    
To check java files under a project folder, providing an regex for source file path pattern:

    bin/BugChecker <path_to_src_folder> <path_to_csv_report_folder> <src_path_regex>
    
    example: following command only checks all java source files for 'main' (ignoring 'test', etc).
    
    bin/BugChecker <path_to_src_folder> <path_to_csv_report_folder> /main/

For windows, replace 'BugChecker' with 'BugChecker.bat'

