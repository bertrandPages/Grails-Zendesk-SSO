package plugin.grails.zendesk.sso

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet

class ZendeskSSOService {

    def grailsApplication

    /**
     * Keep informations from application to generate url and generate callback url
     * @param userId userId in your application
     * @param returnTo return to url provides by zendesk
     * @param organization (optional)
     * @param tags (optional)
     * @param remotePhotoUrl (optional)
     * @return url to login in zendesk
     */
    def zendeskLoginBackURL(userId, returnTo = null, organization = null, tags = null, remotePhotoUrl = null){
        String className = grailsApplication.config.grails.plugins.zendesk.userDomainClassName

        def user = grailsApplication.getClassForName(className).get(userId)

        def email = user.getAt(grailsApplication.config.grails.plugins.zendesk.emailPropertyName)

        def name = []
        def firstname = user.getAt(grailsApplication.config.grails.plugins.zendesk.firstnamePropertyName)
        if(firstname){
            name << firstname
        }
        def lastname = user.getAt(grailsApplication.config.grails.plugins.zendesk.lastnamePropertyName)
        if(lastname){
            name << lastname
        }
        if(name.size() == 0){
            name << user.getAt(grailsApplication.config.grails.plugins.zendesk.namePropertyName?:"")
        }

        def externalId = null
        if(grailsApplication.config.grails.plugins.zendesk.externalId){
            externalId = userId
        }

        def entries = [name:name.join(" "), email:email, external_id:externalId?:"", organization:organization?: "",
                tags:tags?: "", remote_photo_url:remotePhotoUrl?:""]

        def jwtString = generateHash(entries)
        if(jwtString){
            return generateUrl(jwtString, returnTo)
        }
        return null
    }

    /**
     * Generate hash code to be verified by zendesk with secret token shared with zendesk
     * @param entries other paramaters to generate jwt code : name, email, external_id, organization, tags, remote_photo_url and timestamp
     * @return jwt code to be verified by zendesk
     */
    private generateHash(Map entries){
        def sharedKey = grailsApplication.config.grails.plugins.zendesk.secret

        JWTClaimsSet jwtClaims = new JWTClaimsSet();
        jwtClaims.setIssueTime(new Date());
        jwtClaims.setJWTID(UUID.randomUUID().toString());
        def name = entries.name
        jwtClaims.setCustomClaim("name", name);
        jwtClaims.setCustomClaim("email", entries.email);
        jwtClaims.setCustomClaim("external_id", entries.external_id);
        if(entries.tags) jwtClaims.setCustomClaim("tags", entries.tags);
        if(entries.organization) jwtClaims.setCustomClaim("organization", entries.organization);
        if(entries.remote_photo_url) jwtClaims.setCustomClaim("remote_photo_url", entries.remote_photo_url);


        // Create JWS header with HS256 algorithm
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        header.setContentType("text/plain");
        // Create JWS object
        JWSObject jwsObject = new JWSObject(header, new Payload(jwtClaims.toJSONObject()));

        // Create HMAC signer
        JWSSigner signer = new MACSigner(sharedKey.getBytes());

        try {
            jwsObject.sign(signer);
        } catch(com.nimbusds.jose.JOSEException e) {
            System.err.println("Error signing JWT: " + e.getMessage());
            return null
        }
        // Serialise to JWT compact form
        return jwsObject.serialize();
    }

    /**
     * Generate url to call zendesk authentication
     * @param jwtString jwt code
     * @param returnTo return to url provides by zendesk
     * @return url to login in zendesk
     */
    private generateUrl(String jwtString, String returnTo){
        def baseUrl = grailsApplication.config.grails.plugins.zendesk.baseURL
        if(returnTo){
            return baseUrl + "jwt?jwt=" + jwtString + "&return_to=" + returnTo
        } else {
            return baseUrl + "jwt?jwt=" + jwtString
        }
    }
}
