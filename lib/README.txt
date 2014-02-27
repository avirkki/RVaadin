The bundled Eclipse .classpath points to this directory to find
the required REngine jar packages:

 <classpathentry kind="lib" path="lib/REngine.jar"/>
 <classpathentry kind="lib" path="lib/RserveEngine.jar"/>

REngine is needed to communicate with Rserve-enabled R installations. 
The library is developed by Simon Urbanek (see https://github.com/s-u/REngine)
and the sources are distributed under the LGPL license. For details of using
LGPL in Java projects, see e.g. https://www.gnu.org/licenses/lgpl-java.html

To download the files (under Linux), issue:
./getlibs.sh

