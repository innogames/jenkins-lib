package com.innogames.jenkinslib.debdrop


import com.innogames.jenkinslib.io.ShellService
import com.innogames.jenkinslib.logger.Logger
import hudson.FilePath
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DebDropService {

	Logger log

	ShellService shell

	@Autowired
	DebDropService(Logger log, ShellService shell) {
		this.log = log
		this.shell = shell
	}

	def uploadPackage(FilePath file, String repository, String token, String debDropUrl, int keepVersions = 5, boolean dryRun = false) {
		def filePath = file.getRemote()
		def fileName = file.getName()
		if (!file.exists()) {
			throw new IllegalArgumentException("File ${filePath} doesn't exists, abort uploading to repo")
		}

		def curlCommand = "curl -qf -F token='${token}' -F repos='${repository}' -F package='@${filePath}' -F versions='${keepVersions}' '${debDropUrl}'"
		if (dryRun) {
			log.log("Emulate uploading of ${fileName} to ${repository} with token ${token}")
			log.log("${curlCommand}")
			log.log("${fileName} uploaded to ${repository}")
		} else {
			if (shell.sh(curlCommand) == 0) {
				log.log("${fileName} uploaded to ${repository}")
			} else {
				log.error('Something went wrong!')
			}
		}
	}

}
