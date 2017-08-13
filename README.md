#Order Management System
Hackaton

This application is a skeleton of an Order Management System. It has 2 main components:

Web services
1)	User must be able to logon before placing any order, first time user needs to register first before he/she can logon
2)	Logon consists username, password, company
3)	Once logon successfully, user can query the webservice for a list of tradable products and also return your current list of orders
4)	You can buy/sell these tradable products if the price match, otherwise it will reject to explain why.

The Client
1)	HTML Client to interact with the WebService.
2)	client deployed on the same webserver as web service/ 

Application is build with springboot.

Functionalities
 - Authentication based on username and password
 - User management (register new user)
 - CRUD operation on orders
 - view list of products
 
 
 ##NOTE:
 POC is not 100% functional. UI is available just for user management operations.
 Implementation is not Threadsafe
 Security is not implemented
 unit tests fail partially. unit test coverage is low
 integration tests not available
 


#Project plan

##Iteration 1:
1.	Define the requirements(high level, with focus on the customer requested functionalities)
2.	Define the solution
3.	Create a POC for demo/minimum viable product
##Iteration 2:
1.	Extend the requirements (security, api documentation)
2.	Refactor and scale the solution

###TODO:
1.	Decouple security from web service implementation. Use web interceptor/filter for handling authorization
2.	Reorganize project structure/packages. First version has a basic/simple organization based on layers. For large scale application, components (and packages) should be organized by functionalty. These functional components should be decoupled.
a.	User management
b.	Security
c.	Order management
d.	Product management
 
##Iteration 3:

More features:
1.	Decouple web app(ui façade) from backend. To be analysed if communication between web app and backed should be synchronous or async. We could use various communication, restull web services,  RPC, messaging, SOAP, WSDL, web sockets.
2.	Caching
3.	Scale the app by using clusters with multiple nodes. Proxy with a load balancer
4.	Handle authentication with SSO
5.	Secure the application resources by using Auth2 or

