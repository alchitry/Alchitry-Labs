grammar LucidIndent;

@header {
//package com.embeddedmicro.mojo.lucid.indent;
}

// starting rule
lucid: indent* (module|global)*;

module: 'module' fluff*? indent*;
global: 'global' fluff*? indent*;

elem: STUFF+ ':' (indent|fluff)*?;

indent 
  : BLOCK_COMMENT 
  | 'always' block
  | always_line
  | '{' (indent|fluff)*? '}'
  | '(' (indent|fluff)*? ')'
  | '[' (indent|fluff)*? ']'
  ;

always_line
  : 'if' indent block else_block?
  | 'for' indent block
  | 'case' indent '{' elem* '}'
  ;

else_block: 'else' block;

block 
  : always_line              #AlwaysBlock
  |'{' (indent|fluff)*? '}'  #MultiBlock
  | (indent|fluff)*? ';'     #SingleBlock
  ;  

fluff: (STUFF|';'|':'|'\\')+?;

BLOCK_COMMENT: ('/*' .*? '*/');
COMMENT: ('//' ~[\r\n]* '\r'? '\n') -> channel(HIDDEN);
WS: [ \t\n\r]+ -> skip;

STUFF: ~[()[\]{} \r\n\t;:]+;



