/*
 * The MIT license
 *
 * Copyright (c) 2010 Radek Ježdík <redhead@email.cz>, Ondřej Brejla <ondrej@brejla.cz>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package org.netbeans.modules.php.nette.macros;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;

/**
 * Base Macro class
 * Handles macro name, if it is pair macro, end and macro name (ifCurrent /if)
 * @author Radek Ježdík
 */
public class LatteMacro {

    protected String macro;
    protected boolean isPair;
    protected String endMacro;

    public LatteMacro(String macro) {
        this(macro, false, macro);
    }

    public LatteMacro(String macro, boolean isPair) {
        this(macro, isPair, macro);
    }

    public LatteMacro(String macro, boolean isPair, String endMacro) {
        this.macro = macro;
        this.isPair = isPair;
        this.endMacro = endMacro;
    }

	/**
	 * Completes the macro in the document
	 * @param jtc
	 * @param dotOffset
	 */
    public void process(JTextComponent jtc, int dotOffset) {
        StyledDocument doc = (StyledDocument) jtc.getDocument();
        try {
            doc.insertString(dotOffset, "{"+macro+"}", null);
            if(isPair) {
				// FIXME get rid of this
				// used when text selected (useless since completion appears only after { char)
                doc.insertString(jtc.getSelectionEnd(), "{/"+endMacro+"}", null);
            } else {
                doc.remove(jtc.getSelectionStart(), jtc.getSelectionEnd()-jtc.getSelectionStart());
            }
            jtc.setCaretPosition(dotOffset + macro.length() + 2);		// moves caret after (start)
        }
        catch(Exception ex) {
            Logger.getLogger(LatteCommentMacro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

	/**
	 * Returns text to be used (shown) in completion box
	 * @return
	 */
    public String getText() {
        String text = getMacro();
        if(isPair)
            text += getEndMacro();
        return text;
    }

	/**
	 * Return start macro itself (with delimiters)
	 * @return
	 */
    public String getMacro() {
        return '{'+macro+'}';
    }

	/**
	 * Return row macro name
	 * @return
	 */
    public String getMacroName() {
        return macro;
    }

	/**
	 * Returns end macro itself (with delimiters)
	 * @return
	 */
    public String getEndMacro() {
        return "{/"+endMacro+"}";
    }

	/**
	 * Returns end macro name (can differ from start macro name!)
	 * @return
	 */
    public String getEndMacroName() {
        return endMacro;
    }

	/**
	 * Is macro a pair macro?
	 * @return
	 */
    public boolean isPair() {
        return isPair;
    }

}
