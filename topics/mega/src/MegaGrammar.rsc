@contributor{Vadim Zaytsev - vadim@grammarware.net - SWAT, CWI}
module MegaGrammar

start syntax MegaModel = MegaDesc? MegaHeader MegaInclude* MegaDecl+ L;
syntax MegaHeader = "megamodel" MegaURI ".";
syntax MegaURI = {ID "/"}+;
syntax MegaInclude = "include" MegaURI ".";
syntax MegaDecl
	= MegaModifier? MegaEntity "."
	| MegaRel "."
	;
syntax MegaModifier = "local" | "variable" ;
syntax MegaEntity
	= MegaArtifact "+"? {MegaIdsBin ","}+
	| "Function" "+"? {MegaIdsFun ","}+
	;
syntax MegaIdsBin = ID (MegaBin ID)?;
syntax MegaIdsFun = ID MegaFun?;
syntax MegaArtifact = "Artifact" | "File" | "Language" | "Technology" | "Fragment" | "ObjectGraph" | "Program" | "Library";
syntax MegaRel
	= ID MegaBin ID 
	| ID MegaFun
	| ID "(" ID ")" "|-\>" ID
	;
syntax MegaBin
    = ( "\<"  | "subsetOf" )
    | ( ":"  | "elementOf" )
    | ( "@"  | "partOf" )
    | ( "="  | "correspondsTo" )
    | ( "~\>" | "dependsOn" | "refersTo" )
    | ( "-|" | "conformsTo" )
    | ( "=\>" | "realizationOf" | "descriptionOf" | "definitionOf" )
    ;
syntax MegaFun = ":" ID "-\>" ID;
lexical ID = //@category="Constant"
 ([a-zA-z] [a-zA-Z0-9_]* !>> [a-zA-Z0-9_]) \ Keywords ;
keyword Keywords 
	= "megamodel" | "include" | "local" | "variable" | "Artifact" | "File" | "Language" | "Technology" | "Fragment" | "ObjectGraph" | "Program" | "Library"
	| "subsetOf" | "elementOf" | "partOf" | "correspondsTo" | "dependsOn" | "refersTo" | "conformsTo" | "realizationOf" | "descriptionOf" | "definitionOf"
	;
syntax STRING = [\"] ![\"]* [\"]; //"
lexical MegaDesc = "{-" MegaDescEl* "-}";
lexical MegaDescEl = ![\-] | [\-] !>> [}];

layout L = LAYOUT* !>> [\ \t\n\r] !>> "--";
lexical LAYOUT
	= [\ \t\n\r]
	| @category="Comment" "--" ![\n]* $
	;
