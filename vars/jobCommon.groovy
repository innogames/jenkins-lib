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

// Sets build name and description, does nothing on blank config
def buildParameters(Map config=[:]) {
    config.each { k, v ->
        switch (k) {
            case 'displayName': currentBuild.k = v; break
            case 'description': currentBuild.k = v; break
            default: break
        }
    }
}

// Cleaning out all previous builds with status in ['ABORTED', 'NOT_BUILT']
def cleanNotFinishedBuilds(statusesToClean = ['ABORTED', 'NOT_BUILT']) {
    def builds = jenkins.model.Jenkins.instance.getItemByFullName(env.JOB_NAME).builds
    println "Checking next builds if their status in ${statusesToClean}: ${builds}"
    // We HAVE to use `for` with reversed order.
    // If we use `[].each` -> after deleting some build java.util.NoSuchElementException caused
    // If we use `i++` -> after any deletion the rest of array shifted "left"
    for (i=builds.size() - 1; i>=0; i--) {
        if (statusesToClean.contains(builds[i].result.toString())) {
            println "going to delete ${builds[i]}"
            try {
                builds[i].delete()
            }
            catch (all) {
                println "Error while deleting: ${all}"
            }
        }
    }
}

// Processing exceptions in `catch` sections
def processException(hudson.AbortException e) {
    currentBuild.result = 'FAILURE'
    error "Something wrong, exception is: ${e}"
}

// Post running slack notifications
def postSlack () {
    colors = [SUCCESS: '#00FF00', FAILURE: '#FF0000', UNSTABLE: '#FFCC00']
   if ( !(
          currentBuild.getPreviousBuild() == null ||
          ['SUCCESS', 'ABORTED', 'NOT_BUILT'].contains(
          currentBuild.getPreviousBuild().result
          )
        ) && currentBuild.currentResult == 'SUCCESS') {
        echo "Previous build status was ${currentBuild.getPreviousBuild().result}, current is ${currentBuild.currentResult}"
        slackSend color: colors[currentBuild.currentResult], message: "Job back to Normal: ${env.JOB_NAME} - #${env.BUILD_NUMBER} Success (${env.BUILD_URL})"
    } else if ( ["FAILURE", "UNSTABLE"].contains(currentBuild.currentResult) ) {
        echo "Current job status is ${currentBuild.currentResult}"
        slackSend color: colors[currentBuild.currentResult], message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} ${currentBuild.currentResult} (${env.BUILD_URL})"
    }
}
