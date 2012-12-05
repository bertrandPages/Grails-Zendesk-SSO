package plugin.grails.zendesk.sso

class ZendeskSSOService {

    def grailsApplication

    /**
     * Keep informations from application to generate url and generate callback url
     * @param userId userId in your application
     * @param timestamp timestamp send by zendesk or generate
     * @param organization (optional)
     * @param tags (optional)
     * @param remotePhotoUrl (optional)
     * @return url to login in zendesk
     */
    def zendeskLoginBackURL(userId, timestamp, organization = null, tags = null, remotePhotoUrl = null){
        String className = grailsApplication.config.zendesk.userDomainClassName

        def user = grailsApplication.getClassForName(className).get(userId)

        def email = user.getAt(grailsApplication.config.zendesk.emailPropertyName)

        def name = []
        def firstname = user.getAt(grailsApplication.config.zendesk.firstnamePropertyName)
        if(firstname){
            name << firstname
        }
        def lastname = user.getAt(grailsApplication.config.zendesk.lastnamePropertyName)
        if(lastname){
            name << lastname
        }
        if(name.size() == 0){
            name << user.getAt(grailsApplication.config.zendesk.namePropertyName?:"")
        }

        def externalId = null
        if(grailsApplication.config.zendesk.externalId){
            externalId = userId
        }

        def entries = [name:name.join(" "), email:email, external_id:externalId?:"", organization:organization?: "",
                tags:tags?: "", remote_photo_url:remotePhotoUrl?:"", timestamp:timestamp]

        return generateUrl(entries)
    }

    /**
     * Generate hash code to be verified by zendesk with secret token shared with zendesk
     * @param entries other paramaters to generate hash : name, email, external_id, organization, tags, remote_photo_url and timestamp
     * @return hash code to be verified by zendesk
     */
    private generateHash(Map entries){
        def params = []
        params << entries.name
        params << entries.email
        params << entries.external_id ?: ""
        params << entries.organization ?: ""
        params << entries.tags ?: ""
        params << entries.remote_photo_url ?: ""
        params << grailsApplication.config.zendesk.secret
        params << entries.timestamp
        def input = params.join('|')
        return input.encodeAsMD5()
    }

    /**
     * generate callback url to login in zendesk
     * @param entries parameters needed to generate url : name, email, external_id, organization, tags, remote_photo_url and timestamp
     * @return
     */
    private generateUrl(Map entries){
        def url = grailsApplication.config.zendesk.baseURL + "?"
        entries.hash = generateHash(entries)
        url += entries.findAll {it.value && it.value != ""}.collect {"${it.key}=${it.value}"}.join('&')
        return url
    }
}
