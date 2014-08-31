

ROOT=.
BASE_ASSETS=$(ROOT)/web/assets
WEBAPP_ASSETS=$(ROOT)/web/src/main/webapp/assets
CORE_VERSION=$(shell cat project/BuildSettings.scala | grep -e version | grep -P '\d+\.\d+\.[\d\-_a-zA-Z]+' -o | sed 's/"//g')
WEB_VERSION=$(shell cat project/Build.scala | grep '/\*WEB_VERSION\*/' | grep -P '\d+\.\d+\.[\d\-_a-zA-Z]+' -o | sed 's/"//g')



all: version
	tup upd
	make assets

version: VERSION

VERSION:
	@@rm -f $<
	echo "core: $(CORE_VERSION)" >> VERSION
	echo "web: $(WEB_VERSION)" >> VERSION
	sed -i.bak 's/VERSION \= ".*"/VERSION = "$(WEB_VERSION)"/' web/src/main/scala/com/ansvia/zufaro/web/WebConfig.scala

assets: $(WEBAPP_ASSETS)/js/zufaro.js $(WEBAPP_ASSETS)/js/zufaro.min.js

$(WEBAPP_ASSETS)/js/zufaro.js: $(BASE_ASSETS)/out/zufaro.js
	cp $(BASE_ASSETS)/out/zufaro.js $@

$(WEBAPP_ASSETS)/js/zufaro.min.js: $(BASE_ASSETS)/out/zufaro.min.js
	cp $(BASE_ASSETS)/out/zufaro.min.js $@


deploy:
	sbt "project zufaro-web" package
	rsync -avzrhcP --exclude=resolution-cache --exclude=streams --exclude=*.war web/target/webapp/ robin@$(ZUFARO_SERVER):zufaro-web
	make restart

restart:
	ssh robin@$(ZUFARO_SERVER) 'sudo chown -R robin:robin /home/robin/zufaro-web && sudo svc -du /etc/service/zufaro-web'

reset:
	ssh robin@$(ZUFARO_SERVER) 'sudo rm -rf jetty_instance/data/*'

clean:

.PHONY: deploy clean restart assets reset




