#!/usr/bin/env sh
rm -rf tm-keys-repo 
git clone https://github.com/smascaro/tm-keys-repo
pushd tm-keys-repo
cp --parents -r app/* ..
popd
rm -rf tm-keys-repo
