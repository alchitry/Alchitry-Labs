grammar Lucid;

@header {
package com.alchitry.labs.parsers.lucid.parser;
}

// starting rule
source: (global | module)* EOF;

global: 'global' name '{' global_stat* '}';

global_stat
  : struct_dec 
  | const_dec ';'
  ;

module: 'module' name param_list? port_list module_body;

param_list: '#(' param_dec (',' param_dec)* ')';
port_list: '(' port_dec (',' port_dec)* ')';

param_dec: param_name (':' param_constraint)?;
port_dec: input_dec | output_dec | inout_dec;

input_dec: SIGNED? 'input' struct_type? name array_size*;
output_dec: SIGNED? 'output' struct_type? name array_size*;
inout_dec: SIGNED? 'inout' struct_type? name array_size*;

param_name: name ('=' expr)?;
param_constraint: expr;

array_size: '[' expr ']';
struct_type: '<' name ('.' name)? '>';

module_body: '{' stat* '}';

stat
  : const_dec ';'   #StatConst
  | var_dec ';'     #StatVar
  | sig_dec ';'     #StatSig
  | fsm_dec ';'     #StatFSM
  | dff_dec ';'     #StatDFF
  | module_inst ';' #StatModuleInst
  | assign_block    #StatAssign
  | always_block    #StatAlways
  | struct_dec      #StatStruct
  ;

const_dec: 'const' name '=' expr;

assign_block: con_list '{' ((dff_dec | fsm_dec | module_inst) ';' | assign_block)* '}'; 
sig_con: '.' name '(' expr ')';
param_con: '#' name '(' (expr | REAL) ')';

type_dec: name array_size*;
dff_single: name array_size* inst_cons?;

var_dec: 'var' type_dec (',' type_dec)*;
sig_dec: SIGNED? 'sig' struct_type? type_dec (',' type_dec)*;
dff_dec: SIGNED? 'dff' struct_type? dff_single (',' dff_single)*;
fsm_dec: 'fsm' name array_size* inst_cons? '=' '{' fsm_states '}';
fsm_states: name (',' name)*;

module_inst: name name array_size* inst_cons?;

inst_cons : '(' con_list ')';
con_list  : connection (',' connection)*;
connection: param_con | sig_con;

struct_member: SIGNED? name struct_type? array_size*;
struct_dec: 'struct' name '{' struct_member (',' struct_member)* '}';

always_block: 'always' block;

always_stat
  : assign_stat ';' #AlwaysStat
  | case_stat       #AlwaysCase
  | if_stat         #AlwaysIf
  | for_stat        #AlwaysFor
  ;

block
  : '{' always_stat* '}'
  | always_stat          
  ;

assign_stat: signal '=' expr; 

array_index: '[' expr ']';
bit_selector
  : '[' expr ':' expr ']'            #BitSelectorConst
  | '[' expr ('+'|'-') ':' expr ']'  #BitSelectorFixWidth
  ;
bit_selection: array_index* (array_index | bit_selector);

signal: name bit_selection? ('.' name bit_selection?)*;

case_stat: 'case' '(' expr ')' '{' case_elem+ '}'; 
case_elem: (expr | 'default') ':' always_stat+;

if_stat: 'if' '(' expr ')' block else_stat?;
else_stat: 'else' block;

for_stat: 'for' '(' assign_stat ';' expr ';' var_assign ')' block;

function: FUNCTION_ID '(' expr (',' expr)* ')';

number: HEX | BIN | DEC | INT | STRING;

expr
  : signal                                      #ExprSignal
  | number                                      #ExprNum
  | function                                    #ExprFunction
  | '(' expr ')'                                #ExprGroup
  | 'c{' expr (',' expr)* '}'                   #ExprConcat
  | expr 'x{' expr '}'                          #ExprDup
  | '{' expr (',' expr)* '}'                    #ExprArray
  | ('~'|'!') expr                              #ExprInvert
  | '-' expr                                    #ExprNegate
  | expr ('*'|'/') expr                         #ExprMultDiv
  | expr ('+'|'-') expr                         #ExprAddSub
  | expr ('>>'|'<<'|'<<<'|'>>>') expr           #ExprShift
  | expr ('|'|'&'|'^'|'~^') expr                #ExprAndOr
  | ('|'|'&'|'~&'|'~|'|'^'|'~^') expr           #ExprCompress
  | expr ('<'|'>'|'=='|'!='|'>='|'<=') expr     #ExprCompare
  | expr ('||'|'&&') expr                       #ExprLogical
  | expr '?' expr ':' expr                      #ExprTernary
  ;

var_assign
  : signal ('++'|'--')
  | assign_stat
  ;

name: TYPE_ID | CONST_ID | SPACE_ID;

HEX: ([1-9][0-9]*)? 'h' ([0-9a-fA-FzZX]|('x' {_input.LA(1) != '{'}?))+;
BIN: ([1-9][0-9]*)? 'b' ([0-1zZX]|('x' {_input.LA(1) != '{'}?))+;
DEC: ([1-9][0-9]*)? 'd' [0-9]+;
REAL: '-'? [0-9]* '.' [0-9]+ | '-'? [0-9]+ '.' [0-9]*;
INT: [0-9]+;
STRING: '"' ( '\\' ~[\r\n] | ~[\\"\r\n] )* '"';

SIGNED: 'signed';
TYPE_ID: [a-z]([a-wy-zA-Z0-9_]|('x' {_input.LA(1) != '{'}?))*;
CONST_ID: [A-Z][A-Z0-9_]*;
SPACE_ID: [A-Z]([a-wy-zA-Z0-9_]|('x' {_input.LA(1) != '{'}?))*;
FUNCTION_ID: '$'[a-z][a-zA-Z0-9_]*;

BLOCK_COMMENT: ('/*' .*? '*/') -> channel(HIDDEN);
COMMENT: ('//' ~[\r\n]*) -> channel(HIDDEN);
WS: [ \t\n\r]+ -> channel(HIDDEN);