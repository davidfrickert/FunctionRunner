FROM alpine:3.13.1

WORKDIR /main

COPY target/app .
COPY target/lib/lib ./lib

ENTRYPOINT ["/main/app"]