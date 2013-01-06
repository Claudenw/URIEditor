
A regular expression URI matcher and editor.

Facilitates easy matching of URI component sections and generating regular expressions that will
match the specified URIs.

The package consists of 3 classes: URIMatcher, URIEditor and URIRewriter

# URIMatcher

The URI matcher accepts regular expression matches for the various components of the URI.  It will then 
generate a regular expression to match the pattern or can be used to test if various URIs are matched 
by the entire pattern.

# URIEditor

Extends the URIMatcher.

The constructor takes a pattern that is then used to edit the matching URI and return a string.
The pattern uses the special tokens {scheme}, {host}, {port}, {path}, {fragment} and {uri} to identify
the various parts of the URI to be inserted in the resulting string.  In addition each token may 
include a colon and an index indicating the matching group within the component represented by the token.

for example if the host pattern was set to "(.*)\.example\.(.+)"  and the pattern was set to 
"{host:2} has the {host:1} server", then matching the URI "http://www.example.com/foo" would yield the 
string "com has the www server"

# URIRewriter

Requires a URIEditor in its constructor.

It is intended to be used in situations where editing of URIs in line is required.  The rewrite() method
accepts a URI as an argument.  If the URI matches the URIEditor then the editor.edit() is executed and 
the resulting string reinterpreted as a URI and returned from the method.  If the URI does not match it
is returned unchanged.

 
