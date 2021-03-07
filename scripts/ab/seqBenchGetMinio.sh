for concurrency_level in "$@" 
do
	echo "starting benchmark for c=$concurrency_level, n=100" 
	ab -c "$concurrency_level" -n 100 "http://localhost:8080/files/isolated/compress/file.txt" \
       		> "log/$(printf '%(%H:%M_%Y-%m-%d)T\n' -1)-isolate"
	echo "done benchmark for c=$concurrency_level, n=100"
	echo "waiting 30 seconds before starting next concurrecy level"
	sleep 30
done
