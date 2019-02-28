grammar AlchitryConstraints;

@header {
package com.alchitry.labs.parsers.constraints;
}

// starting rule
alchitry_constraints: (pin|clock)* EOF;

pin: 'pin' port_name pin_name PULLUP? PULLDOWN? ';';

clock: 'clock' port_name frequency ';';

name: (BASIC_NAME | FREQ_UNIT);

port_name: name array_index? ('.' name array_index?)*;
pin_name: name;
frequency: number FREQ_UNIT;

array_index: '[' INT ']';

number : INT | REAL;

PULLUP: 'pullup';
PULLDOWN: 'pulldown';

FREQ_UNIT: (M | K | G)? H Z;
BASIC_NAME: [a-zA-Z][a-zA-Z0-9_]*;
REAL: [0-9]* '.' [0-9]+ | [0-9]+ '.' [0-9]*;
INT: [0-9]+;

BLOCK_COMMENT: ('/*' .*? '*/');
COMMENT: ('//' ~[\r\n]* '\r'? '\n') -> channel(HIDDEN);
WS: [ \t\n\r]+ -> skip;

fragment A:('a'|'A');
fragment B:('b'|'B');
fragment C:('c'|'C');
fragment D:('d'|'D');
fragment E:('e'|'E');
fragment F:('f'|'F');
fragment G:('g'|'G');
fragment H:('h'|'H');
fragment I:('i'|'I');
fragment J:('j'|'J');
fragment K:('k'|'K');
fragment L:('l'|'L');
fragment M:('m'|'M');
fragment N:('n'|'N');
fragment O:('o'|'O');
fragment P:('p'|'P');
fragment Q:('q'|'Q');
fragment R:('r'|'R');
fragment S:('s'|'S');
fragment T:('t'|'T');
fragment U:('u'|'U');
fragment V:('v'|'V');
fragment W:('w'|'W');
fragment X:('x'|'X');
fragment Y:('y'|'Y');
fragment Z:('z'|'Z');