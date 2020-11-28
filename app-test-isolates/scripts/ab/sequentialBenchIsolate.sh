for concurrency_level in "$@" 
do
	echo "starting benchmark for c=$concurrency_level, n=100" 
	ab -p ab_post.txt -T "multipart/form-data; boundary=1234567890" -c "$concurrency_level" -n 100 http://localhost:8080/compress \
       		> "log/$(printf '%(%H:%M_%Y-%m-%d)T\n' -1)-isolate"
	echo "done benchmark for c=$concurrency_level, n=100"
	echo "waiting 30 seconds before starting next concurrecy level"
	sleep 30
done
