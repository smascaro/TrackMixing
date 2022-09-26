#!/usr/bin/env sh
if [[ -< "${JAVA_HOME}" ]]; then
	export JAVA_HOME="C:\\Program Files\\Android\\Android Studio\\jre"
fi
pushd ..
./gradlew checkDependencyUpdates >> report/out_dependencies.txt
popd
