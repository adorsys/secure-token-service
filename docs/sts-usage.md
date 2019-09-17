# STS USAGE

<h3>"The most important functionality of STS is:
Design a clean key management, so that you can provide POP endpoints, so that you can allow network components 
to communicate with each other free of the hassle of network topologies we can not control."
</h3>    

This document gives an example, how components of STS may be used.

Furhter functionionality of the POP is:
- generate key pairs (needed for pop)
- generate secret keys (for what?)
- key rotation
- clustering (so that many servers can share)

The second functionality of STS is the Secret Server.

The third functionality is tha Token generation. This functionality is 
less important, befause there exist a lot of frameworks for that.

In the following diagram the following use case is shown:  
1. user identifies at IDP
2. user sends JWT to backend
3. backend asks secret server for users secret key
4. secret server retrieves users secret key and returns it
5. backend can en- and decrypt data of user

![Modules map](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/secure-token-service/v2/documentation/docs/sts-usage.puml&fmt=svg&vvv=1&sanitize=true)

## POP ##
The Proof of Posession (POP). 
The POP is part of the STS library. Its main task is the key management.
The key management generates private/public key pairs. Any client of the POP
gets a public key to encrypt data. Decryption can only be done with the
private key. As described above, further features are key rotation and sharing the
keys over the network to other nodes.

In this example, the GUI gives sensible data to the backend. To encrypt the data, the POP 
in the backend returns a public key. So data can be encrypted, independant of the overlying
protocol. So even if the connection from the client to the server is going over a proxy and
the content is logged, only encrypted data is visible.

Notice, that only the data coming from the client to the server is
encrypted. The data going from the server to the client is not 
encrypted or only encrypted by protocol.

## JWTS-URL ##
A similiar functionality as the POP is used in the IDP. It is called the 
JWTS-URL. To retrieve a session token, the GUI talks to the IDP. To pass the 
sensible login credentials they are encrypted with a public key of
the JWTS-URL.  

After successful authentication the IDP returns a token (JWT). This token is 
signed by the IDP. 

Notice, the data given from the IDP to GUI is not encrypted. 


## registered resource server ##
To verify that Datacenter1 is allowed to send requests to Datacenter2 
the Datacenter1 has to be registered to 
the Datacenter2. This component is the "registered resource server". This makes sure,
that a request comming to Datacenter2 comes from a well known client.

Similiar to this is the registration of IDP. It ensures that not any JWT
is accepted. The IDP has to be contacted by the Datacenter2 to verify the
signature of the JWT. See below in secret server description. 

## Secret Server ##
To en- and decrypt the backends persistent data (running in the Datacenter1) the 
users secret key is needed. The users secret key is 
stored in the datacenter 2. The datacenter 2 gets the JWT from the backend server. 
Now, as a second protection (the first is the "registered resource server")
is to verify that the token really comes from the IDP.
verify this signature
the secet server has to get the IDPs public key (classical Auth0).
Eventually when the token is valid, the secret server returns the
users secret key corresponding to the users subject of the JWT.

If there is no users secret key for the user yet, the secret server 
generates a new users secret key and stores the key for the users subject.
And then the new users secret key is returned.

Rather than simply returning the users secet key, the secret server requests the POP of the 
datacenter 1 to retrieve a public key to encrypt the returned users secret key.
With the users secret key the datacenter 1 can en- and deycrypt the data to be stored for the client.

The secrets stored in the secret server are encrypted by a managment key. 
