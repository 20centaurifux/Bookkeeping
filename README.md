# Bookkeeping - README

## Preamble

***Bookkeeping*** is a Java based bookkeeping software. It stores data
in a SQLite database and supports multiple accounts.

Accounts can have different currencies. ***Bookkeeping*** exchanges
one currency for another automatically. To make your life more comfortable
it's possible to create templates. Data can be exported to CSV.

***Bookkeeping*** is free software. Permission is granted to copy, modify
and redistribute it under the provisions of the GNU General Public License
Version 3, as published by the Free Software Foundation; see the file
COPYING for licensing details.

Note, in particular, that ***Bookkeeping*** is provided "as is", in the
hope that it may prove useful, but WITHOUT WARRANTY OF ANY KIND; not even an
implied WARRANTY OF MERCHANTABILITY, nor of FITNESS FOR ANY PARTICULAR
PURPOSE. Under no circumstances will the authors accept liability for
any damages, however caused, arising from the use of this software.


## Requirements

***Bookkeeping*** requires the following components:

* picocontainer (http://picocontainer.org)
* SQLite JDBC driver (https://bitbucket.org/xerial/sqlite-jdbc)
