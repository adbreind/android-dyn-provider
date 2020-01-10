android-dyn-provider
====================

#### Configurable/dynamic Android Content Provider implementation

It would be nice to gain the benefits of Content Providers without writing all of the boilerplate code...

There are a number of scripts and plugins which can take some params or GUI inputs and generate a provider, however that yields static Java code that must be maintained and possibly duplicated over time.

Another approach would be to have, ideally, a single content provider type which can be configured statically (or even dynamically) to handle various data types represented by various authorities and URI mappings.

While not all providers map down to SQLite databases, a great many do ... and a simple mapping to SQLite seems like a reasonable start.

Because the content URI system and the SQL language provide an existing "meta" layer, and because even SQLite itself doesn't enforce typing on columns, creating a generic provider like this should be somewhat easier than general metaprogramming against Java's strict type system.

####Current version

Is a "first check-in" style absolute minimum code that runs and supports a single provider, authority, and database file.

It's entirely driven off of a resource file included in the app (package) to which the provider will belong.

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>  	    
    <string name="p_authority">com.selfmummy.dynprovider</string>
	<string name="p_entity">dummy</string>
	<string name="p_mime_minor">vnd.selfmummy.dummy</string>
	<string-array name="p_field_names">
	    <item>time</item>
	    <item>message</item>
	</string-array>
	<string-array name="p_field_types">
	    <item>integer</item>
	    <item>text</item>
	</string-array>
</resources>
```

##### Usage:

For any particular project, plug in: 

* an authority
* an entity name (like "customer" or "restaurant")
* field names
	* an autoincrementing integer ID that maps to BaseColumns._ID for use in lists is automatically included
* field types 
	* these are SQLite-compatible SQL types for now

#### Roadmap

If time allows, there are a ton of minor and major features that desperately ought to be added here :)

That said, if any version of this code is usable by anyone, including myself, to avoid writing out yet another stock provider, then that's a win.
