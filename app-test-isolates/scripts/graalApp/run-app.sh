output="log/$1log-$(printf '%(%H:%M_%Y-%m-%d)T\n' -1)"
../../target/app 2> "$output"
./generateGCData.sh "$output"
