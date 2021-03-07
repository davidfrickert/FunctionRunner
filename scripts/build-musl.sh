WORKDIR=~/projects/FaaS-GraalVM
SRC_LIB=$WORKDIR/src/lib
TARGET_LIB=$WORKDIR/target/lib

cd "$WORKDIR/src/lib/musl-1.2.2" || exit

./configure --disable-shared --prefix=$TARGET_LIB
make
make install

export CC=musl-gcc

cd ../zlib-1.2.11 || exit
./configure --static --prefix=$TARGET_LIB
make
make install

cp "$(find $SRC_LIB -name "libstdc++*" -type f)" $TARGET_LIB/lib/libstdc++.a