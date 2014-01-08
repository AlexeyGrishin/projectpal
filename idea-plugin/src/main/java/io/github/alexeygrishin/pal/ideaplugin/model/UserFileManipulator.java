package io.github.alexeygrishin.pal.ideaplugin.model;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolder;
import io.github.alexeygrishin.pal.api.PalFunction;
import io.github.alexeygrishin.pal.ideaplugin.model.lang.FunctionCallString;
import io.github.alexeygrishin.tools.threads.InThread;
import io.github.alexeygrishin.tools.threads.UIThread;

public class UserFileManipulator {

    public static Key<UserFileManipulator> USER_FILE = Key.create("pal.user_file");

    public static UserFileManipulator getInstance(UserDataHolder holder) {
        return holder.getUserData(USER_FILE);
    }

    /**
     * Adds function to the pal class (if not in it alraedy) and adds pal function call to the
     * file edited by user. Caret will be set between parenthesises.
     * TODO: add 'import/require' depending on language.
     * @param editor editor with file edited by user
     * @param palClass pal class
     * @param function function call to which shall be inserted
     * @param insertFrom offset in document where to insert the call
     * @param deleteTo text from insertFrom to deleteTo positions will be deleted. Keep it <= insertFrom to keep document as is
     */
    public void insertPalFunctionCall(final Editor editor, final PalClass palClass, final PalFunction function, final int insertFrom, final int deleteTo) {
        InThread.execute(new Runnable() {
            @Override
            @UIThread(write = true)
            public void run() {
                FunctionCallString str = palClass.addFunction(function);
                Document document = editor.getDocument();
                if (deleteTo > insertFrom) {
                    document.replaceString(insertFrom, deleteTo, "");
                }
                document.insertString(insertFrom, str.functionCall);
                editor.getCaretModel().moveToOffset(insertFrom + str.functionCall.length() + str.caretOffsetFromEnd);
            }
        });
    }

    /**
     * Same as {@link #insertPalFunctionCall(com.intellij.openapi.editor.Editor, PalClass, io.github.alexeygrishin.pal.api.PalFunction, int, int)} but inserts at current caret position
     * @param editor  editor with file edited by user
     * @param palClass pal class
     * @param function function call to which shall be inserted
     */
    public void insertPalFunctionCallAtCaret(final Editor editor, final PalClass palClass, final PalFunction function) {
        insertPalFunctionCall(editor, palClass, function, editor.getCaretModel().getOffset(), -1);
    }


}
