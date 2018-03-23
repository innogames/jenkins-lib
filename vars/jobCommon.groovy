#!/usr/bin/groovy

// common docker options
def String dockerArgs() {
    return "-v /dev/shm:/dev/shm -v /etc/group:/etc/group:ro -v /etc/passwd:/etc/passwd:ro -v /etc/ssh/ssh_known_hosts:/etc/ssh/ssh_known_hosts:ro -v ${env.HUDSON_HOME}:${env.HUDSON_HOME}:rw"
}

// Get info about job launching cause
def Boolean launchedByUser() {
    return currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause) != null
}

// config should contain next keys:
//  - emulate (optional): for debugging and development of jenkins' jobs
//  - file: debian package file
//  - token: deb-drop token
//  - repo: repository for uploading
def uploadPackage (Map config) {
    if (! fileExists(config.file)) {
        error "File ${config.file} doesn't exists, abort uploading to repo"
    }
    if (config.emulate) {
        echo "Emulate uploading of ${config.file} to ${config.repo} with token ${config.token}"
        sh "echo curl -qf -F token='${config.token}' -F repos='${config.repo}' -F package='@${config.file}' '${env.DEB_DROP_URL}'"
        echo "${config.file} uploaded to ${config.repo}"
    } else {
        sh "curl -qf -F token='${config.token}' -F repos='${config.repo}' -F package='@${config.file}' '${env.DEB_DROP_URL}'"
        echo "${config.file} uploaded to ${config.repo}"
    }
}

def processException(hudson.AbortException e) {
    currentBuild.result = 'FAILURE'
    error "Something wrong, exception is: ${e}"
}
