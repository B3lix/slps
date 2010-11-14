:- ['syntax.pro'].
:- ['semantics.pro'].
:- ['types.pro'].

% Read, typecheck and reduce

main(Input)
 :-
    see(Input),
    read(Term),
    seen,
    format('Input term: ~w~n',[Term]),
    type(Term, Type),    
    format('Type of term: ~w~n',[Type]),
    eval(Term,Value),
    format('Value of term: ~w~n',[Value]),
    halt.

:-
    current_prolog_flag(argv,Argv),
    ( append(_,['--',Input],Argv), main(Input); true ).