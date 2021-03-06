package com.eightbitmage.moonscript.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import java.util.*;
import java.lang.reflect.Field;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;

%%

/*
* moon.l - flex lexer for MoonScript
*/

%class _MoonLexer
%implements FlexLexer, MoonTokenTypes
%type IElementType
%function advance
%unicode
//%debug

%{
    ExtendedSyntaxStrCommentHandler longCommentOrStringHandler = new ExtendedSyntaxStrCommentHandler();
    int current_line_indent = 0;
    int indent_level = 0;
%}

w           =   [ \t]+
nl          =   \r\n|\n|\r
nonl        =   [^\r\n]
nobrknl     =   [^\[\r\n]
name        =   [_a-zA-Z][_a-zA-Z0-9]*
n           =   [0-9]+
exp         =   [Ee][+-]?{n}
number      =   (0[xX][0-9a-fA-F]+|({n}|{n}[.]{n}){exp}?|[.]{n}|{n}[.])
sep         =   =*
luadoc      =   ---[^\r\n]*{nl}([ \t]*--({nobrknl}{nonl}*{nl}|{nonl}{nl}|{nl}))*

%x XLONGSTRING
%x XLONGSTRING_BEGIN
%x XSHORTCOMMENT
%x XLONGCOMMENT
%x XSTRINGQ
%x XSTRINGA
%x XINDENT

%s YYNORMAL
%s YYNAME
%s YYNUMBER

%%

/*************************************************************************************************/

<YYINITIAL> {
  (.|{nl})  { current_line_indent = 0; indent_level = 0; yypushback(1); yybegin(XINDENT); }
}

<YYNORMAL> {
    {nl}     { current_line_indent = 0; yybegin(XINDENT); return NEWLINE;}

    // Error keywords
    "end"          { return WRONG; }

    /* Keywords */
    "and"          { return AND; }
    "break"        { return BREAK; }
    "do"           { return DO; }
    "else"         { return ELSE; }
    "elseif"       { return ELSEIF; }
    "false"        { return FALSE; }
    "for"          { return FOR; }
    "function"     { return FUNCTION; }
    "->"           { return FUNCTION; }
    "=>"           { return FUNCTION; }
    "class"        { return CLASS; }
    "extends"      { return EXTENDS; }
    "if"           { return IF; }
    "in"           { return IN; }
    "local"        { return LOCAL; }
    "nil"          { return NIL; }
    "not"          { return NOT; }
    "or"           { return OR; }
    "repeat"       { return REPEAT; }
    "return"       { return RETURN; }
    "then"         { return THEN; }
    "true"         { return TRUE; }
    "until"        { return UNTIL; }
    "while"        { return WHILE; }

    "export"       { return EXPORT; }
    "import"       { return IMPORT; }
    "with"         { return WITH; }
    "switch"       { return SWITCH; }
    "when"         { return WHEN; }


    //{luadoc}       { yypushback(1); return LUADOC_COMMENT; }

    --\[{sep}\[    { longCommentOrStringHandler.setCurrentExtQuoteStart(yytext().toString()); yybegin( XLONGCOMMENT ); return LONGCOMMENT_BEGIN; }
    --+            { yypushback(yytext().length()); yybegin( XSHORTCOMMENT ); return advance(); }

    "["{sep}"["    { longCommentOrStringHandler.setCurrentExtQuoteStart(yytext().toString()); yybegin( XLONGSTRING_BEGIN ); return LONGSTRING_BEGIN; }

    "\""           { yybegin(XSTRINGQ); return STRING; }
    '              { yybegin(XSTRINGA); return STRING; }

    "#!"           { yybegin( XSHORTCOMMENT ); return SHEBANG; }

    {name}         { yybegin(YYNAME); return NAME; }
    {number}       { yybegin(YYNUMBER); return NUMBER; }

    "..."        { return ELLIPSIS; }
    ".."         { return CONCAT; }
    "..="        { return CONCAT; }
    "=="         { return EQ; }
    ">="         { return GE; }
    "<="         { return LE; }
    "~="         { return NE; }
    "!="         { return NE; }
    "-"          { return MINUS; }
    "-="         { return MINUS; }
    "+"          { return PLUS;}
    "+="         { return PLUS;}
    "*"          { return MULT;}
    "*="         { return MULT;}
    "%"          { return MOD;}
    "%="         { return MOD;}
    "/"          { return DIV; }
    "/="         { return DIV; }
    "="          { return ASSIGN;}
    ">"          { return GT;}
    "<"          { return LT;}
    "("          { return LPAREN;}
    ")"          { return RPAREN;}
    "["          { return LBRACK;}
    "]"          { return RBRACK;}
    "{"          { return LCURLY;}
    "}"          { return RCURLY;}
    "#"          { return GETN;}
    ","          { return COMMA; }
    ";"          { return SEMI; }
    ":"          { return COLON; }
    "."          { return DOT;}
    "\\"         { return DOT;}
    "^"          { return EXP;}

    "@"          { return SELF; }
    "!"          { return FUNCTION; }

    {w}          { return WS; }

    .            { yybegin(YYNORMAL); return WRONG; }
}


<XINDENT>
{
  " "      { current_line_indent++; return WS; }
  "\t"     { current_line_indent++; return WS; }
  {nl}     { current_line_indent = 0; return NEWLINE; /*ignoring blank line */ }
  .        {
                   yypushback(1);
                   if (current_line_indent > indent_level) {
                       indent_level++;
                       yybegin(YYNORMAL);
                       return INDENT;
                   } else if (current_line_indent < indent_level) {
                       indent_level--;
                       yybegin(YYNORMAL);
                       return UNINDENT;
                   } else {
                       yybegin(YYNORMAL);
                   }
           }
}

<XSTRINGQ>
{
  \"\"       { return STRING; }
  \"         { yybegin(YYNORMAL); return STRING; }
  \\[abfnrt] { return STRING; }
  \\\n       { return STRING; }
  \\\"       { return STRING; }
  \\'        { return STRING; }
  \\"["      { return STRING; }
  \\"]"      { return STRING; }
  \\\\       { return STRING; }
  {nl}       { yybegin(YYNORMAL); return WRONG; }
  .          { return STRING; }
}

<XSTRINGA>
{
  ''          { return STRING; }
  '           { yybegin(YYNORMAL); return STRING; }
  \\[abfnrt]  { return STRING; }
  \\\n        { return STRING; }
  \\\'        { return STRING; }
  \\'         { yybegin(YYNORMAL); return STRING; }
  \\"["       { return STRING; }
  \\"]"       { return STRING; }
  \\\\        { return STRING; }
  {nl}        { yybegin(YYNORMAL);return WRONG;  }
  .           { return STRING; }
}

<XLONGSTRING_BEGIN>
{
    {nl}     { return NL_BEFORE_LONGSTRING; }
    .        { yypushback(1); yybegin(XLONGSTRING); return advance(); }
}

<XLONGSTRING>
{
  "]"{sep}"]"     {
                    if (longCommentOrStringHandler.isCurrentExtQuoteStart(yytext())) {
                       yybegin(YYNORMAL); longCommentOrStringHandler.resetCurrentExtQuoteStart(); return LONGSTRING_END;
                    } else { yypushback(yytext().length()-1); }
                    return LONGSTRING;
                  }
  {nl}     { return LONGSTRING; }
  .        { return LONGSTRING; }
}

<XSHORTCOMMENT>
{
  {nl}      {yybegin(YYNORMAL);  yypushback(yylength()); return NEWLINE; }
  .         { return SHORTCOMMENT;}
}

<XLONGCOMMENT>
{
  "]"{sep}"]"     {
                    if (longCommentOrStringHandler.isCurrentExtQuoteStart(yytext())) {
                       yybegin(YYINITIAL); longCommentOrStringHandler.resetCurrentExtQuoteStart(); return LONGCOMMENT_END;
                    }  else { yypushback(yytext().length()-1); }
                  return LONGCOMMENT;
                  }

  {nl}     { return LONGCOMMENT;}
  .        { return LONGCOMMENT;}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/*****************************************************************/
/* Characters than can follow an identifier or a number directly */
/*****************************************************************/

<YYNAME, YYNUMBER> {
  "."          { yybegin(YYNORMAL); return DOT; }
  ":"          { yybegin(YYNORMAL); return COLON; }
  ","          { yybegin(YYNORMAL); return COMMA; }
  "["          { yybegin(YYNORMAL); return LBRACK; }
  "]"          { yybegin(YYNORMAL); return RBRACK; }
  ")"          { yybegin(YYNORMAL); return RPAREN; }
  "}"          { yybegin(YYNORMAL); return RCURLY; }
  "+"          { yybegin(YYNORMAL); return PLUS; }
  "-"          { yybegin(YYNORMAL); return MINUS; }
  "*"          { yybegin(YYNORMAL); return MULT; }
  "%"          { yybegin(YYNORMAL); return MOD; }
  "/"          { yybegin(YYNORMAL); return DIV; }
  "!"          { yybegin(YYNORMAL); return FUNCTION; }
  {nl}         { yybegin(YYNORMAL); yypushback(yylength()); return NEWLINE; }
  {w}          { yybegin(YYNORMAL); return WS; }
}


/**********************************************************************/
/* An identifier has some more characters that can follow it directly */
/**********************************************************************/

<YYNAME> {
  "\""        { yybegin(XSTRINGQ); return STRING; }
  '           { yybegin(XSTRINGA); return STRING; }

  "("         { yybegin(YYNORMAL); return LPAREN; }
  ";"         { yybegin(YYNORMAL); return SEMI; }
  "\\"        { yybegin(YYNORMAL); return DOT; }
  "!"         { yybegin(YYNORMAL); return FUNCTION; }
  "="         { yybegin(YYNORMAL); return ASSIGN; }
}


/*******************/
/* Nothing matched */
/*******************/

 .    { return WRONG; }