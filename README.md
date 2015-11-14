Beacon is a an open-source social network and accompanying Android app that
allows you to anonymously share interesting things and events with people in
your local community. Beacon is still in active development, but when finished,
using Beacon will be something like this:

Users take a picture in-app. Beacon takes a geotag and posts it on the map.
Beacons that are more popular are indicated on the map. You can select a beacon
to see what people are saying about it and to add your own comment.

Beacon was started as a university project and as such, is not currently open
for pull requests. In January 2016, however, Beacon will become open for
contributions.

## Signing the Frontend

You don't need to set up a keystore in order to create a debug build, but in
order to take advantage of authentication services like Facebook and Google, the
key of the apk must match the key registered with Facebook or Google.

In order to build a signed release apk, there are several steps you must take
after cloning the repository. Start by creating a properties file at the project
root named ```release.properties``` and filling it with the following.

```groovy
keyStore=release.keystore
keyStorePassword=PASSWORDA
keyAlias=BeaconReleaseKey
keyAliasPassword=PASSWORDB

```

This will allow gradle to access your keystore, which you will now generate. Run
the following command at the project root.

```bash
keytool -genkey -v -keystore release.keystore -alias BeaconReleaseKey -keyalg
RSA -keysize 2048 -validity 10000
```

The command will prompt you for information. What you enter is not important for
a development build, except that the passwords you provide must match those that
you entered in the ```release.properties``` file.

This will generate a keystore file at ```release.keystore```.
