# STS USAGE
This document gives an example, how components of STS may be used.

<h3>"The most important functionality of STS is:
Design a clean key management, so that you can provide POP endpoints, so that you can allow network components 
to communicate with each other free of the hassle of network topologies we can not control."
</h3>    

Furhter functionionality of the POP is:
- generate key pairs (needed for pop)
- generate secret keys (for what?)
- key rotation
- clustering (so that many servers can share)

The second functionality of STS is the Secret Server.

The third functionality is tha Token generation. This functionality is 
less important, befause there exist a lot of frameworks for that.

In the following diagram the following use case is shown:  
1. User identifies at IDP
2. User sends JWT to Backend
3. Backend ask secret server for users secret key
4. secret server retrieves users secret key and returns it
5. Backend can en- and decrypt data of user

![Modules map](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/secure-token-service/v2/documentation/docs/sts-usage.puml&fmt=svg&vvv=1&sanitize=true)

## POP ##
The Proof of Posession (POP). 
The POP is part of the STS library. Its main part is the key management.
The key management generates private/pbulic key pairs. Client of the POP
get a public key to encrypt data. Decryption can only be done with the
private key. Key pairs do not live for too long and thus are replaced. Further
the keys have to be shared between all nodes.

In this example, the GUI migth give sensible data to the backend. To encrypt the data, the POP 
in the backend returns a public key. So data can be encrypted, independant of the overlying
protocol.

Even if the connection from the client to the server is going over a proxy and
the content is logged, only encrypted data is visible.

Notice, that only the data coming from the client to the server is
encrypted. The data going from the server to the client is not 
encrypted.

## JWTS-URL ##
A similiar functionality as the POP is used in the IDP. It is called the 
JWTS-URL. To retrieve a session token, the GUI talks to the IDP. To pass the 
sensible login credentials they are encrypted with a public key with
the JWTS-URL.  

After successful authentication the IDP returns a token (JWT). This token is 
signed. 

Notice, the data given from the IDP to GUI is not encrypted. 


## registered resource server ##
To verify that the request going from Datacenter1 to Datacenter2 
the Datacenter1 has to be registered to 
the Datacenter2. This component is the "registered resource server". This makes sure,
that a request comming to Datacenter2 comes from a well known client.

Similiar to this is the registration of IDPs. It ensures that not any JWT
is accepted. 

## Secret Server ##
To en- and decrypt the backends (running in the Datacenter1) persistent data the keystore-secret is needed. This is 
stored in the Datacenter 2. The Datacenter 2 gets the token from the backend server. 

Now, as a second protection (the first is the "registered resource server")
is to verify that the token really comes from the IDP.
  
The token retrieved by the GUI from the IDP is signed by the IDP. Now to verify this signature
the secet server has to get the IDPs public key (classical Auth0).

Eventually when the token is valid, the secret server returns the
users secret key corresponding to the users subject of the JWT.

If there is no users secret key for the user yet, the secret server 
generates a new users secret key and stores the key for the users subject.
And than the new users secret key is returned.

Rather than simply returning the users secet key, the secret server requests the POP of the 
Datacenter1 to retrieve a public key to encrypt the returned users secret key.
With the secret key the datacenter 1 can en- and deycrypt the data to be stored for the client.

The secrets stored in the secret server are encrypted by a managment key. 
