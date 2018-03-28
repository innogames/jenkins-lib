#!/usr/bin/groovy

// Check if since last building only commits with [maven-release-plugin] added
// Git commettee ignore doesn't work
// return False on 'git push --force'
def Boolean onlyMavenRelease() {
    switch (env.GIT_PREVIOUS_COMMIT) {
        case null:
            // First build, continue
            return false
        default:
            def String notMavenCommits = sh(returnStdout: true, script:
                '''\
                #!/bin/bash
                # Checking for not rewrited history
                if git cat-file -e ${GIT_PREVIOUS_COMMIT} && git cat-file -e ${GIT_COMMIT}
                then
                    git log --oneline ${GIT_PREVIOUS_COMMIT}..${GIT_COMMIT} | grep -vF '[maven-release-plugin]' || exit 0
                else
                    echo "History was rewritten, continue"
                fi
                '''.stripIndent()).trim()
            echo "Not maven commits are:\n${notMavenCommits}"
            return notMavenCommits == ''
    }
}

def packageVersion() {
    // For building from tags. Getting info about released version
    return sh(returnStdout: true, script:
        '''mvn -Dexec.executable='echo' -Dexec.args='${project.version}' --non-recursive exec:exec -q''').trim()
}
