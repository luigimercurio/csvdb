jar=$(ls -t ./build/libs | grep -m1 .) #
app=$(echo ${jar} | sed 's/-[^-]\+$//g') #
version=$(echo ${jar} | grep -o '[^-]\+$' | sed 's/\.jar$//g') #
#
echo ${app}:${version} #
docker build -t "${app}:${version}" . #
