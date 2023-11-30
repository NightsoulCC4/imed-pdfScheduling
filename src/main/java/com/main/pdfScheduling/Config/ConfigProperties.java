package com.main.pdfScheduling.Config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigProperties {
    private String schedulingRunTime;

    // Config run time.
    public String getSchedulingRunTime() {
        return this.schedulingRunTime;
    }
    
}
