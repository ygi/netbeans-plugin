/* Generated By:JavaCC: Do not edit this line. CCNeonParser.java */
package org.netbeans.modules.php.nette.languages.neon;

import java.util.*;

public class CCNeonParser implements CCNeonParserConstants {
        public List<ParseException> syntaxErrors = new ArrayList<ParseException> ();

        void recover(ParseException ex, int recoveryPoint) {
                syntaxErrors.add(ex);
                Token t;

                do {
                        t = getNextToken ();
                } while (t.kind != EOF && t.kind != recoveryPoint);
        }

  final public void Start() throws ParseException {
    try {
      label_1:
      while (true) {
        if (jj_2_1(3)) {
          ;
        } else {
          break label_1;
        }
        jj_consume_token(EMPTY_LINE);
      }
      label_2:
      while (true) {
        if (jj_2_2(3)) {
          ;
        } else {
          break label_2;
        }
        if (jj_2_3(3)) {
          BlockArray();
        } else if (jj_2_4(3)) {
          BlockHash();
        } else {
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
      label_3:
      while (true) {
        if (jj_2_5(3)) {
          ;
        } else {
          break label_3;
        }
        jj_consume_token(EMPTY_LINE);
      }
      jj_consume_token(0);
    } catch (ParseException ex) {
                recover(ex, EOL);
    }
  }

  final public void BlockArray() throws ParseException {
    try {
      label_4:
      while (true) {
        if (jj_2_6(3)) {
          ;
        } else {
          break label_4;
        }
        jj_consume_token(EMPTY_LINE);
      }
      BlockArrayEntry();
      label_5:
      while (true) {
        if (jj_2_7(3)) {
          ;
        } else {
          break label_5;
        }
        jj_consume_token(EOL);
        label_6:
        while (true) {
          if (jj_2_8(3)) {
            ;
          } else {
            break label_6;
          }
          jj_consume_token(EMPTY_LINE);
        }
        BlockArrayEntry();
      }
      label_7:
      while (true) {
        if (jj_2_9(3)) {
          ;
        } else {
          break label_7;
        }
        jj_consume_token(EMPTY_LINE);
      }
    } catch (ParseException ex) {
                recover(ex, EOL);
    }
  }

  final public void BlockArrayEntry() throws ParseException {
    try {
      if (jj_2_20(3)) {
        label_8:
        while (true) {
          if (jj_2_10(3)) {
            ;
          } else {
            break label_8;
          }
          jj_consume_token(EMPTY_LINE);
        }
        if (jj_2_11(3)) {
          jj_consume_token(SPACE);
        } else {
          ;
        }
        jj_consume_token(DASH);
        if (jj_2_12(3)) {
          jj_consume_token(SPACE);
        } else {
          ;
        }
        jj_consume_token(EOL);
        label_9:
        while (true) {
          if (jj_2_13(3)) {
            ;
          } else {
            break label_9;
          }
          jj_consume_token(EMPTY_LINE);
        }
        if (jj_2_14(3)) {
          BlockHash();
        } else if (jj_2_15(3)) {
          BlockArray();
        } else {
          jj_consume_token(-1);
          throw new ParseException();
        }
      } else if (jj_2_21(3)) {
        if (jj_2_16(3)) {
          jj_consume_token(SPACE);
        } else {
          ;
        }
        jj_consume_token(DASH);
        if (jj_2_17(3)) {
          jj_consume_token(SPACE);
        } else {
          ;
        }
        Value();
        if (jj_2_18(3)) {
          jj_consume_token(SPACE);
        } else {
          ;
        }
        label_10:
        while (true) {
          if (jj_2_19(3)) {
            ;
          } else {
            break label_10;
          }
          jj_consume_token(EMPTY_LINE);
        }
      } else {
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (ParseException ex) {
                recover(ex, EOL);
    }
  }

  final public void BlockHash() throws ParseException {
    try {
      label_11:
      while (true) {
        if (jj_2_22(3)) {
          ;
        } else {
          break label_11;
        }
        jj_consume_token(EMPTY_LINE);
      }
      BlockHashEntry();
      label_12:
      while (true) {
        if (jj_2_23(3)) {
          ;
        } else {
          break label_12;
        }
        jj_consume_token(EOL);
        label_13:
        while (true) {
          if (jj_2_24(3)) {
            ;
          } else {
            break label_13;
          }
          jj_consume_token(EMPTY_LINE);
        }
        BlockHashEntry();
      }
      label_14:
      while (true) {
        if (jj_2_25(3)) {
          ;
        } else {
          break label_14;
        }
        jj_consume_token(EMPTY_LINE);
      }
    } catch (ParseException ex) {
                recover(ex, EOL);
    }
  }

  final public void BlockHashEntry() throws ParseException {
    try {
      label_15:
      while (true) {
        if (jj_2_26(3)) {
          ;
        } else {
          break label_15;
        }
        jj_consume_token(EMPTY_LINE);
      }
      if (jj_2_31(3)) {
        Key();
        jj_consume_token(COLON);
        if (jj_2_27(3)) {
          jj_consume_token(SPACE);
        } else {
          ;
        }
      } else if (jj_2_32(3)) {
        Key();
        if (jj_2_28(3)) {
          jj_consume_token(SPACE);
        } else {
          ;
        }
        jj_consume_token(COLON);
        if (jj_2_29(3)) {
          jj_consume_token(SPACE);
        } else {
          ;
        }
        Value();
        if (jj_2_30(3)) {
          jj_consume_token(SPACE);
        } else {
          ;
        }
      } else {
        jj_consume_token(-1);
        throw new ParseException();
      }
      jj_consume_token(EOL);
      label_16:
      while (true) {
        if (jj_2_33(3)) {
          ;
        } else {
          break label_16;
        }
        jj_consume_token(EMPTY_LINE);
      }
      if (jj_2_34(3)) {
        BlockHash();
      } else if (jj_2_35(3)) {
        BlockArray();
      } else {
        jj_consume_token(-1);
        throw new ParseException();
      }
      label_17:
      while (true) {
        if (jj_2_36(3)) {
          ;
        } else {
          break label_17;
        }
        jj_consume_token(EMPTY_LINE);
      }
    } catch (ParseException ex) {
                recover(ex, EOL);
    }
  }

  final public void Value() throws ParseException {
    try {
      if (jj_2_37(3)) {
        jj_consume_token(KEYWORD);
      } else if (jj_2_38(3)) {
        jj_consume_token(NUMBER);
      } else if (jj_2_39(3)) {
        jj_consume_token(VARIABLE);
      } else if (jj_2_40(3)) {
        jj_consume_token(STRING);
      } else if (jj_2_41(3)) {
        jj_consume_token(LITERAL);
      } else {
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (ParseException ex) {
                recover(ex, EOL);
    }
  }

  final public void Key() throws ParseException {
    try {
      if (jj_2_42(3)) {
        jj_consume_token(NUMBER);
      } else if (jj_2_43(3)) {
        jj_consume_token(STRING);
      } else if (jj_2_44(3)) {
        jj_consume_token(LITERAL);
      } else {
        jj_consume_token(-1);
        throw new ParseException();
      }
      if (jj_2_50(3)) {
        if (jj_2_45(3)) {
          jj_consume_token(SPACE);
        } else {
          ;
        }
        jj_consume_token(EXTENDS);
        if (jj_2_46(3)) {
          jj_consume_token(SPACE);
        } else {
          ;
        }
        if (jj_2_47(3)) {
          jj_consume_token(NUMBER);
        } else if (jj_2_48(3)) {
          jj_consume_token(STRING);
        } else if (jj_2_49(3)) {
          jj_consume_token(LITERAL);
        } else {
          jj_consume_token(-1);
          throw new ParseException();
        }
      } else {
        ;
      }
    } catch (ParseException ex) {
                recover(ex, EOL);
    }
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  private boolean jj_2_5(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_5(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(4, xla); }
  }

  private boolean jj_2_6(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_6(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(5, xla); }
  }

  private boolean jj_2_7(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_7(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(6, xla); }
  }

  private boolean jj_2_8(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_8(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(7, xla); }
  }

  private boolean jj_2_9(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_9(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(8, xla); }
  }

  private boolean jj_2_10(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_10(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(9, xla); }
  }

  private boolean jj_2_11(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_11(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(10, xla); }
  }

  private boolean jj_2_12(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_12(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(11, xla); }
  }

  private boolean jj_2_13(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_13(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(12, xla); }
  }

  private boolean jj_2_14(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_14(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(13, xla); }
  }

  private boolean jj_2_15(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_15(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(14, xla); }
  }

  private boolean jj_2_16(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_16(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(15, xla); }
  }

  private boolean jj_2_17(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_17(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(16, xla); }
  }

  private boolean jj_2_18(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_18(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(17, xla); }
  }

  private boolean jj_2_19(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_19(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(18, xla); }
  }

  private boolean jj_2_20(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_20(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(19, xla); }
  }

  private boolean jj_2_21(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_21(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(20, xla); }
  }

  private boolean jj_2_22(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_22(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(21, xla); }
  }

  private boolean jj_2_23(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_23(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(22, xla); }
  }

  private boolean jj_2_24(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_24(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(23, xla); }
  }

  private boolean jj_2_25(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_25(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(24, xla); }
  }

  private boolean jj_2_26(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_26(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(25, xla); }
  }

  private boolean jj_2_27(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_27(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(26, xla); }
  }

  private boolean jj_2_28(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_28(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(27, xla); }
  }

  private boolean jj_2_29(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_29(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(28, xla); }
  }

  private boolean jj_2_30(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_30(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(29, xla); }
  }

  private boolean jj_2_31(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_31(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(30, xla); }
  }

  private boolean jj_2_32(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_32(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(31, xla); }
  }

  private boolean jj_2_33(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_33(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(32, xla); }
  }

  private boolean jj_2_34(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_34(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(33, xla); }
  }

  private boolean jj_2_35(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_35(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(34, xla); }
  }

  private boolean jj_2_36(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_36(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(35, xla); }
  }

  private boolean jj_2_37(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_37(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(36, xla); }
  }

  private boolean jj_2_38(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_38(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(37, xla); }
  }

  private boolean jj_2_39(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_39(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(38, xla); }
  }

  private boolean jj_2_40(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_40(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(39, xla); }
  }

  private boolean jj_2_41(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_41(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(40, xla); }
  }

  private boolean jj_2_42(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_42(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(41, xla); }
  }

  private boolean jj_2_43(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_43(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(42, xla); }
  }

  private boolean jj_2_44(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_44(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(43, xla); }
  }

  private boolean jj_2_45(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_45(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(44, xla); }
  }

  private boolean jj_2_46(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_46(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(45, xla); }
  }

  private boolean jj_2_47(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_47(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(46, xla); }
  }

  private boolean jj_2_48(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_48(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(47, xla); }
  }

  private boolean jj_2_49(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_49(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(48, xla); }
  }

  private boolean jj_2_50(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_50(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(49, xla); }
  }

  private boolean jj_3_30() {
    if (jj_scan_token(SPACE)) return true;
    return false;
  }

  private boolean jj_3_6() {
    if (jj_scan_token(EMPTY_LINE)) return true;
    return false;
  }

  private boolean jj_3_35() {
    if (jj_3R_18()) return true;
    return false;
  }

  private boolean jj_3_29() {
    if (jj_scan_token(SPACE)) return true;
    return false;
  }

  private boolean jj_3_34() {
    if (jj_3R_19()) return true;
    return false;
  }

  private boolean jj_3_28() {
    if (jj_scan_token(SPACE)) return true;
    return false;
  }

  private boolean jj_3R_21() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_37()) {
    jj_scanpos = xsp;
    if (jj_3_38()) {
    jj_scanpos = xsp;
    if (jj_3_39()) {
    jj_scanpos = xsp;
    if (jj_3_40()) {
    jj_scanpos = xsp;
    if (jj_3_41()) return true;
    }
    }
    }
    }
    return false;
  }

  private boolean jj_3_36() {
    if (jj_scan_token(EMPTY_LINE)) return true;
    return false;
  }

  private boolean jj_3_4() {
    if (jj_3R_19()) return true;
    return false;
  }

  private boolean jj_3_2() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_3()) {
    jj_scanpos = xsp;
    if (jj_3_4()) return true;
    }
    return false;
  }

  private boolean jj_3_3() {
    if (jj_3R_18()) return true;
    return false;
  }

  private boolean jj_3_27() {
    if (jj_scan_token(SPACE)) return true;
    return false;
  }

  private boolean jj_3R_18() {
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_6()) { jj_scanpos = xsp; break; }
    }
    if (jj_3R_20()) return true;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_7()) { jj_scanpos = xsp; break; }
    }
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_9()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3_5() {
    if (jj_scan_token(EMPTY_LINE)) return true;
    return false;
  }

  private boolean jj_3_33() {
    if (jj_scan_token(EMPTY_LINE)) return true;
    return false;
  }

  private boolean jj_3_32() {
    if (jj_3R_23()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_28()) jj_scanpos = xsp;
    if (jj_scan_token(COLON)) return true;
    xsp = jj_scanpos;
    if (jj_3_29()) jj_scanpos = xsp;
    if (jj_3R_21()) return true;
    return false;
  }

  private boolean jj_3_1() {
    if (jj_scan_token(EMPTY_LINE)) return true;
    return false;
  }

  private boolean jj_3_31() {
    if (jj_3R_23()) return true;
    if (jj_scan_token(COLON)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_27()) jj_scanpos = xsp;
    return false;
  }

  private boolean jj_3_26() {
    if (jj_scan_token(EMPTY_LINE)) return true;
    return false;
  }

  private boolean jj_3_24() {
    if (jj_scan_token(EMPTY_LINE)) return true;
    return false;
  }

  private boolean jj_3_23() {
    if (jj_scan_token(EOL)) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_24()) { jj_scanpos = xsp; break; }
    }
    if (jj_3R_22()) return true;
    return false;
  }

  private boolean jj_3R_22() {
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_26()) { jj_scanpos = xsp; break; }
    }
    xsp = jj_scanpos;
    if (jj_3_31()) {
    jj_scanpos = xsp;
    if (jj_3_32()) return true;
    }
    if (jj_scan_token(EOL)) return true;
    return false;
  }

  private boolean jj_3_49() {
    if (jj_scan_token(LITERAL)) return true;
    return false;
  }

  private boolean jj_3_25() {
    if (jj_scan_token(EMPTY_LINE)) return true;
    return false;
  }

  private boolean jj_3_48() {
    if (jj_scan_token(STRING)) return true;
    return false;
  }

  private boolean jj_3_47() {
    if (jj_scan_token(NUMBER)) return true;
    return false;
  }

  private boolean jj_3_22() {
    if (jj_scan_token(EMPTY_LINE)) return true;
    return false;
  }

  private boolean jj_3_18() {
    if (jj_scan_token(SPACE)) return true;
    return false;
  }

  private boolean jj_3_46() {
    if (jj_scan_token(SPACE)) return true;
    return false;
  }

  private boolean jj_3_17() {
    if (jj_scan_token(SPACE)) return true;
    return false;
  }

  private boolean jj_3_15() {
    if (jj_3R_18()) return true;
    return false;
  }

  private boolean jj_3_45() {
    if (jj_scan_token(SPACE)) return true;
    return false;
  }

  private boolean jj_3_16() {
    if (jj_scan_token(SPACE)) return true;
    return false;
  }

  private boolean jj_3_14() {
    if (jj_3R_19()) return true;
    return false;
  }

  private boolean jj_3_50() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_45()) jj_scanpos = xsp;
    if (jj_scan_token(EXTENDS)) return true;
    xsp = jj_scanpos;
    if (jj_3_46()) jj_scanpos = xsp;
    xsp = jj_scanpos;
    if (jj_3_47()) {
    jj_scanpos = xsp;
    if (jj_3_48()) {
    jj_scanpos = xsp;
    if (jj_3_49()) return true;
    }
    }
    return false;
  }

  private boolean jj_3R_19() {
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_22()) { jj_scanpos = xsp; break; }
    }
    if (jj_3R_22()) return true;
    return false;
  }

  private boolean jj_3_19() {
    if (jj_scan_token(EMPTY_LINE)) return true;
    return false;
  }

  private boolean jj_3_44() {
    if (jj_scan_token(LITERAL)) return true;
    return false;
  }

  private boolean jj_3_43() {
    if (jj_scan_token(STRING)) return true;
    return false;
  }

  private boolean jj_3_42() {
    if (jj_scan_token(NUMBER)) return true;
    return false;
  }

  private boolean jj_3_13() {
    if (jj_scan_token(EMPTY_LINE)) return true;
    return false;
  }

  private boolean jj_3_21() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_16()) jj_scanpos = xsp;
    if (jj_scan_token(DASH)) return true;
    xsp = jj_scanpos;
    if (jj_3_17()) jj_scanpos = xsp;
    if (jj_3R_21()) return true;
    xsp = jj_scanpos;
    if (jj_3_18()) jj_scanpos = xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_19()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3_12() {
    if (jj_scan_token(SPACE)) return true;
    return false;
  }

  private boolean jj_3_11() {
    if (jj_scan_token(SPACE)) return true;
    return false;
  }

  private boolean jj_3_41() {
    if (jj_scan_token(LITERAL)) return true;
    return false;
  }

  private boolean jj_3_40() {
    if (jj_scan_token(STRING)) return true;
    return false;
  }

  private boolean jj_3_10() {
    if (jj_scan_token(EMPTY_LINE)) return true;
    return false;
  }

  private boolean jj_3_39() {
    if (jj_scan_token(VARIABLE)) return true;
    return false;
  }

  private boolean jj_3_20() {
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_10()) { jj_scanpos = xsp; break; }
    }
    xsp = jj_scanpos;
    if (jj_3_11()) jj_scanpos = xsp;
    if (jj_scan_token(DASH)) return true;
    xsp = jj_scanpos;
    if (jj_3_12()) jj_scanpos = xsp;
    if (jj_scan_token(EOL)) return true;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_13()) { jj_scanpos = xsp; break; }
    }
    xsp = jj_scanpos;
    if (jj_3_14()) {
    jj_scanpos = xsp;
    if (jj_3_15()) return true;
    }
    return false;
  }

  private boolean jj_3R_23() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_42()) {
    jj_scanpos = xsp;
    if (jj_3_43()) {
    jj_scanpos = xsp;
    if (jj_3_44()) return true;
    }
    }
    xsp = jj_scanpos;
    if (jj_3_50()) jj_scanpos = xsp;
    return false;
  }

  private boolean jj_3_38() {
    if (jj_scan_token(NUMBER)) return true;
    return false;
  }

  private boolean jj_3_37() {
    if (jj_scan_token(KEYWORD)) return true;
    return false;
  }

  private boolean jj_3_8() {
    if (jj_scan_token(EMPTY_LINE)) return true;
    return false;
  }

  private boolean jj_3_7() {
    if (jj_scan_token(EOL)) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_8()) { jj_scanpos = xsp; break; }
    }
    if (jj_3R_20()) return true;
    return false;
  }

  private boolean jj_3R_20() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_20()) {
    jj_scanpos = xsp;
    if (jj_3_21()) return true;
    }
    return false;
  }

  private boolean jj_3_9() {
    if (jj_scan_token(EMPTY_LINE)) return true;
    return false;
  }

  /** Generated Token Manager. */
  public CCNeonParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[0];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[50];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public CCNeonParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public CCNeonParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new CCNeonParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public CCNeonParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new CCNeonParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public CCNeonParser(CCNeonParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(CCNeonParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[21];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 0; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 21; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 50; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
            case 3: jj_3_4(); break;
            case 4: jj_3_5(); break;
            case 5: jj_3_6(); break;
            case 6: jj_3_7(); break;
            case 7: jj_3_8(); break;
            case 8: jj_3_9(); break;
            case 9: jj_3_10(); break;
            case 10: jj_3_11(); break;
            case 11: jj_3_12(); break;
            case 12: jj_3_13(); break;
            case 13: jj_3_14(); break;
            case 14: jj_3_15(); break;
            case 15: jj_3_16(); break;
            case 16: jj_3_17(); break;
            case 17: jj_3_18(); break;
            case 18: jj_3_19(); break;
            case 19: jj_3_20(); break;
            case 20: jj_3_21(); break;
            case 21: jj_3_22(); break;
            case 22: jj_3_23(); break;
            case 23: jj_3_24(); break;
            case 24: jj_3_25(); break;
            case 25: jj_3_26(); break;
            case 26: jj_3_27(); break;
            case 27: jj_3_28(); break;
            case 28: jj_3_29(); break;
            case 29: jj_3_30(); break;
            case 30: jj_3_31(); break;
            case 31: jj_3_32(); break;
            case 32: jj_3_33(); break;
            case 33: jj_3_34(); break;
            case 34: jj_3_35(); break;
            case 35: jj_3_36(); break;
            case 36: jj_3_37(); break;
            case 37: jj_3_38(); break;
            case 38: jj_3_39(); break;
            case 39: jj_3_40(); break;
            case 40: jj_3_41(); break;
            case 41: jj_3_42(); break;
            case 42: jj_3_43(); break;
            case 43: jj_3_44(); break;
            case 44: jj_3_45(); break;
            case 45: jj_3_46(); break;
            case 46: jj_3_47(); break;
            case 47: jj_3_48(); break;
            case 48: jj_3_49(); break;
            case 49: jj_3_50(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
