#!/bin/bash
./lib/add_to_local_mvn_repo.sh
./mvnw clean package
cp target/app target/exec
zip -j target/app.zip target/exec