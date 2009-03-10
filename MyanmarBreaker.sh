#!/bin/bash
mmParserBase=`dirname $0`
if test -f /usr/local/share/myanmar-parser/org.thanlwinsoft.myanmar.jar;
then 
	mmParserBase=/usr/local/share/myanmar-parser;
else 
	if test -f /usr/share/myanmar-parser/org.thanlwinsoft.myanmar.jar;
	then
		mmParserBase=/usr/share/myanmar-parser;
	else
		mmParserBase=$mmParserBase/org.thanlwinsoft.myanmar;
	fi
fi
echo $mmParserBase;
java -cp $mmParserBase/org.thanlwinsoft.myanmar.jar org.thanlwinsoft.myanmar.MyanmarBreaker $@

