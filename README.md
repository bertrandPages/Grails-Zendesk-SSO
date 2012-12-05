# Grails Zendesk Single Sign On
This project intend to provide a grails plugin to interact with zendesk and particularly with the remote auth functionality.


## Installation
The plugin isn't available yet on the official Grails plugin repository so you can't pull the dependencies directly.

## License

    Copyright 2012 the original author or authors.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

## Configuration
zendesk.baseURL: Base URL of your zendesk domain remote auth
zendesk.secret: Secret generated by zendesk
zendesk.externalId: Allow you to use your id in znedesk application
zendesk.userDomainClassName: User class name of your application
zendesk.emailPropertyName: Email property name of your user class
zendesk.firstnamePropertyName: Firstname property name of your user class, use to compose final name for zendesk
zendesk.lastnamePropertyName: Lastname property name of your user class, use to compose final name for zendesk
zendesk.namePropertyName: If there is no firstname and lastname, property name use to compose final name for zendesk

eg:
zendesk {
    baseURL = "https://domain.zendesk.com/access/remoteauth"
    secret = "secrectSharedWithZendesk"
    externalId = true
    userDomainClassName = 'Person'
    emailPropertyName = 'email'
    firstnamePropertyName = 'firstname'
    lastnamePropertyName = 'lastname'
    namePropertyName = 'name'
}