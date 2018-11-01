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
        case null :
            return ""
        default:
            error('Something wrong in parameters')
    }
}

def BranchOrTag(env) {
    if ([env['TAG_DATE'], env['TAG_NAME'], env['TAG_TIMESTAMP'], env['TAG_UNIXTIME']] != [null, null, null, null] && env['TAG_NAME'] == env['BRANCH_NAME']) {
        return 'tag'
    } else if (env['BRANCH_NAME'] != null) {
        return 'branch'
    }
    return null
}

def call(Map config) {
    // Every UPPERCASE var should be passed into env
    // Checking out sources with tags if needed
    if (config.checkout) {
        def results = checkout([
            $class: 'GitSCM',
            branches: scm.branches,
            doGenerateSubmoduleConfigurations: scm.doGenerateSubmoduleConfigurations,
            extensions: [
                [$class: 'CloneOption', noTags: false, shallow: false, depth: 0, reference: ''],
                [$class: 'WipeWorkspace']
            ],
            userRemoteConfigs: scm.userRemoteConfigs,
        ])

        // Map all results variables into env
        results.each{ k, v ->
            env[k] = v
        }
    }

    // Remote repo name. We don't expect more than one remote
    env['GIT_REMOTE'] = sh(returnStdout: true, script: 'git remote').trim()
    // Do we building from branch or tag?
    env['GIT_BRANCH_OR_TAG'] = BranchOrTag(env)
    // Last version from tags
    env['GIT_LAST_VERSION_TAG'] = sh(returnStdout: true,
        script: 'git tag -l "v[0-9]*"| sort -V | tail -n 1').trim()
    env['GIT_LOCAL_BRANCH'] = "${env['GIT_BRANCH'].replaceFirst('^' + env['GIT_REMOTE'] + '/', '')}"

    // Geting version parts
    versionParts = [:]
    if (!(env['GIT_LAST_VERSION_TAG'] =~ /^v(\d+\.)+\d+$/)) {
        echo "There no or wrong last version in tags (${env['GIT_LAST_VERSION_TAG']}), creating the first release 0.0.1"
        versionParts.major = 0
        versionParts.minor = 0
        versionParts.patch = 1
        env['NEW_VERSION'] = "${versionParts.major}.${versionParts.minor}.${versionParts.patch}"
    } else {
        env['GIT_LAST_VERSION'] = env['GIT_LAST_VERSION_TAG'].replaceFirst(~/^v/,'')
        vp = env['GIT_LAST_VERSION'].tokenize('.')
        vp.eachWithIndex{v, k ->
            vp[k] = v?.isInteger() ? v.toInteger() : 0
        }
        versionParts.major = vp[0]
        versionParts.minor = vp[1]
        versionParts.patch = vp[2]
        env['NEW_VERSION'] = Version(params.UPDATE_VERSION, versionParts)
    }

    env['GIT_NEW_TAG'] = "v${env['NEW_VERSION']}"
    env['TARGET_VERSION'] = env['GIT_BRANCH_OR_TAG'] == 'branch' ? env['NEW_VERSION'] : env['GIT_LAST_VERSION']

    // We change build display name by default
    if (config.get('changeBuildName', true)) {
        currentBuild.displayName = "v${env['TARGET_VERSION']} (${env['BRANCH_NAME']} - ${params.UPDATE_VERSION})"
    }
}
