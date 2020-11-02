#!/usr/bin/env sh
function print_usage() {
    echo -e "\e[33m\e[1mUsage: .reportLOCs.sh [FILE_EXTENSION]"
    echo -e "Generate a report on current project lines of code count. Supply the desired file extension to count lines on files of the indicated type.\e[0m"
}
if [[ $# -eq 0 ]]; then
  print_usage
  exit
fi

shopt -s globstar
MAX_LINES=0
MAX_FILE=""
MIN_LINES=99999
MIN_FILE=""
declare -A files
REPORT_FILE_PATH="$(realpath $PWD/../report)/lines_report_$1.txt"
echo -e "\e[33m\e[1mGenerating report on $REPORT_FILE_PATH..."
for file in ../app/src/**/*.*$1; do
	CURRENT_LINES=$(wc -l $file|cut -f1 -d' ')
	files["$file"]=$CURRENT_LINES
	if [[ $CURRENT_LINES -gt $MAX_LINES ]] ; then
		MAX_LINES=$CURRENT_LINES
		MAX_FILE=$file
	fi
	if [[ $CURRENT_LINES -lt $MIN_LINES ]] ; then
		MIN_LINES=$CURRENT_LINES
		MIN_FILE=$file
	fi
done
echo -e "\e[32m\e[1mReport generated successfully. Here is a brief resume:"
echo -e "\e[35m\e[1mBiggest file: $MAX_LINES lines ($MAX_FILE)"
echo -e "Smallest file: $MIN_LINES lines ($MIN_FILE)"
rm -f $REPORT_FILE_PATH
for k in "${!files[@]}" ; do
    echo $k ' - ' ${files["$k"]}
done | sort -rn -k3 >> $REPORT_FILE_PATH