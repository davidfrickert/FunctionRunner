FROM alpine:3.13.2

WORKDIR /main

COPY target/app .
COPY target/lib/lib ./lib

ENTRYPOINT ["/main/app"]