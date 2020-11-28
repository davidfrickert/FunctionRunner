ab -p ab_post.txt -T "multipart/form-data; boundary=1234567890" -c "$1" -n "$2" http://localhost:8080/compressNoIsolate \
	> "log/$(printf '%(%H:%M_%Y-%m-%d)T\n' -1)-noisolate"
