#!/usr/bin/groovy

import hudson.Util

// common docker options
def String dockerArgs() {
    return "-v /dev/shm:/dev/shm -v /etc/group:/etc/group:ro -v /etc/passwd:/etc/passwd:ro -v /etc/ssh/ssh_known_hosts:/etc/ssh/ssh_known_hosts:ro -v ${env.HUDSON_HOME}:${env.HUDSON_HOME}:rw"
}

// Get info about job launching cause
def Boolean launchedByUser() {
    return currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause) != null
}

// Check if since last building only commits from ${env.GIT_COMMITTER_EMAIL} was added
// return false on 'git push --force'
def Boolean onlyJenkinsRelease() {
    // If git commiter not configured, so push back from jenkins shouldn't be possible
    if ([null, ''].contains(env.GIT_COMMITTER_EMAIL)) {
        return false
    }
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
                    git log --oneline --pretty='format:%h (%ce): %s' ${GIT_PREVIOUS_COMMIT}..${GIT_COMMIT} | grep -vF "${GIT_COMMITTER_EMAIL}" || exit 0
                else
                    echo "History was rewritten, continue"
                fi
                '''.stripIndent()).trim()
            echo "Not jenkins commits are:\n${notMavenCommits}"
            return notMavenCommits == ''
    }
}

// Upload built package to repo
// config should contain next keys:
//  - emulate (optional): for debugging and development of jenkins' jobs
//  - file: debian package file
//  - token: deb-drop token
//  - repo: repository for uploading
def uploadPackage(Map config) {
    if (!fileExists(config.file)) {
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
def buildParameters(Map config = [:]) {
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
    for (i = builds.size() - 1; i >= 0; i--) {
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
def processException(java.lang.Exception e) {
    currentBuild.result = 'FAILURE'
    error "Something wrong: ${e.getClass().getCanonicalName()} thrown with message: ${e.getMessage()}"
}

// Post running slack notifications
def postSlack(verbose = false, channel = null) {
    echo "Slack notification with verbose=${verbose} and overwriting of default channel=${channel}"
    colors = [SUCCESS: 'good', FAILURE: 'danger', UNSTABLE: 'warning']
    msg = env.JOB_NAME + " - " + currentBuild.displayName

    if (['FAILURE', 'UNSTABLE'].contains(currentBuild.currentResult)) {
        if (
            currentBuild.getPreviousBuild() &&
            ['FAILURE', 'UNSTABLE'].contains(currentBuild.getPreviousBuild().result)
        ) {
            msg += ' Still failing'
        } else {
            msg += ' Failure'
        }

        msg += ' after ' + Util.getTimeSpanString(currentBuild.duration)
    } else if (
      ['ABORTED', 'NOT_BUILT'].contains(currentBuild.currentResult) &&
      verbose
    ) {
        msg += ' Aborted after ' + Util.getTimeSpanString(currentBuild.duration)
    } else if (
      currentBuild.getPreviousBuild() && // if it's not null
      currentBuild.getPreviousBuild().result != 'SUCCESS'
    ) {
        lastSuccessfulBuild = currentBuild.getPreviousBuild()
        while (lastSuccessfulBuild && lastSuccessfulBuild.result != 'SUCCESS') {
            lastSuccessfulBuild = lastSuccessfulBuild.getPreviousBuild()
        }

        msg += ' Back to normal'
        if (lastSuccessfulBuild) {
            msg += ' ' + Util.getTimeSpanString(System.currentTimeMillis() - lastSuccessfulBuild.startTimeInMillis)
        }
    } else if (verbose) {
        msg += ' Success after ' + Util.getTimeSpanString(currentBuild.duration)
    } else {
        return
    }

    msg += " (<${currentBuild.absoluteUrl}|Open>)"

    slackSend color: colors[currentBuild.currentResult], message: msg, channel: channel
}
