

ROOT=.
BASE_ASSETS=$(ROOT)/web/assets
WEBAPP_ASSETS=$(ROOT)/web/src/main/webapp/assets




all: assets

assets: $(WEBAPP_ASSETS)/js/zufaro.js $(WEBAPP_ASSETS)/js/zufaro.min.js
	tup upd

$(WEBAPP_ASSETS)/js/zufaro.js:
	cp $(BASE_ASSETS)/out/zufaro.js $@

$(WEBAPP_ASSETS)/js/zufaro.min.js:
	cp $(BASE_ASSETS)/out/zufaro.min.js $@


deploy:
	sbt "project zufaro-web" package
	rsync -avzrhcP --exclude=resolution-cache --exclude=streams --exclude=*.war web/target/webapp/ robin@$(ZUFARO_SERVER):zufaro-web
	make restart

restart:
	ssh robin@$(ZUFARO_SERVER) 'sudo chown -R robin:robin /home/robin/zufaro-web && sudo svc -du /etc/service/zufaro-web'

clean:

.PHONY: deploy clean restart assets



