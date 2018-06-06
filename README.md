## Jenkins shared library
This library is used as `adminLibs` in InnoGames GmbH Jenkins instances

## Important:
You have to develop in branch with some version (ie. 0.1) and then, after finishing, you should tag stable state. Don't use default version in your Jenkinsfiles (`library 'adminsLib'`) but `library 'adminsLib@version'`. Version could be one of a branch or a commit or a tag.

## "Features"
* Groovy in jenkins couldn't process `Map.with{}` closure, so you have to do it in ugly way
* Each "variable" from `vars/` MUST be a function for using in Jenkinsfiles
* env.MEMBER is available only as env['MEMBER'] in `vars/*.groovy`
* When you have function `call (Map config)` in var/example.groovy, you could call step `example` without any arguments in pipeline, but if you have `exampleFunction(Map config)`, than you HAVE TO pass there any arguments.
