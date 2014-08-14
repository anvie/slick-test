







all:



deploy:
	sbt "project zufaro-web" package
	rsync -avzrhcP --exclude=resolution-cache --exclude=streams --exclude=classes --exclude=*.war web/target/ robin@$(ZUFARO_SERVER):zufaro-web

restart:
	ssh -c robin@$(ZUFARO_SERVER) svc -du /etc/service/zufaro-web

clean:

.PHONY: deploy clean restart


