# Java Signing Tool

Standalone Swing tool for signing an order `hash_value` with an RSA private key.

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

The tool does not call the web app, database, localhost API, or network.
