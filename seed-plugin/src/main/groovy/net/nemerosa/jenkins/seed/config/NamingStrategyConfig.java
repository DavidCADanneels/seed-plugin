package net.nemerosa.jenkins.seed.config;

import lombok.Data;
import lombok.experimental.Wither;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

import static net.nemerosa.jenkins.seed.support.Evaluator.evaluate;

@Data
public class NamingStrategyConfig {

    /**
     * Placeholder for the branch name
     */
    public static final String BRANCH_PLACEHOLDER = "*";

    /**
     * Path to the project folder
     */
    @Wither
    private final String projectFolderPath;

    /**
     * Path to the branch folder
     */
    @Wither
    private final String branchFolderPath;

    /**
     * Name of the project seed
     */
    @Wither
    private final String projectSeedName;

    /**
     * Name of the project destructor
     */
    @Wither
    private final String projectDestructorName;

    /**
     * Name of the branch seed
     */
    @Wither
    private final String branchSeedName;

    /**
     * Start job name for the branch
     */
    @Wither
    private final String branchStartName;

    /**
     * Branch name expression
     */
    @Wither
    private final String branchName;

    /**
     * Branch prefixes to ignore
     */
    @Wither
    private final String ignoredBranchPrefixes;

    @DataBoundConstructor
    public NamingStrategyConfig(String projectFolderPath, String branchFolderPath, String projectSeedName, String projectDestructorName, String branchSeedName, String branchStartName, String branchName, String ignoredBranchPrefixes) {
        this.projectFolderPath = projectFolderPath;
        this.branchFolderPath = branchFolderPath;
        this.projectSeedName = projectSeedName;
        this.projectDestructorName = projectDestructorName;
        this.branchSeedName = branchSeedName;
        this.branchStartName = branchStartName;
        this.branchName = branchName;
        this.ignoredBranchPrefixes = ignoredBranchPrefixes;
    }

    /**
     * Constructor with default values
     */
    public NamingStrategyConfig() {
        this(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    public String getProjectFolder(ProjectParameters parameters) {
        return evaluate(projectFolderPath, "${project}", "project", parameters.getProject());
    }

    public String getProjectSeedJob(ProjectParameters parameters) {
        return evaluate(projectSeedName, "${project}-seed", "project", parameters.getProject());
    }

    public String getProjectDestructorJob(ProjectParameters parameters) {
        return evaluate(projectDestructorName, "${project}-destructor", "project", parameters.getProject());
    }

    public String getBranchName(String branch) {
        String value = branch;
        // Gets the branch prefixes
        List<String> prefixes = LineParser.parseLines(ignoredBranchPrefixes);
        // Removes branch prefixes
        for (String prefix : prefixes) {
            value = StringUtils.removeStart(value, prefix);
        }
        // Normalisation
        value = normalise(value);
        // Evaluation
        return evaluate(branchName, "${branch}", "branch", value);
    }

    public String getBranchFolderPath(ProjectParameters parameters, String branch) {
        return evaluate(branchFolderPath, "${project}-*", "project", parameters.getProject())
                .replace(BRANCH_PLACEHOLDER, getBranchName(branch));
    }

    public String getBranchSeedName(ProjectParameters parameters, String branch) {
        return evaluate(branchSeedName, "${project}-*-seed", "project", parameters.getProject())
                .replace(BRANCH_PLACEHOLDER, getBranchName(branch));
    }

    public String getBranchStartName(ProjectParameters parameters, String branch) {
        return evaluate(branchStartName, "${project}-*-build", "project", parameters.getProject())
                .replace(BRANCH_PLACEHOLDER, getBranchName(branch));
    }

    public static String normalise(String value) {
        return value.replaceAll("[^A-Za-z0-9._-]", "-");
    }

}
