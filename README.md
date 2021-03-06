Beacon is a an open-source social network and accompanying Android app that
allows you to anonymously share interesting things and events with people in
your local community. Beacon is still in active development, but when finished,
using Beacon will be something like this:

Users take a picture in-app. Beacon takes a geotag and posts it on the map.
Beacons that are more popular are indicated on the map. You can select a beacon
to see what people are saying about it and to add your own comment.

A video demonstrating the current functionality can be found
[here](https://www.youtube.com/watch?v=KVeSS2WxJBo).

Apk's can be downloaded [here](http://bin.gnossen.com/beacon-frontend/).

Beacon was started as a university project and as such, is not currently open
for pull requests. In January 2016, however, Beacon will become open for
contributions.

## Configuring the Frontend
In order to build the frontend, you need to configure several values. The first
is the URL of the backend. Create a file at the project root named
```beacon.properties``` and add the following line

```groovy
ServerURL=SERVER_URL
```

And replace the string ```SERVER_URL``` with your actual URL. Next, you'll need
a server client ID from Google in order to authenticate with the Google sign-in
API.

Add the following line to ```beacon.properties```.

```groovy
ServerClientID=ID_STRING
DebugServerClientID=ID_STRING
```

And replace the string ```ID_STRING``` with your personal ID. It should end in
```apps.googleusercontent.com```. You can find directions for obtaining such an
ID
[here](https://developers.google.com/identity/sign-in/android/start-integrating).

## Authenticating with Google Sign-In

Creating a configuration file at ```app/google-services.json``` is necessary for
authenticating with Google's Sign-In api and, therefore for the app itself.
Follow the directions
[here](https://developers.google.com/identity/sign-in/android/start-integrating) to get
a configuration file of your own. We have experienced trouble with the Google
Services libraries and emulators, so it is recommended that you develop using a
physical device.

## Signing the Frontend

You don't need to set up a keystore in order to create a debug build, but in
order to take advantage of authentication services like Facebook and Google, the
key of the apk must match the key registered with Facebook or Google.

In order to build a signed release apk, there are several steps you must take
after cloning the repository. Add the following lines to ```beacon.properties```.

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
