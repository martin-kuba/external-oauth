# external-oauth
Example of external authentication using OAuth with famous providers. 

Provides authentication using Facebook, Google, LinkedIn, OrcID and GitHub.
 
All of the authentication providers support OAuth 2 protocol, so a common ancestor class BaseOAuthServlet contains all the actual protocol handling.
 
To get it working, the file src/main/resources/config.properties must be added containing client_id and client_secret for each provider. It is necessary to register this application at each OAuth provider to obtain the **client_id** and **client_secret**.
   
   
