package com.innogames.jenkins

// Uncomment for local development, this is the current latest version in jenkins plugins
// https://github.com/jenkinsci/github-api-plugin/releases
//@Grab(group='org.kohsuke', module='github-api', version='1.95')

import org.kohsuke.github.GHRelease
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHubBuilder
import java.nio.file.Files


class GitHub implements Serializable {
    private final org.kohsuke.github.GitHub gh
    private GHRepository repo
    List<String> tagsList
    private GHRelease release

    // For GitHub enterprise
    GitHub(String apiUri, String token) {
        def builder = new GitHubBuilder()
        builder.withEndpoint(apiUri)
                .withOAuthToken(token)
        this.gh = builder.build()
    }

    // For github.com
    GitHub(String token) {
        def builder = new GitHubBuilder()
        builder.withOAuthToken(token)
        this.gh = builder.build()
    }

    static repoNameFromUrl(String url) {
        // Returns repository username/name by env.GIT_URL
        // Matches url parts:
        // 1, 2 - scheme and scheme name
        // 3, 4 - username@ and username
        // 5 - hostname
        // 6 - repository
        def re = ~$/^((https|git|ssh)://)*((\w+)@)*([-a-zA-Z0-9.]+)[/:](\S+)\.git$$/$
        def match
        if ((match = url =~ re)) {
            return match[0][6]
        }
    }

    def setRepo(String repository) {
        this.repo = this.gh.getRepository(repository)
    }

    boolean checkTagExists(String tag) throws IOException {
        if (this.repo == null) {
            throw new IOException('Repository is not assigned')
        }
        this.tagsList = this.repo
                .listTags()
                .asList()
                .collect() { it.name }
        return tagsList.contains(tag)
    }

    // Create release only if tag already exists
    def safeCreateRelease(String tag) throws IOException {
        if (this.repo == null) {
            throw new IOException('Repository is not assigned')
        }
        def release = this.repo.createRelease(tag)
        if (this.checkTagExists(tag)) {
            this.release = release.create()
        } else {
            this.release = null
        }
    }

    // Create release only if tag already exists
    def safeCreateRelease(String tag, String body) throws IOException {
        if (this.repo == null) {
            throw new IOException('Repository is not assigned')
        }
        def release = this.repo.createRelease(tag)
        if (this.checkTagExists(tag)) {
            this.release = release.body(body)
                    .create()
        } else {
            this.release = null
        }
    }

    // Create release only if tag already exists
    def safeCreateRelease(String tag, String name, String body) throws IOException {
        if (this.repo == null) {
            throw new IOException('Repository is not assigned')
        }
        def release = this.repo.createRelease(tag)
        if (this.checkTagExists(tag)) {
            this.release = release.body(body)
                    .name(name)
                    .create()
        } else {
            this.release = null
        }
    }

    def releaseByTag(String tag) throws IOException {
        if (this.repo == null) {
            throw new IOException('Repository is not assigned')
        }
       this.release = this.repo.getReleaseByTagName(tag)
    }

    def uploadAsset(String filename) throws IOException {
        if (this.release == null) {
            throw new IOException('Release is not assigned')
        }
        File file = new File(filename)
        String mime = Files.probeContentType(file.toPath())
        InputStream stream = new FileInputStream(file)
        this.release.uploadAsset(filename, stream, mime)
    }
}
