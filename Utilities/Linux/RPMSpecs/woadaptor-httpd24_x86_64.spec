Summary: woadaptor is a Apache 2.4 module to connect to wotaskd.
Name: woadaptor-httpd24
Version: 6.1
Release: 1
Prefix: /
ExclusiveArch: x86_64
ExclusiveOS: linux
Group: System Environment/Daemons
Source0: Adaptors-src-20220829.tar.gz
URL: http://wiki.wocommunity.org/
Vendor: WOCommunity Association
Packager: Pascal Robert <info@wocommunity.org>
License: NetStruxr Public Software License
BuildRoot: %{_builddir}/%{name}-root
Requires: httpd >= 2.4.0
BuildRequires: gcc, make, sed >= 4.1.4
BuildRequires: httpd-devel >= 2.4.0

%description
woadaptor is a Apache 2.4 module that act as a bridge between your
Web server (Apache) and your wotaskd instances.

%prep
%setup -q -n Adaptors
# This tells ant to install software in a specific directory.
#cat << EOF >> build.properties
#base.path=%%{buildroot}%%{_libdir}/httpd/modules/
#EOF

%build
#cd Adaptors
# TODO: This should be handled in a patch file
sed -i 's/ADAPTOR_OS = MACOS/ADAPTOR_OS = LINUX/' make.config
sed -i 's/ADAPTORS = CGI Apache2.2/ADAPTORS = CGI Apache2.4/' make.config
make CC=gcc

%install
rm -Rf %{buildroot}
mkdir -p %{buildroot}%{_libdir}/httpd/modules/
mkdir -p %{buildroot}/etc/httpd/conf.d/
#mkdir -p %%{buildroot}/opt/Local/Library/WebServer/Documents/WebObjects
%{__cp} $RPM_BUILD_DIR/Adaptors/Apache2.4/mod_WebObjects.so %{buildroot}%{_libdir}/httpd/modules/
%{__cp} $RPM_BUILD_DIR/Adaptors/Apache2.4/apache.conf %{buildroot}%{_sysconfdir}/httpd/conf.d/webobjects.conf
#sed -i 's"^ScriptAlias /cgi-bin/"## ScriptAlias /cgi-bin/"' %%{buildroot}%%{_sysconfdir}/httpd/conf/httpd.conf

%clean
rm -rf %{buildroot}

%pre

%post
service httpd graceful > /dev/null 2>&1

%preun
if [ "$1" = "0" ]; then
  # do we really need to make a backup?
  cp %{_sysconfdir}/httpd/conf.d/webobjects.conf %{_sysconfdir}/httpd/conf.d/webobjects.conf.bak
  # removing the file is part of the package managers jobs
  # rm %%{_libdir}/httpd/modules/mod_WebObjects.so
  # service httpd graceful
fi

%postun
if [ "$1" = "0" ]; then
  service httpd graceful
fi

%files
%defattr(-,root,wheel,-)
%{_libdir}/httpd/modules/mod_WebObjects.so
%config(noreplace) %{_sysconfdir}/httpd/conf.d/webobjects.conf

%changelog
* Mon Aug 29 2022 Suriel Puhl <github@xmit.xyz> - WO Adaptor URL Sanitization Fixes 
Added fix-ups to the Utilities/Adaptors subfolders specifically to address a vulnerability in parsing, whereby an adversary can directly inject their own headers and content into the web requests going to the application (WO) servers behind the adaptor.

The new code returns a 404 on any encounter of a 0x0D (carriage-return) or a 0x0A (line-feed) character in the adaptor translate functions, and the defined forbidden character set is written in such a way as to be expandable later as necessary. This behavior of returning a 404 error mimics Apache's mitigation of the use of %2f in request URLs.

IMPORTANTLY: This URL cleanliness will not affect content within query strings usually, since those characters are not typically expanded by webserver software before reaching the adaptor interface.

