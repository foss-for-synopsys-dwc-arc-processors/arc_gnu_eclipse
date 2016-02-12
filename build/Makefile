# Choose ZIP executable, because on servers default is zip ancient like
# dinosaurs and we need to override it.
ZIP := zip

# Location of Eclipse installation.
ECLIPSE_HOME := /remote/ru20home1/apologo/eclipse

# Qualifier
VERSION_QUALIFIER := $(shell date --utc +%Y%m%d%H%M)

all: arc_gnu_plugins.zip

%.zip: %
	cd $< && $(ZIP) -vr - * > ../$@

arc_gnu_plugins: feature_export
	sed -e 's/qualifier/$(VERSION_QUALIFIER)/g' ../updatesite/site.xml > $</site.xml
	$(ECLIPSE_HOME)/eclipse -nosplash \
	    -configuration @user.dir/configuration \
	    -application org.eclipse.equinox.p2.publisher.UpdateSitePublisher \
	    -metadataRepository file://$$(readlink -m $@) \
	    -artifactRepository file://$$(readlink -m $@) \
	    -source $<\
	    -publishArtifacts \
	    -compress

feature_export:
	mkdir feature_export
	$(ECLIPSE_HOME)/eclipse -nosplash \
	    -configuration @user.dir/configuration \
	    -application org.eclipse.ant.core.antRunner \
	    -buildfile ../feature/build.xml \
	    -DbuildDirectory=$$(readlink -m $@) \
	    -Dqualifier=$(VERSION_QUALIFIER) \
	    -DprojectRoot=.. \
	    -DeclipseHome=$(ECLIPSE_HOME)


.NOTPARALLEL: clean
.PHONY: clean
clean:
	rm -rf feature_export arc_gnu_plugins *.zip

.PHONY:
help:
	@echo "Targets:"
	@echo "all      Build all"
	@echo "clean    Remove artifacts"
	@echo "feature_export"
	@echo "arc_gnu_plugins"
	@echo "arc_gnu_plugin.zip"
	@echo "help     Show this help"

