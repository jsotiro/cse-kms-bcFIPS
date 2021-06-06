# Using BouncyCastle FIPS for Envelop Client Side Encryption (CSE) with a KMS-provided data key
This is a sample showing how to use the FIPS edition of BouncyCastle crypto library in combination with a data key generated and encrypted/decrypted using a KMS CMK. 
It uses 256-bit AES data Keys with the Galois/Counter Mode (GCM) authenticated mode of encryption but can be adapted to other encryption methods. If you wish to do so a great resource with examples for all other modes is *[The Bouncy Castle FIPS Java API in100 Examples](https://www.bouncycastle.org/fips-java/BCFipsIn100.pdf)* PDF document. The document provides an in depth coverage of BouncyCastle FIPS JAVA API but it does not cover using with KMS, which this sample adds. 

The sample assumes that you have already created or imported a KMS CMS and have usage permissions for the user or role credentials you will use. If have not created a KMS CMK yet use the following links to [create a KMS CMK from the AWS Console] (https://docs.aws.amazon.com/kms/latest/developerguide/create-cmk-keystore.html#create-cmk-keystore-console)  or [create a KMS CMS using AWS CLI](https://docs.aws.amazon.com/kms/latest/developerguide/create-cmk-keystore.html#create-cmk-keystore-api) 

Envelope encryption uses a CMK to generate a data key used for data encryption/decryption. For KMS this is needed as CMKs can only handle 4Kb of data, but it also has other benefits inlcuding Defense in Depth, the ability to combine other crypto libraries with KMS Key management, as well as use multiple CMKs if needed. For more info read [this section of the AWS documenation]  (https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#enveloping)

## Setup 

#### Install AWS CLI v2 
If you want to run the sample without any code changes, you will need to install the AWS CLI and run aws config to create a default profile. For more info, see below on how the sample authenticates.


#### Use maven to build the project
``mvn clean install``
to download all despendencies in the POM file and build the project 

#### Create Environment Variables
The sample uses AWS SDK 2.x to create a KMS client which handles the data key operations (generation, encryption, decryption). This expects to have two environment variables setup 
1. Region - The region of KMS Service - eg <br>
``export AWS_REGION=eu-west-1`` <br>
2. KMS Key ARN Id - the ARN Id for the CML to use as Master Key - eg  <br>
``export AWS_KMS_CMK_ARN=arn:aws:kms:eu-west-1:123456789ab:key/1a234b4c-d678-9e01-fg23-4hk456789l0``<br>
### Run the Sample 
The sample is a command line java app - *FipsCseClient* - that takes one argument the text to encrypt and then decrypt. You can test it using java or mvn e.g.

``mvn compile exec:java -Dexec.mainClass="xyz.leansecurity.crypto.bcfips.FipsCseClient"   -Dexec.args="EncryptMeNow!"``

### About the code
- The FipsCse class which in turn performs  encryption / deryption using and a DataKey interface to delegate data key management. It also does all the conversation of keys from byte arrays to java.crypto SecretKey which is what BounceCastle FIPS expects. 
- The FipsCse instantiates FipsCse and set the DataKey interface to its KMS implemention KmsDataKey. This class uses a kms client to handle data key generation, encryption, and decryption. The encrypted key is returned in Base64 encoded format so that it can be saved in readable metadata attributes if needed but the byte array format is easily obtained using java's Base64 decoder
- slf4j is used for logging because that's what bouncy castle uses.
- the sample uses the BasicProfileCredentialsProvider to get the default profile credentials. This assumes you have the AWS CLI 2.v installed. However, the KmsDataClient takes an AwsCredentials instance and you can change it to a credential object of your choice in the FipsCseClient
### Known Issues
The sample has been tested on Ubuntu 20.x / MacOS and Java 14. Although it works fine, when running it using mvn it gets stuck waiting for the threat to be destroyed. This does not happen when running it in the IDE  

