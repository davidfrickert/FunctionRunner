jar_name=$(find lib -type f -name "*.jar" -printf "%f\n")

./mvnw install:install-file \
      -Dfile="$(find lib -name "*.jar")" \
      -DgroupId=photons.graal.functions \
      -DartifactId="$(echo "$jar_name" | sed 's/\.[^.]*$//')"  \
      -Dversion="$(echo "$jar_name" | sed 's/\.[^.]*$//' | sed -nre 's/^[^0-9]*(([0-9]+\.)*[0-9]+).*/\1/p')" \
      -Dpackaging=jar
