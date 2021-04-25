jar_name=$(find lib -type f -name "*.jar" -printf "%f\n")
groupId="photons.graal.functions"
artifactId="${jar_name%%-[0-9]*}"
version="$(echo "$jar_name" | sed 's/\.[^.]*$//' | sed -nre 's/^[^0-9]*(([0-9]+\.)*[0-9]+).*/\1/p')"

echo "installing $groupId-$artifactId::$version"

./mvnw install:install-file \
      -Dfile="$(find lib -name "*.jar")" \
      -DgroupId="$groupId" \
      -DartifactId="$artifactId"  \
      -Dversion="$version" \
      -Dpackaging=jar