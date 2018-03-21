#!/usr/bin/groovy

// Check if since last building only commits with [maven-release-plugin] added
// Git commettee ignore doesn't work
// return False on 'git push --force'
def Boolean onlyMavenRelease() {
    def String notMavenCommits = sh(returnStdout: true, script:
        '''\
        #!/bin/bash
        git log --oneline ${GIT_PREVIOUS_COMMIT}..${GIT_COMMIT} | grep -vF '[maven-release-plugin]' || exit 0
        '''.stripIndent()).trim()
    echo "Not maven commits are:\n${notMavenCommits}"
    return notMavenCommits == ''
}

def packageVersion() {
    // For building from tags. Getting info about released version
    env['VERSION_FROM_POM'] = sh(returnStdout: true, script:
        '''mvn -Dexec.executable='echo' -Dexec.args='${project.version}' --non-recursive exec:exec -q''').trim()
    echo env['VERSION_FROM_POM']
}
