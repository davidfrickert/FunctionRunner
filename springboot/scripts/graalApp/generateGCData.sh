grep "GC (" $1 | awk '{ print $3" "$6" "$7" "$1 }' \
       	| awk -F "->| |[][]" 'BEGIN {print "GC_TYPE,HEAP_BEFORE,HEAP_AFTER,TIME_TAKEN,GC_TIMESTAMP"}; {print $1 "," substr($2, 1, length($2) -1) / 1000 "," substr($3, 1, length($3) -2) / 1000 "," $4 "," $6 }' \
	> "../../../ThesisPlot/$1"
