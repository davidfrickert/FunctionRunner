#!/bin/bash
./lib/add_to_local_mvn_repo.sh
./mvnw clean package
mv target/app target/exec
zip -j target/app.zip target/exec
mv target/exec target/app