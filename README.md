# angular2-oauth2-swagger2

There is a back end server (Spring Boot) with OAuth2 server and a basic Spring Rest web service (to retrieve users).


The front end (web) is an Angular2 app that performs a login by calling the back end oauth2 server.

# Install mongo db v3

# To build

mvn clean install (from top level).


# Start server
cd server
./start
http:localhost:8080

# Start web
cd web
npm install
npm start

http://localhost:3000


# Swagger
http://localhost:8080/v2/api-docs - this gives you the 'swagger json'
http://localhost:8080/swagger-ui/index.html



# Tip 1

If you want to validate your swagger.json you can hit your endpoint ending /api-docs (see above) and paste it into :  http://editor.swagger.io

This will not only tell you if there is anything wrong, it will also allow you to generate source code for clients.  This is useful in my
example as I am interested in generating client code for Angular 2.


# Tip 2

You must understand this statement:

Just to give you a high level idea without getting into the code, security has different pieces that all work together in concert

The API itself needs to be protected. This is achieved by using, for simplicity sake, spring security and may also use a combination of servlet container and tomcat/jersey etc.
   - This means doing something like this to your API:
   
       @ApiOperation(value = "Get blah blah", 
           authorizations = {
             @Authorization(
                 value="oauth", 
                 scopes = { @AuthorizationScope(scope = "read") }
                 )
           }
         )
         
         The value of authorization must match the oauth2 
                         
The security scheme which describes the techniques you've used to protect the api. Spring fox supports whatever schemes swagger specification supports (ApiKey, BasicAuth and OAuth2 (certain profiles))

  - This means you MUST setup securitySchemes on the Swagger Docket object
  
Finally the security contexts which actually provides information on which api's are protected by which schemes.

  - This means you MUST setup securityContexts on the Swagger Docket object
  
  These last two are setup in the SwaggerConfig file.

# Tip 3

Documentation links are:

* http://springfox.github.io/springfox/docs/current/
* https://github.com/swagger-api/swagger-core/wiki/Annotations-1.5.X
* https://groups.google.com/forum/#!forum/swagger-swaggersocket - for support


# Tip 4

If you see any CORS errors from Swagger Ui, treat it with caution, it is often an error which has nothing to do with CORS.

# Tip 5

There are the following different OAuth2 flows, and Swagger Ui does not support all of them:

https://docs.oracle.com/cd/E39820_01/doc.11121/gateway_docs/content/oauth_flows.html

* Authorization Code (or Web Server) Flow - this *IS* supported by swagger
* Implicit Grant (or User Agent) Flow - this *IS* supported by swagger and is implemented in this sample.
* Resource Owner Password Credentials Flow - I don't believe this is supported by swagger ui.
* Client Credentials Grant Flow - only to be used by confidential clients - I don't believe this is supported by swagger ui.
* OAuth 2.0 JWT Flow - I don't believe this is supported by swagger ui.

  
# Tip 6

When you create an instance of the OAuth object in your SwaggerConfig, (ie. new OAuth("oauth2")), it *may be* important to supply the name as oauth2 specifically.

# Tip 7

Don't import swagger-ui using the maven import / webjar method because this copy of swagger-ui is out of date, get the latest from here:

https://github.com/swagger-api/swagger-ui/tree/master/dist

download it, and stick in src/main/resources/static (if you are using Spring Boot), or where your public html files are served from (ie. htdocs).

Then, edit the index.html file and setup url so the swagger.json can be retrieved by swagger ui, ie. = "/v2/api-docs"
/v2/api-docs will be available if you have swagger-core imported (aka maven/gradle etc.).  

# Tip 8

I don't think you can just deploy the editor locally on your server.  You either use the cloud version or 
you run another web server (ie. node)

# Tip 9 

As you are deploying the swagger-ui locally, you should not have any CORS issues.  There is a CORS filter in this example, but it is
for handling the requests from the angular web sample.

# Tip 10

I ended up enabling form login to allow the user to login directly from my backend server.  This is because I did not want the users having to generate 
an access token (using curl for example), and having to paste that token in.  In order to do this, I had to enable formLogin in my Spring Security setup.
However, this then gave me an additional problem as the oauth2 filters were fighting with the form login filter.  

So I decided to separate out the config so I have two WebSecurtyConfig's, this allowed me to use the @Order annotation, so I could give one greater precedence
over the other, but this mean't that either ONLY the form login worked, or ONLY the oauth2 login worked.

So I decided I actually wanted the oauth2 filter to be processing my REST calls, so I told the formLogin WebSecurityConfig to ignore the call to my REST service (/users) 
and this mean't the oauth2 web security config picked it up instead.  

/users -> oauth2 filter/security config
/swagger-ui/index.html - allow through
/login -> form login filter/security config