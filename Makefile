







all:



deploy:
	sbt "project zufaro-web" package
	rsync -avzrhcP --exclude=resolution-cache --exclude=streams --exclude=*.war web/target/webapp/ robin@$(ZUFARO_SERVER):zufaro-web
	make restart

restart:
	ssh robin@$(ZUFARO_SERVER) 'sudo chown -R robin:robin /home/robin/zufaro-web && sudo svc -du /etc/service/zufaro-web'

clean:

.PHONY: deploy clean restart


