# UnlegitLibrary

## License Information
GNU General Public License v3.0 (GPLv3)<br />
The default license. Applies to all users, projects, and distributions unless explicitly stated otherwise.<br />
→ https://github.com/UnlegitDqrk/UnlegitLibrary/blob/master/LICENSE

Open Autonomous Public License (OAPL)<br />
A special exception applies exclusively to the project Open Autonomous Connection (OAC).<br />
Within OAC, the UnlegitLibrary is also licensed under the OAPL.<br />
In this context, OAPL terms take precedence.<br />
→ https://github.com/Open-Autonomous-Connection/OAPL

## Include in own projects
````
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/unlegitdqrk/unlegitlibrary</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>me.finn.unlegitlibrary</groupId>
        <artifactId>unlegitlibrary</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
````

## Certificate generation for NetworkSystem
### Creating Root-CA:
````
openssl genrsa -out myCA.key 4096
openssl req -x509 -new -nodes -key myCA.key -sha256 -days 3650 -out myCA.pem

myCA.key = private Key for CA (keep secret)
myCA.pem = public Root-Certificate for signing server and client certificates
````
### Creating Server Certificate based on Root-CA:
````
openssl genrsa -out server.key 2048
openssl req -new -key server.key -out server.csr
openssl x509 -req -in server.csr -CA myCA.pem -CAkey myCA.key -CAcreateserial -out server.crt -days 825 -sha256

server.key = private Key for Server
server.crt = Server-Certificate signed by Root-CA
````
### Optional: Creating Client Certificate based on Root-CA:
````
openssl genrsa -out client.key 2048
openssl req -new -key client.key -out client.csr
openssl x509 -req -in client.csr -CA myCA.pem -CAkey myCA.key -CAcreateserial -out client.crt -days 825 -sha256

client.key = private Key for Client
client.crt = Client-Certificate signed by Root-CA
````

1. Generate a Root-CA. Every client and server NEED this Root-CA *.pem-File. Keep the *.key file private<br />
2. Generate a Server-Certificate
3. Optional: Generate a Client-Certificate
4. Put the Root-CA on your server and client in "certificates/ca"-Folder
5. Put the Server-Certificate-Key in "certificates/key"-Folder
6. Put the Server-Certificate in "certificates/server"-Folder
7. Optional: Put the Client-Certificate-Key in "certificates/key"-Folder
8. Optional: Put the Client-Certificate in "certificates/client"-Folder