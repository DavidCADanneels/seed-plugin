package net.nemerosa.jenkins.seed.config;

import com.google.common.base.Function;
import lombok.Data;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Configuration for a project.
 */
@Data
public class ProjectPipelineConfig {

    /**
     * General pipeline configuration, set by a Seed job.
     */
    private final PipelineConfig pipelineConfig;

    /**
     * Name of the project
     */
    private final String project;

    /**
     * SCM type
     */
    private final String scmType;

    /**
     * SCM base (without any branch)
     */
    private final String scmBase;

    /**
     * SCM credentials (ref. to Jenkins credentials)
     */
    private final String scmCredentials;

    @DataBoundConstructor
    public ProjectPipelineConfig(PipelineConfig pipelineConfig, String project, String scmType, String scmBase, String scmCredentials) {
        this.pipelineConfig = pipelineConfig;
        this.project = project;
        this.scmType = scmType;
        this.scmBase = scmBase;
        this.scmCredentials = scmCredentials;
    }

    /**
     * Gets the parameters for a project
     */
    public ProjectParameters getProjectParameters(Function<String, String> expandFn) {
        return new ProjectParameters(
                expandFn.apply(project),
                expandFn.apply(scmType),
                expandFn.apply(scmBase),
                expandFn.apply(scmCredentials)
        );
    }

}