tools = ../../../../shared/tools
validator = ${tools}/validate

d:
	../mbgf2xbgf.py fl.mbgf sdf dcg test.2to1.xbgf
	xbgf test.2to1.xbgf bgf/sdf.bgf bgf/sdf.2.bgf
	gdts bgf/sdf.2.bgf bgf/dcg.bgf

test:
	./m2 fl.mbgf sdf rsc

go:
	../mbgf2xbgf.py fl.mbgf 3 2 test.3to2.xbgf
	xbgf test.3to2.xbgf bgf/rascal.bgf bgf/rascal.2.bgf
	gdts bgf/rascal.2.bgf bgf/sdf.bgf
	../mbgf2xbgf.py fl.mbgf 1 2 test.1to2.xbgf
	xbgf test.1to2.xbgf bgf/dcg.bgf bgf/dcg.2.bgf
	gdts bgf/dcg.2.bgf bgf/sdf.bgf

t:
	../mbgf2xbgf.py fl.mbgf 1 3 test.res.xbgf
	xbgf2xbnf test.res.xbgf

try:
	xsltproc --stringparam in 1 --stringparam out 2 ~/projects/slps/shared/xsl/mbgf2xbgf.xslt fl.mbgf > test.1to2.xbgf
	xbgf test.1to2.xbgf bgf/dcg.bgf bgf/dcg.2.bgf
	gdts bgf/dcg.2.bgf bgf/rascal.bgf
	xsltproc --stringparam in 3 --stringparam out 2 ~/projects/slps/shared/xsl/mbgf2xbgf.xslt fl.mbgf > test.1to2.xbgf
	xbgf test.1to2.xbgf bgf/dcg.bgf bgf/dcg.2.bgf

build:
	cp ../../xbgf/fl/snapshot/*.bgf snapshot/
	cp ../../xbgf/fl/snapshot/*.bgf bgf/

check:
	${validator} lcf fl.lcf
	ls -1 xbgf/*.xbgf | xargs -n1 ${validator} xbgf
	ls -1  bgf/*.bgf  | xargs -n1 ${validator} bgf

clean:
	rm -f *~
	rm -f codesamples/* xmlsamples/* bgf/*
	rm -rf bin obj
	rm -f *.user *.suo
	rm -f architecture*
	rm -f fl.log

