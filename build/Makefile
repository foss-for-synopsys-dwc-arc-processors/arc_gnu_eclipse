# Eclipse workspace location. do not confuse with Jenkins workspace.
ECLIPSE_WORKSPACE := /home/akolesov/eclipse/workspace1

# Choose ZIP executable, because on servers default is zip anchient like
# dinosaurs and we need to override it.
ZIP_BIN := zip

# Location of Eclipse installation.
ECLIPSE_HOME := /home/akolesov/eclipse/eclipse-rcp

all: arc_gnu_plugins.zip

%.zip: %
	cd $< && $(ZIP_BIN) -vr - * > ../$@

arc_gnu_plugins: feature_export
	$(ECLIPSE_HOME)/eclipse -nosplash -data $(ECLIPSE_WORKSPACE) -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher -metadataRepository file://$$(readlink -m $@) -artifactRepository file://$$(readlink -m $@) -source $< -publishArtifacts -compress
	cp ../updatesite/site.xml $@/

feature_export: feature_export.xml
	$(ECLIPSE_HOME)/eclipse -nosplash -application org.eclipse.ant.core.antRunner -f $< -data $(ECLIPSE_WORKSPACE)


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

