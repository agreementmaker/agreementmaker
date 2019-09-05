#!/bin/sh

START_GWENG='<GWENG>'
END_GWENG='</GWENG>'

END_DOC='</DOC>'

if [ $1 = "-v" ]
then
	VERBOSE_ON="true"
	if [ -d $2 ]
	then
		INPUT_FOLDER=$2
	else
		echo "\"$2\" is not a directory. Please provide a directory."
		exit 1
	fi
else
	INPUT_FOLDER=$1
	if [ -d $1 ]
	then
		INPUT_FOLDER=$1
	else
		echo "\"$1\" is not a directory. Please provide a directory."
		exit 1
	fi
fi

FILE_LIST=$(ls $INPUT_FOLDER | grep '.gz')
FILE_COUNT=$(ls $INPUT_FOLDER | grep '.gz' | wc -l)

for i in $FILE_LIST
do

	FILE_NAME=$(echo $i | grep '.gz' | cut -d'.' -f1)
	echo $FILE_NAME
	gzip -d $INPUT_FOLDER'/'$i
	
	FOLDER_NAME=$INPUT_FOLDER'/'$FILE_NAME"_folder"
	mkdir $FOLDER_NAME

	NEW_FILE_COUNT=$(grep 'id="' $INPUT_FOLDER'/'$FILE_NAME | wc -l)

	j=$((0))
	while read LINE
	do   

	    if [ "$LINE" = "$START_GWENG" ] || [ "$LINE" = "$END_GWENG" ]
	    then
		continue
	    fi

	    FILE_CREATED="./$FOLDER_NAME/$j"
	    echo $LINE >> $FILE_CREATED

	    if [ "$LINE" = "$END_DOC" ]
	    then
		ID=$(grep 'id="' $FILE_CREATED | cut -d'"' -f2 | cut -d'_' -f3)
		`mv "$FILE_CREATED" "./$FOLDER_NAME/$ID"`
		j=$(($j + 1))
		if [ "$VERBOSE_ON" = "true" ]
		then
			echo "$j of $NEW_FILE_COUNT"
		fi
	    fi

	done < $INPUT_FOLDER'/'$FILE_NAME
done

#rm "$INPUT_FOLDER/*"
exit 0

