package com.bugchecker;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Created by elnggng on 2/19/18.
 */
public class CommandLineTool {

    /**
     * Usage:  <java src file> <report file> |
     *         <java src folder> <report folder> [path_regex]
     * @param args
     */
    public static void main (String [] args) {
        File src = new File (args[0]);
        File report = new File (args[1]);

        if (src.isFile()) {
            ClassBugReport bugReport = BugChecker.getInstance().checkAllBugs(src);
            bugReport.generateClassReport_CSV(report);
        } else if (src.isDirectory()) {
            String pathRegex = null;
            if (args.length == 3) {
                pathRegex = args[2];
            }
            BugChecker.getInstance().checkAllBugsInProject(src, report, pathRegex);
        } else {
            System.out.println ("The path to the src shall be either a java file or folder.");
        }
    }
}
