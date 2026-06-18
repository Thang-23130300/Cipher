# Java Signing Tool

Standalone Swing tool for signing an order `hash_value` with an RSA private key.
The private key stays on the user's computer. The tool can load the user's ACTIVE
public key from the authenticated web API to verify that both keys belong to the
same pair before signing.

## Run in IntelliJ IDEA

1. Open `tools/java-signing-tool` as a Maven project.
2. Select JDK 17 or newer.
3. Run `com.cipher.signingtool.Main`.

## Run from terminal

```powershell
mvn clean package
java -jar target/java-signing-tool-1.0.0.jar
```

## Signing rule

The tool signs the exact `hash_value` text as UTF-8:

```java
hashValue.getBytes(StandardCharsets.UTF_8)
```

Do not convert the SHA-256 hex text into raw bytes before signing.

## Key formats

- Public key: PEM X.509, `-----BEGIN PUBLIC KEY-----`
- Private key: PEM PKCS#8, `-----BEGIN PRIVATE KEY-----`
- Signature output: Base64

## Load ACTIVE public key from web

1. Log in to the INOLA website.
2. Load the local `private_key.pem` in the tool.
3. Click **Load Public Key From Web**.
4. Enter the web base URL and the `JSESSIONID` of the current login session.

After generating a new key pair, copy its public key and save it on the website's
`/key-management` page. The website calls the local `/public-key/saved` endpoint
after the key becomes ACTIVE, so the generated pair is ready for signing without
an extra load step.

The session value is used only for that request and is not saved. The tool calls
`GET /api/user/keys/active`, parses the returned X.509 public key, then signs and
verifies a random challenge with `SHA256withRSA`. The private key is never sent to
the website or stored in the database.

When the local connection is enabled, the website may notify the tool after an
ACTIVE key is saved by calling `POST http://127.0.0.1:9090/public-key/saved`.
The tool compares the returned PEM with its generated public key and updates the
shared signing state immediately.
