package com.jdreamer.rest;

/** Planner rules relating to the CSV adapter. */
public abstract class CsvRules {
    private CsvRules() {}

    /** Rule that matches a {@link org.apache.calcite.rel.core.Project} on
     * a {@link CsvTableScan} and pushes down projects if possible. */
    public static final CsvProjectTableScanRule PROJECT_SCAN =
            CsvProjectTableScanRule.Config.DEFAULT.toRule();
}