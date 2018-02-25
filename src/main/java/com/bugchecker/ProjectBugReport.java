package com.bugchecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by elnggng on 2/19/18.
 */
public class ProjectBugReport {

    private static Logger log = LoggerFactory.getLogger(ProjectBugReport.class);

    private List<ClassBugReport> classReports = new LinkedList<ClassBugReport> ();

    private File projectFolder;
    private File reportFolder;
    private boolean reportFolderInitialized = false;

    private long blockScanned = 0;

    private long bugsFound = 0;

    BugCountsPerRule bugCountsPerRule = new BugCountsPerRule();

    public ProjectBugReport(File projectFolder, File reportFolder) {
        this.projectFolder = projectFolder;
        this.reportFolder = reportFolder;
    }

    public void addClassReport(ClassBugReport report) {
        classReports.add (report);
        blockScanned += report.getNumBlockScanned();
        bugsFound += report.getBugs().size();
        bugCountsPerRule.addCount(report.getBugCountsPerRule());

        if (reportFolder != null && !reportFolderInitialized) {
            createReportFolder();
            reportFolderInitialized = true;
        }

        if (reportFolder != null && report.getBugs().size() > 0) {
            try {
                generateClassReport_CSV (report);
            } catch (IOException e) {
                log.error("Exception when adding class report for ");
            }
        }
    }

    public void completeReport () {
        generateSummaryReport_CSV (new File(reportFolder, "summary.csv"));
    }

    public long getNumBugs() { return bugsFound;}

    public long getNumBlockScanned() {return blockScanned;}

    public List<ClassBugReport> getClassReports() {return classReports;}

    public void generateSummaryReport_CSV(File reportFile) {

        try (PrintStream ps = new PrintStream(reportFile)) {
            //Print CSV titles
            ps.print ("JavaSrc,BlockScanned");

            List<String> rules = bugCountsPerRule.getListRuleNames();
            rules.forEach(rule -> {
                        ps.print(",");
                        ps.print("issues." + rule);
                    }
            );
            ps.println();

            ps.print ("Total," + blockScanned + ",");
            ps.println (bugCountsPerRule.getStringCountValues (rules, ","));

            classReports.forEach( report -> {
                ps.print (report.getJavaFile().toString());
                ps.print (",");
                ps.print (report.getNumBlockScanned());
                ps.print (",");

                ps.println (report.getBugCountsPerRule().getStringCountValues(rules, ","));
            });
        } catch (IOException e) {
            log.error ("Error when generating report to " + reportFile.toString(), e);
        }
    }

    private void createReportFolder() {
        try {
            if (reportFolder.exists()) {
                throw new IOException("Target folder already exists: " + reportFolder.toString());
            }

            if (!reportFolder.mkdir()) {
                throw new IOException("Dir not created: " + reportFolder.toString());
            }

            if (!new File(reportFolder, "classes").mkdir()) {
                throw new IOException("Dir not created: " + reportFolder.toString() + "/classes");
            }
        } catch (IOException e) {
            log.error ("Error when creating report folder: " + reportFolder.toString(), e);
            System.exit(1);
        }
    }

    private void generateClassReport_CSV (ClassBugReport report) throws IOException {
        URI srcFolderURI = projectFolder.toURI();

        URI srcFileURI = report.getJavaFile().toURI();
        String relative = srcFolderURI.relativize(srcFileURI).getPath();

        File folder = new File(reportFolder, "classes/" + relative).getParentFile();
        if (!folder.isDirectory() && !new File(reportFolder, "classes/" + relative).getParentFile().mkdirs()) {
            throw new IOException("Dirs not created for: " + reportFolder + "/classes/" + relative);
        }

        report.generateClassReport_CSV(
                new File(reportFolder, "classes/" + relative + ".csv"));
    }

    public void generateReportBundle_CSV(File reportFolder) {
        try {
            this.reportFolder = reportFolder;
            this.reportFolderInitialized = false;

            createReportFolder();
            for (ClassBugReport classBugReport : getClassReports()) {
                if (classBugReport.getBugs().size() > 0) {
                    generateClassReport_CSV(classBugReport);
                }
            }
            completeReport();
        } catch (IOException e) {
            log.error("IO Exception when generating project bug report.", e);
            System.exit(1);
        }
    }
}
