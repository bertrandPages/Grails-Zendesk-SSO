package plugin.grails.zendesk.sso

class ZendeskController {

    // Call when user want login
    def auth() {

    }

    // Call after user login in your application
    def backToZendesk(){

    }

    // Call when an error occur or when user logout
    def logout() {
        session.invalidate()

        // Email return by zendesk
        def email = params.email
        // External id return by zendesk if exists
        def external_id = params.external_id
        // Type of message return by zendesk
        def kind = params.kind
        // Message return by zendesk
        def message = params.message

        [kind:kind, message:message]
    }
}
