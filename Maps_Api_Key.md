# Introduction #

This page describes how to configure your API key for project development.

For information on how to obtain a Maps API key see http://code.google.com/android/add-ons/google-apis/mapkey.html

# Details #
To be able to use the Maps Library on Android you must have a API key.

The key should be stored in **res/values/apikey.xml**:
```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="api_key">[KEY GOES HERE]</string>
</resources>
```

This file should not be added to source control but there there will be sample file at **res/values/apikey.default.xml** which you can copy/rename.