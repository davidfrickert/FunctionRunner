FROM alpine:3.13.3

WORKDIR /main

COPY target/app .
COPY target/lib/lib ./lib

ENTRYPOINT ["/main/app"]