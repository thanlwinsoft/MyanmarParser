#!/bin/bash
mmParserBase=`dirname $0`
java -cp $mmParserBase/org.thanlwinsoft.myanmar.jar org.thanlwinsoft.myanmar.MyanmarValidator $@

