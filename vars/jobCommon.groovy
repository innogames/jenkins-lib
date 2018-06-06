#!/usr/bin/groovy

// common docker options
def String dockerArgs() {
    return "-v /dev/shm:/dev/shm -v /etc/group:/etc/group:ro -v /etc/passwd:/etc/passwd:ro -v /etc/ssh/ssh_known_hosts:/etc/ssh/ssh_known_hosts:ro -v ${env.HUDSON_HOME}:${env.HUDSON_HOME}:rw"
}

// Get info about job launching cause
def Boolean launchedByUser() {
    return currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause) != null
}

def uploadPackage (Map config) {
    sh """\
        #!/bin/bash -e
        ls -l "${config.file}"
        echo curl -qf -F token="${config.token}" -F repos="${config.repo}" -F package="@${config.file}" "${env.DEB_DROP_URL}"
       """.stripIndent()
    echo "${config.pkg} uploaded to ${config.repo}"
}
