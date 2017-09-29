if [[ "$TRAVIS_TAG" =~ ^v[[:digit:].]+$ ]] then
  echo "RELEASE TAG -> publish $TRAVIS_TAG to mvn central"
  mvn deploy javadoc:javadoc gpg:sign -Prelease -DskipTests -B -U
else
  echo "NO RELEASE TAG -> don't publish to mvn central"
  mvn package -U
fi
