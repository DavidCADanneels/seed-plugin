package net.nemerosa.seed.generator;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.nemerosa.seed.config.SeedNamingStrategyHelper;
import net.nemerosa.seed.config.SeedProjectEnvironment;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Build step which creates a branch folder and a branch seed inside.
 */
public class BranchSeedBuilder extends AbstractSeedBuilder {

    private final String branch;

    @DataBoundConstructor
    public BranchSeedBuilder(String project, String projectClass, String projectScmType, String projectScmUrl, String branch) {
        super(project, projectClass, projectScmType, projectScmUrl);
        this.branch = branch;
    }

    @Override
    protected String replaceExtensionPoints(String script, EnvVars env, SeedProjectEnvironment projectEnvironment) {
        String theBranch = env.expand(branch);
        String result = replaceExtensionPoint(script, "pipelineGeneration", new BranchPipelineGeneratorExtension(projectEnvironment, theBranch).generate());
        result = replaceExtensionPoint(result, "branchSeedScm", new BranchSeedScmExtension(projectEnvironment, theBranch).generate());
        return result;
    }

    @SuppressWarnings("unused")
    public String getBranch() {
        return branch;
    }

    @Override
    protected void configureEnvironment(EnvVars env, SeedProjectEnvironment projectEnvironment) {
        String theBranch = env.expand(branch);
        env.put("branchSeedFolder", SeedNamingStrategyHelper.getBranchSeedFolder(
                projectEnvironment.getNamingStrategy(),
                projectEnvironment.getId(),
                theBranch
        ));
        env.put("branchSeedPath", projectEnvironment.getNamingStrategy().getBranchSeed(
                projectEnvironment.getId(),
                theBranch
        ));
        // Computation of seed names
        env.put("SEED_PROJECT", projectEnvironment.getProjectConfiguration().getName());
        env.put("SEED_BRANCH", projectEnvironment.getNamingStrategy().getBranchName(theBranch));
    }

    @Override
    protected String getScriptPath() {
        return "/branch-seed-generator.groovy";
    }

    @Extension
    public static class BranchSeedBuilderDescription extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Branch seed generator";
        }
    }
}