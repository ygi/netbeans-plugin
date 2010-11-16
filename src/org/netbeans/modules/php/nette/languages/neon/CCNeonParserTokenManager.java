/* Generated By:JavaCC: Do not edit this line. CCNeonParserTokenManager.java */
package org.netbeans.modules.php.nette.languages.neon;
import java.util.*;

/** Token Manager. */
public class CCNeonParserTokenManager implements CCNeonParserConstants
{

  /** Debug output. */
  public  java.io.PrintStream debugStream = System.out;
  /** Set debug output. */
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x40L) != 0L)
            return 63;
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 44:
         return jjStopAtPos(0, 5);
      case 45:
         return jjStartNfaWithStates_0(0, 6, 63);
      case 58:
         return jjStopAtPos(0, 4);
      case 60:
         return jjStopAtPos(0, 3);
      case 61:
         jjmatchedKind = 7;
         return jjMoveStringLiteralDfa1_0(0x100L);
      case 91:
         return jjStopAtPos(0, 11);
      case 93:
         return jjStopAtPos(0, 12);
      case 123:
         return jjStopAtPos(0, 9);
      case 125:
         return jjStopAtPos(0, 10);
      default :
         return jjMoveNfa_0(3, 0);
   }
}
private int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 62:
         if ((active0 & 0x100L) != 0L)
            return jjStopAtPos(1, 8);
         break;
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
private int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private int jjMoveNfa_0(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 63;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 63:
                  if ((0x8bffec52ffffd9ffL & l) != 0L)
                  {
                     if (kind > 17)
                        kind = 17;
                     jjCheckNAdd(40);
                  }
                  if ((0x3fe000000000000L & l) != 0L)
                  {
                     if (kind > 15)
                        kind = 15;
                     jjCheckNAddTwoStates(36, 30);
                  }
                  else if (curChar == 48)
                  {
                     if (kind > 15)
                        kind = 15;
                     jjCheckNAdd(30);
                  }
                  break;
               case 3:
                  if ((0x8bffec52ffffd9ffL & l) != 0L)
                  {
                     if (kind > 17)
                        kind = 17;
                     jjCheckNAdd(40);
                  }
                  else if ((0x100000200L & l) != 0L)
                  {
                     if (kind > 19)
                        kind = 19;
                     jjCheckNAddStates(0, 3);
                  }
                  else if ((0x2400L & l) != 0L)
                  {
                     if (kind > 1)
                        kind = 1;
                  }
                  else if (curChar == 35)
                     jjCheckNAddTwoStates(56, 57);
                  else if (curChar == 37)
                     jjCheckNAdd(38);
                  else if (curChar == 39)
                     jjCheckNAddTwoStates(26, 27);
                  else if (curChar == 34)
                     jjCheckNAddTwoStates(23, 24);
                  if ((0x3fe000000000000L & l) != 0L)
                  {
                     if (kind > 15)
                        kind = 15;
                     jjCheckNAddTwoStates(36, 30);
                  }
                  else if (curChar == 13)
                     jjCheckNAddTwoStates(52, 53);
                  else if (curChar == 48)
                  {
                     if (kind > 15)
                        kind = 15;
                     jjCheckNAdd(30);
                  }
                  else if (curChar == 45)
                     jjAddStates(4, 5);
                  break;
               case 22:
                  if (curChar == 34)
                     jjCheckNAddTwoStates(23, 24);
                  break;
               case 23:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddTwoStates(23, 24);
                  break;
               case 24:
                  if (curChar == 34 && kind > 14)
                     kind = 14;
                  break;
               case 25:
                  if (curChar == 39)
                     jjCheckNAddTwoStates(26, 27);
                  break;
               case 26:
                  if ((0xffffff7fffffdbffL & l) != 0L)
                     jjCheckNAddTwoStates(26, 27);
                  break;
               case 27:
                  if (curChar == 39 && kind > 14)
                     kind = 14;
                  break;
               case 28:
                  if (curChar == 45)
                     jjAddStates(4, 5);
                  break;
               case 29:
                  if (curChar != 48)
                     break;
                  if (kind > 15)
                     kind = 15;
                  jjCheckNAdd(30);
                  break;
               case 30:
                  if (curChar == 46)
                     jjCheckNAdd(31);
                  break;
               case 31:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 15)
                     kind = 15;
                  jjCheckNAddTwoStates(31, 32);
                  break;
               case 33:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(34);
                  break;
               case 34:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 15)
                     kind = 15;
                  jjCheckNAdd(34);
                  break;
               case 35:
                  if ((0x3fe000000000000L & l) == 0L)
                     break;
                  if (kind > 15)
                     kind = 15;
                  jjCheckNAddTwoStates(36, 30);
                  break;
               case 36:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 15)
                     kind = 15;
                  jjCheckNAddTwoStates(36, 30);
                  break;
               case 37:
                  if (curChar == 37)
                     jjCheckNAdd(38);
                  break;
               case 38:
                  if ((0x8bffec52ffffd9ffL & l) != 0L)
                     jjCheckNAddTwoStates(38, 39);
                  break;
               case 39:
                  if (curChar == 37 && kind > 16)
                     kind = 16;
                  break;
               case 40:
                  if ((0x8bffec52ffffd9ffL & l) == 0L)
                     break;
                  if (kind > 17)
                     kind = 17;
                  jjCheckNAdd(40);
                  break;
               case 51:
                  if (curChar == 13)
                     jjCheckNAddTwoStates(52, 53);
                  break;
               case 52:
                  if (curChar == 10 && kind > 1)
                     kind = 1;
                  break;
               case 53:
                  if (curChar == 10 && kind > 18)
                     kind = 18;
                  break;
               case 54:
                  if ((0x2400L & l) != 0L && kind > 1)
                     kind = 1;
                  break;
               case 55:
                  if (curChar == 35)
                     jjCheckNAddTwoStates(56, 57);
                  break;
               case 56:
                  if ((0xffffffffffffdbffL & l) == 0L)
                     break;
                  if (kind > 13)
                     kind = 13;
                  jjCheckNAdd(56);
                  break;
               case 57:
                  if ((0xffffffffffffdbffL & l) != 0L)
                     jjCheckNAddStates(6, 8);
                  break;
               case 58:
                  if (curChar == 13)
                     jjCheckNAdd(53);
                  break;
               case 59:
                  if ((0x2400L & l) != 0L && kind > 18)
                     kind = 18;
                  break;
               case 60:
                  if ((0x100000200L & l) == 0L)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjCheckNAddStates(0, 3);
                  break;
               case 61:
                  if ((0x100000200L & l) != 0L)
                     jjCheckNAddStates(9, 11);
                  break;
               case 62:
                  if ((0x100000200L & l) == 0L)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjCheckNAdd(62);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 63:
               case 40:
                  if ((0xd7ffffffd7fffffeL & l) == 0L)
                     break;
                  if (kind > 17)
                     kind = 17;
                  jjCheckNAdd(40);
                  break;
               case 3:
                  if ((0xd7ffffffd7fffffeL & l) != 0L)
                  {
                     if (kind > 17)
                        kind = 17;
                     jjCheckNAdd(40);
                  }
                  if (curChar == 78)
                     jjAddStates(12, 13);
                  else if (curChar == 110)
                     jjAddStates(14, 15);
                  else if (curChar == 89)
                     jjstateSet[jjnewStateCnt++] = 20;
                  else if (curChar == 121)
                     jjstateSet[jjnewStateCnt++] = 17;
                  else if (curChar == 70)
                     jjstateSet[jjnewStateCnt++] = 14;
                  else if (curChar == 102)
                     jjstateSet[jjnewStateCnt++] = 10;
                  else if (curChar == 84)
                     jjstateSet[jjnewStateCnt++] = 6;
                  else if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               case 0:
                  if (curChar == 101 && kind > 2)
                     kind = 2;
                  break;
               case 1:
                  if (curChar == 117)
                     jjCheckNAdd(0);
                  break;
               case 2:
                  if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 4:
                  if (curChar == 69 && kind > 2)
                     kind = 2;
                  break;
               case 5:
                  if (curChar == 85)
                     jjCheckNAdd(4);
                  break;
               case 6:
                  if (curChar == 82)
                     jjstateSet[jjnewStateCnt++] = 5;
                  break;
               case 7:
                  if (curChar == 84)
                     jjstateSet[jjnewStateCnt++] = 6;
                  break;
               case 8:
                  if (curChar == 115)
                     jjCheckNAdd(0);
                  break;
               case 9:
                  if (curChar == 108)
                     jjstateSet[jjnewStateCnt++] = 8;
                  break;
               case 10:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 9;
                  break;
               case 11:
                  if (curChar == 102)
                     jjstateSet[jjnewStateCnt++] = 10;
                  break;
               case 12:
                  if (curChar == 83)
                     jjCheckNAdd(4);
                  break;
               case 13:
                  if (curChar == 76)
                     jjstateSet[jjnewStateCnt++] = 12;
                  break;
               case 14:
                  if (curChar == 65)
                     jjstateSet[jjnewStateCnt++] = 13;
                  break;
               case 15:
                  if (curChar == 70)
                     jjstateSet[jjnewStateCnt++] = 14;
                  break;
               case 16:
                  if (curChar == 115 && kind > 2)
                     kind = 2;
                  break;
               case 17:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 16;
                  break;
               case 18:
                  if (curChar == 121)
                     jjstateSet[jjnewStateCnt++] = 17;
                  break;
               case 19:
                  if (curChar == 83 && kind > 2)
                     kind = 2;
                  break;
               case 20:
                  if (curChar == 69)
                     jjstateSet[jjnewStateCnt++] = 19;
                  break;
               case 21:
                  if (curChar == 89)
                     jjstateSet[jjnewStateCnt++] = 20;
                  break;
               case 23:
                  jjAddStates(16, 17);
                  break;
               case 26:
                  jjAddStates(18, 19);
                  break;
               case 32:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(20, 21);
                  break;
               case 38:
                  if ((0xd7ffffffd7fffffeL & l) != 0L)
                     jjAddStates(22, 23);
                  break;
               case 41:
                  if (curChar == 110)
                     jjAddStates(14, 15);
                  break;
               case 42:
                  if (curChar == 111 && kind > 2)
                     kind = 2;
                  break;
               case 43:
                  if (curChar == 108 && kind > 2)
                     kind = 2;
                  break;
               case 44:
                  if (curChar == 108)
                     jjstateSet[jjnewStateCnt++] = 43;
                  break;
               case 45:
                  if (curChar == 117)
                     jjstateSet[jjnewStateCnt++] = 44;
                  break;
               case 46:
                  if (curChar == 78)
                     jjAddStates(12, 13);
                  break;
               case 47:
                  if (curChar == 79 && kind > 2)
                     kind = 2;
                  break;
               case 48:
                  if (curChar == 76 && kind > 2)
                     kind = 2;
                  break;
               case 49:
                  if (curChar == 76)
                     jjstateSet[jjnewStateCnt++] = 48;
                  break;
               case 50:
                  if (curChar == 85)
                     jjstateSet[jjnewStateCnt++] = 49;
                  break;
               case 56:
                  if (kind > 13)
                     kind = 13;
                  jjstateSet[jjnewStateCnt++] = 56;
                  break;
               case 57:
                  jjAddStates(6, 8);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 63:
               case 40:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 17)
                     kind = 17;
                  jjCheckNAdd(40);
                  break;
               case 3:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 17)
                     kind = 17;
                  jjCheckNAdd(40);
                  break;
               case 23:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(16, 17);
                  break;
               case 26:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(18, 19);
                  break;
               case 38:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(22, 23);
                  break;
               case 56:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 13)
                     kind = 13;
                  jjstateSet[jjnewStateCnt++] = 56;
                  break;
               case 57:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(6, 8);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 63 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   61, 58, 59, 62, 29, 35, 57, 58, 59, 61, 58, 59, 47, 50, 42, 45, 
   23, 24, 26, 27, 33, 34, 38, 39, 
};

/** Token literal values. */
public static final String[] jjstrLiteralImages = {
"", null, null, "\74", "\72", "\54", "\55", "\75", "\75\76", "\173", "\175", 
"\133", "\135", null, null, null, null, null, null, null, null, };

/** Lexer state names. */
public static final String[] lexStateNames = {
   "DEFAULT",
};
protected SimpleCharStream input_stream;
private final int[] jjrounds = new int[63];
private final int[] jjstateSet = new int[126];
protected char curChar;
/** Constructor. */
public CCNeonParserTokenManager(SimpleCharStream stream){
   if (SimpleCharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}

/** Constructor. */
public CCNeonParserTokenManager(SimpleCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 63; i-- > 0;)
      jjrounds[i] = 0x80000000;
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}

/** Switch to specified lex state. */
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   final Token t;
   final String curTokenImage;
   final int beginLine;
   final int endLine;
   final int beginColumn;
   final int endColumn;
   String im = jjstrLiteralImages[jjmatchedKind];
   curTokenImage = (im == null) ? input_stream.GetImage() : im;
   beginLine = input_stream.getBeginLine();
   beginColumn = input_stream.getBeginColumn();
   endLine = input_stream.getEndLine();
   endColumn = input_stream.getEndColumn();
   t = Token.newToken(jjmatchedKind, curTokenImage);

   t.beginLine = beginLine;
   t.endLine = endLine;
   t.beginColumn = beginColumn;
   t.endColumn = endColumn;

   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

/** Get the next Token. */
public Token getNextToken() 
{
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {
   try
   {
      curChar = input_stream.BeginToken();
   }
   catch(java.io.IOException e)
   {
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedPos == 0 && jjmatchedKind > 20)
   {
      jjmatchedKind = 20;
   }
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
         matchedToken = jjFillToken();
         return matchedToken;
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

private void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}

private void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}

}
