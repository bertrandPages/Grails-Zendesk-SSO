includeTargets << grailsScript('_GrailsBootstrap')

USAGE = """
Usage: grails zendesk-s2-quick-start <domain-class-package> <user-class-name> <email-property-name>
    <firstname-property-name> <lastname-property-name> <name-property-name> <domain-url>

Creates controllers, view and update configuration

Example: grails zendesk-s2-quick-start com.yourapp User email firstname lastname name grails
"""
overwriteAll = false
packageName = ''
userClassName = ''
emailPropertyName = ''
firstnamePropertyName = ''
lastnamePropertyName = ''
namePropertyName = ''
urlDomain = ''
templateDir = "$zendeskSsoPluginDir/src/templates"
appDir = "$basedir/grails-app"

target(zendeskS2Quickstart: "Create artifacts for zendesk plugins using spring security") {

    if(!configure()){
        return 1
    }

    copyControllersAndViews()
    updateConfig()
    printMessage """
*******************************************************
* Created controllers, and GSPs.                      *
* Your grails-app/conf/Config.groovy has been updated *
* with the class names of the configured domain       *
* classes; please verify that the values are correct. *
*******************************************************
"""
}

private boolean configure() {

    def argValues = parseArgs()
    if (!argValues) {
        return false
    }

    if (argValues.size() == 7) {
        (packageName, userClassName, emailPropertyName,firstnamePropertyName, lastnamePropertyName, namePropertyName, urlDomain) = argValues
    } else {
        return false
    }

    true
}

private parseArgs() {
    def args = argsMap.params

    if (7 == args.size()) {
        printMessage "Updating configuration"
        return args
    }

    errorMessage USAGE
    null
}

private void copyControllersAndViews() {
    ant.mkdir dir: "$appDir/views/zendesk"
    copyFile "$templateDir/ZendeskController.groovy.template", "$appDir/controllers/ZendeskController.groovy"
}

private void updateConfig() {

    def configFile = new File(appDir, 'conf/Config.groovy')
    if (configFile.exists()) {
        configFile.withWriterAppend {
            it.writeLine '\n// Added by the Zendesk SSO plugin:'
            it.writeLine "grails.plugins.zendesk.baseURL = 'https://${urlDomain}.zendesk.com/access/remoteauth'"
            it.writeLine "grails.plugins.zendesk.secret = 'ChangeZendeskSecret'"
            it.writeLine "grails.plugins.zendesk.externalId = true"
            it.writeLine "grails.plugins.zendesk.userDomainClassName = '${packageName}.$userClassName'"
            it.writeLine "grails.plugins.zendesk.emailPropertyName = '$emailPropertyName'"
            it.writeLine "grails.plugins.zendesk.firstnamePropertyName = '$firstnamePropertyName'"
            it.writeLine "grails.plugins.zendesk.lastnamePropertyName = '$lastnamePropertyName'"
            it.writeLine "grails.plugins.zendesk.namePropertyName = '$namePropertyName'"
        }
    }
}

okToWrite = { String dest ->

    def file = new File(dest)
    if (overwriteAll || !file.exists()) {
        return true
    }

    String propertyName = "file.overwrite.$file.name"
    ant.input(addProperty: propertyName, message: "$dest exists, ok to overwrite?",
            validargs: 'y,n,a', defaultvalue: 'y')

    if (ant.antProject.properties."$propertyName" == 'n') {
        return false
    }

    if (ant.antProject.properties."$propertyName" == 'a') {
        overwriteAll = true
    }

    true
}

copyFile = { String from, String to ->
    if (!okToWrite(to)) {
        return
    }

    ant.copy file: from, tofile: to, overwrite: true
}

printMessage = { String message -> event('StatusUpdate', [message]) }
errorMessage = { String message -> event('StatusError', [message]) }


setDefaultTarget 'zendeskS2Quickstart'
