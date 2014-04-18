/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.app.scan.application;

import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.AddressbookDestinationSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.DestinationSettingItem;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.FtpAddressManualDestinationSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.MailAddressManualDestinationSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.NcpAddressManualDestinationSetting;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.NcpAddressManualDestinationSetting.ConnectionType;
import jp.co.ricoh.ssdk.sample.function.scan.attribute.standard.SmbAddressManualDestinationSetting;

/**
 * 宛先設定情報クラスです。
 * Destination setting information class.
 */
public class DestinationSettingDataHolder {

    /**
     * 宛先設定データ
     * Destination setting data
     */
    private DestinationSettingItem mDestSettingItem;

    /**
     * アドレス帳参照の宛先データを生成します。
     * メールの場合、宛先のタイプはToです。
     * Creates the destination data to be checked in address book.
     * For email, the type of destination is "To".
     *
     * @param destKind
     * @param entryId
     * @return destination setting item
     */
    public static DestinationSettingItem createAddressbookDestinationSetting(AddressbookDestinationSetting.DestinationKind destKind, String entryId) {
        AddressbookDestinationSetting dest = new AddressbookDestinationSetting();
        dest.setDestinationKind(destKind);
        dest.setEntryId(entryId);
        // 宛先タイプがフォルダだった場合、次のMailToCcBcc設定は無視されます。
        dest.setMailToCcBcc(AddressbookDestinationSetting.MailToCcBcc.TO);
        return dest;
    }

    /**
     * メール宛先（直接入力）のデータを生成します。
     * 宛先タイプはToです。
     * Creates the data of email destination (manual entry).
     * The type of destination is "To".
     *
     * @param mailAddress
     * @return destination setting item
     */
    public static DestinationSettingItem createMailAddressManualDestinationSetting(String mailAddress) {
        MailAddressManualDestinationSetting dest = new MailAddressManualDestinationSetting();
        dest.setMailAddress(mailAddress);
        dest.setMailToCcBcc(MailAddressManualDestinationSetting.MailToCcBcc.TO);
        return dest;
    }

    /**
     * smbフォルダ宛先（直接入力）のデータを生成します。
     * Creates the smb folder destination (manual entry).
     *
     * @param path
     * @param user
     * @param password
     * @return destination setting item
     */
    public static DestinationSettingItem createSmbManualDestinationSetting(String path, String user, String password) {
        SmbAddressManualDestinationSetting dest = new SmbAddressManualDestinationSetting();
        dest.setPath(path);
        dest.setUserName(user);
        dest.setPassword(password);
        return dest;
    }

    /**
     * ncpフォルダ宛先（直接入力）のデータを生成します。
     * Creates the ncp folder destination (manual entry).
     *
     * @param path
     * @param user
     * @param password
     * @param type
     * @return destination setting item
     */
    public static DestinationSettingItem createNcpManualDestinationSetting(String path, String user, String password, ConnectionType type) {
        NcpAddressManualDestinationSetting dest = new NcpAddressManualDestinationSetting();
        dest.setPath(path);
        dest.setUserName(user);
        dest.setPassword(password);
        dest.setConnectionType(type);
        return dest;
    }

    /**
     * ftpフォルダ宛先（直接入力）のデータを生成します。
     * Creates the ftp folder destination (manual entry).
     *
     * @param server
     * @param path
     * @param user
     * @param password
     * @param port
     * @return destination setting item
     */
    public static DestinationSettingItem createFtpManualDestinationSetting(String server, String path, String user, String password, int port) {
        FtpAddressManualDestinationSetting dest = new FtpAddressManualDestinationSetting();
        dest.setServerName(server);
        dest.setPath(path);
        dest.setUserName(user);
        dest.setPassword(password);
        dest.setPort(port);
        return dest;
    }

    /**
     * 宛先設定に設定情報をセットします。
     * セットする設定情報は宛先種別によって異なり、以下の種類があります。
     *  (1)アドレス帳宛先設定（メール/フォルダ）
     *  (2)直接入力メール宛先設定
     *  (3)直接入力FTP宛先設定
     *  (4)直接入力NCP宛先設定
     *  (5)直接入力SMB宛先設定
     *
     *  The following setting information is set. Setting information differs by destination type.
     *  (1) Address book destination setting (email/folder)
     *  (2) Manual entry email destination setting
     *  (3) Manual entry FTP destination setting
     *  (3) Manual entry NCP destination setting
     *  (3) Manual entry SMP destination setting
     *
     * @param destItem
     */
    public void setDestinationSettingItem(DestinationSettingItem destItem) {
        mDestSettingItem = destItem;
    }

    /**
     * 宛先設定の設定情報を取得します。
     *  Obtains setting information of the destination setting.
     * @return
     */
    public DestinationSettingItem getDestinationSettingItem() {
        return mDestSettingItem;
    }
}
