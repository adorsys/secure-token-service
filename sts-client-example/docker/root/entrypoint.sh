#!/bin/bash

FILE_NAME=/opt/app-root/src/env.json

echo '{' > ${FILE_NAME}

# convert all env variables to JSON (consider only with prefix)
# https://stackoverflow.com/a/25765360
while IFS='=' read -r name value ; do
  if [[ ${name} == "${PREFIX:-NG_}"* ]]; then
    printf '  "%s": "%s",\n' "${name}" "${value//\"/\\\"}" >> ${FILE_NAME}
  fi
done < <(env)

# remove last comma
# https://stackoverflow.com/questions/27305177
truncate -s -2 ${FILE_NAME}

echo -e "\n}" >> ${FILE_NAME}

exec /docker-entrypoint.sh "$@"
