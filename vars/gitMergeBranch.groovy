#!/usr/bin/groovy
// Just merge source branch to destination, fail on fail

def call(Map config) {
    // We merge ${srcBranch} to ${dstBranch}
    if (config.srcBranch != null && config.dstBranch != null && env.GIT_REMOTE != null) {
        sh """
            #!/bin/bash -ex
            git checkout develop
            git pull ${env['GIT_REMOTE']}
            git merge ${env['GIT_REMOTE']}/${config.srcBranch}
            git push ${env['GIT_REMOTE']} ${config.dstBranch}
        """.stripIndent()
    }
}

