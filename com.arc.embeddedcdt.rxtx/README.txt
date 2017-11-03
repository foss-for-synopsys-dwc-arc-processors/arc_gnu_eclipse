This plugin contains only RXTX library and no source code. It is needed because
Debugger plugin depends on org.eclipse.tm.terminal plugin which in turn requires
RXTX library. To be able to add this library to dependencies we have to enclose
it into a plugin.

Earlier this library lay in Debugger plugin, but then it was not possible to
build project using ant because it could not resolve circular plugin dependencies
that arose (Debugger plugin depended on TM Terminal and TM Terminal depended on
Debugger plugin).
