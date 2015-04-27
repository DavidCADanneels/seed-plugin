package net.nemerosa.seed.jenkins.step

import com.google.inject.Guice
import com.google.inject.Injector
import net.nemerosa.seed.jenkins.generator.scm.SCMService
import net.nemerosa.seed.jenkins.generator.scm.SCMServiceModule
import net.nemerosa.seed.jenkins.generator.scm.SCMServiceRegistry
import net.nemerosa.seed.jenkins.support.SeedProjectEnvironment

class BranchSeedScmExtension {

    private final SeedProjectEnvironment projectEnvironment
    private final String branch

    private final Injector injector = Guice.createInjector(new SCMServiceModule())

    BranchSeedScmExtension(SeedProjectEnvironment projectEnvironment, String branch) {
        this.branch = branch
        this.projectEnvironment = projectEnvironment
    }

    String generate() {
        // Gets the SCM registry
        SCMServiceRegistry scmServiceRegistry = injector.getInstance(SCMServiceRegistry)
        // Gets the SCM service
        SCMService scmService = scmServiceRegistry.getScm(projectEnvironment.scmType)
        // Generation
        return scmService.generatePartial(projectEnvironment, branch, 'seed')
    }
}
