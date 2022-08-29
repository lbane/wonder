How to make an RPM package (esp. woadaptor-httpd24_x86_64.spec)

- install packages httpd-devel, rpmdevtools, and optionally rpmlint
- execute "rpmdev-setuptree" to create the build directories (~/rpmbuild/...)
- put the spec file into ~/rpmbuild/SPEC
- put a tar archive of the sourcecode into ~/rpmbuild/SOURCES
- modify the spec to reference the right source file in "Source0: <sourcefile>" (this could also be an URL pointing to the source)
- the current Spec assumes the tar contains only the directory Adaptors (as tared from the Utilities directory), adapt 
  if necessary
- add a changelog if possible, pay attention to the header line containing the date and contributor, the format is fixed
- build with: rpmbuild -ba <SPECFILE>
  (builds binary and source rpm, binary only: -bb)
- "rpmlint -i <SPECFILE>" may be used to check for spec file errors
- when commenting out lines in the spec file, escape %macros by doubling %

For a complete introduction to rpm builds see:
- https://rpm-packaging-guide.github.io
- https://rpm.org