@contributor{Vadim Zaytsev - vadim@grammarware.net - SWAT, CWI}
module analyse::Mining

import analyse::Metrics;
import language::BGF;
import io::ReadBGF;
import String;
import List;
import Map;
import IO;
import lib::Rascalware;
import export::BNF;

alias dict = map[BGFExpression,int];
alias NPC = tuple[int ns, int clasns, int ps, int cx, dict patterns];
alias SGrammar = tuple[set[str] roots, map[str,set[BGFProduction]] prods];

public void main(list[str] as)
{
	loc zoo = |home:///projects/webslps/zoo|;
	NPC npc = getZoo(|home:///projects/webslps/zoo|,<0,0,0,0,()>);
	npc = getZoo(|home:///projects/webslps/tank|,npc);
	println("Total: <npc.cx> grammars, <npc.ps> production rules, <npc.ns> nonterminals (<npc.ns-npc.clasns> thereof classified), <len(npc.patterns)> patterns.");
	// Just the Zoo:
	//              Total: 42 grammars, 8927 production rules, 8277 nonterminals.
	// Zoo + Tank:
	//              Total: 99 grammars, 11570 production rules, 10943 nonterminals.
	// TODO: only report unclassified ones
	// for (BGFExpression e <- domain(npc.patterns))
	// 	println("<pp(e)>: <npc.patterns[e]>");
}

NPC getZoo(loc zoo, NPC npc)
{
	dict patterns = npc.patterns;
	int n = npc.ns, pcx = npc.ps, cx = npc.cx, cns = npc.clasns;
	BGFGrammar g;
	SGrammar s;
	set[str] allNTs = {};
	for (str lang <- listEntries(zoo), !endsWith(lang,".html"), str s <- listEntries(zoo+"/<lang>"), endsWith(s,".bgf"))
	{
		println(s);
		cx += 1;
		g = readBGF(zoo+"/<lang>/<s>");
		allNTs = allNs(g);
		n += len(allNTs);
		pcx += len(g.prods);

		sg = splitGrammar(g);
		if (domain(sg.prods) != allNTs)
			println("Nonterminal sets are not equal!\n<range(sg.prods)>\n<allNTs>");
		
		for (metric <- {tops, bottoms, ifroots, multiroots, horizontals, verticals})
		{
			res = metric(sg);
			println("Classified as <metric>: <len(res)>.");
			allNTs -= res;
		}
		cns += len(allNTs);
		
		g = abstractPattern(g);
		for (BGFProduction p <- abstractPattern(g).prods)
			if (p.rhs in domain(patterns))
				patterns[p.rhs] += 1;
			else
				patterns[p.rhs] = 1;
	}
	return <n,cns,pcx,cx,patterns>;
}

BGFGrammar abstractPattern(BGFGrammar g)
	= visit(g)
	{
		case nonterminal(_) => nonterminal("N")
		case terminal(_) => terminal("T")
		case selectable(_, BGFExpression expr) => expr
	};

SGrammar splitGrammar(BGFGrammar g)
{
	map[str,set[BGFProduction]] ps = ();
	for (BGFProduction p <- g.prods)
		if (p.lhs in domain(ps))
			ps[p.lhs] += {p};
		else
			ps[p.lhs] = {p};
	for (str n <- bottomNs(g))
		ps[n] = {};
	return <toSet(g.roots), ps>;
}

set[str] tops(SGrammar g)    = definedNs(g) - usedNs(g); // = {t | str t <- domain(g.prods), /nonterminal(t) !:= range(g.prods)};
set[str] bottoms(SGrammar g) = usedNs(g) - definedNs(g);
set[str] ifroots(SGrammar g) = g.roots & domain(g.prods);
// TODO: not _, but in fact [*nonterminal(_)]
// TODO: also account for vertical roots
set[str] multiroots(SGrammar g) = {n | str n<-g.roots, {production(_,n,choice(L))} := g.prods[n], allnonterminals(L)};

set[str] horizontals(SGrammar g) = {n | str n <- domain(g.prods), {production(_,n,choice(L))} := g.prods[n] };
set[str] verticals(SGrammar g) = {n | str n <- domain(g.prods), len(g.prods[n])>1 };

// lower level functions
set[str] definedNs(SGrammar g) = domain(g.prods);
set[str] usedNs(SGrammar g) = {t | /nonterminal(t) := range(g.prods)};

bool allnonterminals([]) = true;
// bool allnonterminals([x]) = nonterminal(_) := x;
default bool allnonterminals(BGFExprList xs) = nonterminal(_) := xs[0] && allnonterminals(tail(xs));
