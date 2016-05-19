package net.nemerosa.jenkins.seed.integration

import hudson.model.*
import hudson.model.queue.QueueTaskFuture
import net.nemerosa.jenkins.seed.config.PipelineConfig
import net.nemerosa.jenkins.seed.config.ProjectPipelineConfig
import net.nemerosa.jenkins.seed.generator.ProjectGenerationStep
import net.nemerosa.jenkins.seed.integration.SeedRule.Build
import net.nemerosa.jenkins.seed.test.TestUtils
import org.apache.commons.lang.StringUtils
import org.junit.Assert
import org.jvnet.hudson.test.JenkinsRule

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class SeedRule extends JenkinsRule {

    /**
     * Creates a Seed job, with default settings
     */
    String defaultSeed() {
        return seed(PipelineConfig.defaultConfig())
    }

    /**
     * Creates a Seed job with the mentioned settings
     * @param pipelineConfig Configuration to set
     * @param jobName Job name (defaults to a generated name if not specified)
     * @return Name of the seed job
     */
    String seed(PipelineConfig pipelineConfig, String jobName = null) {
        // Name of the seed job
        String name = jobName ?: TestUtils.uid('seed-')
        // Creates a job
        def job = createFreeStyleProject(name)
        // Parameters
        job.addProperty(new ParametersDefinitionProperty(
                new StringParameterDefinition('PROJECT', ''),
                new ChoiceParameterDefinition('PROJECT_SCM_TYPE', ['git', 'svn'] as String[], ''),
                new StringParameterDefinition('PROJECT_SCM_URL', ''),
                new StringParameterDefinition('PROJECT_SCM_CREDENTIALS', ''),
                new ChoiceParameterDefinition('PROJECT_TRIGGER_TYPE', ['', 'github', 'bitbucket', 'http'] as String[], ''),
                new StringParameterDefinition('PROJECT_TRIGGER_SECRET', ''),
        ))
        // Generation step
        job.buildersList << new ProjectGenerationStep(
                new ProjectPipelineConfig(
                        pipelineConfig,
                        '${PROJECT}',
                        '${PROJECT_SCM_TYPE}',
                        '${PROJECT_SCM_URL}',
                        '${PROJECT_SCM_CREDENTIALS}',
                        '${PROJECT_TRIGGER_TYPE}',
                        '${PROJECT_TRIGGER_SECRET}',
                )
        )
        // OK
        return name
    }

    /**
     * Fires a job with a set of parameters and returns a build object which can be monitored for completion
     * and status.
     * @param path Path to the job
     * @param parameters Parameters to pass to the job
     * @param timeoutSeconds Timeout in seconds
     * @return Build instance
     */
    Build fireJob(String path, Map<String, String> parameters = [:], int timeoutSeconds = 120) {
        // Gets the job to fire
        AbstractProject job = findJobByPath(path)
        // Fires the job with parameters
        QueueTaskFuture<AbstractBuild> future = job.scheduleBuild2(
                0,
                new Cause.UserIdCause(),
                new ParametersAction(
                        parameters.collect { name, value ->
                            new StringParameterValue(name, value)
                        }
                )
        )
        // Pointer to the build
        return new BuildImpl(path, future, timeoutSeconds)
    }

    AbstractProject findJobByPath(String path) {
        return findJobByPath(instance, path)
    }

    static AbstractProject findJobByPath(ItemGroup root, String path) {
        if (path.contains('/')) {
            String folderName = StringUtils.substringBefore(path, '/')
            String rest = StringUtils.substringAfter(path, '/')
            def folder = root.getItem(folderName) as ItemGroup
            return findJobByPath(folder, rest)
        } else {
            return root.getItem(path) as AbstractProject
        }
    }

    interface Build {

        void checkSuccess()

        void checkFailure()

        String getOutput()

    }

    class BuildImpl implements Build {

        private final String path
        private final QueueTaskFuture<AbstractBuild> future
        private final int timeoutSeconds

        private AbstractBuild build

        BuildImpl(String path, QueueTaskFuture<AbstractBuild> future, int timeoutSeconds) {
            this.path = path
            this.timeoutSeconds = timeoutSeconds
            this.future = future
        }

        protected AbstractBuild waitForBuild() {
            if (build) {
                return build
            } else {
                try {
                    build = future.get(timeoutSeconds, TimeUnit.SECONDS)
                    return build
                } catch (TimeoutException ex) {
                    Assert.fail("Could not build ${path} in less than ${timeoutSeconds} seconds.")
                    throw new RuntimeException(ex)
                }
            }
        }

        @Override
        void checkSuccess() {
            def build = waitForBuild()
            if (!build.result.isBetterOrEqualTo(Result.SUCCESS)) {
                println "Output for ${path}#${build.number}:"
                println build.logReader.text
                Assert.fail("${path} resulted in ${build.result}")
            }
        }

        @Override
        void checkFailure() {
            def build = waitForBuild()
            if (build.result.isBetterThan(Result.FAILURE)) {
                Assert.fail("${path} was expected to fail but was ${build.result}")
            }
        }

        @Override
        String getOutput() {
            def build = waitForBuild()
            return build.logReader.text
        }
    }
}
