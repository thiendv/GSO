/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.function.print.attribute;

/**
 * プリンタで発生するエラーをカプセル化した例外クラスです。
 * Encapsulates the error conditions that occur for printing.
 */
public class PrintException extends Exception{
    public PrintException(){
    }

    public PrintException(String message) {
        super(message);
    }

    public PrintException(Exception ex) {
        super(ex.getMessage());
    }
}
