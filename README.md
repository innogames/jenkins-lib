## Jenkins shared library
This library is used as `adminLibs` in InnoGames GmbH Jenkins instances

## "Features"
* Each "variable" from `vars/` MUST be a function for using in Jenkinsfiles
* env.MEMBER is available only as env['MEMBER'] in `vars/*.groovy`
* When you have function `call (Map config)` in var/example.groovy, you could call step `example` without any arguments in pipeline, but if you have `exampleFunction(Map config)`, than you HAVE TO pass there any arguments.
