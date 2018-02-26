#!/usr/bin/groovy

// Depends on input parameters we could have different versions
def Version(String upd, Map part) {
    switch (upd) {
        case 'patch':
            return "${part.major}.${part.minor}.${part.patch + 1}"
        case 'minor':
            return "${part.major}.${part.minor + 1}.0"
        case 'major':
            return "${part.major + 1}.0.0"
    }
}

def call(Map config) {
    // Getting all parameters in env
    params.each{ k, v ->
        env[k] = v
    }
    // Every UPPERCASE var should be passed into env
    // Checking out sources
    def results = checkout scm

    // Map all results variables into env
    results.each{ k, v ->
        env[k] = v
    }

    // Remote repo name
    env.GIT_REMOTE = sh(returnStdout: true, script: 'git remote').trim()

    // Do we building from branch or tag?
    env.GIT_BRANCH_OR_TAG = sh(returnStdout: true, script:
    """\
        #!/bin/bash -e
        if git rev-parse --verify -q refs/remotes/${env.GIT_REMOTE}/${env.GIT_BRANCH}^{} | grep -q ${env.GIT_COMMIT}
          then echo branch
          exit 0
        elif git rev-parse --verify -q refs/tags/${env.GIT_BRANCH}^{} | grep -q ${env.GIT_COMMIT}
          then echo tag
          exit 0
        else
          echo unknown
          exit 0
      fi
    """.stripIndent()).trim()

    // Last version from tags
    env.GIT_LAST_VERSION = sh(returnStdout: true,
        script: 'git tag -l "v[0-9]*"| sort -V | tail -n 1').trim()

    // Geting version parts
    def Map versionParts = [:]
    if (!(env.GIT_LAST_VERSION =~ /^v(\d+\.)+\d+$/)) {
        echo "There no or wrong last version in tags (${env.GIT_LAST_VERSION}), creating the first release 0.0.1"
        versionParts.with{ (major, minor, patch) = [0, 0, 1] }
    } else {
        versionParts.with{ (major, minor, patch) = env.GIT_LAST_VERSION.replaceFirst(~/^v/,'').tokenize('.') }
        versionParts.each{ k, v ->
            versionParts[k] = v?.isInteger() ? v.toInteger() : 0
        }
    }
    env.NEW_VERSION = Version(params.UPDATE_VERSION, versionParts)

    env.GIT_NEW_TAG = "v${env.VERSION_FOR_TAGING}"
}
