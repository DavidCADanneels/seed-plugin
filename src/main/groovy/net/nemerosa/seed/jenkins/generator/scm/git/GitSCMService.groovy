package net.nemerosa.seed.jenkins.generator.scm.git

import net.nemerosa.seed.jenkins.generator.scm.SCMService
import net.nemerosa.seed.jenkins.support.SeedProjectEnvironment

class GitSCMService implements SCMService {

    @Override
    String getId() {
        'git'
    }

    @Override
    String generatePartial(SeedProjectEnvironment env, String branch, String path) {
        String credentialsId = env.getConfigurationValue(SCM_CREDENTIALS_ID, '')
        """\
scm {
    git {
        remote {
            url '${env.scmUrl}'
            credentials '${credentialsId}'
        }
        branch '${branch}'
        configure { node ->
            node / 'extensions' / 'hudson.plugins.git.extensions.impl.CloneOption' {
                'shallow' true
                'reference'()
            }
        }
    }
}
"""
    }
}
