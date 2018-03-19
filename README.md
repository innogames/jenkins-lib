## Jenkins shared library
This library is used as `adminLibs` in InnoGames GmbH Jenkins instances

## "Features"
* Each "variable" from `vars/` MUST be a function for using in Jenkinsfiles
* env.MEMBER is available only as env['MEMBER'] in `vars/*.groovy`
