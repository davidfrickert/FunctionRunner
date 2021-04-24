jar_name=$(find lib -type f -name "*.jar" -printf "%f\n")
artifactId="$(echo "$jar_name" | sed 's/\.[^.]*$//')"
version="$(echo "$jar_name" | sed 's/\.[^.]*$//' | sed -nre 's/^[^0-9]*(([0-9]+\.)*[0-9]+).*/\1/p')"

./mvnw install:install-file \
      -Dfile="$(find lib -name "*.jar")" \
      -DgroupId=photons.graal.functions \
      -DartifactId="$artifactId"  \
      -Dversion="$version" \
      -Dpackaging=jar
