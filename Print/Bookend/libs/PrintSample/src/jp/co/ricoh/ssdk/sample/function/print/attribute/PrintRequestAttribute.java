/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.function.print.attribute;

import jp.co.ricoh.ssdk.sample.function.attribute.Attribute;

/**
 *  印刷時に機器に対して設定できる属性を示すインターフェースです。
 *  Attribute set interface to indicate print request attributes.
 */
public interface PrintRequestAttribute extends Attribute {
    public abstract Object getValue();
}
