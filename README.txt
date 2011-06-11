This is a Git repo fork of http://www.odata4j.org (http://code.google.com/p/odata4j/).

It was originally created on and by https://github.com/vorburger/odata4j.

Here is how to keep it in sync with the upstream SVN repository:
	$ git checkout master
	$ git svn rebase

It seems it also possible to commit back to SVN, like this:  (UNTESTED)
	$ git svn dcommit

This is how the initial import from SVN into Git was done,
as per e.g. http://help.github.com/import-from-subversion/ :
	1. created an svn.authorsfile, and manually completed it,
	   as per http://technicalpickles.com/posts/creating-a-svn-authorsfile-when-migrating-from-subversion-to-git/
	2. git svn clone --authors-file=svn.authorsfile -s http://odata4j.googlecode.com/svn/ odata4j
	3. cd odata4j
	4. git branch -a (note branches, and currently checked-out * master)
	5. git svn rebase (should work; if this causes an error, something went wrong)
	6. git gc (reduced it from 166M to 100M)
	7. cp ../svn.authorsfile .; git add svn.authorsfile; git commit -m "Keeping svn.authorsfile used in git svn clone --authors-file"
	8. git remote add origin git@github.com:vorburger/odata4j.git; git push --all; git push --tags

PS: Thanks to https://github.com/blog/626-announcing-svn-support,
and https://github.com/blog/644-subversion-write-support, this
GitHub repository can actually also been accessed as a SVN repository
at e.g. http://svn.github.com/vorburger/odata4j.git
